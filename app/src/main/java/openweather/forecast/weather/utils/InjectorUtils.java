package openweather.forecast.weather.utils;

import android.content.Context;

import java.util.Date;

import openweather.forecast.weather.AppExecutors;
import openweather.forecast.weather.data.WeatherRepository;
import openweather.forecast.weather.data.database.WeatherDatabase;
import openweather.forecast.weather.data.network.WeatherNetworkDataSource;
import openweather.forecast.weather.ui.main.MainActivityViewModelFactory;

/**
 * For dependency injection
 */
public class InjectorUtils {

    /**
     * Provides Repository - which handles DB/Network APIs. {@link WeatherRepository}
     *
     * @param context
     * @return
     */
    public static WeatherRepository provideRepository(Context context) {
        WeatherDatabase database = WeatherDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        WeatherNetworkDataSource networkDataSource =
                WeatherNetworkDataSource.getInstance(context.getApplicationContext(), executors);
        return WeatherRepository.getInstance(database.weatherDao(), networkDataSource, executors);
    }

    public static WeatherNetworkDataSource provideNetworkDataSource(Context context) {
        AppExecutors executors = AppExecutors.getInstance();
        return WeatherNetworkDataSource.getInstance(context.getApplicationContext(), executors);
    }

    /*public static DetailViewModelFactory provideDetailViewModelFactory(Context context, Date date) {
        SunshineRepository repository = provideRepository(context.getApplicationContext());
        return new DetailViewModelFactory(repository, date);
    }*/

    public static MainActivityViewModelFactory provideMainActivityViewModelFactory(Context context) {
        WeatherRepository repository = provideRepository(context.getApplicationContext());
        return new MainActivityViewModelFactory(repository);
    }
}