package jp.les.kasa.sample.mykotlinapp.data

import jp.les.kasa.sample.mykotlinapp.R

enum class LEVEL(val drawableRes: Int) {
    NORMAL(R.drawable.ic_sentiment_neutral_green_24dp),
    GOOD(R.drawable.ic_sentiment_very_satisfied_pink_24dp),
    BAD(R.drawable.ic_sentiment_dissatisfied_black_24dp),
}

enum class WEATHER(val drawableRes: Int) {
    FINE(R.drawable.ic_wb_sunny_yellow_24dp),
    RAIN(R.drawable.ic_iconmonstr_umbrella_1),
    CLOUD(R.drawable.ic_cloud_gley_24dp),
    SNOW(R.drawable.ic_grain_gley_24dp),
    COLD(R.drawable.ic_iconmonstr_weather_64),
    HOT(R.drawable.ic_flare_red_24dp)
}

data class StepCountLog(
    val date: String,
    val step: Int,
    val level: LEVEL = LEVEL.NORMAL,
    val weather: WEATHER = WEATHER.FINE
)
