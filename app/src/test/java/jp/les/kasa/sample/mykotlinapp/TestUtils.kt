package jp.les.kasa.sample.mykotlinapp

import android.content.DialogInterface
import android.graphics.drawable.StateListDrawable
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.view.LayoutInflater
import android.view.View
import android.widget.Adapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.annotation.RealObject
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadow.api.Shadow.directlyOn
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowListView
import org.robolectric.util.ReflectionHelpers
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


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


@Suppress("unused")
@Implements(AlertDialog::class)
open class ShadowAlertDialog : ShadowDialog() {

    @RealObject
    private lateinit var realAlertDialog: AlertDialog

    private val items: Array<CharSequence>? = null
    private val clickListener: DialogInterface.OnClickListener? = null
    private val isMultiItem: Boolean = false
    private val isSingleItem: Boolean = false
    private val multiChoiceClickListener: DialogInterface.OnMultiChoiceClickListener? = null

    private var custom: FrameLayout? = null

    val customView: FrameLayout
        get() = custom ?: FrameLayout(realAlertDialog.context).apply { custom = this }

    val adapter: Adapter?
        get() = shadowAlertController.adapter

    /**
     * @return the message displayed in the dialog
     */
    open val message: CharSequence
        get() = shadowAlertController.getMessage()

    /**
     * @return the view set with [AlertDialog.Builder.setView]
     */
    val view: View?
        get() = shadowAlertController.view

    /**
     * @return the icon set with [AlertDialog.Builder.setIcon]
     */
    val iconId: Int
        get() = shadowAlertController.iconId

    /**
     * @return return the view set with [AlertDialog.Builder.setCustomTitle]
     */
    val customTitleView: View?
        get() = shadowAlertController.customTitleView

    private val shadowAlertController: ShadowAlertController
        get() {
            val alertController = ReflectionHelpers.getField<Any>(realAlertDialog, "mAlert")
            return Shadow.extract<ShadowAlertController>(alertController)
        }

    /**
     * Simulates a click on the `Dialog` item indicated by `index`. Handles both multi- and single-choice dialogs, tracks which items are currently
     * checked and calls listeners appropriately.
     *
     * @param index the index of the item to click on
     */
    fun clickOnItem(index: Int) {
        val shadowListView = Shadow.extract<ShadowListView>(realAlertDialog.listView)
        shadowListView.performItemClick(index)
    }

    override fun getTitle(): CharSequence {
        return shadowAlertController.getTitle()
    }

    /**
     * @return the items that are available to be clicked on
     */
    fun getItems(): Array<CharSequence>? {
        val adapter = shadowAlertController.adapter ?: return null
        return Array(adapter.count) { adapter.getItem(it) as CharSequence }
    }

    public override fun show() {
        super.show()
        latestShadowAlertDialog = this
    }

    @Implements(AlertDialog.Builder::class)
    class ShadowBuilder

    companion object {

        private var latestShadowAlertDialog: ShadowAlertDialog? = null

        /**
         * @return the most recently created `AlertDialog`, or null if none has been created during this test run
         */
        val latestAlertDialog: AlertDialog?
            get() = latestShadowAlertDialog?.realAlertDialog

        /**
         * Resets the tracking of the most recently created `AlertDialog`
         */
        fun reset() {
            latestShadowAlertDialog = null
        }
    }
}

@Suppress("unused")
@Implements(className = ShadowAlertController.clazzName, isInAndroidSdk = false)
class ShadowAlertController {

    companion object {

        const val clazzName = "androidx.appcompat.app.AlertController"
    }

    @RealObject
    private lateinit var realAlertController: Any

    private var title: CharSequence? = null
    private var message: CharSequence? = null

    var view: View? = null
        @Implementation
        set(view) {
            field = view
            directlyOn<Any>(
                realAlertController,
                clazzName,
                "setView",
                ReflectionHelpers.ClassParameter(View::class.java, view)
            )
        }

    var customTitleView: View? = null
        private set

    var iconId: Int = 0
        private set

    val adapter: Adapter?
        get() = ReflectionHelpers.callInstanceMethod<ListView>(realAlertController, "getListView").adapter

    @Implementation
    @Throws(InvocationTargetException::class, IllegalAccessException::class)
    fun setTitle(title: CharSequence) {
        this.title = title
        directlyOn<Any>(
            realAlertController,
            clazzName,
            "setTitle",
            ReflectionHelpers.ClassParameter(CharSequence::class.java, title)
        )
    }

    fun getTitle(): CharSequence = title ?: ""

    @Implementation
    fun setCustomTitle(customTitleView: View) {
        this.customTitleView = customTitleView
        directlyOn<Any>(
            realAlertController,
            clazzName,
            "setCustomTitle",
            ReflectionHelpers.ClassParameter(View::class.java, customTitleView)
        )
    }

    @Implementation
    fun setMessage(message: CharSequence) {
        this.message = message
        directlyOn<Any>(
            realAlertController,
            clazzName,
            "setMessage",
            ReflectionHelpers.ClassParameter(CharSequence::class.java, message)
        )
    }

    fun getMessage(): CharSequence = message ?: ""

    @Implementation(minSdk = LOLLIPOP)
    fun setView(resourceId: Int) {
        view = LayoutInflater.from(ApplicationProvider.getApplicationContext()).inflate(resourceId, null)
    }

    @Implementation
    fun setIcon(iconId: Int) {
        this.iconId = iconId
        directlyOn<Any>(
            realAlertController,
            clazzName,
            "setIcon",
            ReflectionHelpers.ClassParameter(Int::class.java, iconId)
        )
    }
}

fun shadowOfAlert(dialog: AlertDialog): ShadowAlertDialog {
    return Shadow.extract<ShadowAlertDialog>(dialog)
}

fun <T> LiveData<T>.observeForTesting(block: () -> Unit) {
    val observer = Observer<T> { Unit }
    try {
        observeForever(observer)
        block()
    } finally {
        removeObserver(observer)
    }
}

class TestObserver<T>(count: Int = 1) : Observer<T> {

    private val latch: CountDownLatch = CountDownLatch(count)

    override fun onChanged(t: T?) {
        latch.countDown()
    }

    fun await(timeout: Long = 6, unit: TimeUnit = TimeUnit.SECONDS) {
        if (!latch.await(timeout, unit)) {
            throw TimeoutException()
        }
    }
}
