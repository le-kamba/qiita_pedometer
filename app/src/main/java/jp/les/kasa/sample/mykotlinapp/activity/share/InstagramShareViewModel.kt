package jp.les.kasa.sample.mykotlinapp.activity.share

import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Instagram投稿画面用のViewModel
 * @date 2019-11-15
 **/
class InstagramShareViewModel : ViewModel() {

    // 保存完了を貰う
    private val _savedBitmapFile = MutableLiveData<File>()
    val savedBitmapFile = _savedBitmapFile as LiveData<File>

    /**
     * bitmapを保存する
     * @param bitmap
     * @param dir ファイルを保存するディレクトリFile
     */
    @WorkerThread
    suspend fun createShareImage(bitmap: Bitmap, dir: File) {

        // タイムスタンプをファイル名にする
        val date = Date()
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
        val filepath = File(dir, formatter.format(date) + ".jpg") // jpgにする
        val resultFile = saveBitmap(bitmap, filepath)
        _savedBitmapFile.postValue(resultFile)
    }

    private fun saveBitmap(bitmap: Bitmap, filepath: File): File? {

        // 親ディレクトリまで作成
        filepath.parentFile.mkdirs()

        val fos = FileOutputStream(filepath)
        try {
            // 書込実施
            val result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

            if (result) {
                return filepath
            }

        } catch (e: IOException) {
        } finally {
            fos.close()
        }
        return null
    }
}