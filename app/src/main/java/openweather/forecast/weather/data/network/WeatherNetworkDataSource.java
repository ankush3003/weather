package openweather.forecast.weather.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import openweather.forecast.weather.AppExecutors;
import openweather.forecast.weather.data.database.WeatherEntry;

/**
 * Provides an API for doing all operations with the server data
 */
public class WeatherNetworkDataSource {
    // Default no of days in forecast api is 5
    public static final int NUM_DAYS = 5;
    private static final String LOG_TAG = WeatherNetworkDataSource.class.getSimpleName();

    // Interval at which to sync with the weather.
    private static final int SYNC_INTERVAL_HOURS = 12;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String SYNC_TAG = "openweather-sync";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static WeatherNetworkDataSource sInstance;
    private final Context mContext;

    private final AppExecutors mExecutors;

    // LiveData storing the latest downloaded weather forecasts
    private final MutableLiveData<WeatherEntry[]> mDownloadedWeatherForecasts;

    private WeatherNetworkDataSource(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
        mDownloadedWeatherForecasts = new MutableLiveData<WeatherEntry[]>();
    }

    /**
     * Get the singleton for this class
     */
    public static WeatherNetworkDataSource getInstance(Context context, AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new WeatherNetworkDataSource(context.getApplicationContext(), executors);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public LiveData<WeatherEntry[]> getCurrentWeatherForecasts() {
        return mDownloadedWeatherForecasts;
    }

    /**
     * Starts an intent service to fetch the weather.
     */
    public void startFetchWeatherService() {
        Intent intentToFetch = new Intent(mContext, WeatherIntentService.class);
        mContext.startService(intentToFetch);
    }

    /**
     * Schedules a repeating job service which fetches the weather.
     */
    public void scheduleRecurringFetchWeatherSync() {
        Driver driver = new GooglePlayDriver(mContext);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the Job to periodically sync weather
        Job syncSunshineJob = dispatcher.newJobBuilder()
                .setService(FirebaseJobService.class)
                .setTag(SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        // Schedule job
        dispatcher.schedule(syncSunshineJob);
        Log.d(LOG_TAG, "Job scheduled");
    }

    /**
     * Gets latest weather
     */
    void fetchWeather() {
        mExecutors.networkIO().execute(() -> {
            try {
                URL weatherRequestUrl = NetworkUtils.getUrl();
                Log.d(LOG_TAG, "URL: " + weatherRequestUrl);

                String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);

                // Parse response
                WeatherResponse response = new OpenWeatherJsonParser().parse(jsonWeatherResponse);
                Log.d(LOG_TAG, "JSON Parsing finished");

                // trigger observers of LiveData
                if (response != null && response.getWeatherForecast().length != 0) {
                    Log.d(LOG_TAG, "JSON not null and has " + response.getWeatherForecast().length
                            + " values");

                    mDownloadedWeatherForecasts.postValue(response.getWeatherForecast());
                }
            } catch (Exception e) {
                // Server probably invalid
                Log.e(LOG_TAG, "ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

}