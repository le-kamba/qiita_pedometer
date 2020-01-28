package jp.les.kasa.sample.mykotlinapp.activity.logitem


import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.les.kasa.sample.mykotlinapp.clearTime
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.ShareStatus
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
import jp.les.kasa.sample.mykotlinapp.getDay
import jp.les.kasa.sample.mykotlinapp.getMonth
import jp.les.kasa.sample.mykotlinapp.getYear
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import java.util.*

@RunWith(AndroidJUnit4::class)
class LogItemViewModelTest {
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    lateinit var viewModel: LogItemViewModel

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        viewModel = LogItemViewModel(context)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun init() {
        assertThat(viewModel.logItem.value)
            .isNull() // 初期化したときはnull
    }

    @Test
    fun changeLog() {
        viewModel.changeLog(
            StepCountLog("2019/06/21", 12345, LEVEL.BAD, WEATHER.COLD),
            ShareStatus(true, true, false)
        )

        assertThat(viewModel.logItem.value)
            .isEqualToComparingFieldByField(
                LogItemData(
                    StepCountLog("2019/06/21", 12345, LEVEL.BAD, WEATHER.COLD),
                    ShareStatus(true, true, false)
                )
            )
    }

    @Test
    fun dateSelected() {
        var date = Calendar.getInstance()
        date.set(Calendar.YEAR, 2019)
        date.set(Calendar.MONDAY, 5)
        date.set(Calendar.DAY_OF_MONTH, 15)
        date = date.clearTime()
        viewModel.dateSelected(date)

        assertThat(viewModel.selectDate.value!!.getYear())
            .isEqualTo(2019)
        assertThat(viewModel.selectDate.value!!.getMonth())
            .isEqualTo(5)
        assertThat(viewModel.selectDate.value!!.getDay())
            .isEqualTo(15)
    }
}