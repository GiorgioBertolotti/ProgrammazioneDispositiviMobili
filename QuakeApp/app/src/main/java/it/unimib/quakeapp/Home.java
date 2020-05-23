package it.unimib.quakeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Home extends Fragment {

    AOIAdapter aoiAdapter;
    Set<String> arrCountries = new TreeSet<>();

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

        TextView iconPlus = root.findViewById(R.id.home_icon_plus);
        iconPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CountryList.class);
                startActivity(i);
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.TAG, Context.MODE_PRIVATE);
        arrCountries = sharedPreferences.getStringSet("areasOfInterest", new TreeSet<String>());

        aoiAdapter = new AOIAdapter(getContext(), arrCountries);
        ListView aoiList = root.findViewById(R.id.home_aoi_list);
        aoiList.setAdapter(aoiAdapter);

       /* String strCountryThree =  sharedPreferences.getString("2", "Germany");
        countryThree = root.findViewById(R.id.home_area_three);
        countryThree.setText(strCountryThree);*/

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

    public void deleteAoi(String country) {
        arrCountries.remove(country);
        aoiAdapter.setCountries(arrCountries);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("areasOfInterest", arrCountries);
        editor.apply();

        aoiAdapter.notifyDataSetChanged();
    }

    public class AOIAdapter extends ArrayAdapter<String> {
        private Context mContext;
        List<String> countries;

        public AOIAdapter(@NonNull Context context, Set<String> countries) {
            super(context, 0, new ArrayList<>(countries));

            this.countries = new ArrayList<>(countries);
            mContext = context;
        }

        public void setCountries(Set<String> countries) {
            this.countries = new ArrayList<>(countries);
        }

        @Override
        public int getCount() {
            return this.countries.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.home_aoi_list_item, parent, false);
            }

            final String country = countries.get(position);

            TextView name = listItem.findViewById(R.id.home_aoi_country);
            name.setText(country);

            LinearLayout tagContainer = listItem.findViewById(R.id.home_aoi_tag);
            tagContainer.setBackground(getActivity().getDrawable(R.drawable.tag_green_safe));

            TextView tagText = listItem.findViewById(R.id.home_aoi_tag_text);
            tagText.setText("SICURO");

            Typeface fontAwesome = Typeface.createFromAsset(getActivity().getAssets(), "fa-solid-900.ttf");

            TextView deleteButton = listItem.findViewById(R.id.home_aoi_delete_btn);
            deleteButton.setTypeface(fontAwesome);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteAoi(country);
                }
            });

            TextView infoButton = listItem.findViewById(R.id.home_aoi_info_btn);
            infoButton.setTypeface(fontAwesome);
            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: Show info button
                }
            });

            return listItem;
        }
    }
}