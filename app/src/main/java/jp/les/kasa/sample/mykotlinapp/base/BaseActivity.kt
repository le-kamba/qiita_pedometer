package jp.les.kasa.sample.mykotlinapp.base

import androidx.appcompat.app.AppCompatActivity
import jp.les.kasa.sample.mykotlinapp.utils.AnalyticsUtilI
import org.koin.android.ext.android.inject

// Analytics送信を基底クラスに持たせる場合のサンプル
abstract class BaseActivity : AppCompatActivity() {

    abstract val screenName: String

    // AnalyticsTool inject by Koin
    val analyticsUtil: AnalyticsUtilI by inject()

    override fun onResume() {
        super.onResume()
        analyticsUtil.sendScreenName(screenName)
    }
}