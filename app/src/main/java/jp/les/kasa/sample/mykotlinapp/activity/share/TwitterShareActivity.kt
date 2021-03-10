package jp.les.kasa.sample.mykotlinapp.activity.share

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.base.BaseActivity
import jp.les.kasa.sample.mykotlinapp.databinding.ActivityTwitterShareBinding
import jp.les.kasa.sample.mykotlinapp.utils.openPlayStore


class TwitterShareActivity : BaseActivity() {

    companion object {
        const val KEY_TEXT = "text"

        const val SCREEN_NAME = "Twitterシェア画面"
    }

    // 画面報告名
    override val screenName: String
        get() = SCREEN_NAME

    private lateinit var binding: ActivityTwitterShareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTwitterShareBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.content.editTextShareMessage.setText(intent.getStringExtra(KEY_TEXT))

        binding.content.buttonShareTwitter.setOnClickListener {
            val message = binding.content.editTextShareMessage.text.toString()
            post(message)
        }
    }

    private fun post(message: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val encoded = Uri.encode(message)
        intent.data = Uri.parse("twitter://post?message=$encoded")
        try {
            startActivity(intent)
            analyticsUtil.sendShareEvent("Twitter")
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(binding.viewRoot, R.string.error_no_twitter_app, Snackbar.LENGTH_LONG)
                .setAction(R.string.install) {
                    openPlayStore("com.twitter.android")
                }
                .show()
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

    override fun finish() {
        // Instagramを続けて投稿する用に、受けたintentをそのまま返すように設定しておく
        setResult(RESULT_OK, intent)
        super.finish()
    }
}
