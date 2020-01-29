package jp.les.kasa.sample.mykotlinapp.di

import androidx.room.Room
import jp.les.kasa.sample.mykotlinapp.data.LogRoomDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

// テスト用にモックするモジュール
val mockModule = module {
    single(override = true) {
        Room.inMemoryDatabaseBuilder(
            androidApplication(),
            LogRoomDatabase::class.java
        ).allowMainThreadQueries().build()
    }
}
