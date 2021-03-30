package jp.les.kasa.sample.mykotlinapp.activity.main

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
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
    fun showPage() {

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

        // グリッドの表示確認
        onView(withId(R.id.log_list)).check(matches(RecyclerViewMatchers.hasItemCount(42)))

        // 項目の確認
        // 全セルをテストするのは多すぎるので、ログがある日の表示が他の日付のセルに繰り返されたりしていないか、
        // 前後のチェックをしています。
        var index = 0

        onView(withId(R.id.log_list))
            // @formatter:off
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.GONE), R.id.stepTextView)))
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.GONE), R.id.suffixTextView)))
            .check(matches(atPositionOnView(index, withText("26"), R.id.dayTextView)))
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.GONE), R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.GONE), R.id.levelImageView)))
            // @formatter:on

        checkCellNull(1, "27")
        checkCellNull(2, "28")
        checkCellNull(3, "29")
        index = 4
        onView(withId(R.id.log_list))
            // @formatter:off
            .check(matches(atPositionOnView(index, withText("612"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.VISIBLE), R.id.stepTextView)))
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.VISIBLE), R.id.suffixTextView)))
            .check(matches(atPositionOnView(index, withText("30"), R.id.dayTextView)))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.ic_sentiment_neutral_green_24dp),R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                        withDrawable(R.drawable.ic_cloud_gley_24dp),R.id.weatherImageView)))
        // @formatter:on
        checkCellNull(5, "31")
        checkCellNull(6, "1")

        checkCellNull(17, "12")
        index = 18
        onView(withId(R.id.log_list))
            // @formatter:off
            .check(matches(atPositionOnView(index, withText("12345"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.VISIBLE), R.id.stepTextView)))
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.VISIBLE), R.id.suffixTextView)))
            .check(matches(atPositionOnView(index, withText("13"), R.id.dayTextView)))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.ic_sentiment_very_satisfied_pink_24dp),R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                        withDrawable(R.drawable.ic_wb_sunny_yellow_24dp),R.id.weatherImageView)))
        // @formatter:on
        checkCellNull(19, "14")

        checkCellNull(23, "18")
        index = 24
        onView(withId(R.id.log_list))
            // @formatter:off
            .check(matches(atPositionOnView(index, withText("666"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.VISIBLE), R.id.stepTextView)))
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.VISIBLE), R.id.suffixTextView)))
            .check(matches(atPositionOnView(index, withText("19"), R.id.dayTextView)))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp),R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                        withDrawable(R.drawable.ic_iconmonstr_umbrella_1),R.id.weatherImageView)))
        // @formatter:on
        checkCellNull(25, "20")
        checkCellNull(36, "1")
        checkCellNull(41, "6")
    }

    private fun checkCellNull(index: Int, day: String) {
        onView(withId(R.id.log_list))
            // @formatter:off
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.GONE), R.id.stepTextView)))
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.GONE), R.id.suffixTextView)))
            .check(matches(atPositionOnView(index, withText(day), R.id.dayTextView)))
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.GONE), R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                withEffectiveVisibility(Visibility.GONE), R.id.levelImageView)))
        // @formatter:on
    }

    @Test
    fun cellBackground_active() {
        // 「今日」のセルの背景が変わっているのを確認
        val index = 33 // mock calendarProviderが返すのは"2019/06/28"なのでそのindex

        val fragmentArgs = Bundle().apply {
            putString(MonthlyPageFragment.KEY_DATE_YEAR_MONTH, "2019/06")
        }
        launchFragmentInContainer<MonthlyPageFragment>(fragmentArgs)

        // @formatter:off
        onView(withId(R.id.log_list))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.cell_active), R.id.logItemLayout)))
        // @formatter:on
    }

    @Test
    fun cellBackground_grey() {
        // 前月、翌月のセルの背景が変わっているのを確認
        val fragmentArgs = Bundle().apply {
            putString(MonthlyPageFragment.KEY_DATE_YEAR_MONTH, "2019/06")
        }
        launchFragmentInContainer<MonthlyPageFragment>(fragmentArgs)

        // @formatter:off
        onView(withId(R.id.log_list))
            .check(matches(atPositionOnView(0,
                withDrawable(R.drawable.cell_nonactive_grey), R.id.logItemLayout)))
        onView(withId(R.id.log_list))
            .check(matches(atPositionOnView(5,
                withDrawable(R.drawable.cell_nonactive_grey), R.id.logItemLayout)))
        onView(withId(R.id.log_list))
            .check(matches(atPositionOnView(36,
                withDrawable(R.drawable.cell_nonactive_grey), R.id.logItemLayout)))
        onView(withId(R.id.log_list))
            .check(matches(atPositionOnView(41,
                withDrawable(R.drawable.cell_nonactive_grey), R.id.logItemLayout)))
        // @formatter:on
    }

    @Test
    fun cellBackground_nonactive() {
        // 「今日」以外のセルの背景の確認
        val fragmentArgs = Bundle().apply {
            putString(MonthlyPageFragment.KEY_DATE_YEAR_MONTH, "2019/06")
        }
        launchFragmentInContainer<MonthlyPageFragment>(fragmentArgs)

        // 一日と、月末日と、任意の中間日でチェック
        // @formatter:off
        onView(withId(R.id.log_list))
            .check(matches(atPositionOnView(6,
                withDrawable(R.drawable.cell_nonactive), R.id.logItemLayout)))

        onView(withId(R.id.log_list))
            .check(matches(atPositionOnView(15,
                withDrawable(R.drawable.cell_nonactive), R.id.logItemLayout)))

        onView(withId(R.id.log_list))
            .check(matches(atPositionOnView(35,
                withDrawable(R.drawable.cell_nonactive), R.id.logItemLayout)))
        // @formatter:on
    }
}