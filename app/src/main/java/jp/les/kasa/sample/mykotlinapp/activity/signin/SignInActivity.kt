package jp.les.kasa.sample.mykotlinapp.activity.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import com.firebase.ui.auth.ErrorCodes.*
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.alert.ErrorDialog
import jp.les.kasa.sample.mykotlinapp.base.ScopeBaseActivity
import jp.les.kasa.sample.mykotlinapp.databinding.ActivitySigninBinding
import jp.les.kasa.sample.mykotlinapp.utils.AuthProviderI

class SignInActivity : ScopeBaseActivity() {
    companion object {
        const val DIALOG_TAG_AUTH_ERROR = "auth_error_dialog"
        const val SCREEN_NAME = "サインイン画面"
    }

    // 画面報告名
    override val screenName: String
        get() = SCREEN_NAME

    private val authProvider: AuthProviderI by inject()
    private lateinit var binding: ActivitySigninBinding

    // for ActivityResult API
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(), get()) {
            onAuthProviderResult(it)
        }

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
            activityResultLauncher.launch(authProvider.createSignInIntent(this))
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

    private fun onAuthProviderResult(result: ActivityResult) {
        FirebaseCrashlytics.getInstance()
            .log("FirebaseUI Auth finished. result code = [${result.resultCode}]")

        val response = IdpResponse.fromResultIntent(result.data)

        if (result.resultCode == Activity.RESULT_OK) {
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

