package jp.les.kasa.sample.mykotlinapp.activity.logitem

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog

/**
 * ログアイテム表示画面用のViewModel
 * @date 2019/06/19
 **/
class LogItemViewModel : ViewModel() {

    private val _stepCountLog = MutableLiveData<StepCountLog>()

    val stepCountLog = _stepCountLog as LiveData<StepCountLog>

    @UiThread
    fun changeLog(data: StepCountLog) {
        _stepCountLog.value = data
    }
}