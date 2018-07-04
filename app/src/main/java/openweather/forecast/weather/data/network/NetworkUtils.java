package openweather.forecast.weather.data.network;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String FORECAST_BASE_URL =
            "https://api.openweathermap.org/data/2.5/forecast";//?q=London,us&appid=97795dadae2bc5aa2a809abf5646d20e";

    private static final String units = "metric";

    // Query Params
    private static final String QUERY_PARAM = "q";
    private static final String UNITS_PARAM = "units";

    /**
     * Retrieves the proper URL for the weather data.
     *
     * @return URL to query weather service
     */
    static URL getUrl() {
        String locationQuery = "Mountain View, CA";
        return buildUrlWithLocationQuery(locationQuery);
    }

    /**
     * Builds the URL for forecast query to OpenWeatherMap API
     *
     * @param locationQuery target location
     * @return URL
     */
    private static URL buildUrlWithLocationQuery(String locationQuery) {
        Uri weatherQueryUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, locationQuery)
                .appendQueryParameter(UNITS_PARAM, units)
                .build();
        try {
            URL weatherQueryUrl = new URL(weatherQueryUri.toString());
            Log.v(TAG, " URL: " + weatherQueryUrl);
            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns result from the HTTP response.
     *
     * @param url target URL
     * @return HTTP response
     * @throws IOException error
     */
    static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            Log.v(TAG, " Response:  " + response);
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}