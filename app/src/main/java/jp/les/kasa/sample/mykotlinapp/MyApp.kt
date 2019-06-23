package jp.les.kasa.sample.mykotlinapp

import android.app.Application

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        copyFiles(this)
    }
}