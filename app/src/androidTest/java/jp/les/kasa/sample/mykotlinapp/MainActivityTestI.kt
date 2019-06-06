package jp.les.kasa.sample.mykotlinapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @date 2019/06/05
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTestI {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun inputDialogFragmentShown() {
        onView(withText(R.string.label_input_title)).check(matches(isDisplayed()))
        onView(withText(R.string.label_step)).check(matches(isDisplayed()))
        onView(withId(R.id.editStep)).check(matches(isDisplayed()))
        onView(withText(R.string.resist)).check(matches(isDisplayed()))
        onView(withText(android.R.string.cancel)).check(matches(isDisplayed()))
    }

    @Test
    fun inputStep() {
        onView(withId(R.id.editStep)).perform(replaceText("12345"))
        onView(withText(R.string.resist)).perform(click())

        onView(withText(R.string.label_input_title)).check(doesNotExist())
        onView(withId(R.id.textView)).check(matches(withText("12345")))
    }
}