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
import it.unimib.quakeapp.models.SeismicNetwork;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static it.unimib.quakeapp.MainActivity.TAG;

public class SeismicNetworkListRetriever extends AsyncTask<Void, Void, String> {

    private final String SEISMIC_NETWORK_REQUEST_URL = "https://www.fdsn.org/ws/networks/1/query?format=geojson";
    private final int SEISMIC_NETWORK_PER_REQUEST = 20;
    private int snLoaded = 0;

    private List<SeismicNetwork> _seismicNetworks = new ArrayList<>();
    private List<SeismicNetwork> _filteredSeismicNetworks = null;
    private BaseAdapter adapter;
    private Context context;
    private String url;
    private boolean loading;
    private ProgressDialog progressDialog;
    private Function callback;

    public List<SeismicNetwork> seismicNetworks() {
        if (_filteredSeismicNetworks != null) {
            return _filteredSeismicNetworks;
        }

        return _seismicNetworks;
    }

    public void setFilter(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            _filteredSeismicNetworks = null;
            return;
        }

        _filteredSeismicNetworks = new ArrayList<>();

        for (SeismicNetwork seismicNetwork : _seismicNetworks) {
            if (seismicNetwork.doi.toLowerCase().contains(filterText.toLowerCase())
                    || seismicNetwork.fdsnCode.toLowerCase().contains(filterText.toLowerCase())) {
                _filteredSeismicNetworks.add(seismicNetwork);
            }
        }
    }

    public void retrieve(@NonNull Context context, BaseAdapter adapter, androidx.arch.core.util.Function callback) {
        this.adapter = adapter;
        this.context = context;
        this.url = String.format("%s&limit=%s", SEISMIC_NETWORK_REQUEST_URL, SEISMIC_NETWORK_PER_REQUEST);
        this.loading = true;
        this.callback = callback;
        this.execute();
    }

    public void retrieve(@NonNull Context context, BaseAdapter adapter, boolean loading, androidx.arch.core.util.Function callback) {
        this.adapter = adapter;
        this.context = context;
        this.url = String.format("%s&limit=%s", SEISMIC_NETWORK_REQUEST_URL, SEISMIC_NETWORK_PER_REQUEST);
        this.loading = loading;
        this.callback = callback;
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        if (this.loading) {
            progressDialog = ProgressDialog.show(context,
                    context.getString(R.string.load),
                    context.getString(R.string.loading_seismic_networks));
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
                JSONObject root = new JSONObject(result);
                JSONArray networks = root.getJSONArray("networks");

                _seismicNetworks = new ArrayList<>();

                for (int i = 0; i < networks.length(); i++) {
                    JSONObject network = networks.getJSONObject(i);
                    SeismicNetwork sn = new SeismicNetwork(network);
                    _seismicNetworks.add(sn);
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