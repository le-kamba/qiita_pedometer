package jp.les.kasa.sample.mykotlinapp

import androidx.multidex.MultiDexApplication
import com.google.firebase.crashlytics.FirebaseCrashlytics
import jp.les.kasa.sample.mykotlinapp.data.SettingRepository
import jp.les.kasa.sample.mykotlinapp.di.appModules
import jp.les.kasa.sample.mykotlinapp.utils.AnalyticsUtil
import jp.les.kasa.sample.mykotlinapp.utils.uniqueUserId
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger
import org.koin.core.logger.Level

class MyApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) androidLogger(Level.DEBUG) else EmptyLogger()
            androidContext(this@MyApp)
            modules(appModules)
        }

        val analyticsUtil: AnalyticsUtil by inject()
        val settingRepository: SettingRepository by inject()
        // 一度だけUserIdを作成する
        val userId = settingRepository.readUserId() ?: uniqueUserId()
        analyticsUtil.setUserId(userId)
        FirebaseCrashlytics.getInstance().setUserId(userId)
        settingRepository.saveUserId(userId)
    }
}