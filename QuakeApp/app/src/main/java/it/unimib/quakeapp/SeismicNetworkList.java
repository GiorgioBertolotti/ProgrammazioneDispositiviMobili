package it.unimib.quakeapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import it.unimib.quakeapp.models.SeismicNetwork;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static it.unimib.quakeapp.MainActivity.TAG;

public class SeismicNetworkList extends Fragment implements AdapterView.OnItemSelectedListener {

    private final String SEISMIC_NETWORK_REQUEST_URL = "https://www.fdsn.org/ws/networks/1/query?format=geojson";
    private final int SEISMIC_NETWORK_PER_REQUEST = 20;
    private int snLoaded = 0;
    private SeismicNetworkList.SeismicNetworkAdapter listAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private ListView seismicNetworkList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = String.format("%s&limit=%s", SEISMIC_NETWORK_REQUEST_URL, SEISMIC_NETWORK_PER_REQUEST);

        final SeismicNetworkListRetriever retriever = new SeismicNetworkListRetriever(url);
        retriever.execute();

        snLoaded += SEISMIC_NETWORK_PER_REQUEST;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seismic_network_list, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.seismicNetworkList = view.findViewById(R.id.seismic_network_list);

        this.listAdapter = new SeismicNetworkAdapter(view.getContext());
        this.seismicNetworkList.setAdapter(this.listAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class SeismicNetworkListRetriever extends AsyncTask<Void, Void, String> {
        private String url;
        private boolean loading;
        ProgressDialog progressDialog;

        public SeismicNetworkListRetriever(String url) {
            this.url = url;
            this.loading = true;
        }

        public SeismicNetworkListRetriever(String url, boolean loading) {
            this.url = url;
            this.loading = loading;
        }

        @Override
        protected void onPreExecute() {
            if (this.loading) {
                progressDialog = ProgressDialog.show(getContext(),
                        getString(R.string.load),
                        getString(R.string.loading_seismic_networks));
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
                    JSONArray networks = root.getJSONArray("network");

                    List<SeismicNetwork> seismicNetworks = new ArrayList<>();

                    for (int i = 0; i < networks.length(); i++) {
                        JSONObject network = networks.getJSONObject(i);
                        SeismicNetwork sn = new SeismicNetwork(network);
                        seismicNetworks.add(sn);
                    }

                    listAdapter.clear();

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            if (this.loading && progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    public class SeismicNetworkAdapter extends BaseAdapter {
        private static final int TYPE_ITEM = 0;
        private static final int TYPE_HEADER = 1;

        private ArrayList<SeismicNetwork> seismicNetworks = new ArrayList<>();
        private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

        private LayoutInflater mInflater;

        public SeismicNetworkAdapter(Context context) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void clear() {
            seismicNetworks.clear();
            sectionHeader.clear();
        }

        public void addItem(final SeismicNetwork item) {
            seismicNetworks.add(item);
            notifyDataSetChanged();
        }

        public void addSectionHeaderItem(final SeismicNetwork item) {
            seismicNetworks.add(item);
            sectionHeader.add(seismicNetworks.size() - 1);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return sectionHeader.contains(position) ? TYPE_HEADER : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return seismicNetworks.size();
        }

        @Override
        public SeismicNetwork getItem(int position) {
            return seismicNetworks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            int rowType = getItemViewType(position);
            final SeismicNetwork seismicNetwork = seismicNetworks.get(position);

            switch (rowType) {
                case TYPE_ITEM: {
                    convertView = mInflater.inflate(R.layout.seismic_network_list_item, null);

                    TextView snDoi = convertView.findViewById(R.id.sni_doi);
                    TextView snFdsnCode = convertView.findViewById(R.id.sni_fdsn_code);

                    View eqiDivider = convertView.findViewById(R.id.sni_divider);

                    snDoi.setText(seismicNetwork.doi);
                    snFdsnCode.setText(seismicNetwork.fdsnCode);

                    if (position == seismicNetworks.size() - 1 || (seismicNetworks.size() > position + 1 && getItemViewType(position + 1) == TYPE_HEADER)) {
                        eqiDivider.setVisibility(View.GONE);
                    }
                    break;
                }
                case TYPE_HEADER: {
                    convertView = mInflater.inflate(R.layout.earthquake_list_header, null);

                    TextView snHeaderTitle = convertView.findViewById(R.id.sn_header_title);
                    snHeaderTitle.setText("????");
                    break;
                }
            }
            return convertView;
        }
    }
}