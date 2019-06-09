package jp.les.kasa.sample.mykotlinapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
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
        onView(withText("12345")).check(matches(isDisplayed()))
    }

    @Test
    fun addRecordMenuIcon() {
        Espresso.pressBack()

        onView(
            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
        ).check(matches(isDisplayed()))
    }

    @Test
    fun addRecordMenu() {
        Espresso.pressBack()

        onView(
            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
        ).perform(click())

        onView(withText(R.string.label_input_title)).check(matches(isDisplayed()))
    }

    @Test
    fun addRecordList() {
        onView(withId(R.id.editStep)).perform(replaceText("12345"))
        onView(withText(R.string.resist)).perform(click())

        onView(
            Matchers.allOf(withId(R.id.add_record), withContentDescription("記録を追加"))
        ).perform(click())
        onView(withId(R.id.editStep)).perform(replaceText("666"))
        onView(withText(R.string.resist)).perform(click())

        // リストの表示確認
        var index = 0
        onView(withId(R.id.log_list))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(
                matches(
                    atPositionOnView(
                        index, withText("12345"), R.id.stepTextView
                    )
                )
            )

        index = 1
        onView(withId(R.id.log_list))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            .check(
                matches(
                    atPositionOnView(
                        index, withText("666"), R.id.stepTextView
                    )
                )
            )
    }

    private fun atPositionOnView(
        position: Int, itemMatcher: Matcher<View>, targetViewId: Int
    ): Matcher<View> {

        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has view id $itemMatcher at position $position")
            }

            override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                val targetView = viewHolder!!.itemView.findViewById<View>(targetViewId)
                return itemMatcher.matches(targetView)
            }
        }
    }
}
