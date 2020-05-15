package jp.les.kasa.sample.mykotlinapp.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Firebase Analytics送信用のラッパークラス
 */
class AnalyticsUtil(app: Application) {

    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(app) }

    init {
        FirebaseApp.initializeApp(app)
    }

    /**
     * 画面名報告
     */
    fun sendScreenName(activity: Activity, screenName: String, classOverrideName: String? = null) {
        firebaseAnalytics.setCurrentScreen(activity, screenName, classOverrideName)
    }

    /**
     * ボタンタップイベント送信
     */
    fun sendButtonEvent(buttonName: String) {
        val bundle = Bundle().apply { putString("buttonName", buttonName) }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
    }


    /**
     * シェアイベント送信
     */
    fun sendShareEvent(type: String) {
        val bundle = Bundle().apply { putString("share_type", type) }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }

    /**
     * カレンダーセルタップイベント送信
     */
    fun sendCalendarCellEvent(date: String) {
        val bundle = Bundle().apply { putString("date", date) }
        firebaseAnalytics.logEvent("CalendarCell", bundle)
    }

    /**
     * ユーザープロパティ設定の例
     */
    fun setPetDogProperty(hasDog: Boolean) {
        firebaseAnalytics.setUserProperty("pet_dog", hasDog.toString())
    }

    /**
     * ユーザーIDのセット
     */
    fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }

    /**
     * サインイン開始ボタンイベント送信
     */
    fun sendSignInStartEvent() {
        firebaseAnalytics.logEvent("StartSignIn", null)
    }

    /**
     * サインイン開始ボタンイベント送信
     */
    fun sendSignInErrorEvent(errorCode: Int) {
        val bundle = Bundle().apply { putInt("errorCode", errorCode) }
        firebaseAnalytics.logEvent("ErrorSignIn", bundle)
    }

    /**
     * サインイン開始ボタンイベント送信
     */
    fun sendSignInEvent() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, null)
    }
}
