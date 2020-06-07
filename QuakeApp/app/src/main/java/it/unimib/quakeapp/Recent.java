package it.unimib.quakeapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import it.unimib.quakeapp.models.Earthquake;
import it.unimib.quakeapp.models.Seismograph;

import static it.unimib.quakeapp.MainActivity.MAPS_API_KEY;
import static it.unimib.quakeapp.MainActivity.TAG;

public class Recent extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private EarthquakeListRetriever retriever;
    private GoogleMap map;
    private Map<Marker, Earthquake> markerEarthquakeMap = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.retriever = new EarthquakeListRetriever();
        this.retriever.retrieve(getContext(), null, new Function() {
            @Override
            public Object apply(Object input) {
                showEarthquakesOnMap();
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

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
}