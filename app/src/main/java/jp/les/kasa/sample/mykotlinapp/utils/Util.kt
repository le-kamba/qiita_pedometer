package jp.les.kasa.sample.mykotlinapp.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import jp.les.kasa.sample.mykotlinapp.BuildConfig
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
import org.hashids.Hashids
import java.text.SimpleDateFormat
import java.util.*

/**
 * いろいろ便利処理を集めたUtilクラス
 * @date 2019/06/05
 **/

class Util {
    companion object {
        fun getVersionCode() =
            BuildConfig.VERSION_CODE

        fun getVersionName() =
            BuildConfig.VERSION_NAME
    }
}

fun Calendar.getDateStringYMD(): String {
    val fmt = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
    return fmt.format(time)
}

fun Calendar.getDateStringYM(): String {
    val fmt = SimpleDateFormat("yyyy/MM", Locale.JAPAN)
    return fmt.format(time)
}

fun Calendar.clearTime(): Calendar {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return this
}


fun Calendar.getYear(): Int {
    return get(Calendar.YEAR)
}

fun Calendar.getMonth(): Int {
    return get(Calendar.MONTH)
}

fun Calendar.getDay(): Int {
    return get(Calendar.DAY_OF_MONTH)
}

fun Calendar.addDay(addDayNum: Int): Calendar {
    val newCal = this.clone() as Calendar
    newCal.add(Calendar.DAY_OF_MONTH, addDayNum)
    return newCal
}

fun Calendar.equalsYMD(other: Calendar): Boolean {
    return (this.getYear() == other.getYear() &&
            this.getMonth() == other.getMonth() &&
            this.getDay() == other.getDay())
}

fun Calendar.equalsYMD(dateStr: String): Boolean {
    val fmt = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
    val cal = Calendar.getInstance()
    cal.time = fmt.parse(dateStr)!!
    return this.equalsYMD(cal)
}

fun levelFromRadioId(checkedRadioButtonId: Int): LEVEL {
    return when (checkedRadioButtonId) {
        R.id.radio_good -> LEVEL.GOOD
        R.id.radio_bad -> LEVEL.BAD
        else -> LEVEL.NORMAL
    }
}

fun weatherFromSpinner(selectedItemPosition: Int): WEATHER {
    return WEATHER.values()[selectedItemPosition]
}

/**
 * Playストアの指定アプリのページを開く
 */
fun Context.openPlayStore(packageName: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    var url = getString(R.string.market_url, packageName)
    intent.data = Uri.parse(url)
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
    }

}

/**
 * 9文字のUserIdを作成する
 * SaltにはrandomUUIDを利用
 * ハッシュするソース元の数値は現在時刻を利用(ms)
 */
fun uniqueUserId(): String {
    val hashids = Hashids(UUID.randomUUID().toString(), 9)
    return hashids.encode(System.currentTimeMillis())
}