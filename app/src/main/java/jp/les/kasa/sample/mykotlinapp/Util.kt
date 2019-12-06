package jp.les.kasa.sample.mykotlinapp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import jp.les.kasa.sample.mykotlinapp.data.LEVEL
import jp.les.kasa.sample.mykotlinapp.data.ShareStatus
import jp.les.kasa.sample.mykotlinapp.data.WEATHER
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

fun Calendar.clearTime(): Calendar {
    set(Calendar.HOUR, 0)
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

fun Fragment.saveShareStatus(shareStatus: ShareStatus) {
    // 設定に保存
    val pref = context?.applicationContext?.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
    pref?.apply {
        val edit = pref.edit()
        edit.putBoolean("postSns", shareStatus.doPost)
        edit.putBoolean("postTwitter", shareStatus.postTwitter)
        edit.putBoolean("postInstagram", shareStatus.postInstagram)
        edit.apply()
    }
}

fun Fragment.readShareStatus(): ShareStatus {
    val pref = context?.applicationContext?.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
    pref ?: return ShareStatus()
    val doPost = pref.getBoolean("postSns", false)
    val postTwitter = pref.getBoolean("postTwitter", false)
    val postInstagram = pref.getBoolean("postInstagram", false)
    return ShareStatus(doPost, postTwitter, postInstagram)
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