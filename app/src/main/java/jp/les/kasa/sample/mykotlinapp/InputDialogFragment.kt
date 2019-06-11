package jp.les.kasa.sample.mykotlinapp

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import kotlinx.android.synthetic.main.dialog_input.view.*
import java.text.SimpleDateFormat
import java.util.*

class InputDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        val view = View.inflate(context, R.layout.dialog_input, null)
        val builder = AlertDialog.Builder(requireContext())

        builder.setView(view)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.resist) { _, _ ->
                val step = view.editStep.text.toString()
                val date = getDateString(Calendar.getInstance().time)
                viewModel.addStepCount(StepCountLog(date, step.toInt()))
            }
        return builder.create()
    }

    private fun getDateString(time: Date): String {
        val fmt = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
        return fmt.format(time)
    }
}
