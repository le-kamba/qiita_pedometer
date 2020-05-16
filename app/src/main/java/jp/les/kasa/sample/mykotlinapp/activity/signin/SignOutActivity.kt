package jp.les.kasa.sample.mykotlinapp.activity.signin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.alert.ConfirmDialog
import jp.les.kasa.sample.mykotlinapp.base.BaseActivity
import jp.les.kasa.sample.mykotlinapp.data.LoginUserData
import jp.les.kasa.sample.mykotlinapp.databinding.ActivitySignOutBinding
import kotlinx.android.synthetic.main.activity_sign_out.*
import kotlinx.android.synthetic.main.activity_signin.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignOutActivity : BaseActivity(), ConfirmDialog.ConfirmEventListener {
    companion object {
        const val TAG_CONFIGM_1 = "confirm"
        const val TAG_CONFIGM_2 = "confirm_again"

        const val SCREEN_NAME = "サインアウト画面"
    }

    // 画面報告名
    override val screenName: String
        get() = SCREEN_NAME

    private val authUI = AuthUI.getInstance()

    lateinit var binding: ActivitySignOutBinding
    val viewModel by viewModel<SignOutViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_out)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val user = FirebaseAuth.getInstance().currentUser
        val userData = LoginUserData(
            user?.displayName ?: getString(R.string.label_you),
            user?.email ?: getString(R.string.label_no_email)
        )

        binding.userData = userData


        // サインアウトボタン
        buttonSignOut.setOnClickListener {
            analyticsUtil.sendSignOutStartEvent()
            authUI.signOut(this)
                .addOnCompleteListener {
                    analyticsUtil.sendSignOutEvent()
                    Log.d("AUTH", "User logout completed.")
                    // サインイン画面に戻る
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                }
        }

        // ローカルデータに変更ボタン
        buttonConvert.setOnClickListener {
            analyticsUtil.sendButtonEvent("convert_to_local_data")
            // TODO FirestoreのデータをRoomに入れる
        }

        // アカウント削除ボタン
        buttonAccountDelete.setOnClickListener {
            analyticsUtil.sendButtonEvent("delete_account")
            // 確認フローを開始する
            val dialog = ConfirmDialog.Builder().data(Bundle().apply {
                putString("tag", TAG_CONFIGM_1)
            })
                .message(R.string.confirm_account_delete_1)
                .create()
            dialog.show(supportFragmentManager, TAG_CONFIGM_1)
        }
    }

    private fun doDeleteAccount() {
        authUI.delete(this)
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
            TAG_CONFIGM_1 -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    // データ削除した、なので削除決行
                    doDeleteAccount()
                } else {
                    // データ削除しなくてよいかもう一度確認
                    val dialog = ConfirmDialog.Builder().data(Bundle().apply {
                        putString("tag", TAG_CONFIGM_2)
                    })
                        .message(R.string.confirm_account_delete_2)
                        .create()
                    dialog.show(supportFragmentManager, TAG_CONFIGM_2)
                }
            }
            TAG_CONFIGM_2 -> {
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
