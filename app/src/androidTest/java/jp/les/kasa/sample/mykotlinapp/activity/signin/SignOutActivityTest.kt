package jp.les.kasa.sample.mykotlinapp.activity.signin

import android.app.Instrumentation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.di.testMockModule
import jp.les.kasa.sample.mykotlinapp.utils.AuthProviderI
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class SignOutActivityTest : AutoCloseKoinTest() {
    @get:Rule
    val activityRule = ActivityTestRule(SignOutActivity::class.java, false, false)

    lateinit var activity: SignOutActivity

    private val authProvider: AuthProviderI by inject()

    @Before
    fun setUp() {
        loadKoinModules(testMockModule)
    }

    /**
     *   起動直後の表示のテスト<br>
     */
    @Test
    fun layout() {
        activity = activityRule.launchActivity(null)

        // サインイン中
        onView(withText(R.string.text_sign_in_now))
            .check(matches(isDisplayed()))
        // クラウドアイコン
        onView(withId(R.id.imageCloudDone))
//            .check(matches(withDrawable(R.drawable.ic_cloud_upload_24dp))) // Tintカラー付けていると使えない
            .check(matches(isDisplayed()))
        // ユーザー名
        onView(withText("ユーザー名")).check(matches(isDisplayed()))
        // メールアドレス
        onView(withText("foo@bar.com")).check(matches(isDisplayed()))
        // 文言
        onView(withText(R.string.text_sign_out_description))
            .check(matches(isDisplayed()))
        // ログアウトボタン
        onView(withText(R.string.label_sign_out))
            .check(matches(isDisplayed()))
        // ローカルデータ変換ボタン
        onView(withText(R.string.label_convert_to_local))
            .check(matches(isDisplayed()))
        // アカウント削除ボタン
        onView(withText(R.string.label_account_delete))
            .check(matches(isDisplayed()))
    }

    @Test
    fun signOut() {
        activity = activityRule.launchActivity(null)

        // ResultActivityの起動を監視
        val monitor = Instrumentation.ActivityMonitor(
            SignInActivity::class.java.canonicalName, null, false
        )
        InstrumentationRegistry.getInstrumentation().addMonitor(monitor)

        // ログアウトボタン
        onView(withText(R.string.label_sign_out))
            .perform(click())

        Assertions.assertThat(activity.isFinishing).isEqualTo(true)

        // ResultActivityが起動したか確認
        InstrumentationRegistry.getInstrumentation().waitForMonitorWithTimeout(monitor, 1000L)
        Assertions.assertThat(monitor.hits).isEqualTo(1)
    }

    @Test
    fun deleteAccount_cancel() {
        activity = activityRule.launchActivity(null)

        onView(withId(R.id.signOutScroll)).perform(swipeUp())
        // アカウント削除ボタン
        onView(withId(R.id.buttonAccountDelete))
            .perform(scrollTo(), click())

        onView(withText(R.string.confirm_account_delete_1))
            .check(matches(isDisplayed()))

        onView(withText(R.string.label_no))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText(R.string.confirm_account_delete_1))
            .check(doesNotExist())

        onView(withText(R.string.confirm_account_delete_2))
            .check(matches(isDisplayed()))

        onView(withText(R.string.label_no))
            .check(matches(isDisplayed()))
            .perform(click())

        Assertions.assertThat(activity.isFinishing).isEqualTo(false)
    }

    @Test
    fun deleteAccount_data_converted() {
        // ResultActivityの起動を監視
        val monitor = Instrumentation.ActivityMonitor(
            SignInActivity::class.java.canonicalName, null, false
        )
        InstrumentationRegistry.getInstrumentation().addMonitor(monitor)

        activity = activityRule.launchActivity(null)
        onView(withId(R.id.signOutScroll)).perform(swipeUp())
        // アカウント削除ボタン
        onView(withId(R.id.buttonAccountDelete))
            .perform(scrollTo(), click())

        onView(withText(R.string.confirm_account_delete_1))
            .check(matches(isDisplayed()))

        onView(withText(R.string.label_yes))
            .check(matches(isDisplayed()))
            .perform(click())

        // コンバートしましたに「はい」と答えたので、アカウント削除をし自分は終了した
        Assertions.assertThat(activity.isFinishing).isEqualTo(true)

        // ResultActivityが起動したか確認
        InstrumentationRegistry.getInstrumentation()
            .waitForMonitorWithTimeout(monitor, 1000L)
        Assertions.assertThat(monitor.hits).isEqualTo(1)
    }

    @Test
    fun deleteAccount_anyway() {
        // ResultActivityの起動を監視
        val monitor = Instrumentation.ActivityMonitor(
            SignInActivity::class.java.canonicalName, null, false
        )
        InstrumentationRegistry.getInstrumentation().addMonitor(monitor)

        activity = activityRule.launchActivity(null)

        onView(withId(R.id.signOutScroll)).perform(swipeUp())
        // アカウント削除ボタン
        onView(withId(R.id.buttonAccountDelete))
            .perform(scrollTo(), click())

        onView(withText(R.string.confirm_account_delete_1))
            .check(matches(isDisplayed()))

        onView(withText(R.string.label_no))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText(R.string.confirm_account_delete_2))
            .check(matches(isDisplayed()))

        onView(withText(R.string.label_yes))
            .check(matches(isDisplayed()))
            .perform(click())

        // アカウント削除をし自分は終了した
        Assertions.assertThat(activity.isFinishing).isEqualTo(true)

        // ResultActivityが起動したか確認
        InstrumentationRegistry.getInstrumentation()
            .waitForMonitorWithTimeout(monitor, 1000L)
        Assertions.assertThat(monitor.hits).isEqualTo(1)
    }
}