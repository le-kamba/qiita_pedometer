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
            val f = File(filePath).parentFile!!
            if (f.exists() && f.listFiles().isNotEmpty()) {
                return
            }

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

fun File.isImageFile(): Boolean {
    if (isDirectory) return false
    if (extension == "png") return true
    if (extension == "PNG") return true
    if (extension == "jpg") return true
    if (extension == "JPG") return true
    if (extension == "jpeg") return true
    if (extension == "JPEG") return true
    return false
}

/**
 * カメラのディレクトリにある固定画像を読み込み
 */
fun getSrcBitmaps(): List<Bitmap> {
    val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
    val dir = File(root, "Camera")
    val files = dir.walkTopDown().filter { it.isImageFile() }.sortedByDescending { it.lastModified() }

    val bitmapList = mutableListOf<Bitmap>()
    val options = BitmapFactory.Options()
    options.inSampleSize = 4

    run loop@{
        files.forEach { file ->
            if (file.exists()) {
                Log.d("MYOCR", "file = ${file.name}")
                val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
                bitmapList.add(bitmap)
                if (bitmapList.size > 6) return@loop
            }
        }
    }
    return bitmapList
}

