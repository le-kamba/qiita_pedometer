package jp.les.kasa.sample.mykotlinapp.activity.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.LogRoomDatabase
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
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
import org.koin.test.get
import org.koin.test.inject
import java.util.*

@RunWith(AndroidJUnit4::class)
class MainViewModelTest : AutoCloseKoinTest() {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    val viewModel: MainViewModel by inject()

    @Before
    fun setUp() {
        loadKoinModules(mockModule)
        get<LogRoomDatabase>().clearAllTables()
    }

    @After
    fun tearDown() {
        get<LogRoomDatabase>().clearAllTables()
    }

    @Test
    fun init() {
        assertThat(viewModel.repository)
            .isNotNull()
    }

    @Test
    fun addStepCount() = runBlocking<Unit> {

        viewModel.addStepCount(StepCountLog("2019/06/21", 123))
        viewModel.addStepCount(StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))

        val list = viewModel.repository.allLogs()
        assertThat(list.size).isEqualTo(2)
        assertThat(list[0]).isEqualToComparingFieldByField(
            StepCountLog(
                "2019/06/22",
                456,
                LEVEL.BAD,
                WEATHER.HOT
            )
        )
        assertThat(list[1]).isEqualToComparingFieldByField(StepCountLog("2019/06/21", 123))
    }

    @Test
    fun deleteStepCount() = runBlocking<Unit> {

        viewModel.addStepCount(StepCountLog("2019/06/21", 123))
        viewModel.addStepCount(StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
        Thread.sleep(500)
        viewModel.deleteStepCount(StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))

        val list = viewModel.repository.allLogs()
        assertThat(list.size).isEqualTo(1)
        assertThat(list[0]).isEqualToComparingFieldByField(StepCountLog("2019/06/21", 123))
    }

    @Test
    fun makePageList() {
        val calendar = Calendar.getInstance()
        calendar.set(2020, 2 - 1, 3)
        val list = viewModel.makePageList("2019/10/10", calendar)
        assertThat(list.size).isEqualTo(5)
        assertThat(list[0]).isEqualTo("2019/10")
        assertThat(list[1]).isEqualTo("2019/11")
        assertThat(list[2]).isEqualTo("2019/12")
        assertThat(list[3]).isEqualTo("2020/01")
        assertThat(list[4]).isEqualTo("2020/02")

        val cal2 = Calendar.getInstance()
        cal2.set(2020, 2 - 1, 22)
        val list2 = viewModel.makePageList("2019/09/10", cal2)
        assertThat(list2.size).isEqualTo(6)
        assertThat(list2[0]).isEqualTo("2019/09")
        assertThat(list2[1]).isEqualTo("2019/10")
        assertThat(list2[2]).isEqualTo("2019/11")
        assertThat(list2[3]).isEqualTo("2019/12")
        assertThat(list2[4]).isEqualTo("2020/01")
        assertThat(list2[5]).isEqualTo("2020/02")

        val list3 = viewModel.makePageList(null, cal2)
        assertThat(list3.size).isEqualTo(1)
        assertThat(list3[0]).isEqualTo("2020/02")
    }
}
