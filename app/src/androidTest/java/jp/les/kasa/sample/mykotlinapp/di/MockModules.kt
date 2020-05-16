package jp.les.kasa.sample.mykotlinapp.di

import android.app.Application
import androidx.room.Room
import com.google.firebase.auth.FirebaseUser
import jp.les.kasa.sample.mykotlinapp.data.LogRoomDatabase
import jp.les.kasa.sample.mykotlinapp.data.LoginUserData
import jp.les.kasa.sample.mykotlinapp.utils.AuthProviderI
import jp.les.kasa.sample.mykotlinapp.utils.clearTime
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

    single(override = true) {
        TestAuthProvider(androidApplication())
    }
}


// カレンダークラスで現在日付を持つInstance取得を提供するプロバイダのテスト用
class TestCalendarProvider : CalendarProviderI {
    override val now: Calendar
        get() {
            val cal = Calendar.getInstance().clearTime()
            cal.set(Calendar.YEAR, 2019)
            cal.set(Calendar.MONTH, 6 - 1) // 月は0 based index
            cal.set(Calendar.DATE, 28)
            return cal
        }
}

// FirebaseAuthを提供するプロバイダのテスト用
class TestAuthProvider(app: Application) : AuthProviderI(app) {
    override val user: FirebaseUser? = null

    override val userData: LoginUserData
        get() {
            return LoginUserData(
                "ユーザー名",
                "foo@bar.com"
            )
        }
}