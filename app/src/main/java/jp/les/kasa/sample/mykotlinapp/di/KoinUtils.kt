package jp.les.kasa.sample.mykotlinapp.di

import org.koin.core.KoinComponent
import org.koin.core.inject


inline fun <reified T> byKoinInject(): T {
    return object : KoinComponent {
        val value: T by inject()
    }.value
}
