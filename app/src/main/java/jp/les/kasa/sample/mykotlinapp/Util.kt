package jp.les.kasa.sample.mykotlinapp

import java.text.SimpleDateFormat
import java.util.*

/**
 * いろいろ便利処理を集めたUtilクラス
 * @date 2019/06/05
 **/

class Util {
    companion object {
        fun getVersionCode() = BuildConfig.VERSION_CODE

        fun getVersionName() = BuildConfig.VERSION_NAME
    }
}

fun Calendar.getDateStringYMD(): String {
    val fmt = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
    return fmt.format(time)
}
