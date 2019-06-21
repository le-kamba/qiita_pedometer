package jp.les.kasa.sample.mykotlinapp

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

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

    @Test
    fun calendar_getStringYMD() {
        val cal = Calendar.getInstance()
        cal.set(2020, 9 - 1, 11) // 月だけはindex扱いなので、実際の月-1のセットとしなければならない
        assertThat(cal.getDateStringYMD()).isEqualTo("2020/09/11")
    }

    @Test
    fun calendar_clearTime() {
        val cal = Calendar.getInstance()
        // 時間関連が0にならないようにセット
        cal.set(Calendar.HOUR, 1)
        cal.set(Calendar.MINUTE, 10)
        cal.set(Calendar.SECOND, 20)
        cal.set(Calendar.MILLISECOND, 300)
        // 0でないことの確認
        assertThat(cal.get(Calendar.HOUR)).isNotEqualTo(0)
        assertThat(cal.get(Calendar.MINUTE)).isNotEqualTo(0)
        assertThat(cal.get(Calendar.SECOND)).isNotEqualTo(0)
        assertThat(cal.get(Calendar.MILLISECOND)).isNotEqualTo(0)

        cal.clearTime()
        // 0になっていることの確認
        assertThat(cal.get(Calendar.HOUR)).isEqualTo(0)
        assertThat(cal.get(Calendar.MINUTE)).isEqualTo(0)
        assertThat(cal.get(Calendar.SECOND)).isEqualTo(0)
        assertThat(cal.get(Calendar.MILLISECOND)).isEqualTo(0)
    }
}