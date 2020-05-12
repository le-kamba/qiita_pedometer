package jp.les.kasa.sample.mykotlinapp.activity.logitem

import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.utils.addDay
import jp.les.kasa.sample.mykotlinapp.utils.clearTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class LogInputFragmentTest {

    @Test
    fun validation_success_today() {
        val today = Calendar.getInstance()

        val result = logInputValidation(today.clearTime(), today.clearTime(), "123")
        assertThat(result).isNull()
    }

    @Test
    fun validation_success_yesterday() {
        val today = Calendar.getInstance()
        val yesterday = today.addDay(-1)

        val result = logInputValidation(today.clearTime(), yesterday.clearTime(), "123")
        assertThat(result).isNull()
    }

    @Test
    fun validation_error_tomorrow() {
        val today = Calendar.getInstance()
        val tomorrow = today.addDay(1)

        val result = logInputValidation(today.clearTime(), tomorrow.clearTime(), "123")
        assertThat(result).isEqualTo(R.string.error_validation_future_date)
    }

    @Test
    fun validation_error_emptyCount() {
        val today = Calendar.getInstance()
        val yesterday = today.addDay(-1)

        val result = logInputValidation(today.clearTime(), yesterday.clearTime(), "")
        assertThat(result).isEqualTo(R.string.error_validation_empty_count)
    }
}
