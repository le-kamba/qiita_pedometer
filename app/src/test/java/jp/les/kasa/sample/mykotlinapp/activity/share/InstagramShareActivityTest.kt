package jp.les.kasa.sample.mykotlinapp.activity.share

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.TestObserver
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
import jp.les.kasa.sample.mykotlinapp.di.mockModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.AutoCloseKoinTest

@RunWith(AndroidJUnit4::class)
class InstagramShareActivityTest : AutoCloseKoinTest() {

    @get:Rule
    val activityRule = ActivityTestRule(InstagramShareActivity::class.java, false, false)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    var grantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    lateinit var activity: InstagramShareActivity

    @Before
    fun setUp() {
        loadKoinModules(mockModule)
    }

    @Test
    fun layout() {
        val intent = Intent().apply {
            putExtra(
                InstagramShareActivity.KEY_STEP_COUNT_DATA,
                StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT)
            )
        }
        activity = activityRule.launchActivity(intent)

        onView(withText("2019/06/22")).check(matches(isDisplayed()))
        onView(withText("456")).check(matches(isDisplayed()))
        onView(withText("歩")).check(matches(isDisplayed()))
        onView(withText(R.string.app_copyright)).check(matches(isDisplayed()))
        onView(withText(R.string.label_post)).check(matches(isDisplayed()))
    }

    @Test
    fun post() {
        val intent = Intent().apply {
            putExtra(
                InstagramShareActivity.KEY_STEP_COUNT_DATA,
                StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT)
            )
        }
        activity = activityRule.launchActivity(intent)

        // activityに反応させないため、いったんすべての監視者を削除
        activity.viewModel.savedBitmapUri.removeObservers(activity)

        // テスト用の監視
        val testObserver = TestObserver<Uri>(1)
        activity.viewModel.savedBitmapUri.observeForever(testObserver)

        onView(withText(R.string.label_post)).perform(click())

        testObserver.await(10)

        assertThat(activity.viewModel.savedBitmapUri.value).isNotNull()

        activity.viewModel.savedBitmapUri.removeObserver(testObserver)
    }
}