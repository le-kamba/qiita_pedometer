package jp.les.kasa.sample.mykotlinapp.di

import androidx.room.Room
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemViewModel
import jp.les.kasa.sample.mykotlinapp.activity.main.MainViewModel
import jp.les.kasa.sample.mykotlinapp.activity.main.MonthlyPageViewModel
import jp.les.kasa.sample.mykotlinapp.activity.share.InstagramShareViewModel
import jp.les.kasa.sample.mykotlinapp.activity.signin.SignOutViewModel
import jp.les.kasa.sample.mykotlinapp.data.DATABASE_NAME
import jp.les.kasa.sample.mykotlinapp.data.LogRepository
import jp.les.kasa.sample.mykotlinapp.data.LogRoomDatabase
import jp.les.kasa.sample.mykotlinapp.data.SettingRepository
import jp.les.kasa.sample.mykotlinapp.utils.AnalyticsUtil
import jp.les.kasa.sample.mykotlinapp.utils.clearTime
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

/**
 * Koin用モジュール群
 **/

// ViewModel
val viewModelModule = module {
    viewModel { MainViewModel(androidApplication(), get(), get()) }
    viewModel { MonthlyPageViewModel(androidApplication(), get()) }
    viewModel { LogItemViewModel(androidApplication(), get()) }
    viewModel { InstagramShareViewModel() }
    viewModel { SignOutViewModel(androidApplication()) }
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
    factory { CalendarProvider() as CalendarProviderI }
}

// FirebaseService
val firebaseModule = module {
    single { AnalyticsUtil(androidApplication()) }
}

// モジュール群
val appModules = listOf(
    viewModelModule
    , daoModule
    , repositoryModule
    , providerModule
    , firebaseModule
)


// カレンダークラスで現在日付を持つInstance取得を提供するプロバイダ
interface CalendarProviderI {
    val now: Calendar
}

class CalendarProvider : CalendarProviderI {
    override val now: Calendar
        get() = Calendar.getInstance().clearTime()
}
