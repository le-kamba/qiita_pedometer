package jp.les.kasa.sample.mykotlinapp.base

import jp.les.kasa.sample.mykotlinapp.utils.AnalyticsUtilI
import org.koin.androidx.scope.ScopeActivity

abstract class ScopeBaseActivity : ScopeActivity() {

    abstract val screenName: String

    // AnalyticsTool inject by Koin
    val analyticsUtil: AnalyticsUtilI by inject()

    override fun onResume() {
        super.onResume()
        analyticsUtil.sendScreenName(screenName)
    }
}
