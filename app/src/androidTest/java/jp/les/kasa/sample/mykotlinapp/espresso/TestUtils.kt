package jp.les.kasa.sample.mykotlinapp.espresso

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun <T> LiveData<T>.observeForTesting(block: () -> Unit) {
    val observer = Observer<T> { Unit }
    try {
        observeForever(observer)
        block()
    } finally {
        removeObserver(observer)
    }
}

class TestObserver<T>(count: Int = 1) : Observer<T> {

    private val latch: CountDownLatch = CountDownLatch(count)

    override fun onChanged(t: T?) {
        latch.countDown()
    }

    fun await(timeout: Long = 6, unit: TimeUnit = TimeUnit.SECONDS) {
        if (!latch.await(timeout, unit)) {
            throw TimeoutException()
        }
    }
}

