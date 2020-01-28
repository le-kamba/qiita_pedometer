package jp.les.kasa.sample.mykotlinapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class MainViewModelTest : AutoCloseKoinTest() {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    val viewModel: MainViewModel by inject()

    @Test
    fun init() {
        assertThat(viewModel.repository)
            .isNotNull()
        assertThat(viewModel.stepCountList)
            .isNotNull()
    }

    //    @Test
    fun addStepCount() {
        viewModel.addStepCount(StepCountLog("2019/06/21", 123))
        viewModel.addStepCount(StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))

        assertThat(viewModel.stepCountList.value)
            .isNotEmpty()

        val list = viewModel.stepCountList.value as List<StepCountLog>
        assertThat(list.size).isEqualTo(2)
        assertThat(list[0]).isEqualToComparingFieldByField(StepCountLog("2019/06/21", 123))
        assertThat(list[1]).isEqualToComparingFieldByField(StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
    }
}
