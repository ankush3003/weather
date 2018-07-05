package openweather.forecast.weather.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.Date;
import java.util.List;

import openweather.forecast.weather.AppExecutors;
import openweather.forecast.weather.data.database.WeatherDao;
import openweather.forecast.weather.data.database.WeatherEntry;
import openweather.forecast.weather.data.network.WeatherNetworkDataSource;
import openweather.forecast.weather.utils.DateUtils;

/**
 * Handles data operations
 */
public class WeatherRepository {
    private static final String TAG = WeatherRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static WeatherRepository sInstance;
    private final WeatherDao mWeatherDao;
    private final WeatherNetworkDataSource mWeatherNetworkDataSource;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;

    private WeatherRepository(WeatherDao weatherDao,
                              WeatherNetworkDataSource weatherNetworkDataSource,
                              AppExecutors executors) {
        mWeatherDao = weatherDao;
        mWeatherNetworkDataSource = weatherNetworkDataSource;
        mExecutors = executors;

        LiveData<WeatherEntry[]> networkData = mWeatherNetworkDataSource.getCurrentWeatherForecasts();
        networkData.observeForever(newForecastsFromNetwork -> {
            mExecutors.diskIO().execute(() -> {
                deleteOldData();

                mWeatherDao.bulkInsert(newForecastsFromNetwork);
                Log.d(TAG, "New values inserted");
            });
        });
    }

    public synchronized static WeatherRepository getInstance(
            WeatherDao weatherDao, WeatherNetworkDataSource weatherNetworkDataSource,
            AppExecutors executors) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new WeatherRepository(weatherDao, weatherNetworkDataSource,
                        executors);
            }
        }
        return sInstance;
    }

    /**
     * init fetch if not already done or sufficient data not present (5 days)
     */
    public synchronized void initializeData() {
        if (mInitialized) return;
        mInitialized = true;

        mExecutors.diskIO().execute(() -> {
            if (isFetchNeeded()) {
                startFetchWeatherService();
            }
        });
    }

    /**
     * Deletes old weather data
     */
    private void deleteOldData() {
        Date today = DateUtils.getNormalizedUtcDateForToday();
        mWeatherDao.deleteOldData(today);
    }

    /**
     * Checks if there are 5 days future data present or not
     *
     * @return Whether a fetch is needed
     */
    private boolean isFetchNeeded() {
        Date startDate = DateUtils.getNormalizedUtcDateForToday();
        return (mWeatherDao.countAllFutureWeather(startDate) < WeatherNetworkDataSource.NUM_DAYS);
    }

    /**
     * Starts fetch weather service
     */
    private void startFetchWeatherService() {
        mWeatherNetworkDataSource.startFetchWeatherService();
    }

    public LiveData<WeatherEntry> getWeatherByDate(Date date) {
        initializeData();
        return mWeatherDao.getWeatherByDate(date);
    }

    public LiveData<List<WeatherEntry>> getCurrentWeatherForecasts() {
        initializeData();
        Date today = DateUtils.getNormalizedUtcDateForToday();
        return mWeatherDao.getCurrentWeatherForecasts(today);
    }
}