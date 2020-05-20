package it.unimib.quakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        textView = findViewById(R.id.country_list_search);
        countryListSpinner =  findViewById(R.id.country_list_spinner);
        countryListSpinner.setTitle("Seleziona un paese");
        countryListSpinner.setPositiveButton("OK");

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, countryList);
        countryListSpinner.setAdapter(adapter);

        countryListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    Toast.makeText(getApplicationContext(), "Inserisci un paese", Toast.LENGTH_SHORT).show();
                    textView.setText("");
                } else {
                    String country = (String)parentView.getItemAtPosition(position);
                    textView.setText(country);
                    returnCountry(country);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    public String returnCountry(String country)  {
        return country;
    }
}
