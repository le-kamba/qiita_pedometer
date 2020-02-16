package jp.les.kasa.sample.mykotlinapp.activity.main

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onIdle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemActivity
import jp.les.kasa.sample.mykotlinapp.activity.share.InstagramShareActivity
import jp.les.kasa.sample.mykotlinapp.activity.share.TwitterShareActivity
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.ShareStatus
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
import jp.les.kasa.sample.mykotlinapp.di.testMockModule
import jp.les.kasa.sample.mykotlinapp.espresso.*
import kotlinx.android.synthetic.main.activity_main.*
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.AutoCloseKoinTest


/**
 * @date 2019/06/05
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTestI : AutoCloseKoinTest() {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java, false, false)

    @Before
    fun setUp() {
        loadKoinModules(testMockModule)
        activityRule.launchActivity(Intent())
    }

    @After
    fun tearDown() {
        activityRule.finishActivity()
    }

    @Test
    fun onActivityResult_Add() {

        val resultData = Intent().apply {
            // @formatter:off
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW))
            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus())
            // @formatter:on
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 登録画面を起動
        onView(
            Matchers.allOf(withContentDescription("記録を追加")) // FIXME
        ).perform(click())

        val resultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 500L)
        resultActivity.setResult(Activity.RESULT_OK, resultData)
        resultActivity.finish()

        getInstrumentation().waitForIdleSync()

        // 反映を確認
        val index = 24

        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(matches(atPositionOnView(index, withText("666"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index, withText("2019/06/19"), R.id.dayTextView)))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp), R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                        withDrawable(R.drawable.ic_grain_gley_24dp),R.id.weatherImageView)))
            // @formatter:on
    }

    @Test
    fun onActivityResult_Edit() {
        // 最初にデータ投入
        val mainActivity = activityRule.activity

        mainActivity.runOnUiThread {
            // @formatter:off
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
            // @formatter:on
        }
        getInstrumentation().waitForIdleSync()

        val resultData = Intent().apply {
            // @formatter:off
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 5000, LEVEL.NORMAL, WEATHER.CLOUD))
            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus())
            // @formatter:on
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 編集画面を起動
        val index = 24

        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
        // @formatter:on
        getInstrumentation().waitForIdleSync()


        val resultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 500L)
        resultActivity.setResult(Activity.RESULT_OK, resultData)
        resultActivity.finish()
        getInstrumentation().waitForIdleSync()

        // 反映を確認
        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(matches(atPositionOnView(index, withText("5000"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index, withText("2019/06/19"), R.id.dayTextView)))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.ic_sentiment_neutral_green_24dp), R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                        withDrawable(R.drawable.ic_cloud_gley_24dp),R.id.weatherImageView)))
            // @formatter:on
    }

    @Test
    fun onActivityResult_Delete() {
        // 最初にデータ投入
        val mainActivity = activityRule.activity

        mainActivity.runOnUiThread {
            // @formatter:off
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
            // @formatter:on
        }
        getInstrumentation().waitForIdleSync()

        val resultData = Intent().apply {
            // @formatter:off
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
            // @formatter:on
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        onView(withId(R.id.log_list)).check(matches(RecyclerViewMatchers.hasItemCount(42)))

        // 編集画面を起動
        val index = 24

        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
        // @formatter:on
        getInstrumentation().waitForIdleSync()


        val resultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 500L)
        resultActivity.setResult(MainActivity.RESULT_CODE_DELETE, resultData)
        resultActivity.finish()
        getInstrumentation().waitForIdleSync()

        onView(withId(R.id.log_list)).check(matches(RecyclerViewMatchers.hasItemCount(42)))

        // 反映を確認
        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(matches(atPositionOnView(index, withText("12345"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index, withText("2019/06/13"), R.id.dayTextView)))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.ic_sentiment_very_satisfied_pink_24dp), R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                        withDrawable(R.drawable.ic_wb_sunny_yellow_24dp),R.id.weatherImageView)))
            // @formatter:on
    }

    @Test
    fun onActivityResult_TwitterShare() {
        val resultData = Intent().apply {
            // @formatter:off
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW))
            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(true, true, false))
            // @formatter:on
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 登録画面を起動
        onView(
            Matchers.allOf(withContentDescription("記録を追加")) // FIXME
        ).perform(click())

        val resultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 500L)

        // Twitterシェア画面起動確認用のモニタ
        val shareMonitor = Instrumentation.ActivityMonitor(
            TwitterShareActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(shareMonitor)

        // 登録画面に結果をセットして終了させる
        resultActivity.setResult(Activity.RESULT_OK, resultData)
        resultActivity.finish()

        getInstrumentation().waitForIdleSync()

        // Twitterシェア画面起動を確認
        val shareActivity = getInstrumentation()
            .waitForMonitorWithTimeout(shareMonitor, 500L) as TwitterShareActivity
        assertThat(shareMonitor.hits).isEqualTo(1)
        assertThat(shareActivity).isNotNull()
    }

    @Test
    fun onActivityResult_InstagramShare() {
        val resultData = Intent().apply {
            // @formatter:off
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW))
            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(true, false, true))
            // @formatter:on
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 登録画面を起動
        onView(
            Matchers.allOf(withContentDescription("記録を追加")) // FIXME
        ).perform(click())

        val resultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 500L)

        // シェア画面起動確認用のモニタ
        val shareMonitor = Instrumentation.ActivityMonitor(
            InstagramShareActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(shareMonitor)

        // 登録画面に結果をセットして終了させる
        resultActivity.setResult(Activity.RESULT_OK, resultData)
        resultActivity.finish()

        getInstrumentation().waitForIdleSync()

        // シェア画面起動を確認
        val shareActivity = getInstrumentation().waitForMonitorWithTimeout(shareMonitor, 500L)
                as InstagramShareActivity
        assertThat(shareMonitor.hits).isEqualTo(1)
        assertThat(shareActivity).isNotNull()
    }

    @Test
    fun onActivityResult_NoneShare() {
        val resultData = Intent().apply {
            // @formatter:off
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW))
            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(false, true, true))
            // @formatter:on
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 登録画面を起動
        onView(
            Matchers.allOf(withContentDescription("記録を追加")) // FIXME
        ).perform(click())

        val resultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 500L)

        // Twitter/Instagramシェア画面起動確認用のモニタ
        val shareMonitor = Instrumentation.ActivityMonitor(
            TwitterShareActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(shareMonitor)
        val shareMonitor2 = Instrumentation.ActivityMonitor(
            InstagramShareActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(shareMonitor2)

        // 登録画面に結果をセットして終了させる
        resultActivity.setResult(Activity.RESULT_OK, resultData)
        resultActivity.finish()

        getInstrumentation().waitForIdleSync()

        // シェア画面起動なしを確認
        val shareActivity = getInstrumentation().waitForMonitorWithTimeout(shareMonitor, 500L)
        val shareActivity2 = getInstrumentation().waitForMonitorWithTimeout(shareMonitor2, 500L)
        assertThat(shareMonitor.hits).isEqualTo(0)
        assertThat(shareMonitor2.hits).isEqualTo(0)
        assertThat(shareActivity).isNull()
        assertThat(shareActivity2).isNull()
    }

    @Test
    fun onActivityResult_ShareAll() {
        val resultData = Intent().apply {
            // @formatter:off
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW))
            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(true, true, true))
            // @formatter:on
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 登録画面を起動
        onView(
            Matchers.allOf(withContentDescription("記録を追加")) // FIXME
        ).perform(click())

        val resultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 500L)

        // Twitter/Instagramシェア画面起動確認用のモニタ
        val shareMonitor = Instrumentation.ActivityMonitor(
            TwitterShareActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(shareMonitor)
        val shareMonitor2 = Instrumentation.ActivityMonitor(
            InstagramShareActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(shareMonitor2)

        // 登録画面に結果をセットして終了させる
        resultActivity.setResult(Activity.RESULT_OK, resultData)
        resultActivity.finish()

        getInstrumentation().waitForIdleSync()

        // Twitterシェア画面起動を確認
        val shareActivity = getInstrumentation().waitForMonitorWithTimeout(shareMonitor, 500L)
                as TwitterShareActivity
        assertThat(shareMonitor.hits).isEqualTo(1)
        assertThat(shareActivity).isNotNull()
        assertThat(shareMonitor2.hits).isEqualTo(0) // Instagramはまだ起動されてない

        // Twitterシェア画面の戻りは自動でセット(そのまま返る)
        shareActivity.finish()

        getInstrumentation().waitForIdleSync()

        // Instagramシェア画面起動を確認
        val shareActivity2 = getInstrumentation().waitForMonitorWithTimeout(shareMonitor2, 500L)
                as InstagramShareActivity
        assertThat(shareMonitor2.hits).isEqualTo(1)
        assertThat(shareActivity2).isNotNull()
    }

    @Test
    fun onClickListItem() {
        // 最初にデータ投入
        val mainActivity = activityRule.activity

        mainActivity.runOnUiThread {
            // @formatter:off
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
            // @formatter:on
        }
        getInstrumentation().waitForIdleSync()

        // 監視モニター
        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)


        val index = 24

        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
        // @formatter:on
        getInstrumentation().waitForIdleSync()

        // ResultActivityが起動したか確認
        val resultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 1000L)
        assertThat(monitor.hits).isEqualTo(1)
        assertThat(resultActivity).isNotNull()

        // その起動Intentに必要な情報があるかチェック
        val extraData =
            resultActivity.intent.getSerializableExtra(LogItemActivity.EXTRA_KEY_DATA) as StepCountLog
        assertThat(extraData)
            .isEqualToComparingFieldByField(
                StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN)
            )
    }

    @Test
    fun pages() {
        // 最初にデータ投入
        val mainActivity = activityRule.activity

        mainActivity.runOnUiThread {
            // @formatter:off
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
            mainActivity.viewModel.addStepCount(StepCountLog("2018/12/19", 666, LEVEL.BAD, WEATHER.RAIN))
            // @formatter:on
        }
        getInstrumentation().waitForIdleSync()

        // ページ数が正しいかのテスト(2018/12〜2019/06までの7ページあるはず)
        onView(withId(R.id.viewPager)).check(matches(ViewPagerMatchers.hasItemCount(7)))

        // 今表示されているのが2019/06かどうかのテスト
        onView(withText("2019年 6月")).check(matches(isCompletelyDisplayed()))

        // currentPageのチェック
        onView(withId(R.id.viewPager)).check(matches(ViewPagerMatchers.isCurrent(6)))
    }

    @Test
    fun swipe() {
        // 最初にデータ投入
        val mainActivity = activityRule.activity

        mainActivity.runOnUiThread {
            // @formatter:off
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
            mainActivity.viewModel.addStepCount(StepCountLog("2018/12/19", 666, LEVEL.BAD, WEATHER.RAIN))
            // @formatter:on
        }
        getInstrumentation().waitForIdleSync()

        // currentPageのチェック
        onView(withId(R.id.viewPager)).check(matches(ViewPagerMatchers.isCurrent(6)))
        // 左からスワイプしてカレントページインデックスのチェック
        onView(withId(R.id.viewPager)).perform(swipePrevious())

        val idleWatcher = ViewPagerIdleWatcher(mainActivity.viewPager)
        idleWatcher.waitForIdle()
        onIdle()
        onView(withId(R.id.viewPager)).check(matches(ViewPagerMatchers.isCurrent(5)))

        // 右からスワイプしてカレントページインデックスのチェック
        onView(withId(R.id.viewPager)).perform(swipeNext())

        idleWatcher.waitForIdle()
        onIdle()
        onView(withId(R.id.viewPager)).check(matches(ViewPagerMatchers.isCurrent(6)))

        idleWatcher.unregister()
    }
}
