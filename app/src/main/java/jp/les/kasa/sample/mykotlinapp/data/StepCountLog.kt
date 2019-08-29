package jp.les.kasa.sample.mykotlinapp.data

import androidx.room.*
import java.io.Serializable

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

class LevelConverter {

    @TypeConverter
    fun levelToString(level: LEVEL): String {
        return level.name
    }

    @TypeConverter
    fun stringToLevel(levelString: String): LEVEL {
        return LEVEL.valueOf(levelString)
    }
}

class WeatherConverter {

    @TypeConverter
    fun weatherToString(weather: WEATHER): String {
        return weather.name
    }

    @TypeConverter
    fun stringToWeather(weatherString: String): WEATHER {
        return WEATHER.valueOf(weatherString)
    }
}


@Entity(tableName = "log_table")
@TypeConverters(LevelConverter::class, WeatherConverter::class)
data class StepCountLog(
    @PrimaryKey @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "step") val step: Int,
    @ColumnInfo(name = "level") val level: LEVEL = LEVEL.NORMAL,
    @ColumnInfo(name = "weather") val weather: WEATHER = WEATHER.FINE
) : Serializable
