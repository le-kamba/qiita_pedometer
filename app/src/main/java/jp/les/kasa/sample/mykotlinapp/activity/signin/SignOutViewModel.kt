package jp.les.kasa.sample.mykotlinapp.activity.signin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * サインアウト画面用のViewModel
 * Eventbus的な使い方はあまり良くないが、ダイアログの表示フロー制御が入り組むのを防ぐために使う
 */
class SignOutViewModel(app: Application) : AndroidViewModel(app) {

    var needConfirm = MutableLiveData<Boolean>()

    private var _deleteAccountConfirmAgain = MutableLiveData<Boolean>()
    val deleteAccountConfirmAgain: LiveData<Boolean> = _deleteAccountConfirmAgain


}