package jp.les.kasa.sample.mykotlinapp.activity.logitem


import android.Manifest.permission
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
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
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.alert.ErrorDialog
import jp.les.kasa.sample.mykotlinapp.clearTime
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
import jp.les.kasa.sample.mykotlinapp.getDateStringYMD
import kotlinx.android.synthetic.main.fragment_log_input.*
import kotlinx.android.synthetic.main.fragment_log_input.view.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
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
        // ストレージから固定ファイルを読込
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//        val file = File(dir, "Camera/IMG_20190622_165554.jpg")
        val file = File(dir, "Camera/IMG_20190622_180008.jpg")
//        val file = File(dir, "Camera/IMG_20190622_174454.jpg")
//        val file = File(dir, "Camera/IMG_20190622_174413.jpg")
        Log.d("MYOCR", "file=${file.absoluteFile}")
        if (!file.exists()) return

        val bitmap = BitmapFactory.decodeFile(file.absolutePath)

        val image = FirebaseVisionImage.fromBitmap(bitmap)
//        val detector = FirebaseVision.getInstance()
//            .onDeviceTextRecognizer

        val detector = FirebaseVision.getInstance().cloudTextRecognizer

        val result = detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                // Task completed successfully
                processTextBlock(firebaseVisionText)
            }
            .addOnFailureListener {
                // Task failed with an exception
                // ...
                Toast.makeText(context, "OCR出来ない！", Toast.LENGTH_SHORT).show()
            }
    }

    private fun processTextBlock(result: FirebaseVisionText) {
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
                val lineConfidence = line.confidence
                val lineLanguages = line.recognizedLanguages
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                for (element in line.elements) {
                    val elementText = element.text
                    Log.d("MYOCR", "elementText=$elementText")
                    val elementConfidence = element.confidence
                    val elementLanguages = element.recognizedLanguages
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                }
            }
        }
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
