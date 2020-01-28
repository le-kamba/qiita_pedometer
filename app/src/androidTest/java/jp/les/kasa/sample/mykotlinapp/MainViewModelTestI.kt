package jp.les.kasa.sample.mykotlinapp

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
import jp.les.kasa.sample.mykotlinapp.espresso.TestObserver
import jp.les.kasa.sample.mykotlinapp.espresso.observeForTesting
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest

@RunWith(AndroidJUnit4::class)
class MainViewModelTestI : AutoCloseKoinTest() {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        val appContext = ApplicationProvider.getApplicationContext<Application>()

        viewModel = MainViewModel(appContext)
    }

    @Test
    fun init() {
        assertThat(viewModel.repository)
            .isNotNull()
        assertThat(viewModel.stepCountList)
            .isNotNull()
        viewModel.stepCountList.observeForTesting {
            assertThat(viewModel.stepCountList.value)
                .isEmpty()
        }
    }

    @Test
    fun addStepCount() {
        val listObserver = TestObserver<List<StepCountLog>>(2)
        viewModel.stepCountList.observeForever(listObserver)

        runBlocking {
            viewModel.addStepCount(StepCountLog("2019/06/21", 123))
            viewModel.addStepCount(StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
        }
        listObserver.await()

        assertThat(viewModel.stepCountList.value)
            .isNotEmpty()

        val list = viewModel.stepCountList.value as List<StepCountLog>
        assertThat(list.size).isEqualTo(2)
        assertThat(list[0]).isEqualToComparingFieldByField(StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
        assertThat(list[1]).isEqualToComparingFieldByField(StepCountLog("2019/06/21", 123))

        viewModel.stepCountList.removeObserver(listObserver)
    }

    @Test
    fun deleteStepCount() {
        val listObserver = TestObserver<List<StepCountLog>>(3)
        viewModel.stepCountList.observeForever(listObserver)

        runBlocking {
            viewModel.addStepCount(StepCountLog("2019/06/21", 123))
            viewModel.addStepCount(StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
            Thread.sleep(500)
            viewModel.deleteStepCount(StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
        }
        listObserver.await()

        assertThat(viewModel.stepCountList.value)
            .isNotEmpty()

        val list = viewModel.stepCountList.value as List<StepCountLog>
        assertThat(list.size).isEqualTo(1)
        assertThat(list[0]).isEqualToComparingFieldByField(StepCountLog("2019/06/21", 123))

        viewModel.stepCountList.removeObserver(listObserver)
    }
}
