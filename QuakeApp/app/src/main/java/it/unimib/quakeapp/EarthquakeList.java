package it.unimib.quakeapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import info.androidhive.fontawesome.FontTextView;
import it.unimib.quakeapp.models.Earthquake;
import it.unimib.quakeapp.models.Place;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static it.unimib.quakeapp.MainActivity.TAG;


public class EarthquakeList extends Fragment implements AdapterView.OnItemSelectedListener,DatePickerDialog.OnDateSetListener{


    enum SortBy {
        DATE,
        RICHTER,
        MERCALLI
    }

    private final String REVERSE_GEOCODING_URL = "https://nominatim.openstreetmap.org/reverse?format=json";
    private final String EARTHQUAKE_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake";
    private final int EARTHQUAKE_PER_REQUEST = 700;
    private int earthquakesLoaded = 0;
    private EarthquakeAdapter listAdapter;
    private ListView earthquakeList;
    private SortBy sortMethod = SortBy.DATE;
    private SwipeRefreshLayout pullToRefresh;
    private RangeSeekBar rangeSeekbar;
    private int cur = 0, filterNum = 0;
    private String DateFrom= "", DateTill = "";
    private int minMag = 0, maxMag = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = String.format("%s&limit=%s", EARTHQUAKE_REQUEST_URL, EARTHQUAKE_PER_REQUEST);

        final EarthquakeListRetriever retriever = new EarthquakeListRetriever(url);
        retriever.execute();

        earthquakesLoaded += EARTHQUAKE_PER_REQUEST;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_earthquake_list, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.earthquakeList = getView().findViewById(R.id.earthquake_list);

        this.listAdapter = new EarthquakeAdapter(getContext());
        this.earthquakeList.setAdapter(this.listAdapter);

        Spinner orderBY = getView().findViewById(R.id.order);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.order_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderBY.setAdapter(adapter);
        orderBY.setOnItemSelectedListener(this);

        final DrawerLayout filtersDrawer = getView().findViewById(R.id.drawer_layout_earthquake_list);
        final LinearLayout filterButton = getView().findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               filtersDrawer.openDrawer(Gravity.RIGHT, false);
            }
        });
/////////////////////////////////////////////////////////////////////////////////////////////////////////
       rangeSeekbar = getView().findViewById(R.id.elfs_range_seekbar);
        final TextView minMagnitude = getView().findViewById(R.id.elfs_seekbar_min);
        final TextView maxMagnitude = getView().findViewById(R.id.elfs_seekbar_max);
        final TextView filterMin = getView().findViewById(R.id.tag_richter_maggiore);
        final TextView filterMax = getView().findViewById(R.id.tag_filtri_richter_minore);
       rangeSeekbar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
           @Override
           public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {


               minMag = (int)bar.getSelectedMinValue();
               maxMag = (int)bar.getSelectedMaxValue();

                    minMagnitude.setText("Min: " + bar.getSelectedMinValue());
                    maxMagnitude.setText("Max: " + bar.getSelectedMaxValue());
                    if(minMag!= 0) {
                        filterMin.setVisibility(View.VISIBLE);
                        filterMin.setText("Richter > " + minMag);
                    }else {
                        filterMin.setVisibility(View.GONE);
                    }
                    if(maxMag != 10) {
                        filterMax.setVisibility(View.VISIBLE);
                        filterMax.setText("Richter < " + maxMag);
                    }else {
                        filterMax.setVisibility(View.GONE);
                    }
                    filter();

           }
       });

       int[] checkBoxes = {
               R.id.elfs_checkbox_till_date,
               R.id.elf_checkbox_from_date,
       };
       int[] dateViews = {
               R.id.elfs_till,
               R.id.elfs_from,
       };
       int[] filterTags = {
               R.id.tag_filtri_till_date,
               R.id.tag_filtri_from_date,
       };
       for (int i = 0; i < checkBoxes.length; i++){
           final TextView date = getView().findViewById(dateViews[i]);
           final CheckBox checkBox = getView().findViewById(checkBoxes[i]);
           final TextView dateTag = getView().findViewById(filterTags[i]);
           final int finalI = i;
           checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   if (isChecked) {
                       String dateToShow = Calendar.getInstance().get(Calendar.YEAR) + "-" +
                               (Calendar.getInstance().get(Calendar.MONTH) +1) + "-"+
                               Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                       date.setText(dateToShow);
                       filterNum++;
                       date.setVisibility(View.VISIBLE);
                       if(finalI== 1) {
                           DateFrom = dateToShow;
                           final TextView dateTag = getView().findViewById(R.id.tag_filtri_from_date);
                           dateTag.setText("Da: " + dateToShow);
                           dateTag.setVisibility(View.VISIBLE);
                       }
                       if(finalI == 0) {
                           DateTill = Calendar.getInstance().get(Calendar.YEAR) + "-" +
                                   (Calendar.getInstance().get(Calendar.MONTH) +1) + "-"+
                                   (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+1);
                           final TextView dateTag = getView().findViewById(R.id.tag_filtri_till_date);
                           dateTag.setText("A: " + dateToShow);
                           dateTag.setVisibility(View.VISIBLE);
                       }
                   } else {
                       date.setVisibility(View.GONE);
                       dateTag.setVisibility(View.GONE);
                       if(finalI == 1) DateFrom = "";
                       if(finalI == 0) DateTill = "";
                       filterNum--;
                   }
                   filter();
               }
           });
       }
       final TextView dateFromV =  getView().findViewById(R.id.elfs_from);
        final TextView dateTillV =  getView().findViewById(R.id.elfs_till);
        dateFromV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cur = 1;
                showDatePickerDialogue();
            }
        });
        dateTillV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cur = 2;
                showDatePickerDialogue();
            }
        });
        final LinearLayout info = getView().findViewById(R.id.elfs_information);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
///////////////////////////////////////////////////////////////////////////

        this.pullToRefresh = getView().findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String url = String.format( "%s&minmagnitude=%s&maxmagnitude=%s&starttime=%s&endtime=%s&limit=%s",
                        EARTHQUAKE_REQUEST_URL, minMag, maxMag, DateFrom, DateTill, earthquakesLoaded);

                final EarthquakeListRetriever retriever = new EarthquakeListRetriever(url, false);
                retriever.execute();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            default:
            case 0:
                sortMethod = SortBy.DATE;
                break;
            case 1:
                sortMethod = SortBy.RICHTER;
                break;
            case 2:
                sortMethod = SortBy.MERCALLI;
                break;
        }
        filter();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void showDatePickerDialogue(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getActivity(),this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dateToShow = year + "-" + (month+1) + "-" + dayOfMonth;
        if(cur == 1){
            final TextView dateText = getView().findViewById(R.id.elfs_from);
            dateText.setText(dateToShow);
            final TextView dateTag = getView().findViewById(R.id.tag_filtri_from_date);
            dateTag.setText("Da: " + dateToShow);
            dateTag.setVisibility(View.VISIBLE);
            DateFrom = year + "-" + (month+1) + "-" + dayOfMonth;
        }
        if(cur == 2){
            final TextView dateText = getView().findViewById(R.id.elfs_till);
            dateText.setText(dateToShow);
            final TextView dateTag = getView().findViewById(R.id.tag_filtri_till_date);
            dateTag.setText("A: " + dateToShow);
            dateTag.setVisibility(View.VISIBLE);
            DateTill = year + "-" + (month+1) + "-" + (dayOfMonth + 1);
        }
        filter();
    }

    public void filter(){
        TextView filtersNum = getView().findViewById(R.id.number_filters_applied);
        int temp_filters = filterNum;
        FontTextView filterIcon = getView().findViewById(R.id.filter_icon);
        String url = String.format( "%s&minmagnitude=%s&maxmagnitude=%s&starttime=%s&endtime=%s&limit=%s",
                EARTHQUAKE_REQUEST_URL, minMag, maxMag, DateFrom, DateTill, earthquakesLoaded);
        if(minMag != 0) temp_filters++;
        if(maxMag != 10) temp_filters++;
      if(temp_filters != 0){
            filterIcon.setVisibility(View.GONE);
            filtersNum.setText("" + temp_filters);
            filtersNum.setVisibility(View.VISIBLE);
        }else{
            filterIcon.setVisibility(View.VISIBLE);
            filtersNum.setVisibility(View.GONE);
        }
        final EarthquakeListRetriever retriever = new EarthquakeListRetriever(url);
        retriever.execute();
    }
    public void showDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this.getActivity());
        View mView = getLayoutInflater().inflate(R.layout.filter_dialog, null, false);
        mBuilder.setView(mView);
        mBuilder.setTitle("Corrisposndenza Magnitudo Richter e Grado Mercalli");
        mBuilder.setPositiveButton("CHIUDI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class EarthquakeListRetriever extends AsyncTask<Void, Void, String> {
        private String url;
        private boolean loading;
        ProgressDialog progressDialog;

        public EarthquakeListRetriever(String url) {
            this.url = url;
            this.loading = true;
        }

        public EarthquakeListRetriever(String url, boolean loading) {
            this.url = url;
            this.loading = loading;
        }

        @Override
        protected void onPreExecute() {
            if (this.loading) {
                progressDialog = ProgressDialog.show(getContext(),
                        getString(R.string.load),
                        getString(R.string.loading_earthquakes));
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(this.url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return null;
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject root = new JSONObject(result);
                    JSONArray features = root.getJSONArray("features");

                    List<Earthquake> earthquakes = new ArrayList<>();

                    for (int i = 0; i < features.length(); i++) {
                        JSONObject feature = features.getJSONObject(i);
                        Earthquake earthquake = new Earthquake(feature);
                        earthquakes.add(earthquake);
                    }
                    listAdapter.clear();

                    switch (sortMethod) {
                        case DATE: {
                            Collections.sort(earthquakes, new EarthquakeList.DateComparator());
                            List<Date> days = new ArrayList<>();

                            for (int i = 0; i < earthquakes.size(); i++) {
                                Earthquake earthquake = earthquakes.get(i);

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(earthquake.time);
                                calendar.set(Calendar.HOUR, 0);
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                Date day = calendar.getTime();

                                if (!days.contains(day)) {
                                    listAdapter.addSectionHeaderItem(earthquake);
                                    days.add(day);
                                }
                                listAdapter.addItem(earthquake);
                            }
                            break;
                        }

                        case RICHTER: {
                            Collections.sort(earthquakes, new EarthquakeList.RichterComparator());
                            List<String> richters = new ArrayList<>();

                            for (int i = 0; i < earthquakes.size(); i++) {
                                Earthquake earthquake = earthquakes.get(i);

                                String richter = Integer.toString((int) Math.floor(earthquake.richter_mag));

                                if (!richters.contains(richter)) {
                                    listAdapter.addSectionHeaderItem(earthquake);
                                    richters.add(richter);
                                }
                                listAdapter.addItem(earthquake);
                            }
                            break;
                        }
                        case MERCALLI: {
                            Collections.sort(earthquakes, new EarthquakeList.MercalliComparator());
                            List<String> mercallis = new ArrayList<>();

                            for (int i = 0; i < earthquakes.size(); i++) {
                                Earthquake earthquake = earthquakes.get(i);

                                String mercalli = Integer.toString((int) Math.floor(earthquake.mercalli));

                                if (!mercallis.contains(mercalli)) {
                                    listAdapter.addSectionHeaderItem(earthquake);
                                    mercallis.add(mercalli);
                                }
                                listAdapter.addItem(earthquake);
                            }
                            break;
                        }
                    }

                    listAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            pullToRefresh.setRefreshing(false);

            if (this.loading) {
                progressDialog.dismiss();
            }
        }
    }

    public class EarthquakeAdapter extends BaseAdapter {
        private static final int TYPE_ITEM = 0;
        private static final int TYPE_HEADER = 1;

        private ArrayList<Earthquake> earthquakes = new ArrayList<Earthquake>();
        private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

        private LayoutInflater mInflater;

        public EarthquakeAdapter(Context context) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void clear() {
            earthquakes.clear();
            sectionHeader.clear();
        }

        public void addItem(final Earthquake item) {
            earthquakes.add(item);
            notifyDataSetChanged();
        }

        public void addSectionHeaderItem(final Earthquake item) {
            earthquakes.add(item);
            sectionHeader.add(earthquakes.size() - 1);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return sectionHeader.contains(position) ? TYPE_HEADER : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
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
            int rowType = getItemViewType(position);
            final Earthquake earthquake = earthquakes.get(position);

            switch (rowType) {
                case TYPE_ITEM: {
                    convertView = mInflater.inflate(R.layout.earthquake_list_item, null);

                    TextView eqiDate = convertView.findViewById(R.id.eqi_date);
                    TextView eqiLocation = convertView.findViewById(R.id.eqi_location);
                    TextView eqiRichterMag = convertView.findViewById(R.id.eqi_richter_mag);
                    TextView eqiMercalliMag = convertView.findViewById(R.id.eqi_mercalli_mag);
                    View eqiDivider = convertView.findViewById(R.id.eqi_divider);

                    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    eqiDate.setText(sdf.format(earthquake.time));

                    eqiLocation.setText(earthquake.getPlaceDescWithoutKm());

                    final String richter = Integer.toString((int) Math.floor(earthquake.richter_mag));
                    final String mercalli = Integer.toString((int) Math.floor(earthquake.mercalli));
                    eqiRichterMag.setText(String.format("%s Richter", richter));
                    eqiMercalliMag.setText(String.format("%s Mercalli", mercalli));

                    if (position == earthquakes.size() - 1 || (earthquakes.size() > position + 1 && getItemViewType(position + 1) == TYPE_HEADER)) {
                        eqiDivider.setVisibility(View.GONE);
                    }
                    final View openBottomSheet = convertView.findViewById(R.id.layout_item);
                    openBottomSheet.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            BottomSheet bottomSheet = new BottomSheet(earthquake);
                            bottomSheet.show(getParentFragmentManager(), "open bottom sheet");
                        }
                    });
                    break;
                }
                case TYPE_HEADER: {
                    convertView = mInflater.inflate(R.layout.earthquake_list_header, null);

                    TextView diHeaderTitle = convertView.findViewById(R.id.di_header_title);

                    switch (sortMethod) {
                        case DATE: {
                            if (isSameDay(new Date(), earthquake.time)) {
                                diHeaderTitle.setText(getString(R.string.today));
                            } else {
                                SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy", Locale.getDefault());
                                diHeaderTitle.setText(capitalizeFirst(sdf.format(earthquake.time)));
                            }
                            break;
                        }
                        case RICHTER: {
                            String richter = Integer.toString((int) Math.floor(earthquake.richter_mag));
                            diHeaderTitle.setText(String.format("Richter %s", richter));
                            break;
                        }
                        case MERCALLI: {
                            String mercalli = Integer.toString((int) Math.floor(earthquake.mercalli));
                            diHeaderTitle.setText(String.format("Mercalli %s", mercalli));
                            break;
                        }
                    }
                    break;
                }
            }
            return convertView;
        }

        boolean isSameDay(Date date1, Date date2) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
            return fmt.format(date1).equals(fmt.format(date2));
        }

        String capitalizeFirst(String text) {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
    }

    public class DateComparator implements Comparator<Earthquake> {
        @Override
        public int compare(Earthquake o1, Earthquake o2) {
            return o2.time.compareTo(o1.time);
        }
    }

    public class RichterComparator implements Comparator<Earthquake> {
        @Override
        public int compare(Earthquake o1, Earthquake o2) {
            return Double.compare(o2.richter_mag, o1.richter_mag);
        }
    }

    public class MercalliComparator implements Comparator<Earthquake> {
        @Override
        public int compare(Earthquake o1, Earthquake o2) {
            return Double.compare(o2.mercalli, o1.mercalli);
        }
    }
}