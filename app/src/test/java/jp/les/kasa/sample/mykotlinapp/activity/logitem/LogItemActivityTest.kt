package jp.les.kasa.sample.mykotlinapp.activity.logitem


import android.app.Activity
import android.content.DialogInterface
import android.widget.Spinner
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import jp.les.kasa.sample.mykotlinapp.*
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemActivity.Companion.EXTRA_KEY_DATA
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
import jp.les.kasa.sample.mykotlinapp.dialog.DateSelectDialogFragment
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

@RunWith(AndroidJUnit4::class)
@Config(
    qualifiers = "xlarge-port",
    shadows = [ShadowAlertDialog::class, ShadowAlertController::class]
) // 長めの縦画面にしないとスクロールが必要になるようでテストが失敗する
class LogItemActivityTest {
    @get:Rule
    val activityRule = ActivityTestRule(LogItemActivity::class.java, false, false)

    lateinit var activity: LogItemActivity

    private val context = InstrumentationRegistry.getInstrumentation().targetContext!!

    private fun getString(resId: Int) = context.applicationContext.getString(resId)

    /**
     *   起動直後の表示のテスト<br>
     *   LogInputFragmentの初期表示をチェックする
     */
    @Test
    fun logInputFragment() {
        activity = activityRule.launchActivity(null)

        // 日時ラベル
        onView(withText(R.string.label_date)).check(matches(isDisplayed()))
        // 日付
        val today = Calendar.getInstance().getDateStringYMD()
        onView(withText(today)).check(matches(isDisplayed()))
        // 日付選択ボタン
        onView(withText(R.string.label_select_date)).check(matches(isDisplayed()))
        // 歩数ラベル
        onView(withText(R.string.label_step_count)).check(matches(isDisplayed()))
        // 歩数ヒント
        onView(withHint(R.string.hint_edit_step)).check(matches(isDisplayed()))
        // 気分ラベル
        onView(withText(R.string.label_level)).check(matches(isDisplayed()))
        // 気分ラジオボタン
        onView(withText(R.string.level_normal)).check(matches(isDisplayed()))
        onView(withText(R.string.level_good)).check(matches(isDisplayed()))
        onView(withText(R.string.level_bad)).check(matches(isDisplayed()))
        onView(withId(R.id.imageView))
            .check(matches(withDrawable(R.drawable.ic_sentiment_neutral_green_24dp)))
            .check(matches(isDisplayed()))
        onView(withId(R.id.imageView2))
            .check(matches(withDrawable(R.drawable.ic_sentiment_very_satisfied_pink_24dp)))
            .check(matches(isDisplayed()))
        onView(withId(R.id.imageView3))
            .check(matches(withDrawable(R.drawable.ic_sentiment_dissatisfied_black_24dp)))
            .check(matches(isDisplayed()))
        // 天気ラベル
        onView(withText(R.string.label_step_count)).check(matches(isDisplayed()))
        // 天気スピナー
        onView(withId(R.id.spinner_weather)).check(matches(isDisplayed()))
        // 登録ボタン
        onView(withText(R.string.resist)).check(matches(isDisplayed()))
    }

    /**
     * 日付選択ボタンでダイアログが表示されるテスト
     */
    @Test
    fun selectDate() {
        activity = activityRule.launchActivity(null)

        val today = Calendar.getInstance()

        // 日付選択ボタン
        onView(withText(R.string.label_select_date)).perform(click())

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        // CalendarViewは特殊で、OSバージョンで表示される物が異なるため、
        // 内容の確認は難しい(表示されているはずの文字列で見つけられない。もしかしたら文字列じゃ無く画像なのかも)
        // なので、直接SupportFragmentManagerから今持っているFragmentでTAGを条件にDialogFragmentを探しだし、
        // そこからCalendarViewのインスタンスを得ている
        val fragment = activity.supportFragmentManager.findFragmentByTag(LogInputFragment.DATE_SELECT_TAG)
                as DateSelectDialogFragment
        // 初期選択時間が、起動前に取得した時間と僅差であることの確認
        assertThat(fragment.calendarView.date).isCloseTo(today.timeInMillis, Offset.offset(1000L))
    }

    /**
     * 選択を変更してキャンセルしたときに表示が変わっていないこと
     */
    @Test
    fun selectDate_cancel() {
        activity = activityRule.launchActivity(null)

        val today = Calendar.getInstance()

        // 日付選択ボタン
        onView(withText(R.string.label_select_date)).perform(click())

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        // CalendarViewは特殊で、OSバージョンで表示される物が異なるため、
        // 内容の確認は難しい
        // なので、直接SupportFragmentManagerから今持っているFragmentでTAGを条件にDialogFragmentを探しだし、
        // そこからCalendarViewのインスタンスを得ている
        val fragment = activity.supportFragmentManager.findFragmentByTag(LogInputFragment.DATE_SELECT_TAG)
                as DateSelectDialogFragment
        val newDate = today.clone() as Calendar
        newDate.add(Calendar.DAY_OF_MONTH, -1) // 未来はNGなので一つ前に
        // 日付を選んだ動作も書けないので、クリックされるときに変わるはずのselectDateを無理矢理上書き。
        // そのため、selectDateの修飾子を特殊な書き方に変更してある
        fragment.selectDate.set(newDate.getYear(), newDate.getMonth(), newDate.getDay())

        // ボタンのクリックはRobolectricは拾えないのでDialogを取得して行う
        val dialog = ShadowAlertDialog.latestAlertDialog!!
        assertThat(dialog.isShowing).isTrue()
        val negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        negative.performClick()

        // 新しい日付は表示されていない
        onView(withText(newDate.getDateStringYMD())).check(doesNotExist())
        // 当日のまま
        onView(withText(today.getDateStringYMD())).check(matches(isDisplayed()))
    }

    /**
     * 選択を変更してOKしたときに表示が変わっていること
     */
    @Test
    fun selectDate_ok() {
        activity = activityRule.launchActivity(null)

        val today = Calendar.getInstance()

        // 日付選択ボタン
        onView(withText(R.string.label_select_date)).perform(click())

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        // CalendarViewは特殊で、OSバージョンで表示される物が異なるため、
        // 内容の確認は難しい
        // なので、直接SupportFragmentManagerから今持っているFragmentでTAGを条件にDialogFragmentを探しだし、
        // そこからCalendarViewのインスタンスを得ている
        val fragment = activity.supportFragmentManager.findFragmentByTag(LogInputFragment.DATE_SELECT_TAG)
                as DateSelectDialogFragment
        val newDate = today.clone() as Calendar
        newDate.add(Calendar.DAY_OF_MONTH, -1) // 未来はNGなので一つ前に
        // 日付を選んだ動作も書けないので、クリックされるときに変わるはずのselectDateを無理矢理上書き。
        // そのため、selectDateの修飾子を特殊な書き方に変更してある
        fragment.selectDate.set(newDate.getYear(), newDate.getMonth(), newDate.getDay())

        // ボタンのクリックはRobolectricは拾えないのでDialogを取得して行う
        val dialog = ShadowAlertDialog.latestAlertDialog!!
        assertThat(dialog.isShowing).isTrue()
        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positive.performClick()

        // 新しい日付になっていること
        onView(withId(R.id.text_date)).check(matches(withText(newDate.getDateStringYMD())))
    }

    /**
     * ステップ数を入力したときのテスト
     */
    @Test
    fun editCount() {
        activity = activityRule.launchActivity(null)

        onView(withId(R.id.edit_count)).check(matches(isDisplayed()))
            .perform(replaceText("12345"))

        onView(withId(R.id.edit_count)).check(matches(withText("12345")))

        // 取り敢えず再入力も
        onView(withId(R.id.edit_count)).check(matches(isDisplayed()))
            .perform(replaceText("4444"))

        onView(withId(R.id.edit_count)).check(matches(withText("4444")))
    }

    /**
     * ラジオグループの初期状態
     */
    @Test
    fun levelRadioGroup() {
        activity = activityRule.launchActivity(null)

        // 初期選択状態
        onView(withId(R.id.radio_normal)).check(matches(isDisplayed()))
            .check(matches(isChecked()))
        onView(withId(R.id.radio_good)).check(matches(isDisplayed()))
            .check(matches(not(isChecked())))
        onView(withId(R.id.radio_bad)).check(matches(isDisplayed()))
            .check(matches(not(isChecked())))
    }

    /**
     * ラジオボタン[GOOD]を押したときのテスト
     */
    @Test
    fun levelRadioButtonGood() {
        activity = activityRule.launchActivity(null)

        onView(withId(R.id.radio_good)).perform(click())

        // 選択状態
        onView(withId(R.id.radio_normal)).check(matches(isDisplayed()))
            .check(matches(not(isChecked())))
        onView(withId(R.id.radio_good)).check(matches(isDisplayed()))
            .check(matches(isChecked()))
        onView(withId(R.id.radio_bad)).check(matches(isDisplayed()))
            .check(matches(not(isChecked())))
    }

    /**
     * ラジオボタン[BAD]を押したときのテスト
     */
    @Test
    fun levelRadioButtonBad() {
        activity = activityRule.launchActivity(null)

        onView(withId(R.id.radio_bad)).perform(click())

        // 選択状態
        onView(withId(R.id.radio_normal)).check(matches(isDisplayed()))
            .check(matches(not(isChecked())))
        onView(withId(R.id.radio_good)).check(matches(isDisplayed()))
            .check(matches(not(isChecked())))
        onView(withId(R.id.radio_bad)).check(matches(isDisplayed()))
            .check(matches(isChecked()))
    }

    /**
     * スピナーを押したときのテスト
     */
    @Test
    fun weatherSpinner() {
        activity = activityRule.launchActivity(null)

        // 初期表示
        onView(withText("晴れ")).check(matches(isDisplayed()))

        // Robolectricはspinnerをクリックできないらしい
        val spinner = activity.findViewById<Spinner>(R.id.spinner_weather)

        // リスト表示を確認
        assertThat(spinner.getItemAtPosition(0)).isEqualTo("晴れ")
        assertThat(spinner.getItemAtPosition(1)).isEqualTo("雨")
        assertThat(spinner.getItemAtPosition(2)).isEqualTo("曇り")
        assertThat(spinner.getItemAtPosition(3)).isEqualTo("雪")
        assertThat(spinner.getItemAtPosition(4)).isEqualTo("寒い")
        assertThat(spinner.getItemAtPosition(5)).isEqualTo("暑い")

        // 初期値以外を選択
        spinner.setSelection(WEATHER.RAIN.ordinal)

        onView(withText("晴れ")).check(doesNotExist())
        onView(withText("雨")).check(matches(isDisplayed()))
    }

    /**
     * 登録ボタン押下のテスト:正常
     */
    @Test
    fun resistButton_success() {
        val scenario = ActivityScenario.launch(LogItemActivity::class.java)

        // Robolectricでは、ActivityRule#getActivityResultでresultが取れなかった
        // この方法なら取れたので、こちらにしてある。
        // 同じコードは逆に、androidTestでは動かない
        scenario.onActivity { activity ->

            onView(withId(R.id.button_resist)).check(matches(isDisplayed()))

            val today = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2019)
                set(Calendar.MONTH, 5)
                set(Calendar.DAY_OF_MONTH, 20)
            }
            activity.viewModel.dateSelected(today)

            onView(withId(R.id.edit_count)).check(matches(isDisplayed()))
                .perform(replaceText("12345"))
            onView(withId(R.id.radio_good)).perform(click())

            // Robolectricはspinnerをクリックできないらしい
            val spinner = activity.findViewById<Spinner>(R.id.spinner_weather)
            spinner.setSelection(WEATHER.CLOUD.ordinal)

            onView(withId(R.id.button_resist)).perform(click())

        }

        assertThat(scenario.result.resultCode).isEqualTo(Activity.RESULT_OK)
        assertThat(scenario.result.resultData).isNotNull()
        val data = scenario.result.resultData.getSerializableExtra(EXTRA_KEY_DATA)
        assertThat(data).isNotNull()
        assertThat(data is StepCountLog).isTrue()
        val expectItem = StepCountLog("2019/06/20", 12345, LEVEL.GOOD, WEATHER.CLOUD)
        assertThat(data).isEqualToComparingFieldByField(expectItem)

    }


    /**
     * 登録ボタン押下のテスト:未来日付エラー
     */
    @Test
    fun resistButton_error_futureDate() {
        activity = activityRule.launchActivity(null)

        val next = Calendar.getInstance().addDay(1)
        activity.viewModel.dateSelected(next)

        onView(withId(R.id.edit_count)).check(matches(isDisplayed()))
            .perform(replaceText("12345"))

        onView(withId(R.id.button_resist)).perform(click())

        // RobolectricはAlertDialogのビューを拾えない・・・
        val alert = ShadowAlertDialog.latestAlertDialog!!
        assertThat(alert.isShowing).isTrue()
        val shadowAlertDialog = shadowOfAlert(alert)
        assertThat(shadowAlertDialog.message).isEqualTo(getString(R.string.error_validation_future_date))
    }

    /**
     * 登録ボタン押下のテスト:カウント未入力エラー
     */
    @Test
    fun resistButton_error_emptyCount() {
        activity = activityRule.launchActivity(null)

        val today = Calendar.getInstance()
        activity.viewModel.dateSelected(today)

        onView(withId(R.id.button_resist)).perform(click())

        // RobolectricはAlertDialogのビューを拾えない・・・
        val alert = ShadowAlertDialog.latestAlertDialog!!
        assertThat(alert.isShowing).isTrue()
        val shadowAlertDialog = shadowOfAlert(alert)
        assertThat(shadowAlertDialog.message).isEqualTo(getString(R.string.error_validation_empty_count))
    }
}