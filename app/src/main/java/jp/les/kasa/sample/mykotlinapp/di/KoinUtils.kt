package jp.les.kasa.sample.mykotlinapp.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("EXPERIMENTAL_API_USAGE")
inline fun <reified T> byKoinInject(): T {
    return object : KoinComponent {
        val value: T by inject()
    }.value
}
