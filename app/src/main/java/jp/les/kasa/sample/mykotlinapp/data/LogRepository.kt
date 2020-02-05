package jp.les.kasa.sample.mykotlinapp.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

/**
 * ログデータリポジトリ
 * @date 2019-08-29
 **/
class LogRepository(private val logDao: LogDao) {

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

    @WorkerThread
    fun searchRange(from: String, to: String): LiveData<List<StepCountLog>> {
        return logDao.getRangeLog(from, to)
    }

    @WorkerThread
    fun allLogs(): List<StepCountLog> {
        return logDao.getAllLogs()
    }
}