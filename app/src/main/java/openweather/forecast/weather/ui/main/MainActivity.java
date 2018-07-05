package openweather.forecast.weather.ui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import openweather.forecast.weather.R;
import openweather.forecast.weather.data.database.WeatherEntry;
import openweather.forecast.weather.utils.InjectorUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // Get ViewModel and start observing data.
        getViewModel().getForecast().observe(this, weatherEntries -> {
            if (weatherEntries != null && weatherEntries.size() != 0) {
                showWeatherDataView(weatherEntries);
            } else {
                showLoading();
            }
        });

    }

    private void showWeatherDataView(List<WeatherEntry> weatherEntries) {
        Toast.makeText(this, "showWeatherDataView called: " + weatherEntries.size(), Toast.LENGTH_LONG).show();
    }

    private void showLoading() {
        Toast.makeText(this, "showLoading called", Toast.LENGTH_SHORT).show();
    }

    /**
     * Gets ViewModel from MainViewModelFactory instantiated using dependency injection
     *
     * @return MainActivityViewModel
     */
    private MainActivityViewModel getViewModel() {
        MainActivityViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(this);
        return ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
