package it.unimib.quakeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.w3c.dom.Text;

public class BottomSheet extends BottomSheetDialogFragment {
    private String date;
    private String place;
    private String richter;
    private String mercalli;
    private String lat;
    private String lng;
    private String depth;

    public BottomSheet(String date, String place, String richter, String mercalli, Double lat, Double lng, Double depth) {
        setDate(date);
        setPlace(place);
        setRichter(richter);
        setMercalli(mercalli);
        setLat(lat);
        setLng(lng);
        setDepth(depth);
    }

    private void setDate(String date) {
        this.date = date;
    }

    private String getDate() {
        return this.date;
    }

    private void setPlace(String place) {
        this.place = place;
    }

    private String getPlace() {
        return this.place;
    }

    private void setRichter(String richter) {
        this.richter = richter;
    }

    private String getRichter() {
        return this.richter;
    }

    private void setMercalli(String mercalli) {
        this.mercalli = mercalli;
    }

    private String getMercalli() {
        return this.mercalli;
    }

    private void setLat(Double lat) {
        String l = String.valueOf(lat);
        this.lat = l;
    }

    private String getLat() {
        return this.lat;
    }

    private void setLng(Double lng) {
        String ln = String.valueOf(lng);
        this.lng = ln;
    }

    private String getLng() {
        return this.lng;
    }

    private void setDepth(Double depth) {
        String d = String.valueOf(depth);
        this.depth = d;
    }

    private String getDepth() {
        return this.depth;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        TextView dateTime = view.findViewById(R.id.bs_date_time);
        dateTime.setText(this.getDate());
        TextView location = view.findViewById(R.id.bs_epicenter_location);
        location.setText(this.getPlace());
        TextView richter = view.findViewById(R.id.bs_richter);
        richter.setText(this.getRichter() + " indice Richter");
        TextView mercalli = view.findViewById(R.id.bs_mercalli);
        mercalli.setText(this.getMercalli() + " scala Mercalli");
        TextView coordinates = view.findViewById(R.id.bs_coordinates);
        coordinates.setText(this.getLat() + ", " + this.getLng());
        TextView depth = view.findViewById(R.id.bs_hypocenter_depth);
        depth.setText(this.getDepth() + "km dalla superficie");
        return view;
    }
}
