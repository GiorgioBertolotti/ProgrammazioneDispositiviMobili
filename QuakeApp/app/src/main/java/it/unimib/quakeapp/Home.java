package it.unimib.quakeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import info.androidhive.fontawesome.FontDrawable;


public class Home extends Fragment {

    public Home() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void onViewCreated(final View root, @Nullable Bundle savedInstanceState) {
        TextView todayEQ = root.findViewById(R.id.home_number_tremors);
        //todayEQ.setText();
        TextView weekEQ = root.findViewById(R.id.home_number_tremors_week);
        //weekEQ.setText();

        Typeface fontAwesome = Typeface.createFromAsset(getActivity().getAssets(), "fa-solid-900.ttf");

        TextView iconGlobe = root.findViewById(R.id.home_icon_globe);
        iconGlobe.setTypeface(fontAwesome);

        TextView iconHat = root.findViewById(R.id.home_icon_safety);
        iconHat.setTypeface(fontAwesome);

        TextView iconPlus = root.findViewById(R.id.home_icon_safety);
        /*iconPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, CountryList);
            }
        });*/
      /*  String text = getString(R.string.home_more_about);

        Spannable s = new SpannableString(getString(R.string.fa_plus_solid) + " " + text);
        s.setSpan(new TypefaceSpan(fontAwesome), 0, 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new TypefaceSpan("roboto_medium"), 2, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Button buttonMoreAbout = root.findViewById(R.id.home_button_more_about);
        buttonMoreAbout.setText(s);

        String text2 = getString(R.string.home_view);

        Spannable s2 = new SpannableString(getString(R.string.fa_eye_solid) + " " + text2);
        s2.setSpan(new TypefaceSpan(fontAwesome), 0, 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s2.setSpan(new TypefaceSpan("roboto_medium"), 2, s2.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Button buttonView = root.findViewById(R.id.home_button_view);
        buttonView.setText(s2);

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FAQ.class);
                startActivity(i);
            }
        });*/
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
    }
}