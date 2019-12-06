package jp.les.kasa.sample.mykotlinapp.activity.share

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.databinding.ActivityInstagramShareBinding
import kotlinx.android.synthetic.main.activity_instagram_share.*
import kotlinx.android.synthetic.main.content_instagram_share.view.*
import android.content.Intent
import android.os.Environment
import java.io.File


class InstagramShareActivity : AppCompatActivity() {
    companion object {
        const val KEY_STEP_COUNT_DATA = "data"
    }

    lateinit var binding: ActivityInstagramShareBinding
    lateinit var viewModel: InstagramShareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityInstagramShareBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_instagram_share)
        binding.stepLog = intent.extras!!.getSerializable(KEY_STEP_COUNT_DATA) as StepCountLog

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(this).get(InstagramShareViewModel::class.java)

        binding.root.button_share_instagram.setOnClickListener {
            createShareImage()
        }

        viewModel.savedBitmapFile.observe(this, Observer { file ->
            val uri = Uri.fromFile(file)
            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/*"
            share.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(share, "Share to"))
        })
    }

    private fun createShareImage(){
        val filepath = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "qiita_pedometer")
        viewModel.createShareImage(binding.root.layout_post_image, filepath)
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

}