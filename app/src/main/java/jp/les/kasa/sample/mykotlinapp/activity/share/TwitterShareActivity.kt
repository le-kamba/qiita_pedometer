package jp.les.kasa.sample.mykotlinapp.activity.share

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.openPlayStore
import kotlinx.android.synthetic.main.activity_twitter_share.*
import kotlinx.android.synthetic.main.content_twitter_share.*


class TwitterShareActivity : AppCompatActivity() {

    companion object {
        const val KEY_TEXT = "text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter_share)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editText_share_message.setText(intent.getStringExtra(KEY_TEXT))

        button_share_twitter.setOnClickListener {
            val message = editText_share_message.text.toString()
            post(message)
        }
    }

    private fun post(message: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val encoded = Uri.encode(message)
        intent.data = Uri.parse("twitter://post?message=$encoded")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(view_root, R.string.error_no_twitter_app, Snackbar.LENGTH_LONG)
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
