package jp.les.kasa.sample.mykotlinapp.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
class SampleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = TextView(context)
        view.text = "HELLO"
        return view
    }

    fun showConfirm() {
        val dialog: ConfirmDialog = ConfirmDialog.Builder()
            .message("てすと").create()
        dialog.show(
            this,
            onPositive = { confirmResult = true },
            onNegative = { confirmResult = false })
    }

    var confirmResult: Boolean? = null
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