package jp.les.kasa.sample.mykotlinapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.les.kasa.sample.mykotlinapp.di.mockModule
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class LogRepositoryTest : AutoCloseKoinTest() {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val database: LogRoomDatabase by inject()
    private val logDao: LogDao by inject()
    private val repository: LogRepository by inject()

    @Before
    fun setUp() {
        loadKoinModules(mockModule)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getAllLogs() {
        runBlocking {
            repository.insert(StepCountLog("2019/08/30", 12345))
            repository.insert(StepCountLog("2019/08/31", 12345, LEVEL.GOOD, WEATHER.CLOUD))
        }

        val items = repository.allLogs()
        assertThat(items).isNotEmpty()
        assertThat(items.size).isEqualTo(2)
        assertThat(items[1]).isEqualToComparingFieldByField(
            StepCountLog("2019/08/30", 12345)
        )
        assertThat(items[0]).isEqualToComparingFieldByField(
            StepCountLog("2019/08/31", 12345, LEVEL.GOOD, WEATHER.CLOUD)
        )
    }

    @Test
    fun insert() {
        runBlocking {
            repository.insert(StepCountLog("2019/08/30", 12345, LEVEL.GOOD, WEATHER.CLOUD))
        }

        val item = logDao.getLog("2019/08/30")
        assertThat(item).isEqualToComparingFieldByField(
            StepCountLog("2019/08/30", 12345, LEVEL.GOOD, WEATHER.CLOUD)
        )
    }

    @Test
    fun update() {
        runBlocking {
            repository.insert(StepCountLog("2019/08/30", 12345, LEVEL.GOOD, WEATHER.CLOUD))
            repository.update(StepCountLog("2019/08/30", 12344))
        }

        val item = logDao.getLog("2019/08/30")
        assertThat(item).isEqualToComparingFieldByField(
            StepCountLog("2019/08/30", 12344, LEVEL.NORMAL, WEATHER.FINE)
        )
    }

    @Test
    fun delete() {
        runBlocking {
            repository.insert(StepCountLog("2019/08/30", 12345))
            repository.delete(StepCountLog("2019/08/30", 12345))
        }

        val item = logDao.getLog("2019/08/30")
        assertThat(item).isNull()
    }

    @Test
    fun deleteAll() {
        runBlocking {
            repository.insert(StepCountLog("2019/08/30", 12345))
            repository.insert(StepCountLog("2019/08/31", 12345, LEVEL.GOOD, WEATHER.CLOUD))
            repository.deleteAll()
        }

        val items = repository.allLogs()
        assertThat(items).isEmpty()
    }

    @Test
    fun searchRange() {
        runBlocking {
            repository.insert(StepCountLog("2019/07/31", 12345))
            repository.insert(StepCountLog("2019/08/01", 12345))
            repository.insert(StepCountLog("2019/08/30", 12345))
            repository.insert(StepCountLog("2019/08/31", 12345, LEVEL.GOOD, WEATHER.CLOUD))
            repository.insert(StepCountLog("2019/09/01", 123, LEVEL.BAD, WEATHER.RAIN))
            repository.insert(StepCountLog("2019/12/31", 1111, LEVEL.BAD, WEATHER.RAIN))
            repository.insert(StepCountLog("2019/01/01", 1111)) // 古いデータ
            repository.insert(StepCountLog("2020/01/01", 11115))
            repository.insert(StepCountLog("2020/02/29", 29))
            repository.insert(StepCountLog("2020/02/28", 28))
            repository.insert(StepCountLog("2020/03/01", 31))
        }

        val data6 = repository.searchRange("2019/06/01", "2019/07/01")
        data6.observeForever {
            assertThat(it).isEmpty()
        }

        val data8 = repository.searchRange("2019/08/01", "2019/09/01")
        data8.observeForever {
            assertThat(it).isNotEmpty()
            assertThat(it!!.size).isEqualTo(3)
            assertThat(it[2]).isEqualToComparingFieldByField(
                StepCountLog("2019/08/01", 12345)
            )
            assertThat(it[1]).isEqualToComparingFieldByField(
                StepCountLog("2019/08/30", 12345)
            )
            assertThat(it[0]).isEqualToComparingFieldByField(
                StepCountLog("2019/08/31", 12345, LEVEL.GOOD, WEATHER.CLOUD)
            )
        }

        // 月またぎ、年またぎ
        val data12 = repository.searchRange("2019/12/01", "2020/02/01")
        data12.observeForever {
            assertThat(it).isNotEmpty()
            assertThat(it!!.size).isEqualTo(2)
            assertThat(it[1]).isEqualToComparingFieldByField(
                StepCountLog("2019/12/31", 1111, LEVEL.BAD, WEATHER.RAIN)
            )
            assertThat(it[0]).isEqualToComparingFieldByField(
                StepCountLog("2020/01/01", 11115)
            )
        }

        // 閏月
        val data2 = repository.searchRange("2020/02/01", "2020/03/01")
        data2.observeForever {
            assertThat(it).isNotEmpty()
            assertThat(it!!.size).isEqualTo(2)
            assertThat(it[1]).isEqualToComparingFieldByField(
                StepCountLog("2020/02/28", 28)
            )
            assertThat(it[0]).isEqualToComparingFieldByField(
                StepCountLog("2020/02/29", 29)
            )
        }
    }

    @Test
    fun getOldestDate() {
        runBlocking {
            repository.insert(StepCountLog("2019/08/30", 12345))
            repository.insert(StepCountLog("2019/09/01", 12345))
            repository.insert(StepCountLog("2019/09/22", 12345))
            repository.insert(StepCountLog("2019/10/10", 12345))
            repository.insert(StepCountLog("2019/10/13", 12345))
            repository.insert(StepCountLog("2019/01/13", 12345))
            repository.insert(StepCountLog("2020/02/03", 12345))
            repository.insert(StepCountLog("2019/02/03", 12345))
            repository.insert(StepCountLog("2020/02/04", 12345))
        }

        val date = repository.getOldestDate()
        date.observeForever {
            assertThat(it).isNotEmpty()
            assertThat(it).isEqualTo("2019/01/13")
        }
    }
}