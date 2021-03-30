package jp.les.kasa.sample.mykotlinapp.di

import android.os.Bundle
import androidx.room.Room
import jp.les.kasa.sample.mykotlinapp.data.LogRoomDatabase
import jp.les.kasa.sample.mykotlinapp.utils.AnalyticsUtilI
import jp.les.kasa.sample.mykotlinapp.utils.clearTime
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.util.*

// テスト用にモックするモジュール
val mockModule = module {
    single(override = true) {
        Room.inMemoryDatabaseBuilder(
            androidApplication(),
            LogRoomDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    single(override = true) {
        MockCalendarProvider() as CalendarProviderI
    }

    single(override = true) {
        MockEnvironmentProvider() as EnvironmentProviderI
    }

    single(override = true) {
        MockAnalyticsUtil() as AnalyticsUtilI
    }
}

// カレンダークラスで現在日付を持つInstance取得を提供するプロバイダのテスト用
class MockCalendarProvider : CalendarProviderI {
    override val now: Calendar
        get() {
            val cal = Calendar.getInstance().clearTime()
            cal.set(Calendar.YEAR, 2019)
            cal.set(Calendar.MONTH, 6 - 1) // 月は0 based index
            cal.set(Calendar.DATE, 28)
            return cal
        }
}

// EnvironmentProviderのモッククラス
class MockEnvironmentProvider : EnvironmentProviderI {
    override fun isExternalStorageMounted(): Boolean = true
}

// AnalyticsUtilのモッククラス
class MockAnalyticsUtil : AnalyticsUtilI() {
    override fun sendScreenName(
        screenName: String,
        classOverrideName: String?
    ) {
    }

    override fun logEvent(eventName: String, bundle: Bundle?) {
    }

    override fun setUserProperty(propertyName: String, value: String) {
    }

    override fun setUserId(userId: String?) {
    }
}