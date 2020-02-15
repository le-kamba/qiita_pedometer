package jp.les.kasa.sample.mykotlinapp.activity.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.les.kasa.sample.mykotlinapp.TestObserver
import jp.les.kasa.sample.mykotlinapp.data.LogRoomDatabase
import jp.les.kasa.sample.mykotlinapp.di.mockModule
import jp.les.kasa.sample.mykotlinapp.equalsYMD
import jp.les.kasa.sample.mykotlinapp.observeForTesting
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Assert.assertTrue
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

    }

    @Test
    fun cellData() {

    }

    @Test
    fun getFromToYMD() {
        val pair = viewModel.getFromToYMD("2020/01")
        assertTrue(pair.first.equalsYMD("2019/12/29"))
        assertTrue(pair.second.equalsYMD("2020/02/08"))

        val pair2 = viewModel.getFromToYMD("2020/12")
        assertTrue(pair2.first.equalsYMD("2020/12/01"))
        assertTrue(pair2.second.equalsYMD("2021/01/01"))
    }

}