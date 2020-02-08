package jp.les.kasa.sample.mykotlinapp.data

import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.activity.main.LogRecyclerAdapter
import jp.les.kasa.sample.mykotlinapp.activity.main.MonthlyPagerAdapter


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

@BindingAdapter("items")
fun setLogItems(view: RecyclerView, logs: List<StepCountLog>?) {
    val adapter = view.adapter as LogRecyclerAdapter? ?: return

    logs?.let {
        adapter.setList(logs)
    }
}

@BindingAdapter("selected")
fun selectWeather(view: Spinner, weather: WEATHER) {
    view.setSelection(weather.ordinal)
}

@BindingAdapter("yearMonth")
fun setDataYearMonth(view: TextView, yearMonth: String?) {
    if (yearMonth == null) return
    val date = yearMonth.split('/')
    val str = view.context.getString(R.string.year_month_label, date[0], Integer.valueOf(date[1]))
    view.text = str
}

@BindingAdapter("items")
fun setPageItems(view: ViewPager2, pages: List<String>?) {
    val adapter = view.adapter as MonthlyPagerAdapter? ?: return

    pages?.let {
        adapter.setList(it)
        view.setCurrentItem(it.size - 1, false)
    }
}