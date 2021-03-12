package jp.les.kasa.sample.mykotlinapp.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

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
    fun searchRange(from: String, to: String): Flow<List<StepCountLog>> {
        return logDao.getRangeLog(from, to)
    }

    @WorkerThread
    fun allLogs(): List<StepCountLog> {
        return logDao.getAllLogs()
    }

    @WorkerThread
    fun getOldestDate(): Flow<String> {
        return logDao.getOldestDate()
    }
}