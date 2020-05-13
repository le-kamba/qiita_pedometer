package jp.les.kasa.sample.mykotlinapp.base

import androidx.fragment.app.Fragment
import jp.les.kasa.sample.mykotlinapp.utils.AnalyticsUtil
import org.koin.android.ext.android.inject

abstract class BaseFragment : Fragment() {

    abstract val screenName: String

    // AnalyticsTool inject by Koin
    val analyticsUtil: AnalyticsUtil by inject()

    override fun onResume() {
        super.onResume()
        activity?.let { analyticsUtil.sendScreenName(it, screenName) }
    }

}