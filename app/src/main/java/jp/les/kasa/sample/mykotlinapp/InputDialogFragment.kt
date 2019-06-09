package jp.les.kasa.sample.mykotlinapp

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.dialog_input.view.*

class InputDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        val view = View.inflate(context, R.layout.dialog_input, null)
        val builder = AlertDialog.Builder(requireContext())

        builder.setView(view)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.resist) { _, _ ->
                val step = view.editStep.text.toString()
                viewModel.addStepCount(step.toInt())
            }
        return builder.create()
    }

}
