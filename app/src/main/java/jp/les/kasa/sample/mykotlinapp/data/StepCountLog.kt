package jp.les.kasa.sample.mykotlinapp.data

enum class LEVEL {
    NORMAL,
    GOOD,
    BAD,
}

enum class WEATHER {
    FINE,
    RAIN,
    CLOUD,
    SNOW,
    COLD,
    HOT,
}

data class StepCountLog(
    val date: String,
    val step: Int,
    val level: LEVEL = LEVEL.NORMAL,
    val weather: WEATHER = WEATHER.FINE
)
