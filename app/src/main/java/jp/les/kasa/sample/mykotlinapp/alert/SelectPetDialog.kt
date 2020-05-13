package jp.les.kasa.sample.mykotlinapp.alert

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import jp.les.kasa.sample.mykotlinapp.utils.AnalyticsUtil
import org.koin.android.ext.android.inject

class SelectPetDialog : DialogFragment(), DialogInterface.OnClickListener {

    interface SelectPetEventListener {
        /**
         * ダイアログのコールバック<br>
         * @param hasDog : true or false
         */
        fun onSelected(hasDog: Boolean)
    }

    private val analyticsUtil: AnalyticsUtil by inject()

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
        activity?.let { analyticsUtil.sendScreenName(it, SCREEN_NAME) }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        // 例外の原因を追いやすくするためどちらを選んだかユーザー操作をCrashlyticsに記録する
        FirebaseCrashlytics.getInstance().log("select_pet_dog = $which")
        try {
            val listener = activity as SelectPetEventListener
            listener.onSelected(which == DialogInterface.BUTTON_POSITIVE)
        } catch (e: ClassCastException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e(
                "SelectPetDialog",
                "Activity should implement ConfirmEventListener!!"
            )
        }
    }

}