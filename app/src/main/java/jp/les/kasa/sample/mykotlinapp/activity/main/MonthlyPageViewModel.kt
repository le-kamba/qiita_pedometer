package jp.les.kasa.sample.mykotlinapp.activity.main

import android.app.Application
import androidx.lifecycle.*
import jp.les.kasa.sample.mykotlinapp.clearTime
import jp.les.kasa.sample.mykotlinapp.data.LogRepository
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.di.CalendarProviderI
import jp.les.kasa.sample.mykotlinapp.getDateStringYMD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * MonthlyPageViewModel
 * @date 2019/06/06
 **/
class MonthlyPageViewModel(
    app: Application,
    val repository: LogRepository,
    val calendarProvider: CalendarProviderI
) : AndroidViewModel(app) {

    // 表示する年月
    private val _dataYearMonth = MutableLiveData<String>()
    val dataYearMonth: LiveData<String> = _dataYearMonth

    // データリスト
    val stepCountList: LiveData<List<StepCountLog>> =
        Transformations.switchMap(_dataYearMonth) {
            val ymd = getFromToYMD(it)
            repository.searchRange(ymd.first, ymd.second)
        }

    fun setYearMonth(yearMonth: String) {
        _dataYearMonth.postValue(yearMonth)
    }

    fun deleteStepCount(stepLog: StepCountLog) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(stepLog)
    }

    /**
     * クエリー用のfromとto日付を取得する
     * @param yyyyMM `yyyy/MM`の形の日付
     * @return <yyyy/MM/01, yyyy/(MM+1)/01>のPair
     */
    fun getFromToYMD(yyyyMM: String): Pair<String, String> {
        val formatter = SimpleDateFormat("yyyy/MM", Locale.JAPAN)
        val from = Calendar.getInstance()
        from.time = formatter.parse(yyyyMM)
        from.set(Calendar.DATE, 1)
        from.clearTime()
        val to = from.clone() as Calendar
        to.add(Calendar.MONTH, 1)

        return Pair(from.getDateStringYMD(), to.getDateStringYMD())
    }
}
