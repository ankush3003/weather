package openweather.forecast.weather.data.network;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;


public class FirebaseJobService extends JobService {
    private static final String LOG_TAG = FirebaseJobService.class.getSimpleName();

    /**
     * Called by JobDispatcher to schedule
     *
     * @return if work remaining.
     */
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.d(LOG_TAG, "Job service started");

        jobFinished(jobParameters, false);
        return true;
    }

    /**
     * Interrupt callback
     *
     * @return if should retry
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}