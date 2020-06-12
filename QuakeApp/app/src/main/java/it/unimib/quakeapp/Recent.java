package it.unimib.quakeapp;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.unimib.quakeapp.models.Earthquake;

public class Recent extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private EarthquakeListRetriever retriever;
    private GoogleMap map;
    private Map<Marker, Earthquake> markerEarthquakeMap = new HashMap<>();
    private RelativeLayout rlAllEarthquake;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.retriever = new EarthquakeListRetriever();
        this.retriever.retrieve(getContext(), null, new Function() {
            @Override
            public Object apply(Object input) {
                showEarthquakesOnMap();
                populateTopEarthquake();
                return null;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setCards();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        populateTopEarthquake();
        
        this.rlAllEarthquake = getActivity().findViewById(R.id.recent_all_quakes);
        this.rlAllEarthquake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.menu_earthquakes));
                EarthquakeList nextFrag = new EarthquakeList();

                View currentFocus = getActivity().getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStackImmediate();
                }
                ft.replace(R.id.nav_host_fragment, nextFrag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void showEarthquakesOnMap() {
        if (map != null) {
            for (Map.Entry<Marker, Earthquake> entry : markerEarthquakeMap.entrySet()) {
                entry.getKey().remove();
            }

            markerEarthquakeMap.clear();

            double avgLat = 0;
            double avgLng = 0;

            for (Earthquake earthquake : retriever.earthquakes) {
                LatLng position = new LatLng(earthquake.coordinates.lat, earthquake.coordinates.lng);

                MarkerOptions options = new MarkerOptions();
                options.position(position);
                options.title(earthquake.placeDesc);

                Marker marker = map.addMarker(options);

                avgLat += earthquake.coordinates.lat;
                avgLng += earthquake.coordinates.lng;

                markerEarthquakeMap.put(marker, earthquake);
            }

            avgLat = avgLat / retriever.earthquakes.size();
            avgLng = avgLng / retriever.earthquakes.size();
            LatLng center = new LatLng(avgLat, avgLng);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 2));

            map.setOnMarkerClickListener(this);
        }
    }

    public void populateTopEarthquake() {
        TextView tvEpicentre = getActivity().findViewById(R.id.recent_most_violent_eq);
        TextView tvMagnitude = getActivity().findViewById(R.id.recent_magnitude_eq);
        TextView tvHypocentre = getActivity().findViewById(R.id.recent_hypocentre_eq);

        if (retriever == null || retriever.earthquakes == null || retriever.earthquakes.size() < 1) {
            tvEpicentre.setText(Html.fromHtml(String.format(getString(R.string.recent_most_violent_eq), "...")));
            tvMagnitude.setText(Html.fromHtml(String.format(getString(R.string.recent_magnitude_eq), "0.0", "0.0")));
            tvHypocentre.setText(Html.fromHtml(String.format(getString(R.string.recent_hypocentre_eq), "0.0")));
            return;
        }

        Earthquake topEarthquake = retriever.earthquakes.get(0);

        for (Earthquake earthquake : retriever.earthquakes) {
            if (earthquake.richter_mag > topEarthquake.richter_mag) {
                topEarthquake = earthquake;
            }
        }

        tvEpicentre.setText(Html.fromHtml(String.format(getString(R.string.recent_most_violent_eq), topEarthquake.placeDesc)));
        tvMagnitude.setText(Html.fromHtml(String.format(getString(R.string.recent_magnitude_eq), String.valueOf(topEarthquake.mercalli), String.valueOf(topEarthquake.richter_mag))));
        tvHypocentre.setText(Html.fromHtml(String.format(getString(R.string.recent_hypocentre_eq), String.valueOf(topEarthquake.coordinates.depth))));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Earthquake earthquake = markerEarthquakeMap.get(marker);

        BottomSheet bottomSheet = new BottomSheet(earthquake);
        bottomSheet.show(getParentFragmentManager(), "open bottom sheet");
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        showEarthquakesOnMap();
    }

    /*public void setCards() {
        TextView richter = getActivity().findViewById(R.id.recent_card_magnitude_richter);
        TextView mercalli = getActivity().findViewById(R.id.recent_card_magnitude_mercalli);
        TextView epicentre = getActivity().findViewById(R.id.recent_card_epicentre);

        Earthquake[] earthquakes = new Earthquake[5];
        for (int i = 0; i < 5; i++) {
            earthquakes[i] = retriever.earthquakes.get(i);
        }

        for (int j = 0; j < 5; j++) {
            richter.setText((int) earthquakes[j].richter_mag + " Richter");
            mercalli.setText((int) earthquakes[j].mercalli + "Mercalli");
            epicentre.setText(Html.fromHtml(String.valueOf(earthquakes[j].coordinates.depth)));
        }
    }*/
}