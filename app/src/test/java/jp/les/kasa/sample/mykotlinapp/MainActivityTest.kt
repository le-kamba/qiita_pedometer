package jp.les.kasa.sample.mykotlinapp

import android.app.Instrumentation
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemActivity
import jp.les.kasa.sample.mykotlinapp.activity.main.MainActivity
import jp.les.kasa.sample.mykotlinapp.data.LogRoomDatabase
import jp.les.kasa.sample.mykotlinapp.di.mockModule
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.robolectric.annotation.Config

/**
 * @date 2019/06/05
 */
@RunWith(AndroidJUnit4::class)
@Config(
    qualifiers = "xlarge-port",
    shadows = [ShadowAlertDialog::class, ShadowAlertController::class]
)
class MainActivityTest : AutoCloseKoinTest() {

    @get:Rule
    val activityRule = IntentsTestRule(MainActivity::class.java, false, false)

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        loadKoinModules(mockModule)
        get<LogRoomDatabase>().clearAllTables()

        activityRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        activityRule.finishActivity()
        get<LogRoomDatabase>().clearAllTables()
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
        getInstrumentation().waitForMonitorWithTimeout(monitor, 3000L)
        assertThat(monitor.hits).isEqualTo(1)
    }

//    @Test
//    fun showList() {
//        // ViewModelのリストに直接追加
//        val mainActivity = activityRule.activity
//
//        val listObserver = TestObserver<List<StepCountLog>>(2)
//        mainActivity.viewModel.stepCountList.observeForever(listObserver)
//
//        runBlocking {
//            // @formatter:off
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
//            // @formatter:on
//        }
//        listObserver.await()
//        getInstrumentation().waitForIdleSync()
//
//        // リストの表示確認
//        var index = 1
//
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .check(matches(atPositionOnView(index, withText("12345"), R.id.stepTextView)))
//            .check(matches(atPositionOnView(index, withText("2019/06/13"), R.id.dateTextView)))
//            .check(matches(atPositionOnView(index, withDrawable(R.drawable.ic_sentiment_very_satisfied_pink_24dp), R.id.levelImageView)))
//            .check(matches(atPositionOnView(index,
//                        withDrawable(R.drawable.ic_wb_sunny_yellow_24dp),R.id.weatherImageView)))
//            // @formatter:on
//        index = 0
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .check(matches(atPositionOnView(index, withText("666"), R.id.stepTextView)))
//            .check(matches(atPositionOnView(index, withText("2019/06/19"), R.id.dateTextView)))
//            .check(matches(atPositionOnView(index,
//                withDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp),R.id.levelImageView)))
//            .check(matches(atPositionOnView(index,
//                        withDrawable(R.drawable.ic_iconmonstr_umbrella_1),R.id.weatherImageView)))
//        // @formatter:on
//
//        mainActivity.viewModel.stepCountList.removeObserver(listObserver)
//    }
//
//    @Test
//    fun onActivityResult_Add() {
//        val resultData = Intent().apply {
//            // @formatter:off
//            putExtra(LogItemActivity.EXTRA_KEY_DATA,StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW))
//            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus())
//            // @formatter:on
//        }
//        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
//
//        // マッチしたIntentが発行されたときに、onActivityResultに結果を返す設定
//        Intents.intending(
//            IntentMatchers.hasComponent(
//                ComponentName(
//                    ApplicationProvider.getApplicationContext<Application>(),
//                    LogItemActivity::class.java
//                )
//            )
//        ).respondWith(result)
//
//        // 登録画面を起動
//        onView(
//            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
//        ).perform(click())
//        getInstrumentation().waitForIdleSync()
//
//        // onActivityResultに先ほどセットしたresultで返ってくるはず
//
//        // 反映を確認
//        val index = 0
//
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .check(matches(atPositionOnView(index, withText("666"), R.id.stepTextView)))
//            .check(matches(atPositionOnView(index, withText("2019/06/19"), R.id.dateTextView)))
//            .check(matches(atPositionOnView(index,
//                withDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp), R.id.levelImageView)))
//            .check(matches(atPositionOnView(index,
//                        withDrawable(R.drawable.ic_grain_gley_24dp),R.id.weatherImageView)))
//            // @formatter:on
//    }
//
//    @Test
//    fun onActivityResult_Delete() {
//        // 最初にデータ投入
//        val mainActivity = activityRule.activity
//
//        val listObserver = TestObserver<List<StepCountLog>>(2)
//        mainActivity.viewModel.stepCountList.observeForever(listObserver)
//
//        runBlocking {
//            // @formatter:off
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
//            // @formatter:on
//        }
//        listObserver.await()
//        getInstrumentation().waitForIdleSync()
//
//        val resultData = Intent().apply {
//            putExtra(
//                LogItemActivity.EXTRA_KEY_DATA,
//                StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN)
//            )
//        }
//        val result = Instrumentation.ActivityResult(MainActivity.RESULT_CODE_DELETE, resultData)
//
//        // マッチしたIntentが発行されたときに、onActivityResultに結果を返す設定
//        Intents.intending(
//            IntentMatchers.hasComponent(
//                ComponentName(
//                    ApplicationProvider.getApplicationContext<Application>(),
//                    LogItemActivity::class.java
//                )
//            )
//        ).respondWith(result)
//
//        val deleteObserver = TestObserver<List<StepCountLog>>()
//        mainActivity.viewModel.stepCountList.observeForever(deleteObserver)
//
//        // 編集画面を起動
//        val index = 0
//
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
//        // @formatter:on
//        getInstrumentation().waitForIdleSync()
//
//        // onActivityResultに先ほどセットしたresultで返ってくるはず
//
//        deleteObserver.await()
//
//        // 反映を確認
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .check(matches(atPositionOnView(index, withText("12345"), R.id.stepTextView)))
//            .check(matches(atPositionOnView(index, withText("2019/06/13"), R.id.dateTextView)))
//            .check(matches(atPositionOnView(index,
//                withDrawable(R.drawable.ic_sentiment_very_satisfied_pink_24dp), R.id.levelImageView)))
//            .check(matches(atPositionOnView(index,
//                withDrawable(R.drawable.ic_wb_sunny_yellow_24dp),R.id.weatherImageView)))
//            // @formatter:on
//
//        mainActivity.viewModel.stepCountList.removeObserver(listObserver)
//        mainActivity.viewModel.stepCountList.removeObserver(deleteObserver)
//    }
//
//    @Test
//    fun onActivityResult_Edit() {
//        // 最初にデータ投入
//        val mainActivity = activityRule.activity
//
//        val listObserver = TestObserver<List<StepCountLog>>(2)
//        mainActivity.viewModel.stepCountList.observeForever(listObserver)
//
//        runBlocking {
//            // @formatter:off
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
//            // @formatter:on
//        }
//        listObserver.await()
//        getInstrumentation().waitForIdleSync()
//
//        val resultData = Intent().apply {
//            putExtra(
//                LogItemActivity.EXTRA_KEY_DATA,
//                StepCountLog("2019/06/19", 5000, LEVEL.NORMAL, WEATHER.CLOUD)
//            )
//            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus())
//        }
//        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
//
//        // マッチしたIntentが発行されたときに、onActivityResultに結果を返す設定
//        Intents.intending(
//            IntentMatchers.hasComponent(
//                ComponentName(
//                    ApplicationProvider.getApplicationContext<Application>(),
//                    LogItemActivity::class.java
//                )
//            )
//        ).respondWith(result)
//
//        // 編集画面を起動
//        val index = 0
//
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
//        // @formatter:on
//        getInstrumentation().waitForIdleSync()
//
//        // onActivityResultに先ほどセットしたresultで返ってくるはず
//
//        // 反映を確認
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .check(matches(atPositionOnView(index, withText("5000"), R.id.stepTextView)))
//            .check(matches(atPositionOnView(index, withText("2019/06/19"), R.id.dateTextView)))
//            .check(matches(atPositionOnView(index,
//                withDrawable(R.drawable.ic_sentiment_neutral_green_24dp), R.id.levelImageView)))
//            .check(matches(atPositionOnView(index,
//                        withDrawable(R.drawable.ic_cloud_gley_24dp),R.id.weatherImageView)))
//            // @formatter:on
//
//        mainActivity.viewModel.stepCountList.removeObserver(listObserver)
//    }
//
//    @Test
//    fun onActivityResult_InstagramShare() {
//
//        val resultData = Intent().apply {
//            putExtra(
//                LogItemActivity.EXTRA_KEY_DATA,
//                StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW)
//            )
//            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(true, false, true))
//        }
//        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
//
//        // マッチしたIntentが発行されたときに、onActivityResultに結果を返す設定
//        Intents.intending(
//            IntentMatchers.hasComponent(
//                ComponentName(
//                    ApplicationProvider.getApplicationContext<Application>(),
//                    LogItemActivity::class.java
//                )
//            )
//        ).respondWith(result)
//
//        // シェア画面起動確認用のモニタ
//        val shareMonitor = Instrumentation.ActivityMonitor(
//            InstagramShareActivity::class.java.canonicalName, null, false
//        )
//        getInstrumentation().addMonitor(shareMonitor)
//
//        // 登録画面を起動
//        onView(
//            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
//        ).perform(click())
//
//        getInstrumentation().waitForIdleSync()
//
//        // onActivityResultに先ほどセットしたresultで返ってくるはず
//
//        // シェア画面起動を確認
//        getInstrumentation().waitForMonitorWithTimeout(shareMonitor, 500L)
//        assertThat(shareMonitor.hits).isEqualTo(1)
//    }
//
//    @Test
//    fun onActivityResult_NoneShare() {
//
//        val resultData = Intent().apply {
//            putExtra(
//                LogItemActivity.EXTRA_KEY_DATA,
//                StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW)
//            )
//            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(false, true, true))
//        }
//        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
//
//        // マッチしたIntentが発行されたときに、onActivityResultに結果を返す設定
//        Intents.intending(
//            IntentMatchers.hasComponent(
//                ComponentName(
//                    ApplicationProvider.getApplicationContext<Application>(),
//                    LogItemActivity::class.java
//                )
//            )
//        ).respondWith(result)
//
//        // Twitter/Instagramシェア画面起動確認用のモニタ
//        val shareMonitor = Instrumentation.ActivityMonitor(
//            TwitterShareActivity::class.java.canonicalName, null, false
//        )
//        getInstrumentation().addMonitor(shareMonitor)
//        val shareMonitor2 = Instrumentation.ActivityMonitor(
//            InstagramShareActivity::class.java.canonicalName, null, false
//        )
//        getInstrumentation().addMonitor(shareMonitor2)
//
//        // 登録画面を起動
//        onView(
//            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
//        ).perform(click())
//
//        getInstrumentation().waitForIdleSync()
//
//        // onActivityResultに先ほどセットしたresultで返ってくるはず
//
//        // シェア画面起動なしをval shareActivity = 確認
//        getInstrumentation().waitForMonitorWithTimeout(shareMonitor, 500L)
//        getInstrumentation().waitForMonitorWithTimeout(shareMonitor2, 500L)
//        assertThat(shareMonitor.hits).isEqualTo(0)
//        assertThat(shareMonitor2.hits).isEqualTo(0)
//    }
//
//    @Test
//    fun onActivityResult_ShareAll_1() {
//
//        val resultData = Intent().apply {
//            putExtra(
//                LogItemActivity.EXTRA_KEY_DATA,
//                StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW)
//            )
//            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(true, true, true))
//        }
//        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
//
//        // マッチしたIntentが発行されたときに、onActivityResultに結果を返す設定
//        Intents.intending(
//            IntentMatchers.hasComponent(
//                ComponentName(
//                    ApplicationProvider.getApplicationContext<Application>(),
//                    LogItemActivity::class.java
//                )
//            )
//        ).respondWith(result)
//
//        // Twitter/Instagramシェア画面起動確認用のモニタ
//        val shareMonitor = Instrumentation.ActivityMonitor(
//            TwitterShareActivity::class.java.canonicalName, null, false
//        )
//        getInstrumentation().addMonitor(shareMonitor)
//        val shareMonitor2 = Instrumentation.ActivityMonitor(
//            InstagramShareActivity::class.java.canonicalName, null, false
//        )
//        getInstrumentation().addMonitor(shareMonitor2)
//
//        // 登録画面を起動
//        onView(
//            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
//        ).perform(click())
//
//        getInstrumentation().waitForIdleSync()
//
//        // onActivityResultに先ほどセットしたresultで返ってくるはず
//
//        // Twitterシェア画面起動を確認
//        getInstrumentation().waitForMonitorWithTimeout(shareMonitor, 500L)
//        assertThat(shareMonitor.hits).isEqualTo(1)
//        assertThat(shareMonitor2.hits).isEqualTo(0) // Instagramはまだ起動されてない
//
//        // TwitterShareから戻った続きは、
//        // 起動したActivityが取れないので、別テストとする
//    }
//
//    @Test
//    fun onActivityResult_ShareAll_2() {
//        val activity = activityRule.activity
//
//        val resultData = Intent().apply {
//            putExtra(
//                LogItemActivity.EXTRA_KEY_DATA,
//                StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW)
//            )
//            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(true, true, true))
//        }
//        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
//
//        // マッチしたIntentが発行されたときに、onActivityResultに結果を返す設定
//        Intents.intending(
//            IntentMatchers.hasComponent(
//                ComponentName(
//                    ApplicationProvider.getApplicationContext<Application>(),
//                    TwitterShareActivity::class.java
//                )
//            )
//        ).respondWith(result)
//
//        // Instagramシェア画面起動確認用のモニタ
//        val shareMonitor2 = Instrumentation.ActivityMonitor(
//            InstagramShareActivity::class.java.canonicalName, null, false
//        )
//        getInstrumentation().addMonitor(shareMonitor2)
//
//        // twitter画面を直接起動
//        val intent = Intent(
//            ApplicationProvider.getApplicationContext<Application>(),
//            TwitterShareActivity::class.java
//        ).apply {
//            putExtra(
//                LogItemActivity.EXTRA_KEY_DATA,
//                StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW)
//            )
//            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(true, true, true))
//        }
//        activity.startActivityForResult(intent, MainActivity.REQUEST_CODE_SHARE_TWITTER)
//        getInstrumentation().waitForIdleSync()
//
//        // onActivityResultに先ほどセットしたresultで返ってくるはず
//
//        // Instagramシェア画面起動を確認
//        getInstrumentation().waitForMonitorWithTimeout(shareMonitor2, 500L)
//        assertThat(shareMonitor2.hits).isEqualTo(1)
//    }
//
//    @Test
//    fun onActivityResult_TwitterShare() {
//
//        val resultData = Intent().apply {
//            putExtra(
//                LogItemActivity.EXTRA_KEY_DATA,
//                StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.SNOW)
//            )
//            putExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS, ShareStatus(true, true, false))
//        }
//        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
//
//        // マッチしたIntentが発行されたときに、onActivityResultに結果を返す設定
//        Intents.intending(
//            IntentMatchers.hasComponent(
//                ComponentName(
//                    ApplicationProvider.getApplicationContext<Application>(),
//                    LogItemActivity::class.java
//                )
//            )
//        ).respondWith(result)
//
//        // Twitterシェア画面起動確認用のモニタ
//        val shareMonitor = Instrumentation.ActivityMonitor(
//            TwitterShareActivity::class.java.canonicalName, null, false
//        )
//        getInstrumentation().addMonitor(shareMonitor)
//
//        // 登録画面を起動
//        onView(
//            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
//        ).perform(click())
//
//        getInstrumentation().waitForIdleSync()
//
//        // onActivityResultに先ほどセットしたresultで返ってくるはず
//
//        // Twitterシェア画面起動を確認
//        getInstrumentation().waitForMonitorWithTimeout(shareMonitor, 500L)
//        assertThat(shareMonitor.hits).isEqualTo(1)
//    }
//
//    @Test
//    fun onClickListItem() {
//        // 最初にデータ投入
//        val mainActivity = activityRule.activity
//
//        val listObserver = TestObserver<List<StepCountLog>>(2)
//        mainActivity.viewModel.stepCountList.observeForever(listObserver)
//
//        runBlocking {
//            // @formatter:off
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
//            // @formatter:on
//        }
//        listObserver.await()
//        getInstrumentation().waitForIdleSync()
//
//        // 監視モニター
//        val monitor = Instrumentation.ActivityMonitor(
//            LogItemActivity::class.java.canonicalName, null, false
//        )
//        getInstrumentation().addMonitor(monitor)
//
//
//        val index = 0
//
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
//        // @formatter:on
//        getInstrumentation().waitForIdleSync()
//
//        // ResultActivityが起動したか確認
//        getInstrumentation().waitForMonitorWithTimeout(monitor, 1000L)
//        assertThat(monitor.hits).isEqualTo(1)
//
//        // その起動Intentに必要な情報があるかチェック
//        Intents.intended(
//            IntentMatchers.hasExtra(
//                LogItemActivity.EXTRA_KEY_DATA,
//                StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN)
//            )
//        )
//
//        mainActivity.viewModel.stepCountList.removeObserver(listObserver)
//    }
//
//    @Test
//    fun onLongClickListItem_cancel() {
//        // 最初にデータ投入
//        val mainActivity = activityRule.activity
//
//        val listObserver = TestObserver<List<StepCountLog>>(2)
//        mainActivity.viewModel.stepCountList.observeForever(listObserver)
//
//        runBlocking {
//            // @formatter:off
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
//            // @formatter:on
//        }
//        listObserver.await()
//        getInstrumentation().waitForIdleSync()
//
//        val index = 0
//
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, longClick()))
//        // @formatter:on
//        getInstrumentation().waitForIdleSync()
//
//        // Dialogキャンセル
//        // Robolectricは拾えないのでDialogを取得して行う
//        val dialog = ShadowAlertDialog.latestAlertDialog!!
//        assertThat(dialog.isShowing).isTrue()
//        val negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
//        negative.performClick()
//        getInstrumentation().waitForIdleSync()
//
//        // Dialogの非表示を確認
//        assertThat(dialog.isShowing).isFalse()
//
//        // 削除されてないことの確認
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .check(matches(atPositionOnView(index, withText("666"), R.id.stepTextView)))
//            .check(matches(atPositionOnView(index, withText("2019/06/19"), R.id.dateTextView)))
//            .check(matches(atPositionOnView(index,
//                withDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp), R.id.levelImageView)))
//            .check(matches(atPositionOnView(index,
//                        withDrawable(R.drawable.ic_iconmonstr_umbrella_1),R.id.weatherImageView)))
//            // @formatter:on
//
//        mainActivity.viewModel.stepCountList.removeObserver(listObserver)
//    }
//
//    @Test
//    fun onLongClickListItem_cancel_back() {
//        // 最初にデータ投入
//        val mainActivity = activityRule.activity
//
//        val listObserver = TestObserver<List<StepCountLog>>(2)
//        mainActivity.viewModel.stepCountList.observeForever(listObserver)
//
//        runBlocking {
//            // @formatter:off
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
//            // @formatter:on
//        }
//        listObserver.await()
//        getInstrumentation().waitForIdleSync()
//
//        val index = 0
//
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, longClick()))
//        // @formatter:on
//        getInstrumentation().waitForIdleSync()
//
//        // Dialogの表示確認
//        // Robolectricは拾えないのでDialogを取得して行う
//        val dialog = ShadowAlertDialog.latestAlertDialog!!
//        assertThat(dialog.isShowing).isTrue()
//        val shadowAlertDialog = shadowOfAlert(dialog)
//        assertThat(shadowAlertDialog.message.toString()).isEqualTo(
//            mainActivity.getString(R.string.message_delete_confirm)
//        )
//        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
//        assertThat(positive.text).isEqualTo(mainActivity.getString(android.R.string.yes))
//        val negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
//        assertThat(negative.text).isEqualTo(mainActivity.getString(android.R.string.no))
//
//        // 端末戻るボタン
//        pressBack()
//        getInstrumentation().waitForIdleSync()
//
//        // Dialogの非表示を確認
//        assertThat(dialog.isShowing).isFalse()
//
//        // 削除されてないことの確認
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .check(matches(atPositionOnView(index, withText("666"), R.id.stepTextView)))
//            .check(matches(atPositionOnView(index, withText("2019/06/19"), R.id.dateTextView)))
//            .check(matches(atPositionOnView(index,
//                withDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp), R.id.levelImageView)))
//            .check(matches(atPositionOnView(index,
//                        withDrawable(R.drawable.ic_iconmonstr_umbrella_1),R.id.weatherImageView)))
//            // @formatter:on
//
//        mainActivity.viewModel.stepCountList.removeObserver(listObserver)
//    }
//
//    @Test
//    fun onLongClickListItem_delete() {
//        // 最初にデータ投入
//        val mainActivity = activityRule.activity
//
//        val listObserver = TestObserver<List<StepCountLog>>(2)
//        mainActivity.viewModel.stepCountList.observeForever(listObserver)
//
//        runBlocking {
//            // @formatter:off
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))
//            mainActivity.viewModel.addStepCount(StepCountLog("2019/06/19", 666, LEVEL.BAD, WEATHER.RAIN))
//            // @formatter:on
//        }
//        listObserver.await()
//        getInstrumentation().waitForIdleSync()
//
//        val index = 0
//
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .check(matches(atPositionOnView(index, withText("666"), R.id.stepTextView)))
//            .check(matches(atPositionOnView(index, withText("2019/06/19"), R.id.dateTextView)))
//            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, longClick()))
//        // @formatter:on
//        getInstrumentation().waitForIdleSync()
//
//        val deleteObserver = TestObserver<List<StepCountLog>>()
//        mainActivity.viewModel.stepCountList.observeForever(deleteObserver)
//
//        // Dialogの表示確認
//        // Robolectricは拾えないのでDialogを取得して行う
//        val dialog = ShadowAlertDialog.latestAlertDialog!!
//        assertThat(dialog.isShowing).isTrue()
//        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
//
//        positive.performClick()
//        getInstrumentation().waitForIdleSync()
//
//        // Dialogの非表示を確認
//        assertThat(dialog.isShowing).isFalse()
//
//        deleteObserver.await()
//
//        // 反映を確認
//        onView(withId(R.id.log_list))
//            // @formatter:off
//            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
//            .check(matches(atPositionOnView(index, withText("12345"), R.id.stepTextView)))
//            .check(matches(atPositionOnView(index, withText("2019/06/13"), R.id.dateTextView)))
//            .check(matches(atPositionOnView(index,
//                withDrawable(R.drawable.ic_sentiment_very_satisfied_pink_24dp), R.id.levelImageView)))
//            .check(matches(atPositionOnView(index,
//                        withDrawable(R.drawable.ic_wb_sunny_yellow_24dp),R.id.weatherImageView)))
//            // @formatter:on
//
//        mainActivity.viewModel.stepCountList.removeObserver(listObserver)
//        mainActivity.viewModel.stepCountList.removeObserver(deleteObserver)
//    }
}