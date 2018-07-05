package openweather.forecast.weather.data.network;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Date;

import openweather.forecast.weather.data.database.WeatherEntry;
import openweather.forecast.weather.utils.DateUtils;

/**
 * Parser for OpenWeatherMap JSON data.
 */
final class OpenWeatherJsonParser {
    private static final String OWM_MESSAGE_CODE = "cod";

    // Each forecast as "list" array
    private static final String OWM_LIST = "list";

    private static final String OWM_LIST_DATE = "dt";
    private static final String OWM_LIST_DATE_TXT = "dt_txt";

    // main array
    private static final String OWM_LIST_MAIN = "main";
    private static final String OWM_LIST_MAIN_TEMP = "temp";
    private static final String OWM_LIST_MAIN_TEMP_MIN = "temp_min";
    private static final String OWM_LIST_MAIN_TEMP_MAX = "temp_max";
    private static final String OWM_LIST_MAIN_PRESSURE = "pressure";
    private static final String OWM_LIST_MAIN_SEA_LEVEL = "sea_level";
    private static final String OWM_LIST_MAIN_GROUND_LEVEL = "grnd_level";
    private static final String OWM_LIST_MAIN_HUMIDITY = "humidity";

    // Weather array
    private static final String OWM_LIST_WEATHER = "weather";
    private static final String OWM_LIST_WEATHER_ID = "id";
    private static final String OWM_LIST_WEATHER_MAIN = "main";
    private static final String OWM_LIST_WEATHER_DESCRIPTION = "description";
    private static final String OWM_LIST_WEATHER_ICON = "icon";

    // wind
    private static final String OWM_LIST_WIND = "wind";
    private static final String OWM_LIST_WIND_SPEED = "speed";
    private static final String OWM_LIST_WIND_DIRECTION = "deg";

    // city - location
    private static final String OWM_MESSAGE_CITY = "city";
    private static final String OWM_MESSAGE_CITY_NAME = "name";
    private static final String OWM_MESSAGE_CITY_COUNTRY = "country";
    private static final String OWM_MESSAGE_CITY_population = "population";

    // verifies Response Code
    private static boolean hasHttpError(JSONObject forecastJson) throws JSONException {
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    return false;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // Location invalid
                default:
                    // Server down OR API limit exceeded
                    return true;
            }
        }
        return false;
    }

    private static WeatherEntry[] fromJson(final JSONObject forecastJson) throws JSONException {
        JSONArray jsonWeatherListArray = forecastJson.getJSONArray(OWM_LIST);

        // Extract values from 'city' object
        String cityCountryName = "";
        double cityPopulation = 0.0;
        JSONObject cityObject = forecastJson.optJSONObject(OWM_MESSAGE_CITY);
        if(cityObject != null) {
            cityCountryName = cityObject.optString(OWM_MESSAGE_CITY_NAME) + ", " +
                    cityObject.optString(OWM_MESSAGE_CITY_COUNTRY);
            cityPopulation = cityObject.optDouble(OWM_MESSAGE_CITY_population);
        }
        WeatherEntry[] weatherEntries = new WeatherEntry[jsonWeatherListArray.length()];

        long normalizedUtcStartDay = DateUtils.getNormalizedUtcMsForToday();

        for (int i = 0; i < jsonWeatherListArray.length(); i++) {
            // Get the JSON object representing the day
            JSONObject dayForecast = jsonWeatherListArray.getJSONObject(i);

            // Create the weather entry object
            long dateTimeMillis = normalizedUtcStartDay + DateUtils.DAY_IN_MILLIS * i;
            WeatherEntry weather = fromJson(dayForecast, dateTimeMillis, cityCountryName, cityPopulation);

            weatherEntries[i] = weather;
        }
        return weatherEntries;
    }

    private static WeatherEntry fromJson(final JSONObject dayForecast,
                                         long dateTimeMillis,
                                         String cityName,
                                         double cityPopulation) throws JSONException {
        // We ignore all the datetime values embedded in the JSON and assume that
        // the values are returned in-order by day (which is not guaranteed to be correct).


        // Extract avlues from 'main' object
        double current_temp, min_temp, max_temp, pressure, sea_level, ground_level;
        current_temp = min_temp = max_temp = pressure = sea_level = ground_level = 0;
        int humidity = 0;
        JSONObject mainObject = dayForecast.optJSONObject(OWM_LIST_MAIN);
        if(mainObject != null) {
            current_temp = mainObject.optDouble(OWM_LIST_MAIN_TEMP);
            min_temp = mainObject.optDouble(OWM_LIST_MAIN_TEMP_MIN);
            max_temp = mainObject.optDouble(OWM_LIST_MAIN_TEMP_MAX);
            pressure = mainObject.optDouble(OWM_LIST_MAIN_PRESSURE);
            sea_level = mainObject.optDouble(OWM_LIST_MAIN_SEA_LEVEL);
            ground_level = mainObject.optDouble(OWM_LIST_MAIN_GROUND_LEVEL);
            humidity = mainObject.optInt(OWM_LIST_MAIN_HUMIDITY);
        }

        // Extract values from 'weather' object (only 1 entry)
        int weatherId = 0;
        String main, description = "", icon;
        JSONObject weatherObject = dayForecast.optJSONArray(OWM_LIST_WEATHER).getJSONObject(0);
        if(weatherObject != null) {
            weatherId = weatherObject.getInt(OWM_LIST_WEATHER_ID);
            main = weatherObject.optString(OWM_LIST_WEATHER_MAIN);
            description = weatherObject.optString(OWM_LIST_WEATHER_DESCRIPTION);
            icon = weatherObject.optString(OWM_LIST_WEATHER_ICON);
        }

        // Extract values from 'wind' object
        double windSpeed, windDirection;
        windSpeed = windDirection = 0;
        JSONObject windObject = dayForecast.optJSONObject(OWM_LIST_WIND);
        if(windObject != null) {
            windSpeed = windObject.optDouble(OWM_LIST_WIND_SPEED);
            windDirection = windObject.optDouble(OWM_LIST_WIND_DIRECTION);
        }

        // Create the weather entry object
        return new WeatherEntry(new Date(dateTimeMillis), current_temp, min_temp, max_temp, pressure, sea_level, ground_level, humidity,
                weatherId, description,
                windSpeed, windDirection,
                cityName);//, cityPopulation);
    }

    /**
     * Parses JSON from response
     *
     * @param forecastJsonStr JSON response
     * @return Array of Strings describing weather data
     * @throws JSONException If error
     */
    @Nullable
    WeatherResponse parse(final String forecastJsonStr) throws JSONException {
        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        // Is there an error?
        if (hasHttpError(forecastJson)) {
            return null;
        }

        WeatherEntry[] weatherForecast = fromJson(forecastJson);

        return new WeatherResponse(weatherForecast);
    }
}