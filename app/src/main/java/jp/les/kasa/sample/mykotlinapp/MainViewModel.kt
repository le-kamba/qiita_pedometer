package jp.les.kasa.sample.mykotlinapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import jp.les.kasa.sample.mykotlinapp.data.LogRepository
import jp.les.kasa.sample.mykotlinapp.data.LogRoomDatabase
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * MainViewModel
 * @date 2019/06/06
 **/
class MainViewModel(app: Application) : AndroidViewModel(app) {

    // データ操作用のリポジトリクラス
    val repository: LogRepository
    // 全データリスト
    val stepCountList: LiveData<List<StepCountLog>>

    // coroutine用
    private var parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)

    init {
        val logDao = LogRoomDatabase.getDatabase(app).logDao()
        repository = LogRepository(logDao)
        stepCountList = repository.allLogs
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    fun addStepCount(stepLog: StepCountLog) = scope.launch(Dispatchers.IO) {
        repository.insert(stepLog)
    }

    fun deleteStepCount(stepLog: StepCountLog) = scope.launch(Dispatchers.IO) {
        repository.delete(stepLog)
    }
}
