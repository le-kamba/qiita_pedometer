package jp.les.kasa.sample.mykotlinapp.activity.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.les.kasa.sample.mykotlinapp.TestObserver
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.LogRoomDatabase
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
import jp.les.kasa.sample.mykotlinapp.di.mockModule
import jp.les.kasa.sample.mykotlinapp.observeForTesting
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

@RunWith(AndroidJUnit4::class)
class MonthlyPageViewModelTest : AutoCloseKoinTest() {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    val viewModel: MonthlyPageViewModel by inject()

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
        assertThat(viewModel.stepCountList)
            .isNotNull()
        viewModel.stepCountList.observeForTesting {
            assertThat(viewModel.stepCountList.value)
                .isNull()
        }
    }

    @Test
    fun setYearMonth() {
        val dateObserver = TestObserver<String>()
        viewModel.dataYearMonth.observeForever(dateObserver)
        viewModel.setYearMonth("2019/06")

        dateObserver.await()

        assertThat(viewModel.dataYearMonth.value).isEqualTo("2019/06")
    }

    @Test
    fun deleteStepCount() {
        val listObserver = TestObserver<List<StepCountLog>>(3)
        viewModel.stepCountList.observeForever(listObserver)
        viewModel.setYearMonth("2019/06")

        runBlocking {
            viewModel.repository.insert(StepCountLog("2019/06/21", 123))
            viewModel.repository.insert(StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
            Thread.sleep(500)
            viewModel.deleteStepCount(StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
            Thread.sleep(500)
        }
        listObserver.await()

        assertThat(viewModel.stepCountList.value)
            .isNotEmpty()

        val list = viewModel.stepCountList.value as List<StepCountLog>
        assertThat(list.size).isEqualTo(1)
        assertThat(list[0]).isEqualToComparingFieldByField(StepCountLog("2019/06/21", 123))

        viewModel.stepCountList.removeObserver(listObserver)
    }

    @Test
    fun getFromToYMD() {
        val pair = viewModel.getFromToYMD("2020/01")
        assertThat(pair.first).isEqualTo("2020/01/01")
        assertThat(pair.second).isEqualTo("2020/02/01")

        val pair2 = viewModel.getFromToYMD("2020/12")
        assertThat(pair2.first).isEqualTo("2020/12/01")
        assertThat(pair2.second).isEqualTo("2021/01/01")
    }
}