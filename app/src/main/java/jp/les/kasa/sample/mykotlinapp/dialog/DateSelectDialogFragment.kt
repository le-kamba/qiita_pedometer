package jp.les.kasa.sample.mykotlinapp.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.CalendarView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemViewModel
import java.util.*

/**
 * 日付選択ダイアログ
 * @date 2019/06/20
 **/
class DateSelectDialogFragment : DialogFragment() {

    // CalendarViewで選択している日付の保存
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val selectDate = Calendar.getInstance()!!

    // CalendarView
    lateinit var calendarView: CalendarView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewModel = ViewModelProviders.of(activity!!).get(LogItemViewModel::class.java)

        // AlertDialogで作成する
        val builder = AlertDialog.Builder(requireContext())

        // CalendarViewのインスタンス生成
        calendarView = CalendarView(requireContext())
        // 初期値(今日)をセット
        calendarView.date = selectDate.timeInMillis

        // 選択している日付が変わったときのイベントリスナー
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectDate.set(year, month, dayOfMonth)
        }

        // AlertDialogのセットアップ
        builder.setView(calendarView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                // ポジティブボタンでVieModelに最後に選択した日付をセット
                viewModel.dateSelected(selectDate)
            }
        return builder.create()
    }
}