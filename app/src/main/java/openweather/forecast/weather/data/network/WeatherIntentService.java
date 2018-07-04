package openweather.forecast.weather.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import weather.tutorial.com.weather.utils.InjectorUtils;

/**
 * An {@link IntentService} subclass for immediately scheduling a sync with the server off of the
 * main thread. This is necessary because {@link com.firebase.jobdispatcher.FirebaseJobDispatcher}
 * will not trigger a job immediately.
 */
public class WeatherIntentService extends IntentService {
    private static final String LOG_TAG = WeatherIntentService.class.getSimpleName();

    public WeatherIntentService() {
        super("WeatherIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "onHandleIntent");
        WeatherNetworkDataSource networkDataSource = InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchWeather();
    }
}