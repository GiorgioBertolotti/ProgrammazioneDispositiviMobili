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
import androidx.arch.core.util.Function;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import it.unimib.quakeapp.models.Earthquake;

public class Home extends Fragment {
    final private int AOI_CODE = 1;
    private AOIAdapter aoiAdapter;
    private Set<String> arrCountries = new TreeSet<>();
    private EarthquakeListRetriever retriever;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AOI_CODE) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            arrCountries = sharedPreferences.getStringSet("areasOfInterest", new TreeSet<String>());
            aoiAdapter.setCountries(arrCountries);

            aoiAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retriever = new EarthquakeListRetriever();
        retriever.retrieve(getContext(), null, new Function() {
            @Override
            public Object apply(Object input) {
                aoiAdapter.notifyDataSetChanged();
                return null;
            }
        });
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
                startActivityForResult(i, AOI_CODE);
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        arrCountries = sharedPreferences.getStringSet("areasOfInterest", new TreeSet<String>());

        aoiAdapter = new AOIAdapter(getContext(), arrCountries);
        ListView aoiList = root.findViewById(R.id.home_aoi_list);
        aoiList.setAdapter(aoiAdapter);
/////////////////////////////////////////////////////////////////////////////////////////////////////////

        Button behaviorBtn = getView().findViewById(R.id.home_button_view);
        behaviorBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Behavior.class);
                startActivity(intent);
            }
        });
    }

    public void deleteAoi(String country) {
        arrCountries.remove(country);
        aoiAdapter.setCountries(arrCountries);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("areasOfInterest", arrCountries);
        editor.apply();
        editor.commit();

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

            TextView tagText = listItem.findViewById(R.id.home_aoi_tag_text);
            LinearLayout tagContainer = listItem.findViewById(R.id.home_aoi_tag);

            List<Earthquake> earthquakes = retriever.earthquakes;

            if (earthquakes != null) {
                int counter = 0;
                double maxRichter = 0;

                for (Earthquake earthquake : earthquakes) {
                    String[] words = country.split(" ");

                    for (String word : words) {
                        if (earthquake.placeDesc.contains(word)) {
                            counter++;

                            if (earthquake.richter_mag > maxRichter) {
                                maxRichter = earthquake.richter_mag;
                            }
                        }
                    }
                }

                if (counter > 0) {
                    if (maxRichter < 6) {
                        tagText.setText(getString(R.string.alert));
                        tagContainer.setBackground(getActivity().getDrawable(R.drawable.tag_yellow_critic));
                    } else {
                        tagText.setText(getString(R.string.emergency));
                        tagContainer.setBackground(getActivity().getDrawable(R.drawable.tag_red_emergency));
                    }
                } else {
                    tagText.setText(getString(R.string.safe));
                    tagContainer.setBackground(getActivity().getDrawable(R.drawable.tag_green_safe));
                }
            }

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