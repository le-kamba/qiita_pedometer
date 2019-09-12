package jp.les.kasa.sample.mykotlinapp

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.les.kasa.sample.mykotlinapp.data.DATABASE_NAME
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
import jp.les.kasa.sample.mykotlinapp.espresso.TestObserver
import jp.les.kasa.sample.mykotlinapp.espresso.observeForTesting
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainViewModelTestI {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        val appContext = ApplicationProvider.getApplicationContext<Context>() as Application

        // 最初にデータを削除する
        appContext.deleteDatabase(DATABASE_NAME)

        viewModel = MainViewModel(appContext)
    }

    @After
    fun tearDown() {
        // 最後にデータを削除する
        val appContext = ApplicationProvider.getApplicationContext<Context>() as Application
        appContext.deleteDatabase(DATABASE_NAME)
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
