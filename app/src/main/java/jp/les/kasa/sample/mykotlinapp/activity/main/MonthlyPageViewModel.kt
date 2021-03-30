package jp.les.kasa.sample.mykotlinapp.activity.main

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import jp.les.kasa.sample.mykotlinapp.data.CalendarCellData
import jp.les.kasa.sample.mykotlinapp.data.LogRepository
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.utils.addDay
import jp.les.kasa.sample.mykotlinapp.utils.clearTime
import jp.les.kasa.sample.mykotlinapp.utils.getDateStringYMD
import java.text.SimpleDateFormat
import java.util.*

/**
 * MonthlyPageViewModel
 * @date 2019/06/06
 **/
class MonthlyPageViewModel(
    app: Application,
    val repository: LogRepository
) : AndroidViewModel(app) {

    // 表示する年月
    private val _dataYearMonth = MutableLiveData<String>()
    val dataYearMonth: LiveData<String> = _dataYearMonth

    // データリスト
    val stepCountList: LiveData<List<StepCountLog>> =
        Transformations.switchMap(_dataYearMonth) {
            val ymd = getFromToYMD(it)
            firstDayInPage = ymd.first
            repository.searchRange(ymd.first.getDateStringYMD(), ymd.second.getDateStringYMD())
                .asLiveData()
        }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var firstDayInPage: Calendar

    // カレンダーのセルデータは、データリストが取れてからにする
    val cellData: LiveData<List<CalendarCellData>> = Transformations.map(stepCountList) {
        createCellData(firstDayInPage, it)
    }

    fun setYearMonth(yearMonth: String) {
        _dataYearMonth.postValue(yearMonth)
    }

    /**
     * クエリー用のfromとto日付を取得する
     * @param yyyyMM `yyyy/MM`の形の日付
     * @return <yyyy/MM/01, yyyy/(MM+1)/01>のPair
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getFromToYMD(yyyyMM: String): Pair<Calendar, Calendar> {
        val formatter = SimpleDateFormat("yyyy/MM", Locale.JAPAN)
        val from = Calendar.getInstance()
        from.time = formatter.parse(yyyyMM)!!
        from.set(Calendar.DATE, 1)
        from.clearTime()
        // 日曜日になるまで日付を遡る
        var dw = from.get(Calendar.DAY_OF_WEEK)
        while (dw != Calendar.SUNDAY) {
            from.add(Calendar.DATE, -1)
            dw = from.get(Calendar.DAY_OF_WEEK)
        }
        // 42日後にする
        val to = from.addDay(42)

        return Pair(from, to)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun createCellData(from: Calendar, logs: List<StepCountLog>): List<CalendarCellData> {

        val cal = from.clone() as Calendar
        val list = mutableListOf<CalendarCellData>()
        var index = 0
        for (i in 1..42) {
            val log =
                if (index < logs.size && logs[index].date == cal.getDateStringYMD()) {
                    logs[index++]
                } else {
                    null
                }
            list.add(CalendarCellData(cal.clone() as Calendar, log))
            cal.add(Calendar.DATE, 1)
        }

        return list
    }
}
