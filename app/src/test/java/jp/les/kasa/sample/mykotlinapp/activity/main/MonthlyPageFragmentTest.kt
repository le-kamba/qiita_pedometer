package jp.les.kasa.sample.mykotlinapp.activity.main

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.les.kasa.sample.mykotlinapp.*
import jp.les.kasa.sample.mykotlinapp.data.*
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
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(
    qualifiers = "xlarge-port",
    shadows = [ShadowAlertDialog::class, ShadowAlertController::class]
)
class MonthlyPageFragmentTest : AutoCloseKoinTest() {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val repository: LogRepository by inject()

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
    fun showDateLabel() {
        val fragmentArgs = Bundle().apply {
            putString(MonthlyPageFragment.KEY_DATE_YEAR_MONTH, "2020/02")
        }
        launchFragmentInContainer<MonthlyPageFragment>(fragmentArgs)

        onView(withId(R.id.textViewYM)).check(matches(isDisplayed()))
            .check(matches(withText("2020年 2月")))
    }

    @Test
    fun showList() {

        // repositoryに直接追加
        runBlocking {
            // @formatter:off
            repository.insert(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
            repository.insert(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
            repository.insert(StepCountLog("2019/05/30", 612, LEVEL.NORMAL, WEATHER.CLOUD))
            // @formatter:on
        }
        val allLogs = repository.allLogs()
        assertThat(allLogs.size).isEqualTo(3)

        val fragmentArgs = Bundle().apply {
            putString(MonthlyPageFragment.KEY_DATE_YEAR_MONTH, "2019/06")
        }
        launchFragmentInContainer<MonthlyPageFragment>(fragmentArgs)

        // リストの表示確認
        onView(withId(R.id.log_list)).check(matches(RecyclerViewMatchers.hasItemCount(2)))

        // リスト項目の確認
        var index = 1

        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(matches(atPositionOnView(index, withText("12345"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index, withText("2019/06/13"), R.id.dateTextView)))
            .check(matches(atPositionOnView(index, withDrawable(R.drawable.ic_sentiment_very_satisfied_pink_24dp), R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                        withDrawable(R.drawable.ic_wb_sunny_yellow_24dp),R.id.weatherImageView)))
            // @formatter:on
        index = 0
        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(matches(atPositionOnView(index, withText("666"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index, withText("2019/06/19"), R.id.dateTextView)))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp),R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                        withDrawable(R.drawable.ic_iconmonstr_umbrella_1),R.id.weatherImageView)))
        // @formatter:on
    }
}