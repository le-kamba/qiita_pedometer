package jp.les.kasa.sample.mykotlinapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 *
 * @date 2019-08-29
 **/
@Dao
interface LogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(log: StepCountLog)

    @Update
    fun update(log: StepCountLog)

    @Delete
    fun delete(log: StepCountLog)

    @Query("DELETE FROM log_table")
    fun deleteAll()

    @Query("SELECT * from log_table ORDER BY date DESC")
    fun getAllLogs(): List<StepCountLog>

    @Query("SELECT * from log_table WHERE date = :srcDate")
    fun getLog(srcDate: String): StepCountLog

    @Query("SELECT * from log_table WHERE date>= :from AND date < :to ORDER BY date")
    fun getRangeLog(from: String, to: String): Flow<List<StepCountLog>>

    @Query("SELECT date from log_table ORDER BY date limit 1")
    fun getOldestDate(): Flow<String>
}

const val DATABASE_NAME = "log_database"


@Database(entities = [StepCountLog::class], version = 1, exportSchema = false)
abstract class LogRoomDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
}
