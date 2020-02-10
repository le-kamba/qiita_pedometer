package jp.les.kasa.sample.mykotlinapp.activity.main

import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.les.kasa.sample.mykotlinapp.di.testMockModule
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.AutoCloseKoinTest

@RunWith(AndroidJUnit4::class)
class MonthlyPageFragmentTestI : AutoCloseKoinTest() {

    @Before
    fun setUp() {
        loadKoinModules(testMockModule)
//        activityRule.launchActivity(Intent())
    }

    @After
    fun tearDown() {
//        activityRule.finishActivity()
    }

}