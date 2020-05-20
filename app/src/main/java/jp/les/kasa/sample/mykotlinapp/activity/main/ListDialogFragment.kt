package jp.les.kasa.sample.mykotlinapp.activity.main

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.utils.AnalyticsUtil
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import org.koin.android.ext.android.inject

@Parcelize
data class HasPet(val map: @RawValue Map<String, Any>) : Parcelable

class ListDialogFragment : DialogFragment() {
    private val analyticsUtil: AnalyticsUtil by inject()

    class Builder(val list: ArrayList<HasPet>) {
        fun create(): ListDialogFragment {
            val d = ListDialogFragment()
            d.arguments = Bundle().apply {
                putParcelableArrayList(KEY_LIST, list)
            }
            return d
        }
    }

    companion object {
        const val KEY_LIST = "list"
        const val SCREEN_NAME = "ユーザーペット情報リスト"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // AlertDialogで作成する
        val builder = AlertDialog.Builder(requireContext())

        // メッセージの決定
        val list = arguments!!.getParcelableArrayList<HasPet>(KEY_LIST)!!.map { t -> t.toString() }

        // AlertDialogのセットアップ
        builder.setItems(list.toTypedArray(), null)
            .setTitle(R.string.user_has_pet)
            .setIcon(android.R.drawable.ic_dialog_info)
        return builder.create()
    }

    override fun onResume() {
        super.onResume()
        activity?.let { analyticsUtil.sendScreenName(it, SCREEN_NAME) }
    }

}

