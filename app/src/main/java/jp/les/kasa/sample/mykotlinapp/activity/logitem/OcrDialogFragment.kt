package jp.les.kasa.sample.mykotlinapp.activity.logitem

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.getSrcBitmaps
import kotlinx.android.synthetic.main.dialog_ocr_result.view.*
import kotlinx.android.synthetic.main.dialog_select_src.view.*

/**
 * OCR結果ダイアログ
 * @date 2019/06/20
 **/
class OcrResultDialogFragment : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewModel = ViewModelProviders.of(activity!!).get(LogItemViewModel::class.java)

        // AlertDialogで作成する
        val builder = AlertDialog.Builder(requireContext())

        val view = View.inflate(context, R.layout.dialog_ocr_result, null)

        view.text_ocr_result.setText(viewModel.ocrResultText.value)

        // AlertDialogのセットアップ
        builder.setView(view)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                // ポジティブボタンでVieModelに数字をセット
                try {
                    viewModel.ocrResultTextToEdit()
                } catch (e: NumberFormatException) {
                    Toast.makeText(activity, "数値以外の文字があります", Toast.LENGTH_LONG).show()
                }
            }
        return builder.create()
    }
}

class OcrSelectSourceDialogFrament : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewModel = ViewModelProviders.of(activity!!).get(LogItemViewModel::class.java)
        val bitmapList = getSrcBitmaps()

        // AlertDialogで作成する
        val builder = AlertDialog.Builder(requireContext())

        val view = View.inflate(context, R.layout.dialog_select_src, null)

        view.image_src_1.setImageBitmap(bitmapList[0])
        view.image_src_2.setImageBitmap(bitmapList[1])
        view.image_src_3.setImageBitmap(bitmapList[2])
        view.image_src_4.setImageBitmap(bitmapList[3])
        view.image_src_5.setImageBitmap(bitmapList[4])
        view.image_src_6.setImageBitmap(bitmapList[5])

        view.image_src_1.setOnClickListener {
            viewModel.ocrSource(bitmapList[0])
            dismiss()
        }
        view.image_src_2.setOnClickListener {
            viewModel.ocrSource(bitmapList[1])
            dismiss()
        }
        view.image_src_3.setOnClickListener {
            viewModel.ocrSource(bitmapList[2])
            dismiss()
        }
        view.image_src_4.setOnClickListener {
            viewModel.ocrSource(bitmapList[3])
            dismiss()
        }
        view.image_src_5.setOnClickListener {
            viewModel.ocrSource(bitmapList[4])
            dismiss()
        }
        view.image_src_6.setOnClickListener {
            viewModel.ocrSource(bitmapList[5])
            dismiss()
        }
        // AlertDialogのセットアップ
        builder.setView(view)
        return builder.create()
    }
}