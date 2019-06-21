package jp.les.kasa.sample.mykotlinapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class MainViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        viewModel = MainViewModel()
    }

    @Test
    fun init() {
        assertThat(viewModel.stepCountList.value)
            .isNotNull()
            .isEmpty()

    }

    @Test
    fun addStepCount() {
//        viewModel.addStepCount(123)
//        viewModel.addStepCount(456)

        assertThat(viewModel.stepCountList.value)
            .isNotEmpty()

        val list = viewModel.stepCountList.value as List<Int>
        assertThat(list.size).isEqualTo(2)
        assertThat(list[0]).isEqualTo(123)
        assertThat(list[1]).isEqualTo(456)
    }
}
