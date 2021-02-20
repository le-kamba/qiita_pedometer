package jp.les.kasa.sample.mykotlinapp.activity.share

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import jp.les.kasa.sample.mykotlinapp.di.EnvironmentProviderI
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Instagram投稿画面用のViewModel
 * @date 2019-11-15
 **/
class InstagramShareViewModel(application: Application,
                              private val environmentProvider: EnvironmentProviderI)
    : AndroidViewModel(application) {

    // 保存完了を貰う
    private val _savedBitmapUri = MutableLiveData<Uri>()
    val savedBitmapUri = _savedBitmapUri as LiveData<Uri>

    /**
     * bitmapを保存する
     * @param bitmap
     */
    @WorkerThread
    suspend fun createShareImage(bitmap: Bitmap) {

        // タイムスタンプをファイル名にする
        val date = Date()
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
        val displayName = formatter.format(date) + ".jpg" // jpgにする
        val resultUri = saveBitmap(bitmap, displayName)
        _savedBitmapUri.postValue(resultUri)
    }

    private fun saveBitmap(bitmap: Bitmap, displayName: String): Uri? {
        // 外部ストレージが使えるかチェック
        if (!environmentProvider.isExternalStorageMounted()) return null

        // Q以上とP以下で処理を分ける
        return if (Build.VERSION.SDK_INT > 28) {
            // Pより大きいAPIレベル
            saveBitmapOver28(bitmap, displayName)
        } else {
            // Q未満のAPIレベル
            saveBitmapUnder29(bitmap, displayName)
        }
    }

    @VisibleForTesting
    @Suppress("DEPRECATION")
    fun saveBitmapUnder29(bitmap: Bitmap, displayName: String): Uri? {
        // 先にファイルを保存する
        val dir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "qiita_pedometer"
        )
        val filepath = File(dir, displayName)
        // 親ディレクトリまで作成
        filepath.parentFile?.mkdirs()

        val fos = FileOutputStream(filepath)
        try {
            // 書込実施
            val result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

            if (result) {
                // ContentResolverを取得
                val resolver = getApplication<Application>().contentResolver

                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
                    put(MediaStore.Images.Media.TITLE, displayName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
                    put(MediaStore.Images.Media.DATA, filepath.absolutePath)
                    put(
                        MediaStore.Images.Media.DATE_ADDED,
                        System.currentTimeMillis() / 1000
                    ) // should be in unit of seconds
                }
                // ContentResolverに挿入
                return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }

        } catch (e: IOException) {
        } finally {
            fos.close()
        }
        return null
    }

    @VisibleForTesting
    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveBitmapOver28(bitmap: Bitmap, displayName: String): Uri? {
        // ContentResolverを取得
        val resolver = getApplication<Application>().contentResolver

        // Imagesコレクションを取得
        val collection = MediaStore.Images.Media
            .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        // 挿入するContentValueをセット
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/qiita_pedometer"
            )
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.TITLE, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.Images.Media.DATE_ADDED,
                System.currentTimeMillis() / 1000
            ) // should be in unit of seconds
            // 排他制御
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }

        // ContentValueを挿入し、アイテムのUriを得る
        val item = resolver.insert(collection, contentValues) ?: return null

        // Uriを得てから、そこに実際のデータを書き込む
        val fos = resolver.openOutputStream(item)
        try {
            // 書込実施
            val result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

            if (result) {
                return item
            }
        } catch (e: IOException) {
        } finally {
            fos?.close()

            // 排他制御を解除
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(item, contentValues, null, null)
        }
        return null
    }
}