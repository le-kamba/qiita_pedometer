package jp.les.kasa.sample.mykotlinapp.activity.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.VisibleForTesting
import com.firebase.ui.auth.ErrorCodes.*
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.alert.ErrorDialog
import jp.les.kasa.sample.mykotlinapp.base.BaseActivity
import jp.les.kasa.sample.mykotlinapp.databinding.ActivitySigninBinding
import jp.les.kasa.sample.mykotlinapp.utils.AuthProviderI
import org.koin.android.ext.android.inject

class SignInActivity : BaseActivity() {
    companion object {
        const val REQUEST_CODE_AUTH = 210
        const val DIALOG_TAG_AUTH_ERROR = "auth_error_dialog"
        const val SCREEN_NAME = "サインイン画面"
    }

    // 画面報告名
    override val screenName: String
        get() = SCREEN_NAME

    private val authProvider: AuthProviderI by inject()
    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.buttonSignIn.setOnClickListener {
            analyticsUtil.sendSignInStartEvent()

            FirebaseCrashlytics.getInstance().log("FirebaseUI Auth called.")

            // 完全にレイアウトをカスタムしたい場合のサンプル
//            val authLayout = AuthMethodPickerLayout.Builder(R.layout.layout_auth)
//                .setEmailButtonId(R.id.email_button)
//                .setFacebookButtonId(R.id.facebook_button)
//                .setTwitterButtonId(R.id.twitter_button)
//                .setGoogleButtonId(R.id.google_button)
//                .setGithubButtonId(R.id.github_button)
//                .build()

            // Create and launch sign-in intent
            startActivityForResult(
                authProvider.createSignInIntent(this),
                REQUEST_CODE_AUTH
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // ログイン中だったら画面を変える
        if (authProvider.user != null) {
            startActivity(Intent(this, SignOutActivity::class.java))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_AUTH) {
            FirebaseCrashlytics.getInstance()
                .log("FirebaseUI Auth finished. result code = [$resultCode]")

            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                Log.d("AUTH", "Auth Completed.")
                // Successfully signed in
                analyticsUtil.sendSignInEvent()
                // TODO Roomのデータをコンバートしてアップロード
                // or Firestoreからデータをダウンロード

            } else response?.error?.errorCode?.let { errorCode ->
                analyticsUtil.sendSignInErrorEvent(errorCode)
                Log.d("AUTH", "Auth Error.")

                FirebaseCrashlytics.getInstance()
                    .log("FirebaseUI Auth finished. error code = [$errorCode]")
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...

                showError(errorCode)
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun showError(errorCode: Int) {
        val messageId =
            when (errorCode) {
                EMAIL_MISMATCH_ERROR -> {
                    // メールアドレス不一致
                    R.string.error_email_mismacth
                }
                ERROR_GENERIC_IDP_RECOVERABLE_ERROR, PROVIDER_ERROR -> {
                    R.string.error_id_provider
                }
                ERROR_USER_DISABLED -> {
                    R.string.error_user_disabled
                }
                NO_NETWORK -> {
                    R.string.error_no_netowork
                }
                PLAY_SERVICES_UPDATE_CANCELLED -> {
                    R.string.error_service_update_canceled
                }
                else -> {
                    R.string.error_unknown
                }
            }

        val error = getString(R.string.label_error_code, errorCode)
        val message = "${getString(messageId)}\n\n$error"
        val dialog = ErrorDialog.Builder().message(message).create()
        dialog.show(supportFragmentManager, DIALOG_TAG_AUTH_ERROR)
    }
}

