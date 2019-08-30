package jp.les.kasa.sample.mykotlinapp.activity.logitem

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import java.util.*

/**
 * ログアイテム表示画面用のViewModel
 * @date 2019/06/19
 **/
class LogItemViewModel : ViewModel() {

    private val _stepCountLog = MutableLiveData<StepCountLog>()
    private val _selectDate = MutableLiveData<Calendar>()

    val stepCountLog = _stepCountLog as LiveData<StepCountLog>
    val selectDate = _selectDate as LiveData<Calendar>

    private val _deleteLog = MutableLiveData<StepCountLog>()
    val deleteLog = _deleteLog as LiveData<StepCountLog>

    @UiThread
    fun changeLog(data: StepCountLog) {
        _stepCountLog.value = data
    }

    @UiThread
    fun dateSelected(selectedDate: Calendar) {
        _selectDate.value = selectedDate
    }

    @UiThread
    fun deleteLog(data: StepCountLog) {
        _deleteLog.value = data
    }
}