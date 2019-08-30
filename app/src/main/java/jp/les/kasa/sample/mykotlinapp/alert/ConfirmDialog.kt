package jp.les.kasa.sample.mykotlinapp.alert

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import jp.les.kasa.sample.mykotlinapp.R

/**
 * 確認メッセージを表示するダイアログ<br>
 *     [YES/NO]ボタンを表示します
 * 2019/08/30
 **/
class ConfirmDialog : DialogFragment(), DialogInterface.OnClickListener {

    interface ConfirmEventListener {
        /**
         * 確認ダイアログのコールバック<br>
         * @param which : AlertDialogの押されたボタン(POSITIVE or NEGATIVE)
         * @param bundle : data()でセットしたBundleデータ
         * @param requestCode : targetFragmentと併せて指定したrequestCode
         */
        fun onConfirmResult(which: Int, bundle: Bundle?, requestCode: Int)
    }

    class Builder() {
        private var message: String? = null
        private var messageResId: Int = 0
        private var target: Fragment? = null
        private var requestCode: Int = 0
        private var data: Bundle? = null

        fun message(message: String): Builder {
            this.message = message
            return this
        }

        fun message(resId: Int): Builder {
            this.messageResId = resId
            return this
        }

        fun target(fragment: Fragment): Builder {
            this.target = fragment
            return this
        }

        /**
         * only for targetFragment
         */
        fun requestCode(requestCode: Int): Builder {
            this.requestCode = requestCode
            return this
        }

        fun data(bundle: Bundle): Builder {
            this.data = bundle
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
                if (data != null) {
                    putBundle(KEY_DATA, data)
                }
            }
            if (target != null) {
                d.setTargetFragment(target, requestCode)
            }
            return d
        }
    }

    companion object {
        const val KEY_MESSAGE = "message"
        const val KEY_RESOURCE_ID = "res_id"
        const val KEY_DATA = "data"
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
            .setTitle(R.string.confirm)
            .setIcon(android.R.drawable.ic_dialog_info)
            .setNegativeButton(android.R.string.no, this)
            .setPositiveButton(android.R.string.yes, this)
        return builder.create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        val data = arguments!!.getBundle(KEY_DATA)
        if (targetFragment is ConfirmEventListener) {
            val listener = targetFragment as ConfirmEventListener
            listener.onConfirmResult(which, data, targetRequestCode)
            return
        } else if (activity is ConfirmEventListener) {
            val listener = activity as ConfirmEventListener
            listener.onConfirmResult(which, data, targetRequestCode)
            return
        }
        Log.e("ConfirmDialog", "Target Fragment or Activity should implement ConfirmEventListener!!")
    }

}
