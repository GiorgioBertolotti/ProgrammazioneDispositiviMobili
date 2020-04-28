package it.unimib.quakeapp;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import info.androidhive.fontawesome.FontTextView;

public class BottomSheet extends BottomSheetDialogFragment {
    private Date date;
    private String place;
    private String richter;
    private String mercalli;
    private String lat;
    private String lng;
    private String depth;
    private String url;

    public BottomSheet(Date date, String place, String richter, String mercalli, Double lat, Double lng, Double depth, String url) {
        setDate(date);
        setPlace(place);
        setRichter(richter);
        setMercalli(mercalli);
        setLat(lat);
        setLng(lng);
        setDepth(depth);
        setUrl(url);
    }

    private void setDate(Date date) {
        this.date = date;
    }

    private Date getDate() {
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

    private void setUrl(String url) {
        this.url = url;
    }

    private String getUrl() {
        return this.url;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());

        dialog.setContentView(R.layout.bottom_sheet);

        FontTextView bsClose = dialog.findViewById(R.id.bs_close);
        bsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog d = dialog;
                FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        TextView title = dialog.findViewById(R.id.bs_location);
        title.setText(this.getPlace());

        TextView dateTime = dialog.findViewById(R.id.bs_date_time);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy HH:mm");
        dateTime.setText(capitalizeFirst(sdf.format(this.getDate())));

        TextView location = dialog.findViewById(R.id.bs_epicenter_location);
        location.setText(this.getPlace());

        TextView richter = dialog.findViewById(R.id.bs_richter);
        String richterText = "<b>" + this.getRichter() + "</b> indice Richter";
        richter.setText(Html.fromHtml(richterText));

        TextView mercalli = dialog.findViewById(R.id.bs_mercalli);
        String mercalliText = "<b>" + this.getRichter() + "</b> scala Mercalli";
        mercalli.setText(Html.fromHtml(mercalliText));

        TextView coordinates = dialog.findViewById(R.id.bs_coordinates);
        coordinates.setText(this.getLat() + ", " + this.getLng());

        TextView depth = dialog.findViewById(R.id.bs_hypocenter_depth);
        String depthText = "<b>" + this.getDepth() + " km</b> dalla superficie";
        depth.setText(Html.fromHtml(depthText));

        TextView link = dialog.findViewById(R.id.bs_link);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getUrl()));
                startActivity(browserIntent);
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            // TODO: Expand header with background image
                        } else {
                            // TODO: Collapse header with tint color
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        });

        return dialog;
    }

    private String capitalizeFirst(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
