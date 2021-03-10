package jp.les.kasa.sample.mykotlinapp.activity.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemActivity
import jp.les.kasa.sample.mykotlinapp.activity.share.InstagramShareActivity
import jp.les.kasa.sample.mykotlinapp.activity.share.TwitterShareActivity
import jp.les.kasa.sample.mykotlinapp.activity.signin.SignInActivity
import jp.les.kasa.sample.mykotlinapp.alert.SelectPetDialog
import jp.les.kasa.sample.mykotlinapp.base.BaseActivity
import jp.les.kasa.sample.mykotlinapp.data.HasPet
import jp.les.kasa.sample.mykotlinapp.data.SettingRepository
import jp.les.kasa.sample.mykotlinapp.data.ShareStatus
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity(), SelectPetDialog.SelectPetEventListener {

    companion object {
        const val REQUEST_CODE_LOGITEM = 100
        const val REQUEST_CODE_SHARE_TWITTER = 101
        const val REQUEST_CODE_SIGN_IN = 201

        const val RESULT_CODE_DELETE = 10

        const val SCREEN_NAME = "トップ画面"
    }

    // VieModel inject by Koin
    val viewModel by viewModel<MainViewModel>()
    private val settingRepository: SettingRepository by inject()

    // 画面報告名
    override val screenName: String
        get() = SCREEN_NAME

    // Access a Cloud Firestore instance from your Activity
    val db = FirebaseFirestore.getInstance()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.pages.observe(this, Observer { list ->
            list?.let {
                binding.viewPager.adapter = MonthlyPagerAdapter(this, it)
                binding.viewPager.setCurrentItem(it.size - 1, false)
            }
        })

// テストに影響あるのでコメントアウト
//        val hasPet = settingRepository.readPetDog()
//        if (hasPet != true) {
//            val dialog = SelectPetDialog()
//            dialog.show(supportFragmentManager, null)
//        } else {
//            showPetListDialog()
//        }
// こちらは上記を実行する場合にはコメントアウトして下さい。
//        onSelected(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_CODE_LOGITEM -> {
                onStepCountLogChanged(resultCode, data)
                return
            }

            REQUEST_CODE_SHARE_TWITTER -> {
                // 続けてInstagramにも投稿する
                val intent = Intent(this, InstagramShareActivity::class.java).apply {
                    val log =
                        data!!.getSerializableExtra(LogItemActivity.EXTRA_KEY_DATA) as StepCountLog
                    putExtra(InstagramShareActivity.KEY_STEP_COUNT_DATA, log)
                }
                startActivity(intent)
                return
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onStepCountLogChanged(resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK -> {
                val log =
                    data!!.getSerializableExtra(LogItemActivity.EXTRA_KEY_DATA) as StepCountLog
                viewModel.addStepCount(log)
                val shareStatus =
                    data.getSerializableExtra(LogItemActivity.EXTRA_KEY_SHARE_STATUS) as ShareStatus
                if (shareStatus.doPost) {
                    // 共有フラグがONならDB登録完了後に投稿画面へ遷移する
                    if (shareStatus.postTwitter) {
                        val intent = Intent(this, TwitterShareActivity::class.java)
                        intent.putExtra(TwitterShareActivity.KEY_TEXT, log.getShareMessage())
                        if (shareStatus.postInstagram) {
                            intent.putExtra(InstagramShareActivity.KEY_STEP_COUNT_DATA, log)
                            // Instagramもチェックされていれば、戻った後で次に起動するため、結果を受け取る必要がある
                            startActivityForResult(intent, REQUEST_CODE_SHARE_TWITTER)
                        } else {
                            startActivity(intent)
                        }
                    } else if (shareStatus.postInstagram) {
                        val intent = Intent(this, InstagramShareActivity::class.java).apply {
                            putExtra(InstagramShareActivity.KEY_STEP_COUNT_DATA, log)
                        }
                        startActivity(intent)
                    }
                }
            }
            RESULT_CODE_DELETE -> {
                val log =
                    data!!.getSerializableExtra(LogItemActivity.EXTRA_KEY_DATA) as StepCountLog
                viewModel.deleteStepCount(log)
            }
        }
    }

    private fun showPetListDialog() {
        db.collection("pets")
//                .whereEqualTo("petDog", true)
//                .whereGreaterThan("born", 2000)
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                val list = arrayListOf<HasPet>()
                for (document in result) {
                    Log.d("FIRESTORE", "${document.id} => ${document.data}")
                    list.add(HasPet(document.data))
                }
                val dialog = ListDialogFragment.Builder(list).create()
                dialog.show(supportFragmentManager, null)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "データを読み込めませんでした。", Toast.LENGTH_SHORT).show()
                Log.w("FIRESTORE", "Error getting documents.", exception)
            }
    }

    /**
     * 犬を飼っているかの選択肢を送信
     */
    override fun onSelected(hasDog: Boolean) {
        analyticsUtil.setPetDogProperty(hasDog)
        if (!hasDog) {
//            // Crashlyticsに送るサンプル用
//            throw RuntimeException("Test Crash")
        }
        settingRepository.savePetDog(hasDog)
        val savedDocReferenceId = settingRepository.getDocReferenceId()

        // Firestoreお試し用
        val pet: HashMap<String, Any> =
            if (hasDog) {
                HasPet.randomPet()
            } else {
                hashMapOf(
                    "petDog" to hasDog,
                    "message" to "Test"
                )
            }
        if (savedDocReferenceId == null) {
            // 新規登録
            db.collection("pets")
                .add(pet)
                .addOnSuccessListener { documentReference ->
                    // document reference idを保存
                    settingRepository.saveDocReferenceId(documentReference.id)
                    Log.d("FIRESTORE", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "登録できませんでした。", Toast.LENGTH_SHORT).show()
                    Log.w("FIRESTORE", "Error adding document", e)
                }
        } else {
            val docRef = db.collection("pets").document(savedDocReferenceId)
            // 上書き更新
            docRef.update(pet)
                .addOnSuccessListener {
                    Log.d("FIRESTORE", "DocumentSnapshot Updated.")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "更新できませんでした。", Toast.LENGTH_SHORT).show()
                    Log.w("FIRESTORE", "Error updating document", e)
                }
        }

        showPetListDialog()
    }

    // メニュー追加
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            return when (it.itemId) {
                R.id.login -> {
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE_SIGN_IN)
                    true
                }
                else -> false
            }
        }
        return false
    }
}

class MonthlyPagerAdapter(fragmentActivity: FragmentActivity, private val items: List<String>) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = items.size
    override fun createFragment(position: Int) = MonthlyPageFragment.newInstance(items[position])

}
