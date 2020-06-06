package it.unimib.quakeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import it.unimib.quakeapp.models.Earthquake;
import it.unimib.quakeapp.models.SeismicNetwork;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static it.unimib.quakeapp.MainActivity.TAG;

public class SeismicNetworkList extends Fragment implements AdapterView.OnItemSelectedListener {

    private SeismicNetworkAdapter listAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private ListView seismicNetworkList;
    private SeismicNetworkListRetriever retriever;
    private TextView tvNumSN;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retriever = new SeismicNetworkListRetriever();
        retriever.retrieve(getContext(), null, new Function() {
            @Override
            public Object apply(Object input) {
                if (tvNumSN != null) {
                    tvNumSN.setText(String.format(getString(R.string.num_seismic_networks), String.valueOf(retriever.seismicNetworks().size())));
                }

                listAdapter.notifyDataSetChanged();
                return null;
            }
        });
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seismic_network_list, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.tvNumSN = getActivity().findViewById(R.id.sn_num_seismic_networks);
        this.tvNumSN.setText(String.format(getString(R.string.num_seismic_networks), "0"));

        this.seismicNetworkList = view.findViewById(R.id.seismic_network_list);

        this.listAdapter = new SeismicNetworkAdapter(view.getContext());
        this.seismicNetworkList.setAdapter(this.listAdapter);
        this.seismicNetworkList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SeismicNetwork seismicNetwork = retriever.seismicNetworks().get(position);

                Intent intentDetail = new Intent(getActivity(), SeismicNetworkDetail.class);
                intentDetail.putExtra("seismicNetwork", seismicNetwork);
                startActivity(intentDetail);
            }
        });


        this.pullToRefresh = getView().findViewById(R.id.sn_pull_to_refresh);
        this.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retriever = new SeismicNetworkListRetriever();
                retriever.retrieve(getContext(), null, false, new Function() {
                    @Override
                    public Object apply(Object input) {
                        if (tvNumSN != null) {
                            tvNumSN.setText(String.format(getString(R.string.num_seismic_networks), String.valueOf(retriever.seismicNetworks().size())));
                        }

                        listAdapter.notifyDataSetChanged();

                        pullToRefresh.setRefreshing(false);
                        return null;
                    }
                });
            }
        });

        EditText snSearch = getView().findViewById(R.id.sn_search);
        snSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                retriever.setFilter(charSequence.toString());
                if (tvNumSN != null) {
                    tvNumSN.setText(String.format(getString(R.string.num_seismic_networks), String.valueOf(retriever.seismicNetworks().size())));
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class SeismicNetworkAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;

        public SeismicNetworkAdapter(Context context) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mContext = context;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public int getCount() {
            return retriever.seismicNetworks().size();
        }

        @Override
        public SeismicNetwork getItem(int position) {
            return retriever.seismicNetworks().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.seismic_network_list_item, parent, false);
            }

            final SeismicNetwork seismicNetwork = getItem(position);

            TextView snDoi = listItem.findViewById(R.id.sni_doi);
            TextView snFdsnCode = listItem.findViewById(R.id.sni_fdsn_code);

            View eqiDivider = listItem.findViewById(R.id.sni_divider);

            snDoi.setText(seismicNetwork.doi.isEmpty() ? getString(R.string.sn_empty_doi) : seismicNetwork.doi);
            snFdsnCode.setText(String.format(getString(R.string.sn_fdsn_code_inline), seismicNetwork.fdsnCode));

            if (position == retriever.seismicNetworks().size() - 1) {
                eqiDivider.setVisibility(View.GONE);
            }

            return listItem;
        }
    }
}