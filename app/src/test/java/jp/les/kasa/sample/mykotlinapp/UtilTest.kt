package jp.les.kasa.sample.mykotlinapp

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * @date 2019/06/05
 */
class UtilTest {

    @Test
    fun getVersionCode() {
        val versionCode = Util.getVersionCode()
        assertThat(versionCode).isEqualTo(1)
    }

    @Test
    fun getVersionName() {
        val versionName = Util.getVersionName()
        assertThat(versionName).isEqualTo("1.0")
    }
}