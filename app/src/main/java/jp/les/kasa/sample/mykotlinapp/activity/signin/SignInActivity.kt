package jp.les.kasa.sample.mykotlinapp.activity.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.StringRes
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes.*
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.alert.ErrorDialog
import jp.les.kasa.sample.mykotlinapp.base.BaseActivity
import jp.les.kasa.sample.mykotlinapp.utils.AuthProviderI
import kotlinx.android.synthetic.main.activity_signin.*
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

    private val authUI = AuthUI.getInstance()
    private val authProvider: AuthProviderI by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        buttonSignIn.setOnClickListener {
            analyticsUtil.sendSignInStartEvent()

            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().setPermissions(listOf("email")).build(),
                AuthUI.IdpConfig.TwitterBuilder().build(),
                AuthUI.IdpConfig.GitHubBuilder().build()
            )

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
                authUI.createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(R.mipmap.ic_launcher)
                    .setTheme(R.style.SinUpTheme)
                    .setTosAndPrivacyPolicyUrls(
                        "https://qiitapedometersample.web.app/policy.html",
                        "https://qiitapedometersample.web.app/policy.html"
                    )
                    .build(),
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

                when (errorCode) {
                    EMAIL_MISMATCH_ERROR -> {
                        // メールアドレス不一致
                        showError(R.string.error_email_mismacth, errorCode)
                    }
                    ERROR_GENERIC_IDP_RECOVERABLE_ERROR, PROVIDER_ERROR -> {
                        showError(R.string.error_id_provider, errorCode)
                    }
                    ERROR_USER_DISABLED -> {
                        showError(R.string.error_user_disabled, errorCode)
                    }
                    NO_NETWORK -> {
                        showError(R.string.error_no_netowork, errorCode)
                    }
                    PLAY_SERVICES_UPDATE_CANCELLED -> {
                        showError(R.string.error_service_update_canceled, errorCode)
                    }
                    else -> {
                        showError(R.string.error_unknown, errorCode)
                    }
                }
            }
        }
    }

    private fun showError(@StringRes messageId: Int, errorCode: Int) {
        val error = getString(R.string.label_error_code, errorCode)
        val message = "${getString(messageId)}\n\n$error"
        val dialog = ErrorDialog.Builder().message(message).create()
        dialog.show(supportFragmentManager, DIALOG_TAG_AUTH_ERROR)
    }
}

