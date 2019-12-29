package jp.les.kasa.sample.mykotlinapp

import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemActivity
import jp.les.kasa.sample.mykotlinapp.activity.share.InstagramShareActivity
import jp.les.kasa.sample.mykotlinapp.activity.share.TwitterShareActivity
import jp.les.kasa.sample.mykotlinapp.data.*
import jp.les.kasa.sample.mykotlinapp.espresso.atPositionOnView
import jp.les.kasa.sample.mykotlinapp.espresso.withDrawable
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * @date 2019/06/05
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTestI {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java, false, false)

    @Before
    fun setUp() {
        val appContext = ApplicationProvider.getApplicationContext<Application>()

        // 最初にデータを削除する
        appContext.deleteDatabase(DATABASE_NAME)

        activityRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        activityRule.finishActivity()

        // 最後にデータを削除する
        val appContext = ApplicationProvider.getApplicationContext<Application>()
        appContext.deleteDatabase(DATABASE_NAME)
    }

    @Test
    fun addRecordMenuIcon() {
        onView(
            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
        ).check(matches(isDisplayed()))
    }

    @Test
    fun addRecordMenu() {
        // ResultActivityの起動を監視
        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 追加メニューをクリック
        onView(
            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
        ).perform(click())

        // ResultActivityが起動したか確認
        val resultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 1000L)
        assertThat(monitor.hits).isEqualTo(1)
        assertThat(resultActivity).isNotNull()

        // 端末戻るボタンで終了を確認
        pressBack()
        assertThat(resultActivity.isFinishing).isTrue()
    }

    @Test
    fun showList() {
        // ViewModelのリストに直接追加
        val mainActivity = activityRule.activity

        mainActivity.runOnUiThread {
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        // リストの表示確認
        var index = 1

        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(matches(atPositionOnView(index, withText("12345"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index, withText("2019/06/13"), R.id.dateTextView)))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.ic_sentiment_very_satisfied_pink_24dp), R.id.levelImageView)))
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

    @Test
    fun onActivityResult_Add() {
        val resultData = Intent().apply {
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW))
            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus())
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 登録画面を起動
        onView(
            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
        ).perform(click())

        val resultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 500L)
        resultActivity.setResult(Activity.RESULT_OK, resultData)
        resultActivity.finish()

        getInstrumentation().waitForIdleSync()

        // 反映を確認
        val index = 0

        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(matches(atPositionOnView(index, withText("666"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index, withText("2019/06/19"), R.id.dateTextView)))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp), R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                        withDrawable(R.drawable.ic_grain_gley_24dp),R.id.weatherImageView)))
            // @formatter:on
    }

    @Test
    fun onClickListItem() {
        // 最初にデータ投入
        val mainActivity = activityRule.activity

        mainActivity.runOnUiThread {
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        // 監視モニター
        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)


        val index = 0

        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
        // @formatter:on
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        // ResultActivityが起動したか確認
        val resultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 1000L)
        assertThat(monitor.hits).isEqualTo(1)
        assertThat(resultActivity).isNotNull()

        // その起動Intentに必要な情報があるかチェック
        val extraData = resultActivity.intent.getSerializableExtra(LogItemActivity.EXTRA_KEY_DATA) as StepCountLog
        assertThat(extraData)
            .isEqualToComparingFieldByField(
                StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN)
            )
    }

    @Test
    fun onActivityResult_Edit() {
        // 最初にデータ投入
        val mainActivity = activityRule.activity

        mainActivity.runOnUiThread {
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        val resultData = Intent().apply {
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 5000, LEVEL.NORMAL, WEATHER.CLOUD))
            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus())
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 編集画面を起動
        val index = 0

        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
        // @formatter:on
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()


        val resultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 500L)
        resultActivity.setResult(Activity.RESULT_OK, resultData)
        resultActivity.finish()

        // 反映を確認
        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(matches(atPositionOnView(index, withText("5000"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index, withText("2019/06/19"), R.id.dateTextView)))
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
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        val resultData = Intent().apply {
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 編集画面を起動
        val index = 0

        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
        // @formatter:on
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()


        val resultActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 500L)
        resultActivity.setResult(MainActivity.RESULT_CODE_DELETE, resultData)
        resultActivity.finish()

        // 反映を確認
        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(matches(atPositionOnView(index, withText("12345"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index, withText("2019/06/13"), R.id.dateTextView)))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.ic_sentiment_very_satisfied_pink_24dp), R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                        withDrawable(R.drawable.ic_wb_sunny_yellow_24dp),R.id.weatherImageView)))
            // @formatter:on
    }

    @Test
    fun onLongClickListItem_cancel_back() {
        // 最初にデータ投入
        val mainActivity = activityRule.activity

        mainActivity.runOnUiThread {
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        val index = 0

        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, longClick()))
        // @formatter:on
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        // Dialogの表示確認
        onView(withText(R.string.message_delete_confirm))
            .check(matches(isDisplayed()))
        onView(withText(android.R.string.yes))
            .check(matches(isDisplayed()))
        onView(withText(android.R.string.no))
            .check(matches(isDisplayed()))

        // 端末戻るボタン
        pressBack()

        // Dialogの非表示を確認
        onView(withText(R.string.message_delete_confirm))
            .check(doesNotExist())

        // 削除されてないことの確認
        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(matches(atPositionOnView(index, withText("666"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index, withText("2019/06/19"), R.id.dateTextView)))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp), R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                        withDrawable(R.drawable.ic_iconmonstr_umbrella_1),R.id.weatherImageView)))
            // @formatter:on
    }

    @Test
    fun onLongClickListItem_cancel() {
        // 最初にデータ投入
        val mainActivity = activityRule.activity

        mainActivity.runOnUiThread {
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        val index = 0

        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, longClick()))
        // @formatter:on
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        // Dialogキャンセル
        onView(withText(R.string.message_delete_confirm))
            .check(matches(isDisplayed()))
        onView(withText(android.R.string.no))
            .perform(click())

        // Dialogの非表示を確認
        onView(withText(R.string.message_delete_confirm))
            .check(doesNotExist())

        // 削除されてないことの確認
        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(matches(atPositionOnView(index, withText("666"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index, withText("2019/06/19"), R.id.dateTextView)))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp), R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                        withDrawable(R.drawable.ic_iconmonstr_umbrella_1),R.id.weatherImageView)))
            // @formatter:on
    }

    @Test
    fun onLongClickListItem_delete() {
        // 最初にデータ投入
        val mainActivity = activityRule.activity

        mainActivity.runOnUiThread {
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        val index = 0

        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, longClick()))
        // @formatter:on
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        // Dialogの表示確認
        onView(withText(R.string.message_delete_confirm))
            .check(matches(isDisplayed()))
        onView(withText(android.R.string.yes))
            .perform(click())

        // Dialogの非表示を確認
        onView(withText(R.string.message_delete_confirm))
            .check(doesNotExist())

        // 反映を確認
        onView(withId(R.id.log_list))
            // @formatter:off
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(matches(atPositionOnView(index, withText("12345"), R.id.stepTextView)))
            .check(matches(atPositionOnView(index, withText("2019/06/13"), R.id.dateTextView)))
            .check(matches(atPositionOnView(index,
                withDrawable(R.drawable.ic_sentiment_very_satisfied_pink_24dp), R.id.levelImageView)))
            .check(matches(atPositionOnView(index,
                        withDrawable(R.drawable.ic_wb_sunny_yellow_24dp),R.id.weatherImageView)))
            // @formatter:on
    }

    @Test
    fun onActivityResult_TwitterShare() {
        val resultData = Intent().apply {
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW))
            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(true, true, false))
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 登録画面を起動
        onView(
            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
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
        val shareActivity = getInstrumentation().waitForMonitorWithTimeout(shareMonitor, 500L) as TwitterShareActivity
        assertThat(shareMonitor.hits).isEqualTo(1)
        assertThat(shareActivity).isNotNull()
    }

    @Test
    fun onActivityResult_InstagramShare() {
        val resultData = Intent().apply {
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW))
            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(true, false, true))
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 登録画面を起動
        onView(
            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
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
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW))
            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(false, true, true))
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 登録画面を起動
        onView(
            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
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
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW))
            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(true, true, true))
        }

        val monitor = Instrumentation.ActivityMonitor(
            LogItemActivity::class.java.canonicalName, null, false
        )
        getInstrumentation().addMonitor(monitor)

        // 登録画面を起動
        onView(
            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
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
}
