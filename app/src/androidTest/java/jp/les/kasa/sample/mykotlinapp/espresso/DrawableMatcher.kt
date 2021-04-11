package jp.les.kasa.sample.mykotlinapp.espresso

import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


class DrawableMatcher(private val expectedId: Int) : TypeSafeMatcher<View>(View::class.java) {
    private var resourceName: String? = null

    override fun matchesSafely(target: View): Boolean {
        if (target is ImageView) {
            if (expectedId < 0) {
                return target.drawable == null
            }
            val resources = target.getContext().resources
            val expectedDrawable = resources.getDrawable(expectedId, null)
            resourceName = resources.getResourceEntryName(expectedId)

            if (expectedDrawable == null) {
                return false
            }

            var drawable = target.drawable
            if (drawable is StateListDrawable) {
                drawable = drawable.getCurrent()
            }

            val bitmap = drawable.toBitmap()
            val otherBitmap = expectedDrawable.toBitmap()
            return bitmap.sameAs(otherBitmap)
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
