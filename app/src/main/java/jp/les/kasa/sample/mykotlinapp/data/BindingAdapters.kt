package jp.les.kasa.sample.mykotlinapp.data

import android.view.View
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.les.kasa.sample.mykotlinapp.R
import jp.les.kasa.sample.mykotlinapp.activity.main.LogRecyclerAdapter
import jp.les.kasa.sample.mykotlinapp.di.CalendarProviderI
import jp.les.kasa.sample.mykotlinapp.di.byKoinInject
import jp.les.kasa.sample.mykotlinapp.utils.equalsYMD
import jp.les.kasa.sample.mykotlinapp.utils.getMonth
import java.util.*


/**
 * DataBinding用
 * @date 2019/06/18
 **/

@BindingAdapter("android:src")
fun setImageLevel(view: ImageView, level: LEVEL?) {
    if (level == null) {
        view.visibility = View.GONE
        return
    }
    val res =
        when (level) {
            LEVEL.GOOD -> R.drawable.ic_sentiment_very_satisfied_pink_24dp
            LEVEL.BAD -> R.drawable.ic_sentiment_dissatisfied_black_24dp
            else -> R.drawable.ic_sentiment_neutral_green_24dp
        }
    view.visibility = View.VISIBLE
    view.setImageResource(res)
}

@BindingAdapter("android:src")
fun setImageWeather(view: ImageView, weather: WEATHER?) {
    if (weather == null) {
        view.visibility = View.GONE
        return
    }
    val res =
        when (weather) {
            WEATHER.RAIN -> R.drawable.ic_iconmonstr_umbrella_1
            WEATHER.CLOUD -> R.drawable.ic_cloud_gley_24dp
            WEATHER.SNOW -> R.drawable.ic_grain_gley_24dp
            WEATHER.COLD -> R.drawable.ic_iconmonstr_weather_64
            WEATHER.HOT -> R.drawable.ic_flare_red_24dp
            else -> R.drawable.ic_wb_sunny_yellow_24dp
        }
    view.visibility = View.VISIBLE
    view.setImageResource(res)
}

@BindingAdapter("items")
fun setLogItems(view: RecyclerView, logs: List<CalendarCellData>?) {
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

@BindingAdapter("day")
fun setDayLabel(view: TextView, calendar: Calendar) {
    view.text = calendar.get(Calendar.DATE).toString()
}

@BindingAdapter(value = ["android:background", "month"], requireAll = true)
fun setCellBackground(view: View, cellDate: Calendar, month: Int) {
    val calendarProvider: CalendarProviderI = byKoinInject()
    val now = calendarProvider.now
    val m = cellDate.getMonth()
    when {
        cellDate.equalsYMD(now) -> {
            view.setBackgroundResource(R.drawable.cell_active)
        }
        m + 1 == month -> {
            view.setBackgroundResource(R.drawable.cell_nonactive)
        }
        else -> {
            view.setBackgroundResource(R.drawable.cell_nonactive_grey)
        }
    }
}

