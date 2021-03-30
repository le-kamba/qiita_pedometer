package jp.les.kasa.sample.mykotlinapp.activity

import androidx.annotation.CallSuper
import jp.les.kasa.sample.mykotlinapp.di.testMockModule
import jp.les.kasa.sample.mykotlinapp.utils.AnalyticsUtil
import org.junit.Before
import org.koin.core.context.loadKoinModules
import org.koin.core.inject
import org.koin.test.AutoCloseKoinTest

open class ActivityTestBase : AutoCloseKoinTest() {

    @Before
    @CallSuper
    open fun setUp() {
        loadKoinModules(testMockModule)
        val analyticsUtil: AnalyticsUtil by inject()
        analyticsUtil.disable()
    }
}