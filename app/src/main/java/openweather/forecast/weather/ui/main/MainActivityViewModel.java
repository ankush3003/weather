package openweather.forecast.weather.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import openweather.forecast.weather.data.WeatherRepository;
import openweather.forecast.weather.data.database.WeatherEntry;

/**
 * ViewModel for {@link MainActivity}
 */
public class MainActivityViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private final LiveData<List<WeatherEntry>> mData;

    public MainActivityViewModel(WeatherRepository sunshineRepository) {
        mData = sunshineRepository.getCurrentWeatherForecasts();
    }

    public LiveData<List<WeatherEntry>> getForecast() {
        return mData;
    }
}
