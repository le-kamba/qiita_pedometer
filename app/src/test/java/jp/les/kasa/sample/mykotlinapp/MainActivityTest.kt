package jp.les.kasa.sample.mykotlinapp

import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import kotlinx.android.synthetic.main.dialog_input.*
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
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
        ).perform(ViewActions.click())

        // Robolectricのバグか、DialogのテストはEspressoで行えない
        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        assertThat(dialog.isShowing).isTrue()
        assertThat(dialog.editStep).isNotNull()
        assertThat(dialog.label_Title.text).isEqualTo(getString(R.string.label_input_title))
    }

    @Test
    fun addRecordList() {
        Espresso.pressBack()

        val mainActivity = activityRule.activity

        mainActivity.viewModel.addStepCount(12345)
        mainActivity.viewModel.addStepCount(666)

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