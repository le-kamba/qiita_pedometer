package jp.les.kasa.sample.mykotlinapp.activity.logitem.ocr

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemViewModel
import kotlinx.android.synthetic.main.dialog_ocr_result.view.*

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

        view.image_ocr_source.setImageBitmap(viewModel.ocrBitmapSource.value)
        view.text_ocr_result.setText(viewModel.ocrResultText.value)

        // AlertDialogのセットアップ
        builder.setView(view)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                // ポジティブボタンでVieModelに数字をセット
                try {
                    viewModel.ocrResultTextToEdit(view.text_ocr_result.text.toString())
                } catch (e: NumberFormatException) {
                    Toast.makeText(activity, "数値以外の文字があります", Toast.LENGTH_LONG).show()
                }
            }
        return builder.create()
    }
}
