package it.unimib.quakeapp;

import android.os.AsyncTask;
import android.util.Log;

import androidx.arch.core.util.Function;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static it.unimib.quakeapp.MainActivity.TAG;

public class EarthquakeCountRetriever extends AsyncTask<Void, Void, String> {
    private final String EARTHQUAKE_COUNT_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/count?format=geojson&starttime=%s&endtime=%s";

    public int daily = 0;
    public int weekly = 0;
    boolean isDaily;
    protected String url;
    private Function callback;

    public void retrieveDaily(Function callback) {
        this.isDaily = true;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        this.url = String.format(EARTHQUAKE_COUNT_REQUEST_URL, sdf.format(today), sdf.format(tomorrow));
        this.callback = callback;
        this.execute();
    }

    public void retrieveWeekly(Function callback) {
        this.isDaily = false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -8);
        Date lastWeek = calendar.getTime();

        this.url = String.format(EARTHQUAKE_COUNT_REQUEST_URL, sdf.format(lastWeek), sdf.format(tomorrow));
        this.callback = callback;
        this.execute();
    }

    @Override
    protected String doInBackground(Void... params) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(this.url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                JSONObject root = new JSONObject(result);
                int count = root.getInt("count");

                if (isDaily) {
                    this.daily = count;
                } else {
                    this.weekly = count;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

        if (callback != null) {
            callback.apply(null);
        }
    }
}
