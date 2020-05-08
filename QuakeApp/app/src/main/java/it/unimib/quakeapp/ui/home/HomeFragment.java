package it.unimib.quakeapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


import info.androidhive.fontawesome.FontDrawable;
import it.unimib.quakeapp.FAQ;
import it.unimib.quakeapp.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        TextView todayEQ = root.findViewById(R.id.home_number_tremors);
        //todayEQ.setText();
        TextView weekEQ = root.findViewById(R.id.home_number_tremors_week);
        //weekEQ.setText();
        View iconGlobe = root.findViewById(R.id.home_icon_globe);
        FontDrawable drawableGlobe = new FontDrawable(root.getContext(), R.string.fa_globe_solid, true, false);
        drawableGlobe.setTextColor(ContextCompat.getColor(root.getContext(), android.R.color.white));
        iconGlobe.setBackgroundDrawable(drawableGlobe);

        View iconHat = root.findViewById(R.id.home_icon_safety);
        FontDrawable drawableHat = new FontDrawable(root.getContext(), R.string.fa_hard_hat_solid, true, false);
        drawableHat.setTextColor(ContextCompat.getColor(root.getContext(), android.R.color.white));
        iconHat.setBackgroundDrawable(drawableHat);

        Button buttonMoreAbout = root.findViewById(R.id.home_button_more_about);
        FontDrawable drawablePlus = new FontDrawable(root.getContext(), R.string.fa_plus_solid, true, false);
        drawablePlus.setTextColor(ContextCompat.getColor(root.getContext(), android.R.color.white));
        buttonMoreAbout.setBackgroundDrawable(drawablePlus);

        Button buttonView = root.findViewById(R.id.home_button_view);
        FontDrawable drawableEye = new FontDrawable(root.getContext(), R.string.fa_eye_solid, true, false);
        drawableEye.setTextColor(ContextCompat.getColor(root.getContext(), android.R.color.white));
        buttonView.setBackgroundDrawable(drawableEye);

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FAQ.class);
                startActivity(i);
            }
        });
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
}
