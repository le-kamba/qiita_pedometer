package jp.les.kasa.sample.mykotlinapp.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

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
    fun getAllLogs(): LiveData<List<StepCountLog>>

    @Query("SELECT * from log_table WHERE date = :srcDate")
    fun getLog(srcDate: String): StepCountLog
}

const val DATABASE_NAME = "log_database"


@Database(entities = [StepCountLog::class], version = 1)
abstract class LogRoomDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao

    companion object {

        @Volatile
        private var INSTANCE: LogRoomDatabase? = null

        fun getDatabase(context: Context): LogRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                // Create database here
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LogRoomDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
