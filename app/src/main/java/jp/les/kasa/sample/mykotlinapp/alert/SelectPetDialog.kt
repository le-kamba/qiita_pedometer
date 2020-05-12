package jp.les.kasa.sample.mykotlinapp.alert

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import jp.les.kasa.sample.mykotlinapp.utils.Analytics
import org.koin.android.ext.android.inject

class SelectPetDialog : DialogFragment(), DialogInterface.OnClickListener {

    interface SelectPetEventListener {
        /**
         * ダイアログのコールバック<br>
         * @param hasDog : true or false
         */
        fun onSelected(hasDog: Boolean)
    }

    private val analytics: Analytics by inject()

    companion object {
        const val SCREEN_NAME = "ペット飼育選択ダイアログ"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // AlertDialogで作成する
        val builder = AlertDialog.Builder(requireContext())

        // メッセージの決定
        val message = "犬を飼っていますか？"
        // AlertDialogのセットアップ
        builder.setMessage(message)
            .setTitle("アンケート")
            .setIcon(android.R.drawable.ic_dialog_info)
            .setNegativeButton("いいえ", this)
            .setPositiveButton("はい", this)
        return builder.create()
    }

    override fun onResume() {
        super.onResume()
        activity?.let { analytics.sendScreenName(it, SCREEN_NAME) }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {

        if (activity is SelectPetEventListener) {
            val listener = activity as SelectPetEventListener
            listener.onSelected(which == DialogInterface.BUTTON_POSITIVE)
            return
        }
        Log.e(
            "SelectPetDialog",
            "Activity should implement ConfirmEventListener!!"
        )
    }

}