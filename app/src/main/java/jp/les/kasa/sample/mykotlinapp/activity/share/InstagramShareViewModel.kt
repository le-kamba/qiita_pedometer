package jp.les.kasa.sample.mykotlinapp.activity.share

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * Instagram投稿画面用のViewModel
 * @date 2019-11-15
 **/
class InstagramShareViewModel : ViewModel() {

    // 保存完了を貰う
    private val _savedBitmapFile = MutableLiveData<File>()
    val savedBitmapFile = _savedBitmapFile as LiveData<File>

    // coroutine用
    private var parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)

    fun createShareImage(view: View, fileapth : File) = scope.launch {
        val bitmap = withContext(context = Dispatchers.Default) {
            getBitmapFromView(view)
        }
        val resultFile = withContext(context = Dispatchers.IO) {
            saveBidmap(bitmap, fileapth)
        }
        _savedBitmapFile.postValue(resultFile)
    }

    // Bitmapを保存
    suspend private fun getBitmapFromView(view: View): Bitmap {

        val height = view.height
        val width = view.width

        val canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)

        val bgDrawable = view.background
        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)

        canvas.save()
        view.draw(canvas)
        canvas.restore()

        return canvasBitmap
    }

    suspend private fun saveBidmap(bitmap: Bitmap, filepath: File): File? {

        return null
    }
}