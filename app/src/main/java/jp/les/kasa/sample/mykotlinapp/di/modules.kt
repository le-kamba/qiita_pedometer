package jp.les.kasa.sample.mykotlinapp.di

import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemViewModel
import jp.les.kasa.sample.mykotlinapp.activity.main.MainActivity
import jp.les.kasa.sample.mykotlinapp.activity.main.MainViewModel
import jp.les.kasa.sample.mykotlinapp.activity.main.MonthlyPageViewModel
import jp.les.kasa.sample.mykotlinapp.activity.share.InstagramShareViewModel
import jp.les.kasa.sample.mykotlinapp.activity.signin.SignInActivity
import jp.les.kasa.sample.mykotlinapp.data.DATABASE_NAME
import jp.les.kasa.sample.mykotlinapp.data.LogRepository
import jp.les.kasa.sample.mykotlinapp.data.LogRoomDatabase
import jp.les.kasa.sample.mykotlinapp.data.SettingRepository
import jp.les.kasa.sample.mykotlinapp.utils.*
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

/**
 * Koin用モジュール群
 **/

// ViewModel
val viewModelModule = module {
    viewModel { MonthlyPageViewModel(androidApplication(), get()) }
    viewModel { LogItemViewModel(androidApplication(), get()) }
    viewModel { InstagramShareViewModel(androidApplication(), get()) }
}

// database,dao
val daoModule = module {
    single {
        Room.databaseBuilder(androidApplication(), LogRoomDatabase::class.java, DATABASE_NAME)
            .build()
    }
    factory { get<LogRoomDatabase>().logDao() }
}

// Repository
val repositoryModule = module {
    single { SettingRepository(androidApplication()) }
    single { LogRepository(get()) }
}

val providerModule = module {
    factory<CalendarProviderI> { CalendarProvider() }
    factory<EnvironmentProviderI> { EnvironmentProvider() }
}

// FirebaseService
val firebaseModule = module {
    single<AnalyticsUtilI> { AnalyticsUtil(androidApplication()) }
    single<AuthProviderI> { AuthProvider(androidApplication()) }
}

// scopedモジュール群
val scopeModules = module {
    scope<MainActivity> {
        viewModel { MainViewModel(androidApplication(), get(), get()) }
        scoped { get<AppCompatActivity>().activityResultRegistry }
    }
    scope<SignInActivity> {
        scoped { get<AppCompatActivity>().activityResultRegistry }
    }
}

// モジュール群
val appModules = listOf(
    viewModelModule, daoModule, repositoryModule, providerModule, firebaseModule, scopeModules
)


// カレンダークラスで現在日付を持つInstance取得を提供するプロバイダ
interface CalendarProviderI {
    val now: Calendar
}

class CalendarProvider : CalendarProviderI {
    override val now: Calendar
        get() = Calendar.getInstance().clearTime()
}

// Environmentチェックを提供するプロバイダ
interface EnvironmentProviderI {
    fun isExternalStorageMounted(): Boolean
}

class EnvironmentProvider : EnvironmentProviderI {
    override fun isExternalStorageMounted(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
}