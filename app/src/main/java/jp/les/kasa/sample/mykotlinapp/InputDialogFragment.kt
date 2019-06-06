package jp.les.kasa.sample.mykotlinapp

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_input.*

class InputDialogFragment : DialogFragment() {

    interface OnClickListerer {
        fun onResist(step: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.dialog_input)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.resist) { _, _ ->
                val step = editStep.text.toString()
            }
        return builder.create()
    }


}
