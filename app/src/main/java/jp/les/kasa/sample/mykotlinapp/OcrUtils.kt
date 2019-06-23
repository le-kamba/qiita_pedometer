package jp.les.kasa.sample.mykotlinapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

private val TESS_DATA_DIR = "tessdata" + File.separator
private val TESS_TRAINED_DATA = arrayListOf("eng.traineddata")
fun copyFiles(context: Context) {
    try {
        TESS_TRAINED_DATA.forEach {
            val filePath = context.filesDir.toString() + File.separator + TESS_DATA_DIR + it
            val f = File(context.filesDir.toString() + File.separator + TESS_DATA_DIR)
            f.mkdirs()

            // assets以下をinputStreamでopenしてbaseApi.initで読み込める領域にコピー
            context.assets.open(TESS_DATA_DIR + it).use { inputStream ->
                FileOutputStream(filePath).use { outStream ->
                    val buffer = ByteArray(1024)
                    var read = inputStream.read(buffer)
                    while (read != -1) {
                        outStream.write(buffer, 0, read)
                        read = inputStream.read(buffer)
                    }
                    outStream.flush()
                }
            }

            val file = File(filePath)
            if (!file.exists()) throw FileNotFoundException()
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

/**
 * カメラのディレクトリにある固定画像を読み込み
 */
fun getSrcBitmaps(): List<Bitmap> {
    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
    val files = listOf(
        File(dir, "Camera/IMG_20190622_165554.jpg"),
        File(dir, "Camera/IMG_20190622_180008~2.jpg"),
        File(dir, "Camera/IMG_20190622_174454.jpg"),
        File(dir, "Camera/IMG_20190622_174413.jpg"),
        File(dir, "Camera/MVIMG_20190622_230731.jpg"),
        File(dir, "Camera/IMG_20190622_231028.jpg")
    )

    val bitmapList = mutableListOf<Bitmap>()
    files.forEach { file ->
        if (file.exists()) {
            Log.d("MYOCR", "file = ${file.name}")
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            bitmapList.add(bitmap)
        }
    }
    return bitmapList
}