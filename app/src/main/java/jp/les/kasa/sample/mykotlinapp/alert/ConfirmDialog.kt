package jp.les.kasa.sample.mykotlinapp.alert

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.utils.AnalyticsUtilI
import org.koin.android.ext.android.inject

/**
 * 確認メッセージを表示するダイアログ<br>
 *     [YES/NO]ボタンを表示します
 * 2019/08/30
 **/
class ConfirmDialog : DialogFragment(), DialogInterface.OnClickListener {

    private val analyticsUtil: AnalyticsUtilI by inject()

    class Builder() {
        private var message: String? = null
        private var messageResId: Int = 0

        fun message(message: String): Builder {
            this.message = message
            return this
        }

        fun message(resId: Int): Builder {
            this.messageResId = resId
            return this
        }

        fun create(): ConfirmDialog {
            val d = ConfirmDialog()
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
        const val SCREEN_NAME = "確認ダイアログ"

        const val TAG = "ConfirmDialog"
        const val REQUEST_KEY = "confirmDialog"
        const val RESULT_KEY_NEGATIVE = "confirmDialogNegative"
        const val RESULT_KEY_POSITIVE = "confirmDialogPositive"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // AlertDialogで作成する
        val builder = AlertDialog.Builder(requireContext())

        // メッセージの決定
        val message =
            when {
                requireArguments().containsKey(KEY_MESSAGE) -> requireArguments().getString(
                    KEY_MESSAGE
                )
                else -> requireContext().getString(
                    requireArguments().getInt(KEY_RESOURCE_ID)
                )
            }
        // AlertDialogのセットアップ
        builder.setMessage(message)
            .setTitle(R.string.confirm)
            .setIcon(android.R.drawable.ic_dialog_info)
            .setNegativeButton(R.string.label_no, this)
            .setPositiveButton(R.string.label_yes, this)
        return builder.create()
    }

    override fun onResume() {
        super.onResume()
        analyticsUtil.sendScreenName(SCREEN_NAME)
    }

    fun show(
        activity: AppCompatActivity,
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null
    ) {
        activity.supportFragmentManager.setFragmentResultListener(
            REQUEST_KEY,
            activity
        ) { requestKey, bundle ->
            if (requestKey != REQUEST_KEY) return@setFragmentResultListener

            when {
                bundle.containsKey(RESULT_KEY_NEGATIVE) -> onNegative?.invoke()
                bundle.containsKey(RESULT_KEY_POSITIVE) -> onPositive?.invoke()
            }
        }
        show(activity.supportFragmentManager, TAG)
    }

    fun show(
        target: Fragment,
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null
    ) {
        target.childFragmentManager.setFragmentResultListener(
            REQUEST_KEY,
            target.viewLifecycleOwner
        ) { requestKey, bundle ->
            if (requestKey != REQUEST_KEY) return@setFragmentResultListener
            when {
                bundle.containsKey(RESULT_KEY_NEGATIVE) -> onNegative?.invoke()
                bundle.containsKey(RESULT_KEY_POSITIVE) -> onPositive?.invoke()
            }
        }
        show(target.childFragmentManager, TAG)
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        FirebaseCrashlytics.getInstance().log("ConfirmDialog selected:$which")
        when (which) {
            DialogInterface.BUTTON_POSITIVE ->
                setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY_POSITIVE to true))
            else ->
                setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY_NEGATIVE to true))
        }
    }

}
