package jp.les.kasa.sample.mykotlinapp.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.data.LoginUserData


/**
 * FirebaseAuthのInstance取得を提供するプロバイダ用の抽象クラス
 */
abstract class AuthProviderI(app: Application) {
    abstract val user: FirebaseUser?

    val defaultUserName: String by lazy { app.getString(R.string.label_you) }
    val defaultEmail: String by lazy { app.getString(R.string.label_no_email) }

    abstract val userData: LoginUserData


    abstract fun createSignInIntent(context: Context): Intent
    abstract fun signOut(context: Context): Task<Void?>
    abstract fun delete(context: Context): Task<Void?>
}


/**
 * FirebaseAuthのInstance取得を提供するプロバイダ用のアプリで実際に使うクラス
 */
class AuthProvider(app: Application) : AuthProviderI(app) {
    override val user: FirebaseUser?
        get() {
            return FirebaseAuth.getInstance().currentUser
        }

    override val userData: LoginUserData
        get() {
            return LoginUserData(
                user?.displayName ?: defaultUserName,
                user?.email ?: defaultEmail
            )
        }
    private val authUI = AuthUI.getInstance()

    override fun createSignInIntent(context: Context): Intent {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().setPermissions(listOf("email")).build(),
            AuthUI.IdpConfig.TwitterBuilder().build(),
            AuthUI.IdpConfig.GitHubBuilder().build()
        )

        return authUI.createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.mipmap.ic_launcher)
            .setTheme(R.style.SinUpTheme)
            .setTosAndPrivacyPolicyUrls(
                "https://qiitapedometersample.web.app/policy.html",
                "https://qiitapedometersample.web.app/policy.html"
            )
            .build()
    }

    override fun signOut(context: Context): Task<Void?> {
        return authUI.signOut(context)
    }

    override fun delete(context: Context): Task<Void?> {
        return authUI.delete(context)
    }

}
