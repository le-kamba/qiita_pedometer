package jp.les.kasa.sample.mykotlinapp.di

import jp.les.kasa.sample.mykotlinapp.MainViewModel
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemViewModel
import jp.les.kasa.sample.mykotlinapp.activity.share.InstagramShareViewModel
import jp.les.kasa.sample.mykotlinapp.data.SettingRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin用モジュール群
 **/

// ViewModel
val viewModelModule = module {
    viewModel { MainViewModel(androidApplication()) }
    viewModel { LogItemViewModel(androidApplication(), get()) }
    viewModel { InstagramShareViewModel() }
}

// Repository
val repositoryModule = module {
    single { SettingRepository(androidApplication()) }
}

// モジュール群
val appModules = listOf(viewModelModule, repositoryModule)
