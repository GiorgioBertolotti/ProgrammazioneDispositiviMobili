package it.unimib.quakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import it.unimib.quakeapp.models.SeismicNetwork;

public class SeismicNetworkList extends AppCompatActivity {

    private final String SEISMIC_NETWORK_REQUEST_URL = "https://www.fdsn.org/ws/networks/1/query?format=geojson";
    private final int SEISMIC_NETWORK_PER_REQUEST = 20;
    private int snLoaded = 0;
    //private SeismicNetworkList.SeismicNetworkAdapter listAdapter;
    //private ListView snList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seismic_network_list);

        String url = String.format("%s&limit=%s", SEISMIC_NETWORK_REQUEST_URL, SEISMIC_NETWORK_PER_REQUEST);

        /*final SeismicNetworkListRetriever retriever = new SeismicNetworkListRetriever(url);
       retriever.execute();

        snLoaded += SEISMIC_NETWORK_PER_REQUEST;*/
    }
}
