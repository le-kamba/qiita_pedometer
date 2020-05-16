package jp.les.kasa.sample.mykotlinapp.utils

import android.app.Application
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
}
