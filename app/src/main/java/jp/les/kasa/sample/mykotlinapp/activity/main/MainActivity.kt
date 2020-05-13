package jp.les.kasa.sample.mykotlinapp.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.activity.logitem.LogItemActivity
import jp.les.kasa.sample.mykotlinapp.activity.share.InstagramShareActivity
import jp.les.kasa.sample.mykotlinapp.activity.share.TwitterShareActivity
import jp.les.kasa.sample.mykotlinapp.alert.SelectPetDialog
import jp.les.kasa.sample.mykotlinapp.base.BaseActivity
import jp.les.kasa.sample.mykotlinapp.data.SettingRepository
import jp.les.kasa.sample.mykotlinapp.data.ShareStatus
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity(), SelectPetDialog.SelectPetEventListener {

    companion object {
        const val REQUEST_CODE_LOGITEM = 100
        const val REQUEST_CODE_SHARE_TWITTER = 101

        const val RESULT_CODE_DELETE = 10

        const val SCREEN_NAME = "トップ画面"
    }

    // VieModel inject by Koin
    val viewModel by viewModel<MainViewModel>()
    val settingRepository: SettingRepository by inject()

    // 画面報告名
    override val screenName: String
        get() = SCREEN_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.pages.observe(this, Observer { list ->
            list?.let {
                viewPager.adapter = MonthlyPagerAdapter(this, it)
                viewPager.setCurrentItem(it.size - 1, false)
            }
        })


        val hasPet = settingRepository.readPetDog()
        if (hasPet == null) {
            val dialog = SelectPetDialog()
            dialog.show(supportFragmentManager, null)
        }

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

    /**
     * 犬を飼っているかの選択肢を送信
     */
    override fun onSelected(hasDog: Boolean) {
        analyticsUtil.setPetDogProperty(hasDog)
        if (!hasDog) {
            // Crashlyticsに送るサンプル用
            throw RuntimeException("Test Crash")
        }
        settingRepository.savePetDog(hasDog)
    }
}

class MonthlyPagerAdapter(fragmentActivity: FragmentActivity, private val items: List<String>) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = items.size
    override fun createFragment(position: Int) = MonthlyPageFragment.newInstance(items[position])

}
