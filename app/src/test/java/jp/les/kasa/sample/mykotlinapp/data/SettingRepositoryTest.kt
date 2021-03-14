package jp.les.kasa.sample.mykotlinapp.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class SettingRepositoryTest : AutoCloseKoinTest() {
    private val repository: SettingRepository by inject()

    @Before
    fun setUp() {
//        repository = SettingRepository.getInstance(ApplicationProvider.getApplicationContext<Context>())
        repository.clear()
    }

    @After
    fun tearDown() {
        repository.clear()
    }

    @Test
    fun saveReadShareStatus() {
        val defaultData = repository.readShareStatus()
        assertThat(defaultData).isEqualToComparingFieldByField(ShareStatus())

        val newData = ShareStatus(true, true, false)
        repository.saveShareStatus(newData)
        val getData = repository.readShareStatus()
        assertThat(getData).isEqualToComparingFieldByField(newData)
    }

    @Test
    fun clear() {
        val newData = ShareStatus(true, true, false)
        repository.saveShareStatus(newData)

        repository.clear()
        val getData = repository.readShareStatus()
        assertThat(getData).isEqualToComparingFieldByField(ShareStatus())
    }
}