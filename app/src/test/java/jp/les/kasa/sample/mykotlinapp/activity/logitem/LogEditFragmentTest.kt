package jp.les.kasa.sample.mykotlinapp.activity.logitem

import jp.les.kasa.sample.mykotlinapp.R
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LogEditFragmentTest {

    @Test
    fun validation_error_emptyCount() {

        val result = logEditValidation("")
        assertThat(result).isEqualTo(R.string.error_validation_empty_count)
    }
}
