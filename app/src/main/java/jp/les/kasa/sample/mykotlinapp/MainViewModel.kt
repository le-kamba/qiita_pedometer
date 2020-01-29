package jp.les.kasa.sample.mykotlinapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import jp.les.kasa.sample.mykotlinapp.data.LogRepository
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * MainViewModel
 * @date 2019/06/06
 **/
class MainViewModel(
    app: Application,
    val repository: LogRepository
) : AndroidViewModel(app) {

    // 全データリスト
    val stepCountList: LiveData<List<StepCountLog>> = repository.allLogs

    fun addStepCount(stepLog: StepCountLog) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(stepLog)
    }

    fun deleteStepCount(stepLog: StepCountLog) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(stepLog)
    }
}
