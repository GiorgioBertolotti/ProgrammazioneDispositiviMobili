package it.unimib.quakeapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unimib.quakeapp.models.Earthquake;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static it.unimib.quakeapp.MainActivity.TAG;

public class EarthquakeListRetriever extends AsyncTask<Void, Void, String> {
    private final String EARTHQUAKE_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake";
    private final int EARTHQUAKE_PER_REQUEST = 200;
    private int earthquakesLoaded = 0;

    public List<Earthquake> earthquakes = new ArrayList<>();
    private BaseAdapter adapter;
    private Context context;
    private String url;
    private boolean loading;
    private ProgressDialog progressDialog;
    private Function callback;

    public void retrieve(@NonNull Context context, BaseAdapter adapter, Function callback) {
        this.adapter = adapter;
        this.context = context;
        this.url = String.format("%s&limit=%s", EARTHQUAKE_REQUEST_URL, EARTHQUAKE_PER_REQUEST);
        this.loading = true;
        this.callback = callback;
        this.execute();
    }

    public void retrieve(@NonNull Context context, BaseAdapter adapter, Function callback, boolean showProgress) {
        this.adapter = adapter;
        this.context = context;
        this.url = String.format("%s&limit=%s", EARTHQUAKE_REQUEST_URL, EARTHQUAKE_PER_REQUEST);
        this.loading = showProgress;
        this.callback = callback;
        this.execute();
    }

    public void retrieveNext(@NonNull Context context, BaseAdapter adapter, Function<Void, Void> callback) {
        this.adapter = adapter;
        this.context = context;
        this.url = String.format("%s&limit=%s&offset=%s", EARTHQUAKE_REQUEST_URL, EARTHQUAKE_PER_REQUEST, earthquakesLoaded);
        this.loading = true;
        this.callback = callback;
        this.execute();
    }

    public void retrieveNext(@NonNull Context context, BaseAdapter adapter, Function<Void, Void> callback, boolean showProgress) {
        this.adapter = adapter;
        this.context = context;
        this.url = String.format("%s&limit=%s&offset=%s", EARTHQUAKE_REQUEST_URL, EARTHQUAKE_PER_REQUEST, earthquakesLoaded);
        this.loading = showProgress;
        this.callback = callback;
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        if (this.loading) {
            progressDialog = ProgressDialog.show(context,
                    context.getString(R.string.load),
                    context.getString(R.string.loading_earthquakes));
        }
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
                JSONArray features = root.getJSONArray("features");

                earthquakes = new ArrayList<>();

                for (int i = 0; i < features.length(); i++) {
                    JSONObject feature = features.getJSONObject(i);
                    Earthquake earthquake = new Earthquake(feature);
                    earthquakes.add(earthquake);
                }

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

        if (this.loading && progressDialog != null) {
            progressDialog.dismiss();
        }

        if (callback != null) {
            callback.apply(null);
        }
    }
}
