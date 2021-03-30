package jp.les.kasa.sample.mykotlinapp.utils

import android.app.Application
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Firebase Analytics送信用のラッパークラス
 */

abstract class AnalyticsUtilI {

    /**
     * スクリーン名報告
     */
    abstract fun sendScreenName(
        screenName: String,
        classOverrideName: String? = null
    )

    /**
     * イベント報告
     */
    abstract fun logEvent(eventName: String, bundle: Bundle?)

    /**
     * ユーザープロパティ設定
     */
    abstract fun setUserProperty(propertyName: String, value: String)

    /**
     * ユーザーIDのセット
     */
    abstract fun setUserId(userId: String?)

    /**
     * ユーザープロパティ設定の例
     */
    fun setPetDogProperty(hasDog: Boolean) {
        setUserProperty("pet_dog", hasDog.toString())
    }

    /**
     * ボタンタップイベント送信
     */
    fun sendButtonEvent(buttonName: String) {
        val bundle = Bundle().apply { putString("buttonName", buttonName) }
        logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
    }

    /**
     * シェアイベント送信
     */
    fun sendShareEvent(type: String) {
        val bundle = Bundle().apply { putString("share_type", type) }
        logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }

    /**
     * カレンダーセルタップイベント送信
     */
    fun sendCalendarCellEvent(date: String) {
        val bundle = Bundle().apply { putString("date", date) }
        logEvent("CalendarCell", bundle)
    }

    /**
     * サインイン開始ボタンイベント送信
     */
    fun sendSignInStartEvent() {
        logEvent("StartSignIn", null)
    }

    /**
     * サインイン開始ボタンイベント送信
     */
    fun sendSignInErrorEvent(errorCode: Int) {
        val bundle = Bundle().apply { putInt("errorCode", errorCode) }
        logEvent("ErrorSignIn", bundle)
    }

    /**
     * サインイン完了イベント送信
     */
    fun sendSignInEvent() {
        logEvent(FirebaseAnalytics.Event.LOGIN, null)
    }

    /**
     * サインアウト開始ボタンイベント送信
     */
    fun sendSignOutStartEvent() {
        logEvent("StartSignOut", null)
    }

    /**
     * サインアウト完了イベント送信
     */
    fun sendSignOutEvent() {
        logEvent("logout", null)
    }

    /**
     * アカウント削除イベント送信
     */
    fun sendDeleteAccountEvent() {
        logEvent("delete_account", null)
    }
}

class AnalyticsUtil(app: Application) : AnalyticsUtilI() {

    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(app) }

    init {
        FirebaseApp.initializeApp(app)
    }

    override fun sendScreenName(screenName: String, classOverrideName: String?) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            classOverrideName?.also {
                putString(
                    FirebaseAnalytics.Param.SCREEN_CLASS,
                    classOverrideName
                )
            }
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    override fun logEvent(eventName: String, bundle: Bundle?) {
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    override fun setUserProperty(propertyName: String, value: String) {
        firebaseAnalytics.setUserProperty(propertyName, value)
    }

    override fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }
}
