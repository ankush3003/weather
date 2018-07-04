package openweather.forecast.weather.data.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * {@link TypeConverter} for long to {@link Date}
 */
public class DateConvertor {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
