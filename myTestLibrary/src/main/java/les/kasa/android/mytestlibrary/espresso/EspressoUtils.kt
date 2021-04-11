package jp.les.kasa.sample.mykotlinapp.espresso

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.viewpager2.widget.ViewPager2
import org.hamcrest.Description
import org.hamcrest.Matcher


fun atPositionOnView(
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

object RecyclerViewMatchers {
    fun hasItemCount(itemCount: Int): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(
            RecyclerView::class.java
        ) {

            override fun describeTo(description: Description) {
                description.appendText("has $itemCount items")
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                return view.adapter!!.itemCount == itemCount
            }
        }
    }
}

object ViewPagerMatchers {
    fun hasItemCount(itemCount: Int): Matcher<View> {
        return object : BoundedMatcher<View, ViewPager2>(
            ViewPager2::class.java
        ) {

            override fun describeTo(description: Description) {
                description.appendText("has $itemCount items")
            }

            override fun matchesSafely(view: ViewPager2): Boolean {
                return view.adapter!!.itemCount == itemCount
            }
        }
    }

    fun isCurrent(index: Int): Matcher<View> {
        return object : BoundedMatcher<View, ViewPager2>(
            ViewPager2::class.java
        ) {

            override fun describeTo(description: Description) {
                description.appendText("is $index index is current")
            }

            override fun matchesSafely(view: ViewPager2): Boolean {
                return view.currentItem == index
            }
        }
    }
}
