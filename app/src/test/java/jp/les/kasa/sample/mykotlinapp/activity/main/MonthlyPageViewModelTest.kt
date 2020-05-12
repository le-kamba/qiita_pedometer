package jp.les.kasa.sample.mykotlinapp.activity.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.les.kasa.sample.mykotlinapp.TestObserver
import jp.les.kasa.sample.mykotlinapp.data.*
import jp.les.kasa.sample.mykotlinapp.di.mockModule
import jp.les.kasa.sample.mykotlinapp.observeForTesting
import jp.les.kasa.sample.mykotlinapp.utils.clearTime
import jp.les.kasa.sample.mykotlinapp.utils.equalsYMD
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
class MonthlyPageViewModelTest : AutoCloseKoinTest() {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val viewModel: MonthlyPageViewModel by inject()

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
        assertThat(viewModel.cellData).isNotNull()
        viewModel.cellData.observeForTesting {
            assertThat(viewModel.cellData.value)
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
    fun stepCountList() {
        val testObserver = TestObserver<List<StepCountLog>>()
        viewModel.stepCountList.observeForever(testObserver)
        viewModel.setYearMonth("2019/06")

        testObserver.await()

        assertThat(viewModel.stepCountList.value).isNotNull().isEmpty()
    }

    @Test
    fun firstDayInPage() {
        val testObserver = TestObserver<List<StepCountLog>>()
        viewModel.stepCountList.observeForever(testObserver)
        viewModel.setYearMonth("2019/06")

        testObserver.await()

        assertThat(viewModel.firstDayInPage).isNotNull()
        assertThat(viewModel.firstDayInPage.equalsYMD("2019/05/26")).isTrue()
    }

    @Test
    fun cellData() {
        val testObserver = TestObserver<List<CalendarCellData>>()
        viewModel.cellData.observeForever(testObserver)
        viewModel.setYearMonth("2019/06")

        testObserver.await()

        assertThat(viewModel.cellData.value!!.size).isEqualTo(42)
    }

    @Test
    fun getFromToYMD() {
        val pair = viewModel.getFromToYMD("2020/01")
        assertThat(pair.first.equalsYMD("2019/12/29")).isTrue()
        assertThat(pair.second.equalsYMD("2020/02/09")).isTrue()

        val pair2 = viewModel.getFromToYMD("2020/12")
        assertThat(pair2.first.equalsYMD("2020/11/29")).isTrue()
        assertThat(pair2.second.equalsYMD("2021/01/10")).isTrue()
    }

    @Test
    fun createCellData() {
        val logs = listOf(
            StepCountLog("2019/12/30", 8888),
            StepCountLog("2020/01/01", 8888),
            StepCountLog("2020/01/02", 1025, LEVEL.BAD),
            StepCountLog("2020/01/03", 8888, weather = WEATHER.COLD),
            StepCountLog("2020/01/08", 8888),
            StepCountLog("2020/01/10", 12345, LEVEL.GOOD, WEATHER.CLOUD),
            StepCountLog("2020/01/21", 123, LEVEL.BAD),
            StepCountLog("2020/01/22", 8888),
            StepCountLog("2020/01/23", 10101, LEVEL.GOOD),
            StepCountLog("2020/01/24", 8888),
            StepCountLog("2020/01/31", 12123),
            StepCountLog("2020/02/08", 5000, LEVEL.BAD, WEATHER.SNOW)
        )
        var cal = Calendar.getInstance().clearTime()
        cal.set(Calendar.YEAR, 2019)
        cal.set(Calendar.MONTH, 11)
        cal.set(Calendar.DAY_OF_MONTH, 29)
        val list = viewModel.createCellData(cal, logs)

        assertThat(list.size).isEqualTo(42)
        assertThat(list[0]).isEqualToComparingFieldByField(CalendarCellData(cal, null))

        cal = Calendar.getInstance().clearTime()
        cal.set(Calendar.YEAR, 2019)
        cal.set(Calendar.MONTH, 11)
        cal.set(Calendar.DAY_OF_MONTH, 30)

        assertThat(list[1]).isEqualToComparingFieldByField(CalendarCellData(cal, logs[0]))

        cal.set(Calendar.DAY_OF_MONTH, 31)
        assertThat(list[2]).isEqualToComparingFieldByField(CalendarCellData(cal, null))

        cal = Calendar.getInstance().clearTime()
        cal.set(Calendar.YEAR, 2020)
        cal.set(Calendar.MONTH, 0)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        assertThat(list[3]).isEqualToComparingFieldByField(CalendarCellData(cal, logs[1]))


        cal = Calendar.getInstance().clearTime()
        cal.set(Calendar.YEAR, 2020)
        cal.set(Calendar.MONTH, 1)
        cal.set(Calendar.DAY_OF_MONTH, 8)
        assertThat(list[41]).isEqualToComparingFieldByField(CalendarCellData(cal, logs[11]))
    }
}