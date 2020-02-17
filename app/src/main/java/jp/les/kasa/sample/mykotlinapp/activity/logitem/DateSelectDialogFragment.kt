package jp.les.kasa.sample.mykotlinapp.activity.logitem

import android.app.Dialog
import android.os.Bundle
import android.widget.CalendarView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import jp.les.kasa.sample.mykotlinapp.di.CalendarProviderI
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * 日付選択ダイアログ
 * @date 2019/06/20
 **/
class DateSelectDialogFragment : DialogFragment() {

    private val calendarProvider: CalendarProviderI by inject()

    // CalendarViewで選択している日付の保存
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val selectDate = calendarProvider.now

    // CalendarView
    lateinit var calendarView: CalendarView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewModel by sharedViewModel<LogItemViewModel>()

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