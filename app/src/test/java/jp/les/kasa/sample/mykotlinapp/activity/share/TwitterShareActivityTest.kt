package jp.les.kasa.sample.mykotlinapp.activity.share

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemActivity
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.ShareStatus
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin


@RunWith(AndroidJUnit4::class)
class TwitterShareActivityTest {
    @get:Rule
    val activityRule = ActivityTestRule(TwitterShareActivity::class.java, false, false)

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun layout() {
        val text = "2019/06/28 は 12335歩 歩きました。気分は上々。"
        val intent = Intent().apply {
            putExtra(
                TwitterShareActivity.KEY_TEXT,
                text
            )
        }
        activityRule.launchActivity(intent)

        onView(withText(text)).check(matches(isDisplayed()))
        Espresso.onView(withText(R.string.label_tweet))
            .check(matches(isDisplayed()))
    }

    @Test
    fun edit() {
        val text = "2019/06/28 は 12335歩 歩きました。気分は上々。"
        val intent = Intent().apply {
            putExtra(
                TwitterShareActivity.KEY_TEXT,
                text
            )
        }
        activityRule.launchActivity(intent)

        onView(ViewMatchers.withId(R.id.editText_share_message)).perform(ViewActions.replaceText("テキスト変更"))
        onView(withText("テキスト変更")).check(matches(isDisplayed()))
    }

    @Test
    fun finishWithIntent() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val intent = Intent(context, TwitterShareActivity::class.java).apply {
            putExtra(
                LogItemActivity.EXTRA_KEY_DATA,
                StepCountLog("2019/06/13", 12345, LEVEL.GOOD)
            )
            putExtra(
                LogItemActivity.EXTRA_KEY_SHARE_STATUS,
                ShareStatus(true, false, true)
            )
        }

        val scenario = ActivityScenario.launch<TwitterShareActivity>(intent)

        // Robolectricでは、ActivityRule#getActivityResultでresultが取れなかった
        // この方法なら取れたので、こちらにしてある。
        // 同じコードは逆に、androidTestでは動かない
        scenario.onActivity { activity ->

            activity.finish()
        }

        Assertions.assertThat(scenario.result.resultCode).isEqualTo(Activity.RESULT_OK)

        val resultData = scenario.result.resultData
        Assertions.assertThat(resultData).isNotNull()

        val extraData = resultData.getSerializableExtra(LogItemActivity.EXTRA_KEY_DATA) as StepCountLog
        Assertions.assertThat(extraData).isNotNull()
        Assertions.assertThat(extraData).isEqualToComparingFieldByField(StepCountLog("2019/06/13", 12345, LEVEL.GOOD))

        val extraData2 = resultData.getSerializableExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS) as ShareStatus
        Assertions.assertThat(extraData2).isNotNull()
        Assertions.assertThat(extraData2).isEqualToComparingFieldByField(ShareStatus(true, false, true))
    }
}
