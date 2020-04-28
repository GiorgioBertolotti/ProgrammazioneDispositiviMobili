package it.unimib.quakeapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import info.androidhive.fontawesome.FontTextView;
import it.unimib.quakeapp.models.Earthquake;

public class BottomSheet extends BottomSheetDialogFragment {
    private Earthquake earthquake;

    public BottomSheet(Earthquake earthquake) {
        setEarthquake(earthquake);
    }

    private void setEarthquake(Earthquake earthquake) {
        this.earthquake = earthquake;
    }

    private Date getDate() {
        return this.earthquake.time;
    }

    private String getRichter() {
        return Double.toString(this.earthquake.richter_mag);
    }

    private String getMercalli() {
        return Double.toString(this.earthquake.mercalli);
    }

    private String getLat() {
        return Double.toString(this.earthquake.coordinates.lat);
    }

    private String getLng() {
        return Double.toString(this.earthquake.coordinates.lng);
    }

    private String getDepth() {
        return Double.toString(this.earthquake.coordinates.depth);
    }

    private String getUrl() {
        return this.earthquake.url;
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
        title.setText(earthquake.getPlaceDescWithoutKm());

        TextView dateTime = dialog.findViewById(R.id.bs_date_time);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy HH:mm");
        dateTime.setText(capitalizeFirst(sdf.format(this.getDate())));

        TextView location = dialog.findViewById(R.id.bs_epicenter_location);
        if (earthquake.place != null && earthquake.place.addressDetails.size() > 0) {
            if (earthquake.place.addressDetails.size() > 1) {
                location.setText(Html.fromHtml(String.format("<b>%s</b>, %s", earthquake.place.addressDetails.get(0), earthquake.place.addressDetails.get(1))));
            } else {
                location.setText(earthquake.place.addressDetails.get(0));
            }
        } else {
            location.setText(earthquake.getPlaceDescWithoutKm());
        }

        TextView richter = dialog.findViewById(R.id.bs_richter);
        String richterText = "<b>" + this.getRichter() + "</b> indice Richter";
        richter.setText(Html.fromHtml(richterText));

        TextView mercalli = dialog.findViewById(R.id.bs_mercalli);
        String mercalliText = "<b>" + this.getMercalli() + "</b> scala Mercalli";
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
