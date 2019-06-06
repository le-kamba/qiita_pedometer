package jp.les.kasa.sample.mykotlinapp

import org.junit.Test
import org.assertj.core.api.Assertions.assertThat

/**
 * @author c2090
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