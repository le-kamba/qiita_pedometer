package jp.les.kasa.sample.mykotlinapp.data

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.les.kasa.sample.mykotlinapp.LogRecyclerAdapter
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.activity.logitem.ocr.BitmapRecyclerAdapter


/**
 * DataBinding用
 * @date 2019/06/18
 **/

@BindingAdapter("android:src")
fun setImageLevel(view: ImageView, level: LEVEL) {
    val res =
        when (level) {
            LEVEL.GOOD -> R.drawable.ic_sentiment_very_satisfied_pink_24dp
            LEVEL.BAD -> R.drawable.ic_sentiment_dissatisfied_black_24dp
            else -> R.drawable.ic_sentiment_neutral_green_24dp
        }
    view.setImageResource(res)
}

@BindingAdapter("android:src")
fun setImageWeather(view: ImageView, level: WEATHER) {
    val res =
        when (level) {
            WEATHER.RAIN -> R.drawable.ic_iconmonstr_umbrella_1
            WEATHER.CLOUD -> R.drawable.ic_cloud_gley_24dp
            WEATHER.SNOW -> R.drawable.ic_grain_gley_24dp
            WEATHER.COLD -> R.drawable.ic_iconmonstr_weather_64
            WEATHER.HOT -> R.drawable.ic_flare_red_24dp
            else -> R.drawable.ic_wb_sunny_yellow_24dp
        }
    view.setImageResource(res)
}

@BindingAdapter("app:items")
fun setLogItems(view: RecyclerView, logs: List<StepCountLog>?) {
    val adapter = view.adapter as LogRecyclerAdapter? ?: return

    logs?.let {
        adapter.setList(logs)
    }
}

@BindingAdapter("app:items")
fun setBitmapItems(view: RecyclerView, bitmaps: List<Bitmap>?) {
    val adapter = view.adapter as BitmapRecyclerAdapter? ?: return

    bitmaps?.let {
        adapter.setList(bitmaps)
    }
}

@BindingAdapter("android:src")
fun setOcrImageBitmap(view: ImageView, bitmap: Bitmap) {
    view.setImageBitmap(bitmap)
}