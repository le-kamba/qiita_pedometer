package jp.les.kasa.sample.mykotlinapp.robolectric

import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.ImageView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.robolectric.Shadows

class DrawableMatcher(private val expectedId: Int) : TypeSafeMatcher<View>(View::class.java) {
    private var resourceName: String? = null

    override fun matchesSafely(target: View): Boolean {
        if (target is ImageView) {
            if (expectedId < 0) {
                return target.drawable == null
            }

            var drawable = target.drawable
            if (drawable is StateListDrawable) {
                drawable = drawable.getCurrent()
            }

            return Shadows.shadowOf(drawable).createdFromResId == expectedId
        } else {
            if (expectedId < 0) {
                return target.background == null
            }

            var drawable = target.background
            if (drawable is StateListDrawable) {
                drawable = drawable.getCurrent()
            }

            return Shadows.shadowOf(drawable).createdFromResId == expectedId
        }
        return false
    }


    override fun describeTo(description: Description) {
        description.appendText("with drawable from resource id: ")
        description.appendValue(expectedId)
        if (resourceName != null) {
            description.appendText("[")
            description.appendText(resourceName)
            description.appendText("]")
        }
    }
}

fun withDrawable(resourceId: Int): Matcher<View> {
    return DrawableMatcher(resourceId)
}
