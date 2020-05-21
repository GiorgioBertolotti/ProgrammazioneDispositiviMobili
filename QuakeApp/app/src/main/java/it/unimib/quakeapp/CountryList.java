package it.unimib.quakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
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
    String[] arrCountries = new String[3];
    TextView countryOne;
    TextView countryTwo;
    TextView countryThree;

    public String[] getArrCountries() {
        return this.arrCountries;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

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
                if (arrCountries[1] != null) {
                    arrCountries[0] = arrCountries[1];
                    countryOne.setText(arrCountries[0]);
                    if (arrCountries[2] != null) {
                        arrCountries[1] = arrCountries[2];
                        countryTwo.setText(arrCountries[1]);
                        arrCountries[2] = null;
                        countryThree.setText("");
                    } else {
                        arrCountries[1] = null;
                        countryTwo.setText("");
                    }
                } else {
                    arrCountries[0] = null;
                    countryOne.setText("");
                }
            }
        });
        TextView iconTrashTwo = findViewById(R.id.aof_icon_trash_two);
        iconTrashTwo.setTypeface(fontAwesome);
        iconTrashTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrCountries[2] != null) {
                    arrCountries[1] = arrCountries[2];
                    countryTwo.setText(arrCountries[1]);
                    arrCountries[2] = null;
                    countryThree.setText("");
                } else {
                    arrCountries[1] = null;
                    countryTwo.setText("");
                }
            }
        });
        TextView iconTrashThree = findViewById(R.id.aof_icon_trash_three);
        iconTrashThree.setTypeface(fontAwesome);
        iconTrashThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrCountries[2] = null;
                countryThree.setText("");
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
                if (position == 0) {
                    Toast.makeText(getApplicationContext(), "Inserisci un paese", Toast.LENGTH_SHORT).show();
                } else {
                    String country = (String) parentView.getItemAtPosition(position);
                    saveCountry(country);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    public void saveCountry(String country) {
        int[] ids = {R.id.country_one, R.id.country_two, R.id.country_three};
        int i = 0;
        int pos = -1;
        boolean add = false;
        while (i < arrCountries.length && !add) {
            if (arrCountries[i] == null) {
                arrCountries[i] = country;
                pos = i;
                add = true;
            } else {
                i++;
            }
        }
        if (add) {
            TextView t = findViewById(ids[pos]);
            t.setText(country);
           /* if (pos == 0) {
                TextView t1 = findViewById(R.id.home_area_one);
                t1.setText(country);
            } else if (pos == 1) {
                TextView t2 = findViewById(R.id.home_area_two);
                t2.setText(country);
            } else {
                TextView t3 = findViewById(R.id.home_area_three);
                t3.setText(country);
            }*/
        } else {
            /*
            System.out.println("FULL");
             */
        }
    }
}