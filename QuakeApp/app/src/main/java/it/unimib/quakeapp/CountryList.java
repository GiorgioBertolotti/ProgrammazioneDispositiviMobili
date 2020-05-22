package it.unimib.quakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


public class CountryList extends AppCompatActivity {

    SearchableSpinner countryListSpinner;
    String[] arrCountries = new String[3];
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    TextView countryOne;
    TextView countryTwo;
    TextView countryThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);
        getSupportActionBar().setTitle("Aree di interesse");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        countryListSpinner = findViewById(R.id.country_list_spinner);
        countryListSpinner.setTitle("Seleziona un paese");
        countryListSpinner.setPositiveButton("OK");
        countryOne = findViewById(R.id.country_one);
        countryTwo = findViewById(R.id.country_two);
        countryThree = findViewById(R.id.country_three);

        Typeface fontAwesome = Typeface.createFromAsset(getAssets(), "fa-solid-900.ttf");

        TextView iconTrashOne = findViewById(R.id.aof_icon_trash_one);
        iconTrashOne.setTypeface(fontAwesome);
        iconTrashOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArea(0);
            }
        });
        TextView iconTrashTwo = findViewById(R.id.aof_icon_trash_two);
        iconTrashTwo.setTypeface(fontAwesome);
        iconTrashTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArea(1);
            }
        });
        TextView iconTrashThree = findViewById(R.id.aof_icon_trash_three);
        iconTrashThree.setTypeface(fontAwesome);
        iconTrashThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArea(2);
            }
        });
        getCountryList();
    }

    public void getCountryList() {
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countryList = new ArrayList<String>();
        countryList.add("Inserisci un paese..");
        String country = "";
        for (Locale l : locale) {
            country = l.getDisplayCountry();
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
                int i = 0;
                if (position == 0) {
                    Toast.makeText(getApplicationContext(), "Seleziona un paese", Toast.LENGTH_SHORT).show();
                } else {
                    String country = (String) parentView.getItemAtPosition(position);
                    savePreferences(country);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    public void savePreferences(String value) {
        int i = 0;
        int pos = -1;
        boolean add = false;
        while (i < arrCountries.length && !add) {
            if (arrCountries[i] == null) {
                arrCountries[i] = value;
                pos = i;
                add = true;
            } else {
                i++;
            }
        }
        if (add) {
            SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(pos), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(String.valueOf(pos), value);
            editor.apply();
            if (pos == 0) {
                countryOne.setText(value);
            } else if (pos == 1) {
                countryTwo.setText(value);
            } else {
                countryThree.setText(value);
            }
        }
    }

    public void deleteArea(int pos) {
        int i = 0;
        boolean delete = false;
        while (i < arrCountries.length && !delete) {
            if (i == pos) {
                arrCountries[pos] = null;
                delete = true;
            } else {
                i++;
            }
        }
        if (delete) {
            if (pos == 0) {
                countryOne.setText("");
            } else if (pos == 1) {
                countryTwo.setText("");
            } else {
                countryThree.setText("");
            }
        }
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
}