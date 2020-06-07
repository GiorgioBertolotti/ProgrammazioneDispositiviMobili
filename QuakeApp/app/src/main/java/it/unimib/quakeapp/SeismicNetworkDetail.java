package it.unimib.quakeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

import it.unimib.quakeapp.models.SeismicNetwork;
import it.unimib.quakeapp.models.Seismograph;

import static it.unimib.quakeapp.MainActivity.MAPS_API_KEY;

public class SeismicNetworkDetail extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private SeismicNetwork network;
    private SwipeRefreshLayout pullToRefresh;
    private SeismographsAdapter listAdapter;
    private SeismographsRetriever retriever;
    private ListView seismographsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seismic_network_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.snd_map);
        mapFragment.getMapAsync(this);

        this.network = (SeismicNetwork) getIntent().getSerializableExtra("seismicNetwork");
        getSupportActionBar().setTitle(network.fdsnCode);

        this.retriever = new SeismographsRetriever();
        this.retriever.retrieve(this, null, network.fdsnCode, new Function() {
            @Override
            public Object apply(Object input) {
                listAdapter.notifyDataSetChanged();

                if (retriever.seismographs == null || retriever.seismographs.size() == 0) {
                    hideSeismographs();
                } else {
                    showSeismographs();
                }

                showSeismographsOnMap();
                return null;
            }
        });

        this.seismographsList = findViewById(R.id.snd_seismographs_list);

        this.listAdapter = new SeismographsAdapter(this);
        this.seismographsList.setAdapter(this.listAdapter);

        final Context context = this;
        this.pullToRefresh = findViewById(R.id.snd_pull_to_refresh);
        this.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retriever = new SeismographsRetriever();
                retriever.retrieve(context, null, network.fdsnCode, false, new Function() {
                    @Override
                    public Object apply(Object input) {
                        listAdapter.notifyDataSetChanged();
                        pullToRefresh.setRefreshing(false);

                        if (retriever.seismographs == null || retriever.seismographs.size() == 0) {
                            hideSeismographs();
                        } else {
                            showSeismographs();
                        }

                        showSeismographsOnMap();
                        return null;
                    }
                });
            }
        });

        TextView tvName = findViewById(R.id.snd_name);
        tvName.setText(network.name);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        TextView tvStartDate = findViewById(R.id.snd_start_date);
        tvStartDate.setText(sdf.format(network.startDate));

        TextView tvEndDate = findViewById(R.id.snd_end_date);
        if (network.endDate != null) {
            tvEndDate.setText(sdf.format(network.endDate));
        } else {
            tvEndDate.setText(getString(R.string.snd_end_date_empty));
        }

        Places.initialize(this, MAPS_API_KEY);
    }

    private void hideSeismographs() {
        LinearLayout llMap = findViewById(R.id.snd_map_container);
        llMap.setVisibility(View.GONE);
        TextView tvSeismographs = findViewById(R.id.snd_seismographs_title);
        tvSeismographs.setVisibility(View.GONE);
        SwipeRefreshLayout srlList = findViewById(R.id.snd_pull_to_refresh);
        srlList.setVisibility(View.GONE);

        TextView tvSeismographsEmpty = findViewById(R.id.snd_seismographs_empty);
        tvSeismographsEmpty.setVisibility(View.VISIBLE);
    }

    private void showSeismographs() {
        LinearLayout llMap = findViewById(R.id.snd_map_container);
        llMap.setVisibility(View.VISIBLE);
        TextView tvSeismographs = findViewById(R.id.snd_seismographs_title);
        tvSeismographs.setVisibility(View.VISIBLE);
        SwipeRefreshLayout srlList = findViewById(R.id.snd_pull_to_refresh);
        srlList.setVisibility(View.VISIBLE);

        TextView tvSeismographsEmpty = findViewById(R.id.snd_seismographs_empty);
        tvSeismographsEmpty.setVisibility(View.GONE);
    }

    private void showSeismographsOnMap() {
        if (map != null) {
            double avgLat = 0;
            double avgLng = 0;

            for (Seismograph seismograph : retriever.seismographs) {
                LatLng position = new LatLng(seismograph.latitude, seismograph.longitude);
                map.addMarker(new MarkerOptions().position(position).title(seismograph.station));

                avgLat += seismograph.latitude;
                avgLng += seismograph.longitude;
            }

            avgLat = avgLat / retriever.seismographs.size();
            avgLng = avgLng / retriever.seismographs.size();
            LatLng center = new LatLng(avgLat, avgLng);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 2));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    public class SeismographsAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;

        public SeismographsAdapter(Context context) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mContext = context;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public int getCount() {
            return retriever.seismographs.size();
        }

        @Override
        public Seismograph getItem(int position) {
            return retriever.seismographs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.seismograph_list_item, parent, false);
            }

            final Seismograph seismograph = getItem(position);

            TextView sndTitle = listItem.findViewById(R.id.sndi_title);
            TextView sndLat = listItem.findViewById(R.id.sndi_lat);
            TextView sndLng = listItem.findViewById(R.id.sndi_lng);
            TextView sndElev = listItem.findViewById(R.id.sndi_elev);
            TextView sndStartDate = listItem.findViewById(R.id.sndi_start_date);
            TextView sndEndDate = listItem.findViewById(R.id.sndi_end_date);
            Button identifyButton = listItem.findViewById(R.id.sndi_identify_btn);

            sndTitle.setText(String.format("%s (%s)", seismograph.sitename, seismograph.station));
            sndLat.setText(String.valueOf(seismograph.latitude));
            sndLng.setText(String.valueOf(seismograph.longitude));
            sndElev.setText(String.valueOf(seismograph.elevation));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sndStartDate.setText(sdf.format(seismograph.startTime));
            if (seismograph.endTime != null) {
                sndEndDate.setText(sdf.format(seismograph.endTime));
            } else {
                sndEndDate.setText(getString(R.string.sndi_end_date_empty));
            }

            identifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (map != null) {
                        LatLng center = new LatLng(seismograph.latitude, seismograph.longitude);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 8));
                    }
                }
            });

            View eqiDivider = listItem.findViewById(R.id.sni_divider);
            if (position == retriever.seismographs.size() - 1) {
                eqiDivider.setVisibility(View.GONE);
            }

            return listItem;
        }
    }
}
