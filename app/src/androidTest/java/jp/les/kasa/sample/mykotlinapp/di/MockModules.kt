package jp.les.kasa.sample.mykotlinapp.di

import androidx.room.Room
import jp.les.kasa.sample.mykotlinapp.clearTime
import jp.les.kasa.sample.mykotlinapp.data.LogRoomDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.util.*

// テスト用にモックするモジュール
val testMockModule = module {
    single(override = true) {
        Room.inMemoryDatabaseBuilder(
            androidApplication(),
            LogRoomDatabase::class.java
        ).build()
    }

    single(override = true) {
        TestCalendarProvider() as CalendarProviderI
    }
}


// カレンダークラスで現在日付を持つInstance取得を提供するプロバイダのテスト用
class TestCalendarProvider : CalendarProviderI {
    override val now: Calendar
        get() {
            val cal = Calendar.getInstance().clearTime()
            cal.set(Calendar.YEAR, 2019)
            cal.set(Calendar.MONTH, 6 - 1) // 月は0base index
            return cal
        }
}
