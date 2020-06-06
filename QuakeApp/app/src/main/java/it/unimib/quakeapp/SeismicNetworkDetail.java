package it.unimib.quakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import it.unimib.quakeapp.models.SeismicNetwork;

public class SeismicNetworkDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seismic_network_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SeismicNetwork network = (SeismicNetwork) getIntent().getSerializableExtra("seismicNetwork");
        getSupportActionBar().setTitle(network.fdsnCode);
    }
}