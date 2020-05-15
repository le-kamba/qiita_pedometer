package jp.les.kasa.sample.mykotlinapp.activity.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes.*
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.alert.ErrorDialog
import jp.les.kasa.sample.mykotlinapp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_signin.*

class SignInActivity : BaseActivity() {
    companion object {
        const val REQUEST_CODE_AUTH = 210
        const val DIALOG_TAG_AUTH_ERROR = "auth_error_dialog"
        const val SCREEN_NAME = "サインイン画面"
    }

    // 画面報告名
    override val screenName: String
        get() = SCREEN_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        buttonSignIn.setOnClickListener {
            analyticsUtil.sendSignInStartEvent()

            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().build(),
                AuthUI.IdpConfig.TwitterBuilder().build(),
                AuthUI.IdpConfig.GitHubBuilder().build()
            )

            FirebaseCrashlytics.getInstance().log("FirebaseUI Auth called.")

            // Create and launch sign-in intent
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                REQUEST_CODE_AUTH
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_AUTH) {
            FirebaseCrashlytics.getInstance()
                .log("FirebaseUI Auth finished. result code = [$resultCode]")

            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser

                analyticsUtil.sendSignInEvent()
                // TODO Roomのデータをコンバートしてアップロード
            } else response?.error?.errorCode?.let { errorCode ->
                analyticsUtil.sendSignInErrorEvent(errorCode)

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

