package jp.les.kasa.sample.mykotlinapp.activity.logitem.ocr

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.annotation.UiThread
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OcrViewModel : ViewModel() {
    private val _ocrBitmapSource = MutableLiveData<Bitmap>()

    // 選択した画像
    var ocrBitmapSource = _ocrBitmapSource as LiveData<Bitmap>

    // 画像のリスト
    val bitmapSourceList = MutableLiveData<List<Bitmap>>()

    init {
        bitmapSourceList.value = listOf()
    }


    @UiThread
    fun onClickBitmap(view: View) {
        if (view !is ImageView) return

        val sourceImage = view.drawable.toBitmap()
        _ocrBitmapSource.value = sourceImage
    }
}