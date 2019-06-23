package jp.les.kasa.sample.mykotlinapp.activity.logitem


import android.Manifest.permission
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.googlecode.tesseract.android.TessBaseAPI
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.alert.ErrorDialog
import jp.les.kasa.sample.mykotlinapp.clearTime
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
import jp.les.kasa.sample.mykotlinapp.dialog.DateSelectDialogFragment
import jp.les.kasa.sample.mykotlinapp.dialog.OcrResultDialogFragment
import jp.les.kasa.sample.mykotlinapp.dialog.OcrSelectSourceDialogFragment
import jp.les.kasa.sample.mykotlinapp.getDateStringYMD
import kotlinx.android.synthetic.main.fragment_log_input.*
import kotlinx.android.synthetic.main.fragment_log_input.view.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.*

@RuntimePermissions()
class LogInputFragment : Fragment() {

    companion object {
        const val TAG = "LogInputFragment"
        const val DATE_SELECT_TAG = "date_select"

        fun newInstance(): LogInputFragment {
            val f = LogInputFragment()
            return f
        }
    }

    private val today = Calendar.getInstance().clearTime()
    lateinit var viewModel: LogItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val contentView = inflater.inflate(R.layout.fragment_log_input, container, false)

        contentView.radio_group.check(R.id.radio_normal)

        contentView.text_date.text = today.getDateStringYMD()

        contentView.button_resist.setOnClickListener {
            validation()?.let {
                val fgm = fragmentManager ?: return@setOnClickListener
                ErrorDialog.Builder().message(it).create().show(fgm, null)
                return@setOnClickListener
            }

            val dateText = text_date.text.toString()
            val stepCount = edit_count.text.toString().toInt()
            val level = levelFromRadioId(radio_group.checkedRadioButtonId)
            val weather = weatherFromSpinner(spinner_weather.selectedItemPosition)
            val stepCountLog = StepCountLog(dateText, stepCount, level, weather)

            viewModel.changeLog(stepCountLog)
        }

        // 日付を選ぶボタンで日付選択ダイアログを表示
        contentView.button_date.setOnClickListener {
            val fgm = fragmentManager ?: return@setOnClickListener // nullチェック
            DateSelectDialogFragment().show(fgm, DATE_SELECT_TAG)
        }

        // カメラボタン
        contentView.button_camera.setOnClickListener {
            onCameraButtonWithPermissionCheck()
        }

        return contentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(LogItemViewModel::class.java)

        // 日付の選択を監視
        viewModel.selectDate.observe(this, Observer {
            text_date.text = it.getDateStringYMD()
        })
        // OCR結果文字列の確定を監視
        viewModel.ocrResultText.observe(this, Observer {
            showProgress(false)

            OcrResultDialogFragment().show(fragmentManager!!, null)
        })
        // OCR画像の確定を監視
        viewModel.ocrBitmapSource.observe(this, Observer {
            showProgress(true)
//            onCameraButtonMLKit(it)
            onCameraButtonTessTwo(it)
        })
        // OCR結果文字列をInt型に変換した結果を監視
        viewModel.ocrResultStepCount.observe(this, Observer {
            edit_count.setText(it?.toString())
        })
    }

    private fun showProgress(flag: Boolean) {
        layout_ocr_progress?.visibility = if (flag) View.VISIBLE else View.INVISIBLE
    }

    private fun levelFromRadioId(checkedRadioButtonId: Int): LEVEL {
        return when (checkedRadioButtonId) {
            R.id.radio_good -> LEVEL.GOOD
            R.id.radio_bad -> LEVEL.BAD
            else -> LEVEL.NORMAL
        }
    }

    private fun weatherFromSpinner(selectedItemPosition: Int): WEATHER {
        return WEATHER.values()[selectedItemPosition]
    }

    private fun validation(): Int? {
        val selectDate = viewModel.selectDate.value?.clearTime()
        return logInputValidation(today, selectDate, edit_count.text.toString())
    }

    @NeedsPermission(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE)
    internal fun onCameraButton() {
        // 取り敢えずストレージから固定ファイルを読込んでリストアップして表示
        val fgm = fragmentManager
        fgm?.let {
            OcrSelectSourceDialogFragment().show(fragmentManager!!, null)
        }
    }

    private fun onCameraButtonMLKit(bitmap: Bitmap) {

        val image = FirebaseVisionImage.fromBitmap(bitmap)
//        val detector = FirebaseVision.getInstance()
//            .onDeviceTextRecognizer

//        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        val detector = FirebaseVision.getInstance().cloudTextRecognizer

        val result = detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                // Task completed successfully
                val string = processTextBlock(firebaseVisionText)

                viewModel.ocrResult(string)
            }
            .addOnFailureListener {
                // Task failed with an exception
                // ...
                Toast.makeText(context, "OCR出来ない！", Toast.LENGTH_LONG).show()
            }
    }

    private fun onCameraButtonTessTwo(bitmap: Bitmap) {
        Handler().post {
            val baseApi = TessBaseAPI()
            // initで言語データを読み込む
            baseApi.init(context?.filesDir?.absolutePath, "eng")
            // ギャラリーから読み込んだ画像をFile or Bitmap or byte[] or Pix形式に変換して渡してあげる
            baseApi.setImage(bitmap)
            // これだけで読み取ったテキストを取得できる
            val recognizedText = baseApi.utF8Text
            viewModel.ocrResult(recognizedText)
            baseApi.end()
        }
    }

    private fun processTextBlock(result: FirebaseVisionText): String {
        val resultText = result.text
        for (block in result.textBlocks) {
            val blockText = block.text
            Log.d("MYOCR", "blockText=$blockText")
            val blockConfidence = block.confidence
            val blockLanguages = block.recognizedLanguages
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            for (line in block.lines) {
                val lineText = line.text
                Log.d("MYOCR", "lineText=$lineText")
                return lineText
//                val lineConfidence = line.confidence
//                val lineLanguages = line.recognizedLanguages
//                val lineCornerPoints = line.cornerPoints
//                val lineFrame = line.boundingBox
//                for (element in line.elements) {
//                    val elementText = element.text
//                    Log.d("MYOCR", "elementText=$elementText")
//                    val elementConfidence = element.confidence
//                    val elementLanguages = element.recognizedLanguages
//                    val elementCornerPoints = element.cornerPoints
//                    val elementFrame = element.boundingBox
//                }
            }
        }
        return resultText
    }
}

fun logInputValidation(
    today: Calendar, selectDate: Calendar?,
    stepCountText: String?
): Int? {
    if (today.before(selectDate)) {
        // 今日より未来はNG
        return R.string.error_validation_future_date
    }
    // ステップ数が1文字以上入力されていること
    if (stepCountText.isNullOrEmpty()) {
        return R.string.error_validation_empty_count
    }
    return null
}
