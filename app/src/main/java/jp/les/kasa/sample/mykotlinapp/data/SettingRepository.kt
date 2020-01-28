package jp.les.kasa.sample.mykotlinapp.data

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

/**
 * 設定ファイル読み書きリポジトリ
 **/
class SettingRepository constructor(private val applicationContext: Context) {
    companion object {
        const val PREF_FILE_NAME = "settings"
    }

    fun saveShareStatus(shareStatus: ShareStatus) {
        // 設定に保存
        val pref = applicationContext.getSharedPreferences(PREF_FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        pref.edit().putBoolean("postSns", shareStatus.doPost)
            .putBoolean("postTwitter", shareStatus.postTwitter)
            .putBoolean("postInstagram", shareStatus.postInstagram)
            .apply()
    }

    fun readShareStatus(): ShareStatus {
        val pref = applicationContext.getSharedPreferences(PREF_FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        val doPost = pref.getBoolean("postSns", false)
        val postTwitter = pref.getBoolean("postTwitter", false)
        val postInstagram = pref.getBoolean("postInstagram", false)
        return ShareStatus(doPost, postTwitter, postInstagram)
    }

    fun clear() {
        val pref = applicationContext.getSharedPreferences(PREF_FILE_NAME, AppCompatActivity.MODE_PRIVATE)
        pref.edit().clear().apply()
    }
}