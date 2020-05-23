package it.unimib.quakeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;


public class CountryList extends AppCompatActivity {
    SearchableSpinner countryListSpinner;
    AOIAdapter aoiAdapter;
    Set<String> arrCountries = new TreeSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);
        getSupportActionBar().setTitle(getString(R.string.home_areas_of_interest));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        countryListSpinner = findViewById(R.id.country_list_spinner);
        countryListSpinner.setTitle("Seleziona un paese");
        countryListSpinner.setPositiveButton("OK");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        arrCountries = sharedPreferences.getStringSet("areasOfInterest", new TreeSet<String>());

        aoiAdapter = new AOIAdapter(this, arrCountries);
        ListView aoiList = findViewById(R.id.aoi_list);
        aoiList.setAdapter(aoiAdapter);

        getCountryList();
    }

    public void getCountryList() {
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countryList = new ArrayList<>();

        for (Locale l : locale) {
            String country = l.getDisplayCountry();
            if (country.length() > 0 && !countryList.contains(country)) {
                countryList.add(country);
            }
        }

        Collections.sort(countryList, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countryList);
        countryListSpinner.setAdapter(adapter);
        countryListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // TODO: Check bug Afghanistan
                String country = (String) parentView.getItemAtPosition(position);
                savePreferences(country);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    public void savePreferences(String country) {
        arrCountries.add(country);
        aoiAdapter.setCountries(arrCountries);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("areasOfInterest", arrCountries);
        editor.apply();
        editor.commit();

        aoiAdapter.notifyDataSetChanged();
    }

    public void deleteArea(String country) {
        arrCountries.remove(country);
        aoiAdapter.setCountries(arrCountries);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("areasOfInterest", arrCountries);
        editor.apply();
        editor.commit();

        aoiAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return true;
        }
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
                listItem = LayoutInflater.from(mContext).inflate(R.layout.area_of_interest_list_item, parent, false);
            }

            final String country = countries.get(position);

            TextView name = listItem.findViewById(R.id.aoi_item_name);
            name.setText(country);

            Typeface fontAwesome = Typeface.createFromAsset(getAssets(), "fa-solid-900.ttf");

            TextView deleteButton = listItem.findViewById(R.id.aoi_item_delete_btn);
            deleteButton.setTypeface(fontAwesome);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteArea(country);
                }
            });

            return listItem;
        }
    }
}