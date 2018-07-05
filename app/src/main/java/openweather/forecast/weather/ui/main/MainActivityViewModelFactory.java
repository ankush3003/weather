package openweather.forecast.weather.ui.main;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import openweather.forecast.weather.data.WeatherRepository;


/**
 * Factory to create {@link MainActivityViewModel}
 */
public class MainActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final WeatherRepository mRepository;

    public MainActivityViewModelFactory(WeatherRepository repository) {
        this.mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainActivityViewModel(mRepository);
    }
}
