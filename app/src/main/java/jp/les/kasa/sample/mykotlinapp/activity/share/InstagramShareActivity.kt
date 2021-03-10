package jp.les.kasa.sample.mykotlinapp.activity.share

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.base.BaseActivity
import jp.les.kasa.sample.mykotlinapp.data.StepCountLog
import jp.les.kasa.sample.mykotlinapp.databinding.ActivityInstagramShareBinding
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import kotlin.coroutines.CoroutineContext

@RuntimePermissions
class InstagramShareActivity : BaseActivity(), CoroutineScope {
    companion object {
        const val KEY_STEP_COUNT_DATA = "data"

        const val SCREEN_NAME = "Instagramシェア画面"
    }

    lateinit var binding: ActivityInstagramShareBinding
    val viewModel by viewModel<InstagramShareViewModel>()

    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    // 画面報告名
    override val screenName: String
        get() = SCREEN_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_instagram_share)
        binding.stepLog = intent.extras!!.getSerializable(KEY_STEP_COUNT_DATA) as StepCountLog

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.content.buttonShareInstagram.setOnClickListener {
            if (Build.VERSION.SDK_INT < 29) {
                createShareImageWithPermissionCheck()
            } else {
                createShareImage()
            }
        }

        viewModel.savedBitmapUri.observe(this, Observer { imageFileUri ->
            // シェア用画像が出来た

            // シェアインテント
            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/*"
            share.putExtra(Intent.EXTRA_STREAM, imageFileUri)
            startActivity(Intent.createChooser(share, "Share to"))
            analyticsUtil.sendShareEvent("Instagram")
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        (job + Dispatchers.Default).cancel()
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun createShareImage() {
        launch {
            val bitmap = withContext(Dispatchers.Default) {
                getBitmapFromView(binding.content.layoutPostImage)
            }
            withContext(Dispatchers.IO) {
                viewModel.createShareImage(bitmap)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }
}