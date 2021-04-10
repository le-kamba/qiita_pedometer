package jp.les.kasa.sample.mykotlinapp.alert

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers
import jp.les.kasa.sample.mykotlinapp.R
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

// 本体アプリではConfirmDialogがFragmentから参照されていないので、テスト用に作る
class SampleFragment : Fragment(), ConfirmDialog.ConfirmEventListener {
    fun showConfirm() {
        val dialog: ConfirmDialog = ConfirmDialog.Builder()
            .target(this).requestCode(100)
            .message("てすと").create()
        dialog.show(requireFragmentManager(), "tag")
    }

    var confirmResult: Boolean? = null

    override fun onConfirmResult(which: Int, bundle: Bundle?, requestCode: Int) {
        confirmResult = when (which) {
            DialogInterface.BUTTON_POSITIVE -> true
            else -> false
        }
    }
}

class ConfirmDialogTest {
    private lateinit var fragment: SampleFragment

    @Test
    fun showFromFragment() {
        val scenario = launchFragmentInContainer<SampleFragment>(themeResId = R.style.AppTheme)
        scenario.onFragment {
            fragment = it
            it.showConfirm()
        }

        // Dialogが表示されている？
        Espresso.onView(ViewMatchers.withText("てすと"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.label_yes))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.label_no))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        assertThat(fragment.confirmResult).isNull()
    }

    @Test
    fun cancelDialog() {
        val scenario = launchFragmentInContainer<SampleFragment>(themeResId = R.style.AppTheme)
        scenario.onFragment {
            fragment = it
            it.showConfirm()
        }

        Espresso.onView(ViewMatchers.withText(R.string.label_no))
            .perform(click())

        Espresso.onView(ViewMatchers.withText("てすと"))
            .check(doesNotExist())

        assertThat(fragment.confirmResult).isEqualTo(false)
    }

    @Test
    fun confirmDialog() {
        val scenario = launchFragmentInContainer<SampleFragment>(themeResId = R.style.AppTheme)
        scenario.onFragment {
            fragment = it
            it.showConfirm()
        }

        Espresso.onView(ViewMatchers.withText(R.string.label_yes))
            .perform(click())

        Espresso.onView(ViewMatchers.withText("てすと"))
            .check(doesNotExist())

        assertThat(fragment.confirmResult).isEqualTo(true)
    }
}