package jp.les.kasa.sample.mykotlinapp.activity.logitem

import android.app.Activity
import android.content.Intent
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
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
import jp.les.kasa.sample.mykotlinapp.espresso.withDrawable
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class LogItemActivityTestI {
    @get:Rule
    val activityRule = ActivityTestRule(LogItemActivity::class.java, false, false)

    lateinit var activity: LogItemActivity

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

        // OKボタンのクリックはEspressoで書ける
        onView(withText(android.R.string.cancel)).perform(click())

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

        // OKボタンのクリックはEspressoで書ける
        onView(withText(android.R.string.ok)).perform(click())

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

        onView(withId(R.id.spinner_weather)).perform(click())

        // リスト表示を確認
        onView(withText("晴れ")).check(matches(isDisplayed()))
        onView(withText("雨")).check(matches(isDisplayed()))
        onView(withText("曇り")).check(matches(isDisplayed()))
        onView(withText("雪")).check(matches(isDisplayed()))
        onView(withText("寒い")).check(matches(isDisplayed()))
        onView(withText("暑い")).check(matches(isDisplayed()))

        // 初期値以外を選択
        onView(withText("雨")).perform(click())

        onView(withText("晴れ")).check(doesNotExist())
        onView(withText("雨")).check(matches(isDisplayed()))
    }

    /**
     * 登録ボタン押下のテスト:正常
     */
    @Test
    fun resistButton_success() {
        activity = activityRule.launchActivity(null)

        val today = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2019)
            set(Calendar.MONTH, 5)
            set(Calendar.DAY_OF_MONTH, 20)
        }
        activity.runOnUiThread {
            activity.viewModel.dateSelected(today)
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(withId(R.id.edit_count)).check(matches(isDisplayed()))
            .perform(replaceText("12345"))
        onView(withId(R.id.radio_good)).perform(click())

        onView(withId(R.id.spinner_weather)).perform(click())
        onView(withText("曇り")).perform(click())

        onView(withId(R.id.button_update)).check(matches(isDisplayed()))
            .perform(click())

        assertThat(activityRule.activityResult.resultCode).isEqualTo(Activity.RESULT_OK)
        assertThat(activityRule.activityResult.resultData).isNotNull()
        val data = activityRule.activityResult.resultData.getSerializableExtra(LogItemActivity.EXTRA_KEY_DATA)
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
        activity.runOnUiThread {
            activity.viewModel.dateSelected(next)
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(withId(R.id.edit_count)).check(matches(isDisplayed()))
            .perform(replaceText("12345"))

        onView(withId(R.id.button_update)).check(matches(isDisplayed()))
            .perform(click())

        onView(withText(R.string.error_validation_future_date)).check(matches(isDisplayed()))
    }

    /**
     * 登録ボタン押下のテスト:カウント未入力エラー
     */
    @Test
    fun resistButton_error_emptyCount() {
        activity = activityRule.launchActivity(null)

        val today = Calendar.getInstance()
        activity.runOnUiThread {
            activity.viewModel.dateSelected(today)
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(withId(R.id.button_update)).check(matches(isDisplayed()))
            .perform(click())

        onView(withText(R.string.error_validation_empty_count)).check(matches(isDisplayed()))
    }

    /**
     *   LogEditFragmentの初期表示をチェックする
     */
    @Test
    fun logEditFragment() {
        // データをセットしてから起動
        val intent = Intent().apply {
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
        }
        activity = activityRule.launchActivity(intent)

        // 日時ラベル
        onView(withText(R.string.label_date)).check(matches(isDisplayed()))
        // 日付
        onView(withText("2019/06/22")).check(matches(isDisplayed()))
        // 日付選択ボタン(非表示)
        onView(withText(R.string.label_select_date)).check(doesNotExist())
        // 歩数ラベル
        onView(withText(R.string.label_step_count)).check(matches(isDisplayed()))
        // 歩数
        onView(withText("456")).check(matches(isDisplayed()))
        // 気分ラベル
        onView(withText(R.string.label_level)).check(matches(isDisplayed()))
        // 気分ラジオボタン
        onView(withText(R.string.level_normal)).check(matches(isDisplayed()))
            .check(matches(not(isChecked())))
        onView(withText(R.string.level_good)).check(matches(isDisplayed()))
        onView(withText(R.string.level_bad)).check(matches(isDisplayed()))
            .check(matches(isChecked()))
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
        onView(withText("暑い")).check(matches(isDisplayed()))
        // 登録ボタン
        onView(withText(R.string.update)).check(matches(isDisplayed()))
        // 削除ボタン
        onView(withText(R.string.delete)).check(matches(isDisplayed()))
    }

    /**
     * 編集画面：ラジオボタン[GOOD]を押したときのテスト
     */
    @Test
    fun logEdit_levelRadioButtonGood() {
        // データをセットしてから起動
        val intent = Intent().apply {
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
        }
        activity = activityRule.launchActivity(intent)

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
     * 編集画面：ラジオボタン[NORMAL]を押したときのテスト
     */
    @Test
    fun logEdit_levelRadioButtonNormal() {
        // データをセットしてから起動
        val intent = Intent().apply {
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
        }
        activity = activityRule.launchActivity(intent)


        onView(withId(R.id.radio_normal)).perform(click())

        // 選択状態
        onView(withId(R.id.radio_bad)).check(matches(isDisplayed()))
            .check(matches(not(isChecked())))
        onView(withId(R.id.radio_good)).check(matches(isDisplayed()))
            .check(matches(not(isChecked())))
        onView(withId(R.id.radio_normal)).check(matches(isDisplayed()))
            .check(matches(isChecked()))
    }

    /**
     * 編集画面：スピナーを押したときのテスト
     */
    @Test
    fun logEdit_weatherSpinner() {
        // データをセットしてから起動
        val intent = Intent().apply {
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
        }
        activity = activityRule.launchActivity(intent)

        // 初期表示
        onView(withText("暑い")).check(matches(isDisplayed()))

        onView(withId(R.id.spinner_weather)).perform(click())

        // リスト表示を確認
        onView(withText("晴れ")).check(matches(isDisplayed()))
        onView(withText("雨")).check(matches(isDisplayed()))
        onView(withText("曇り")).check(matches(isDisplayed()))
        onView(withText("雪")).check(matches(isDisplayed()))
        onView(withText("寒い")).check(matches(isDisplayed()))
        onView(withText("暑い")).check(matches(isDisplayed()))

        // 初期値以外を選択
        onView(withText("雨")).perform(click())

        onView(withText("暑い")).check(doesNotExist())
        onView(withText("雨")).check(matches(isDisplayed()))
    }

    /**
     * 編集画面：更新ボタン押下のテスト:正常
     */
    @Test
    fun updateButton_success() {
        // データをセットしてから起動
        val intent = Intent().apply {
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
        }
        activity = activityRule.launchActivity(intent)

        onView(withId(R.id.edit_count)).check(matches(isDisplayed()))
            .perform(replaceText("12345"))
        onView(withId(R.id.radio_good)).perform(click())

        onView(withId(R.id.spinner_weather)).perform(click())
        onView(withText("曇り")).perform(click())

        onView(withId(R.id.button_update)).check(matches(isDisplayed()))
            .perform(click())

        assertThat(activityRule.activityResult.resultCode).isEqualTo(Activity.RESULT_OK)
        assertThat(activityRule.activityResult.resultData).isNotNull()
        val data = activityRule.activityResult.resultData.getSerializableExtra(LogItemActivity.EXTRA_KEY_DATA)
        assertThat(data).isNotNull()
        assertThat(data is StepCountLog).isTrue()
        val expectItem = StepCountLog("2019/06/22", 12345, LEVEL.GOOD, WEATHER.CLOUD)
        assertThat(data).isEqualToComparingFieldByField(expectItem)
    }

    /**
     * 編集画面：更新ボタン押下のテスト:カウント未入力エラー
     */
    @Test
    fun updateButton_error_emptyCount() {
        // データをセットしてから起動
        val intent = Intent().apply {
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
        }
        activity = activityRule.launchActivity(intent)

        onView(withId(R.id.edit_count)).perform(replaceText(""))

        onView(withId(R.id.button_update)).check(matches(isDisplayed()))
            .perform(click())

        onView(withText(R.string.error_validation_empty_count)).check(matches(isDisplayed()))
    }

    /**
     * 編集画面：削除ボタン押下のテスト:カウント未入力エラー
     */
    @Test
    fun deleteButton() {
        // データをセットしてから起動
        val intent = Intent().apply {
            putExtra(LogItemActivity.EXTRA_KEY_DATA, StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT))
        }
        activity = activityRule.launchActivity(intent)

        onView(withId(R.id.button_delete)).check(matches(isDisplayed()))
            .perform(click())

        // 削除を戻すIntentの確認
        assertThat(activityRule.activityResult.resultCode).isEqualTo(MainActivity.RESULT_CODE_DELETE)
        assertThat(activityRule.activityResult.resultData).isNotNull()
        val data = activityRule.activityResult.resultData.getSerializableExtra(LogItemActivity.EXTRA_KEY_DATA)
        assertThat(data).isNotNull()
        assertThat(data is StepCountLog).isTrue()
        val expectItem = StepCountLog("2019/06/22", 456, LEVEL.BAD, WEATHER.HOT)
        assertThat(data).isEqualToComparingFieldByField(expectItem)
    }
}
