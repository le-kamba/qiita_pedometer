package jp.les.kasa.sample.mykotlinapp.activity.signin

import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.app.ActivityOptionsCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseUiException
import com.firebase.ui.auth.IdpResponse
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.di.TestAuthProvider
import jp.les.kasa.sample.mykotlinapp.di.TestAuthUIActivity
import jp.les.kasa.sample.mykotlinapp.di.testMockModule
import jp.les.kasa.sample.mykotlinapp.utils.AuthProviderI
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.endsWith
import org.hamcrest.core.StringStartsWith.startsWith
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class SignInActivityTestI : AutoCloseKoinTest() {
    @get:Rule
    val activityRule = ActivityTestRule(SignInActivity::class.java, false, false)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var activity: SignInActivity

    private val authProvider: AuthProviderI by inject()

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private fun getString(resId: Int) = context.applicationContext.getString(resId)
    private fun getString(resId: Int, c: Int) = context.applicationContext.getString(resId, c)

    class TestRegistry(private val resultCode: Int, private val errorCode: Int) :
        ActivityResultRegistry() {
        override fun <I, O> onLaunch(
            requestCode: Int,
            contract: ActivityResultContract<I, O>,
            input: I,
            options: ActivityOptionsCompat?
        ) {
            when (resultCode) {
                Activity.RESULT_CANCELED -> dispatchResult(
                    requestCode,
                    Activity.RESULT_CANCELED,
                    null
                )
                Activity.RESULT_OK -> dispatchResult(requestCode, Activity.RESULT_OK, null)
                else -> {
                    val exception = FirebaseUiException(errorCode)
                    val resultIntent = IdpResponse.getErrorIntent(exception)
                    dispatchResult(requestCode, Activity.RESULT_CANCELED, resultIntent)
                }
            }
        }
    }

    @Before
    fun setUp() {
        loadKoinModules(testMockModule)
    }

    @Test
    fun layout() {
        activity = activityRule.launchActivity(null)

        // クラウドアイコン
        onView(withId(R.id.imageCloudUpload))
//            .check(matches(withDrawable(R.drawable.ic_cloud_upload_24dp))) // Tintカラー付けていると使えない
            .check(matches(ViewMatchers.isDisplayed()))
        // 文言
        onView(withText(R.string.text_sign_in_description))
            .check(matches(ViewMatchers.isDisplayed()))
        // ログインボタン
        onView(withText(R.string.label_sign_in))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun signIn() {
        activity = activityRule.launchActivity(null)

        // ResultActivityの起動を監視
        val monitor = Instrumentation.ActivityMonitor(
            TestAuthUIActivity::class.java.canonicalName, null, false
        )
        InstrumentationRegistry.getInstrumentation().addMonitor(monitor)

        onView(withText(R.string.label_sign_in)).perform(click())

        // ResultActivityが起動したか確認
        InstrumentationRegistry.getInstrumentation().waitForMonitorWithTimeout(monitor, 1000L)
        assertThat(monitor.hits).isEqualTo(1)
    }

    /**
     *   ログイン中の場合にサインアウト画面がでるかのテスト
     */
    @Test
    fun moveToSignOut() {
        // モックを作成
        (authProvider as TestAuthProvider).mockFirebaseUser = true
        // ResultActivityの起動を監視
        val monitor = Instrumentation.ActivityMonitor(
            SignOutActivity::class.java.canonicalName, null, false
        )
        InstrumentationRegistry.getInstrumentation().addMonitor(monitor)

        activity = activityRule.launchActivity(null)

        assertThat(activity.isFinishing).isTrue()

        // ResultActivityが起動したか確認
        InstrumentationRegistry.getInstrumentation().waitForMonitorWithTimeout(monitor, 1000L)
        assertThat(monitor.hits).isEqualTo(1)
    }

    @Test
    fun showError_EMAIL_MISMATCH_ERROR() {

        val testRegistry = TestRegistry(Activity.RESULT_FIRST_USER, ErrorCodes.EMAIL_MISMATCH_ERROR)
        // 追加のモジュールにセット
        val scopeModule = module {
            scope<SignInActivity> {
                scoped<ActivityResultRegistry>(override = true) { testRegistry }
            }
        }
        // Koin追加モジュール読み込み
        loadKoinModules(scopeModule)

        // Activityを起動
        activity = activityRule.launchActivity(null)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        // 認証画面を起動
        onView(withText(R.string.label_sign_in)).perform(click())
        // resultがすぐにディスパッチされている

        onView(withText(startsWith(getString(R.string.error_email_mismacth))))
            .check(matches(isDisplayed()))
        // @formatter:off
        onView(withText(endsWith(
            getString(R.string.label_error_code, ErrorCodes.EMAIL_MISMATCH_ERROR))))
            .check(matches(isDisplayed()))
            // @formatter:on
        onView(withText(R.string.close))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText(startsWith(getString(R.string.error_email_mismacth))))
            .check(doesNotExist())
    }
    @Test
    fun showError_ERROR_GENERIC_IDP_RECOVERABLE_ERROR() {
        val testRegistry =
            TestRegistry(Activity.RESULT_FIRST_USER, ErrorCodes.ERROR_GENERIC_IDP_RECOVERABLE_ERROR)
        // 追加のモジュールにセット
        val scopeModule = module {
            scope<SignInActivity> {
                scoped<ActivityResultRegistry>(override = true) { testRegistry }
            }
        }
        loadKoinModules(scopeModule)

        activity = activityRule.launchActivity(null)
        // 認証画面を起動
        onView(withText(R.string.label_sign_in)).perform(click())
        // resultがすぐにディスパッチされている

        onView(withText(startsWith(getString(R.string.error_id_provider))))
            .check(matches(isDisplayed()))
        // @formatter:off
        onView(withText(endsWith(
            getString(R.string.label_error_code, ErrorCodes.ERROR_GENERIC_IDP_RECOVERABLE_ERROR))))
            .check(matches(isDisplayed()))
        // @formatter:on
    }

    @Test
    fun showError_PROVIDER_ERROR() {
        val testRegistry = TestRegistry(Activity.RESULT_FIRST_USER, ErrorCodes.PROVIDER_ERROR)
        // 追加のモジュールにセット
        val scopeModule = module {
            scope<SignInActivity> {
                scoped<ActivityResultRegistry>(override = true) { testRegistry }
            }
        }
        loadKoinModules(scopeModule)

        activity = activityRule.launchActivity(null)
        // 認証画面を起動
        onView(withText(R.string.label_sign_in)).perform(click())
        // resultがすぐにディスパッチされている

        onView(withText(startsWith(getString(R.string.error_id_provider))))
            .check(matches(isDisplayed()))
        // @formatter:off
        onView(withText(endsWith(getString(
            R.string.label_error_code,
            ErrorCodes.PROVIDER_ERROR
        ))))
            .check(matches(isDisplayed()))
        // @formatter:on
    }

    @Test
    fun showError_ERROR_USER_DISABLED() {
        val testRegistry = TestRegistry(Activity.RESULT_FIRST_USER, ErrorCodes.ERROR_USER_DISABLED)
        // 追加のモジュールにセット
        val scopeModule = module {
            scope<SignInActivity> {
                scoped<ActivityResultRegistry>(override = true) { testRegistry }
            }
        }
        loadKoinModules(scopeModule)

        activity = activityRule.launchActivity(null)
        // 認証画面を起動
        onView(withText(R.string.label_sign_in)).perform(click())
        // resultがすぐにディスパッチされている

        onView(withText(startsWith(getString(R.string.error_user_disabled))))
            .check(matches(isDisplayed()))
        // @formatter:off
        onView(withText(endsWith(getString(
            R.string.label_error_code,
            ErrorCodes.ERROR_USER_DISABLED
        ))))
            .check(matches(isDisplayed()))
        // @formatter:on
    }

    @Test
    fun showError_NO_NETWORK() {
        val testRegistry = TestRegistry(Activity.RESULT_FIRST_USER, ErrorCodes.NO_NETWORK)
        // 追加のモジュールにセット
        val scopeModule = module {
            scope<SignInActivity> {
                scoped<ActivityResultRegistry>(override = true) { testRegistry }
            }
        }
        loadKoinModules(scopeModule)

        activity = activityRule.launchActivity(null)
        // 認証画面を起動
        onView(withText(R.string.label_sign_in)).perform(click())
        // resultがすぐにディスパッチされている

        onView(withText(startsWith(getString(R.string.error_no_netowork))))
            .check(matches(isDisplayed()))
        // @formatter:off
        onView(withText(endsWith(getString(
            R.string.label_error_code,
            ErrorCodes.NO_NETWORK
        ))))
            .check(matches(isDisplayed()))
        // @formatter:on
    }

    @Test
    fun showError_PLAY_SERVICES_UPDATE_CANCELLED() {
        val testRegistry =
            TestRegistry(Activity.RESULT_FIRST_USER, ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED)
        // 追加のモジュールにセット
        val scopeModule = module {
            scope<SignInActivity> {
                scoped<ActivityResultRegistry>(override = true) { testRegistry }
            }
        }
        loadKoinModules(scopeModule)

        activity = activityRule.launchActivity(null)
        // 認証画面を起動
        onView(withText(R.string.label_sign_in)).perform(click())
        // resultがすぐにディスパッチされている

        onView(withText(startsWith(getString(R.string.error_service_update_canceled))))
            .check(matches(isDisplayed()))
        // @formatter:off
        onView(withText(endsWith(getString(
            R.string.label_error_code,
            ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED
        ))))
            .check(matches(isDisplayed()))
        // @formatter:on
    }

    @Test
    fun showError_UNKNOWN() {
        val testRegistry = TestRegistry(Activity.RESULT_FIRST_USER, ErrorCodes.UNKNOWN_ERROR)
        // 追加のモジュールにセット
        val scopeModule = module {
            scope<SignInActivity> {
                scoped<ActivityResultRegistry>(override = true) { testRegistry }
            }
        }
        loadKoinModules(scopeModule)

        activity = activityRule.launchActivity(null)
        // 認証画面を起動
        onView(withText(R.string.label_sign_in)).perform(click())
        // resultがすぐにディスパッチされている

        onView(withText(startsWith(getString(R.string.error_unknown))))
            .check(matches(isDisplayed()))
        // @formatter:off
        onView(withText(endsWith(getString(
            R.string.label_error_code,
            ErrorCodes.UNKNOWN_ERROR
        ))))
            .check(matches(isDisplayed()))
        // @formatter:on
    }
}