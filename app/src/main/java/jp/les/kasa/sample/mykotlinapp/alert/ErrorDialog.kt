package jp.les.kasa.sample.mykotlinapp.alert

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import jp.les.kasa.sample.mykotlinapp.R

/**
 * エラーメッセージを表示するダイアログ
 * @date 2019/06/20
 **/
class ErrorDialog : DialogFragment() {

    class Builder() {
        private var message: String? = null
        private var messageResId: Int = R.string.error

        fun message(message: String): Builder {
            this.message = message
            return this
        }

        fun message(resId: Int): Builder {
            this.messageResId = resId
            return this
        }

        fun create(): ErrorDialog {
            val d = ErrorDialog()
            d.arguments = Bundle().apply {
                if (message != null) {
                    putString(KEY_MESSAGE, message)
                } else {
                    putInt(KEY_RESOURCE_ID, messageResId)
                }
            }
            return d
        }
    }

    companion object {
        const val KEY_MESSAGE = "message"
        const val KEY_RESOURCE_ID = "res_id"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // AlertDialogで作成する
        val builder = AlertDialog.Builder(requireContext())

        // メッセージの決定
        val message =
            when {
                arguments!!.containsKey(KEY_MESSAGE) -> arguments!!.getString(KEY_MESSAGE)
                else -> requireContext().getString(
                    arguments!!.getInt(KEY_RESOURCE_ID)
                )
            }
        // AlertDialogのセットアップ
        builder.setMessage(message)
            .setTitle(R.string.error)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setNeutralButton(R.string.close, null)
        return builder.create()

    }
}