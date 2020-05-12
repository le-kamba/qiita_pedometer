package jp.les.kasa.sample.mykotlinapp.activity.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import jp.les.kasa.sample.mykotlinapp.data.LogRepository
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.di.CalendarProviderI
import jp.les.kasa.sample.mykotlinapp.utils.clearTime
import jp.les.kasa.sample.mykotlinapp.utils.getDateStringYM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


/**
 * MainViewModel
 * @date 2019/06/06
 **/
class MainViewModel(
    app: Application,
    val repository: LogRepository,
    private val calendarProvider: CalendarProviderI
) : AndroidViewModel(app) {

    // 一番古いデータの年月
    private val oldestDate = repository.getOldestDate()

    // ページ
    val pages = Transformations.map(oldestDate) {
        makePageList(it, calendarProvider.now)
    }

    fun addStepCount(stepLog: StepCountLog) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(stepLog)
    }

    fun deleteStepCount(stepLog: StepCountLog) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(stepLog)
    }

    fun makePageList(from: String?, to: Calendar): List<String> {
        val formatter = SimpleDateFormat("yyy/MM/dd", Locale.JAPAN)

        to.set(Calendar.DATE, 1)
        to.clearTime()

        if (from == null) {
            return listOf(to.getDateStringYM())
        }

        val date = Calendar.getInstance()
        date.time = formatter.parse(from)
        date.clearTime()
        date.set(Calendar.DATE, 1)

        val list = mutableListOf<String>()

        // toの年月を超えるまで月を足し続ける
        while (!date.after(to)) {
            list.add(date.getDateStringYM())
            date.add(Calendar.MONTH, 1)
        }
        return list
    }
}
