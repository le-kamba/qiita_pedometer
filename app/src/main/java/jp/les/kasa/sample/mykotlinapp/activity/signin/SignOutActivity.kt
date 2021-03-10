package jp.les.kasa.sample.mykotlinapp.activity.signin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.alert.ConfirmDialog
import jp.les.kasa.sample.mykotlinapp.base.BaseActivity
import jp.les.kasa.sample.mykotlinapp.databinding.ActivitySignoutBinding
import jp.les.kasa.sample.mykotlinapp.utils.AuthProviderI
import org.koin.android.ext.android.inject

class SignOutActivity : BaseActivity(), ConfirmDialog.ConfirmEventListener {
    companion object {
        const val TAG_CONFIRM_1 = "confirm"
        const val TAG_CONFIRM_2 = "confirm_again"

        const val SCREEN_NAME = "サインアウト画面"
    }

    // 画面報告名
    override val screenName: String
        get() = SCREEN_NAME

    lateinit var binding: ActivitySignoutBinding

    private val authProvider: AuthProviderI by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_signout)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.userData = authProvider.userData


        // サインアウトボタン
        binding.buttonSignOut.setOnClickListener {
            analyticsUtil.sendSignOutStartEvent()
            authProvider.signOut(this)
                .addOnCompleteListener {
                    analyticsUtil.sendSignOutEvent()
                    Log.d("AUTH", "User logout completed.")
                    // サインイン画面に戻る
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                }
        }

        // ローカルデータに変更ボタン
        binding.buttonConvert.setOnClickListener {
            analyticsUtil.sendButtonEvent("convert_to_local_data")
            // TODO FirestoreのデータをRoomに入れる
        }

        // アカウント削除ボタン
        binding.buttonAccountDelete.setOnClickListener {
            analyticsUtil.sendButtonEvent("delete_account")
            // 確認フローを開始する
            val dialog = ConfirmDialog.Builder().data(Bundle().apply {
                putString("tag", TAG_CONFIRM_1)
            })
                .message(R.string.confirm_account_delete_1)
                .create()
            dialog.show(supportFragmentManager, TAG_CONFIRM_1)
        }
    }

    private fun doDeleteAccount() {
        authProvider.delete(this)
            .addOnCompleteListener {
                Log.d("AUTH", "Account delete completed.")
                analyticsUtil.sendDeleteAccountEvent()
                // サインイン画面に戻る
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfirmResult(which: Int, bundle: Bundle?, requestCode: Int) {
        when (bundle?.get("tag")) {
            TAG_CONFIRM_1 -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    // データ削除した、なので削除決行
                    doDeleteAccount()
                } else {
                    // データ削除しなくてよいかもう一度確認
                    val dialog = ConfirmDialog.Builder().data(Bundle().apply {
                        putString("tag", TAG_CONFIRM_2)
                    })
                        .message(R.string.confirm_account_delete_2)
                        .create()
                    dialog.show(supportFragmentManager, TAG_CONFIRM_2)
                }
            }
            TAG_CONFIRM_2 -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    // アカウント削除してよい、なので削除決行
                    doDeleteAccount()
                } else {
                    // いいえなので何もしない
                }
            }
        }
    }
}
