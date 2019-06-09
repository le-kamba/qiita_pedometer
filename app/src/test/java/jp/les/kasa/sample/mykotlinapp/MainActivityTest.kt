package jp.les.kasa.sample.mykotlinapp

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import kotlinx.android.synthetic.main.dialog_input.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowAlertDialog

/**
 * @date 2019/06/05
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    private val context = InstrumentationRegistry.getInstrumentation().targetContext!!

    @Test
    fun helloWorld() {
        onView(withText("Hello World!")).check(matches(isDisplayed()))
    }


    private fun getString(resId: Int) = context.applicationContext.getString(resId)

    @Test
    fun inputDialogFragmentShown() {
        // Robolectricのバグか、DialogのテストはEspressoで行えない
        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        assertThat(dialog).isNotNull()

        assertThat(dialog.editStep).isNotNull()
        assertThat(dialog.label_Title.text).isEqualTo(getString(R.string.label_input_title))
        assertThat(dialog.label_step.text).isEqualTo(getString(R.string.label_step))

        val negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        assertThat(negative.text).isEqualTo(getString(android.R.string.cancel))
        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        assertThat(positive.text).isEqualTo(getString(R.string.resist))
    }

    @Test
    fun inputStep() {
        // Robolectricのバグか、DialogのテストはEspressoで行えない
        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        assertThat(dialog.isShowing).isTrue()

        dialog.editStep.setText("12345")
        val positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positive.performClick()

        assertThat(dialog.isShowing).isFalse()

        // Dialogが消えた後なのでEspressoでテスト可
        onView(ViewMatchers.withId(R.id.textView)).check(matches(withText("12345")))
    }
}