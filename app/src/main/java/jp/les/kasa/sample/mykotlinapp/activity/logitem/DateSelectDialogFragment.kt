package jp.les.kasa.sample.mykotlinapp.activity.logitem

import android.app.Dialog
import android.os.Bundle
import android.widget.CalendarView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import java.util.*

/**
 * 日付選択ダイアログ
 * @date 2019/06/20
 **/
class DateSelectDialogFragment : DialogFragment() {

    private val selectDate = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewModel = ViewModelProviders.of(activity!!).get(LogItemViewModel::class.java)

        val builder = AlertDialog.Builder(requireContext())

        val calendarView = CalendarView(requireContext())
        calendarView.date = selectDate.timeInMillis

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectDate.set(year, month, dayOfMonth)
        }

        builder.setView(calendarView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.dateSelected(selectDate)
            }
        return builder.create()
    }
}