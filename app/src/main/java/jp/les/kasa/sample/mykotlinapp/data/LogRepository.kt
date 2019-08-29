package jp.les.kasa.sample.mykotlinapp.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

/**
 * ログデータリポジトリ
 * @date 2019-08-29
 **/
class LogRepository(private val logDao: LogDao) {

    val allLogs: LiveData<List<StepCountLog>> = logDao.getAllLogs()

    @WorkerThread
    suspend fun insert(stepCountLog: StepCountLog) {
        logDao.insert(stepCountLog)
    }

    @WorkerThread
    suspend fun update(stepCountLog: StepCountLog) {
        logDao.update(stepCountLog)
    }

    @WorkerThread
    suspend fun delete(stepCountLog: StepCountLog) {
        logDao.delete(stepCountLog)
    }

    @WorkerThread
    suspend fun deleteAll() {
        logDao.deleteAll()
    }

}