package jp.les.kasa.sample.mykotlinapp.utils

import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
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
    fun calendar_getDateStringYMD() {
        val cal = Calendar.getInstance()
        cal.set(2020, 9 - 1, 11) // 月だけはindex扱いなので、実際の月-1のセットとしなければならない
        assertThat(cal.getDateStringYMD()).isEqualTo("2020/09/11")
    }

    @Test
    fun calendar_getDateStringYM() {
        val cal = Calendar.getInstance()
        cal.set(2020, 9 - 1, 11) // 月だけはindex扱いなので、実際の月-1のセットとしなければならない
        assertThat(cal.getDateStringYM()).isEqualTo("2020/09")
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

    @Test
    fun calendar_getYear() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2020)
        cal.set(Calendar.MONTH, 9)
        cal.set(Calendar.DAY_OF_MONTH, 10)

        assertThat(cal.getYear()).isEqualTo(2020)
    }

    @Test
    fun calendar_getMonth() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2020)
        cal.set(Calendar.MONTH, 9)
        cal.set(Calendar.DAY_OF_MONTH, 10)

        assertThat(cal.getMonth()).isEqualTo(9)
    }

    @Test
    fun calendar_getDay() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2020)
        cal.set(Calendar.MONTH, 9)
        cal.set(Calendar.DAY_OF_MONTH, 10)

        assertThat(cal.getDay()).isEqualTo(10)
    }

    @Test
    fun calendar_addDay() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2020)
        cal.set(Calendar.MONTH, 9)
        cal.set(Calendar.DAY_OF_MONTH, 10)

        val next = cal.addDay(1)
        val prev = cal.addDay(-1)

        assertThat(next.getDateStringYMD()).isEqualTo("2020/10/11")
        assertThat(prev.getDateStringYMD()).isEqualTo("2020/10/09")
    }

    @Test
    fun calendar_equalsYMD() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2020)
        cal.set(Calendar.MONTH, 9)
        cal.set(Calendar.DAY_OF_MONTH, 10)

        val other = Calendar.getInstance()
        other.set(Calendar.YEAR, 2020)
        other.set(Calendar.MONTH, 9)
        other.set(Calendar.DAY_OF_MONTH, 10)

        assertThat(cal.equalsYMD(other)).isTrue()
    }

    @Test
    fun calendar_equalsYMD_withString() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2020)
        cal.set(Calendar.MONTH, 9)
        cal.set(Calendar.DAY_OF_MONTH, 10)

        assertThat(cal.equalsYMD("2020/10/10")).isTrue()
    }

    @Test
    fun levelFromRadioId() {
        assertThat(jp.les.kasa.sample.mykotlinapp.utils.levelFromRadioId(R.id.radio_normal)).isEqualTo(
            LEVEL.NORMAL
        )
        assertThat(jp.les.kasa.sample.mykotlinapp.utils.levelFromRadioId(R.id.radio_good)).isEqualTo(
            LEVEL.GOOD
        )
        assertThat(jp.les.kasa.sample.mykotlinapp.utils.levelFromRadioId(R.id.radio_bad)).isEqualTo(
            LEVEL.BAD
        )
    }

    @Test
    fun weatherFromSpinner() {
        assertThat(jp.les.kasa.sample.mykotlinapp.utils.weatherFromSpinner(0)).isEqualTo(WEATHER.FINE)
        assertThat(jp.les.kasa.sample.mykotlinapp.utils.weatherFromSpinner(1)).isEqualTo(WEATHER.RAIN)
        assertThat(jp.les.kasa.sample.mykotlinapp.utils.weatherFromSpinner(2)).isEqualTo(WEATHER.CLOUD)
        assertThat(jp.les.kasa.sample.mykotlinapp.utils.weatherFromSpinner(3)).isEqualTo(WEATHER.SNOW)
        assertThat(jp.les.kasa.sample.mykotlinapp.utils.weatherFromSpinner(4)).isEqualTo(WEATHER.COLD)
        assertThat(jp.les.kasa.sample.mykotlinapp.utils.weatherFromSpinner(5)).isEqualTo(WEATHER.HOT)
    }
}