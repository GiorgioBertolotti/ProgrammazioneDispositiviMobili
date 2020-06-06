package it.unimib.quakeapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unimib.quakeapp.models.Seismograph;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static it.unimib.quakeapp.MainActivity.TAG;

public class SeismographsRetriever extends AsyncTask<Void, Void, String> {
    private final String SEISMIC_NETWORK_REQUEST_URL = "https://service.iris.edu/irisws/fedcatalog/1/query?net=%s&targetservice=station&level=station&includeoverlaps=true&format=text&nodata=404";

    public List<Seismograph> seismographs = new ArrayList<>();
    private BaseAdapter adapter;
    private Context context;
    private String url;
    private boolean loading;
    private ProgressDialog progressDialog;
    private Function callback;

    public void retrieve(@NonNull Context context, BaseAdapter adapter, String fdsnCode, androidx.arch.core.util.Function callback) {
        this.context = context;
        this.adapter = adapter;
        this.url = String.format(SEISMIC_NETWORK_REQUEST_URL, fdsnCode.toUpperCase());
        this.loading = true;
        this.callback = callback;
        this.execute();
    }

    public void retrieve(@NonNull Context context, BaseAdapter adapter, String fdsnCode, boolean loading, androidx.arch.core.util.Function callback) {
        this.context = context;
        this.adapter = adapter;
        this.url = String.format(SEISMIC_NETWORK_REQUEST_URL, fdsnCode.toUpperCase());
        this.loading = loading;
        this.callback = callback;
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        if (this.loading) {
            progressDialog = ProgressDialog.show(context,
                    context.getString(R.string.load),
                    context.getString(R.string.loading_seismic_network));
        }
    }

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

    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                String lines[] = result.split("\\r?\\n");

                seismographs = new ArrayList<>();

                for (String line : lines) {
                    if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                        continue;
                    }

                    seismographs.add(new Seismograph(line));
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