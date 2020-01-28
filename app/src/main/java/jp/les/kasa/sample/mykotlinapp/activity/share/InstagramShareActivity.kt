package jp.les.kasa.sample.mykotlinapp.activity.share

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.databinding.ActivityInstagramShareBinding
import kotlinx.android.synthetic.main.activity_instagram_share.*
import kotlinx.android.synthetic.main.content_instagram_share.view.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import kotlin.coroutines.CoroutineContext

@RuntimePermissions
class InstagramShareActivity : AppCompatActivity(), CoroutineScope {
    companion object {
        const val KEY_STEP_COUNT_DATA = "data"
    }

    lateinit var binding: ActivityInstagramShareBinding
    val viewModel by viewModel<InstagramShareViewModel>() // TODO koinを2.1.0に上げたときはby shareViewModelに要変更

    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_instagram_share)
        binding.stepLog = intent.extras!!.getSerializable(KEY_STEP_COUNT_DATA) as StepCountLog

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.root.button_share_instagram.setOnClickListener {
            createShareImageWithPermissionCheck()
        }

        viewModel.savedBitmapFile.observe(this, Observer { file ->
            // シェア用画像が出来た

            // フォトアルバムなどで見えるようにするため、いったんメディアスキャンを掛ける
            val contentUri = Uri.fromFile(file)
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri)
            sendBroadcast(mediaScanIntent)

            // シェアインテント
            val imageFileUri = FileProvider.getUriForFile(this, "$packageName.provider", file)

            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/*"
            share.putExtra(Intent.EXTRA_STREAM, imageFileUri)
            startActivity(Intent.createChooser(share, "Share to"))
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        (job + Dispatchers.Default).cancel()
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun createShareImage() {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "qiita_pedometer"
        )
        launch {
            val bitmap = withContext(Dispatchers.Default) {
                getBitmapFromView(binding.root.layout_post_image)
            }
            withContext(Dispatchers.IO) {
                viewModel.createShareImage(bitmap, dir)
            }
        }
    }

    // Bitmapを保存
    private suspend fun getBitmapFromView(view: View): Bitmap {

        val height = view.height
        val width = view.width

        val canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)

        val bgDrawable = view.background
        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)

        view.draw(canvas)

        return canvasBitmap
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }
}