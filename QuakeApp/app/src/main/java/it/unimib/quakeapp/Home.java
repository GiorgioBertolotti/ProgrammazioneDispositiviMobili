package it.unimib.quakeapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import it.unimib.quakeapp.models.Earthquake;

public class Home extends Fragment {
    final private int AOI_CODE = 1;
    private AOIAdapter aoiAdapter;
    private Set<String> arrCountries = new TreeSet<>();
    private ListView earthquakeList;
    private EarthquakeListRetriever retriever;
    private EarthquakeAdapter listAdapter;
    private ListView aoiList;
    private TextView dailyEQCounter;
    private TextView weeklyEQCounter;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AOI_CODE) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            arrCountries = sharedPreferences.getStringSet("areasOfInterest", new TreeSet<String>());
            aoiAdapter.setCountries(arrCountries);

            aoiAdapter.notifyDataSetChanged();
            justifyListViewHeightBasedOnChildren(aoiList);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.retriever = new EarthquakeListRetriever();
        this.retriever.retrieve(getContext(), null, new Function() {
            @Override
            public Object apply(Object input) {
                aoiAdapter.notifyDataSetChanged();

                for (int i = 0; i < Math.min(10, retriever.earthquakes.size()); i++) {
                    listAdapter.addItem(retriever.earthquakes.get(i));
                }
                listAdapter.notifyDataSetChanged();

                if (dailyEQCounter != null && weeklyEQCounter != null) {
                    final EarthquakeCountRetriever dailyRetriever = new EarthquakeCountRetriever();
                    dailyRetriever.retrieveDaily(new Function() {
                        @Override
                        public Object apply(Object input) {
                            dailyEQCounter.setText(String.valueOf(dailyRetriever.daily));
                            return null;
                        }
                    });

                    final EarthquakeCountRetriever weeklyRetriever = new EarthquakeCountRetriever();
                    weeklyRetriever.retrieveWeekly(new Function() {
                        @Override
                        public Object apply(Object input) {
                            weeklyEQCounter.setText(String.valueOf(weeklyRetriever.weekly));
                            return null;
                        }
                    });
                }
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
        this.dailyEQCounter = root.findViewById(R.id.home_number_tremors);
        this.weeklyEQCounter = root.findViewById(R.id.home_number_tremors_week);

        Button moreAbout = root.findViewById(R.id.home_button_more_about);
        moreAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.menu_latest));
                Recent nextFrag = new Recent();

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStackImmediate();
                }
                ft.replace(R.id.nav_host_fragment, nextFrag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

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
        aoiList = root.findViewById(R.id.home_aoi_list);
        aoiList.setAdapter(aoiAdapter);
        justifyListViewHeightBasedOnChildren(aoiList);

        Button behaviorBtn = getView().findViewById(R.id.home_button_view);
        behaviorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Behavior.class);
                startActivity(intent);
            }
        });

        this.earthquakeList = getView().findViewById(R.id.home_earthquake_list);
        this.listAdapter = new EarthquakeAdapter(getContext());
        this.earthquakeList.setAdapter(this.listAdapter);
        justifyListViewHeightBasedOnChildren(this.earthquakeList);

        TextView moreBtn = getView().findViewById(R.id.home_more_earthquakes);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.menu_earthquakes));
                EarthquakeList nextFrag = new EarthquakeList();

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStackImmediate();
                }
                ft.replace(R.id.nav_host_fragment, nextFrag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        FloatingActionButton fab = getView().findViewById(R.id.floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


    }
    public void showDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this.getActivity());
        View mView = getLayoutInflater().inflate(R.layout.share_dialog, null, false);
        mBuilder.setView(mView);
        mBuilder.setNegativeButton(getString(R.string.share), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.message));
                startActivity(Intent.createChooser(sharingIntent,getString(R.string.share_with)));
            }
        });
        mBuilder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    public void justifyListViewHeightBasedOnChildren(ListView myListView) {

        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
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
        justifyListViewHeightBasedOnChildren(aoiList);
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

    public class EarthquakeAdapter extends BaseAdapter {

        private ArrayList<Earthquake> earthquakes = new ArrayList<Earthquake>();
        private LayoutInflater mInflater;

        public EarthquakeAdapter(Context context) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final Earthquake item) {
            earthquakes.add(item);
            notifyDataSetChanged();
            justifyListViewHeightBasedOnChildren(earthquakeList);
        }


        @Override
        public int getCount() {
            return earthquakes.size();
        }

        @Override
        public Earthquake getItem(int position) {
            return earthquakes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final Earthquake earthquake = earthquakes.get(position);

            convertView = mInflater.inflate(R.layout.earthquake_list_item, null);

            TextView eqiDate = convertView.findViewById(R.id.eqi_date);
            TextView eqiLocation = convertView.findViewById(R.id.eqi_location);
            TextView eqiRichterMag = convertView.findViewById(R.id.eqi_richter_mag);
            TextView eqiMercalliMag = convertView.findViewById(R.id.eqi_mercalli_mag);

            final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            eqiDate.setText(sdf.format(earthquake.time));

            eqiLocation.setText(earthquake.getPlaceDescWithoutKm());

            final String richter = Integer.toString((int) Math.floor(earthquake.richter_mag));
            final String mercalli = Integer.toString((int) Math.floor(earthquake.mercalli));
            eqiRichterMag.setText(String.format("%s Richter", richter));
            eqiMercalliMag.setText(String.format("%s Mercalli", mercalli));

            final View openBottomSheet = convertView.findViewById(R.id.layout_item);
            openBottomSheet.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    BottomSheet bottomSheet = new BottomSheet(earthquake);
                    bottomSheet.show(getParentFragmentManager(), "open bottom sheet");
                }
            });

            return convertView;
        }
    }
}

