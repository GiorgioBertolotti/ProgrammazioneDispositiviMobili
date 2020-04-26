package it.unimib.quakeapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.unimib.quakeapp.models.Earthquake;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static it.unimib.quakeapp.MainActivity.TAG;


public class EarthquakeList extends Fragment {
    private final String EARTHQUAKE_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=100&eventtype=earthquake";
    private ArrayList<Earthquake> earthquakes = new ArrayList<>();
    private EarthquakeListAdapter listAdapter;
    private ListView earthquakeList;

    public EarthquakeList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EarthquakeListRetriever retriever = new EarthquakeListRetriever(EARTHQUAKE_REQUEST_URL);
        retriever.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_earthquake_list, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.earthquakeList = getView().findViewById(R.id.earthquake_list);

        this.listAdapter = new EarthquakeListAdapter(getContext(), R.layout.earthquake_list_item, this.earthquakes);
        this.earthquakeList.setAdapter(this.listAdapter);
    }

    private class EarthquakeListRetriever extends AsyncTask<Void, Void, String> {
        private String url;
        ProgressDialog progressDialog;

        public EarthquakeListRetriever(String url) {
            this.url = url;
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

                    for (int i = 0; i < features.length(); i++) {
                        JSONObject feature = features.getJSONObject(i);
                        earthquakes.add(new Earthquake(feature));
                    }

                    listAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getContext(),
                    getString(R.string.load),
                    getString(R.string.loading_earthquakes));
        }
    }

    public class EarthquakeListAdapter extends ArrayAdapter<Earthquake> {

        public EarthquakeListAdapter(Context context, int textViewResourceId,
                                     List<Earthquake> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.earthquake_list_item, null);

            TextView eqiDate = convertView.findViewById(R.id.eqi_date);
            TextView eqiLocation = convertView.findViewById(R.id.eqi_location);
            TextView eqiRichterMag = convertView.findViewById(R.id.eqi_richter_mag);
            TextView eqiMercalliMag = convertView.findViewById(R.id.eqi_mercalli_mag);

            Earthquake earthquake = earthquakes.get(position);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            eqiDate.setText(sdf.format(earthquake.time));

            String[] words = earthquake.place.split(" ");
            String place = "";

            if (words.length > 3) {
                for (int i = 3; i < words.length; i++) {
                    place += words[i] + " ";
                }
            } else {
                place = earthquake.place;
            }

            eqiLocation.setText(place);
            String richter = Integer.toString((int) Math.floor(earthquake.richter_mag));
            String mercalli = Integer.toString((int) Math.floor(earthquake.mercalli));
            eqiRichterMag.setText(String.format("%s Richter", richter));
            eqiMercalliMag.setText(String.format("%s Mercalli", mercalli));

            return convertView;
        }

    }
}
