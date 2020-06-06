package it.unimib.quakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import info.androidhive.fontawesome.FontTextView;

public class Behavior extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior);
        getSupportActionBar().setTitle(getString(R.string.title_behavior));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final boolean[] visibilities = {
                false,
                false,
                false

        };
        int[] cases = {
                R.id.behavior_open_area,
                R.id.behavior_indoors,
                R.id.behavior_after
        };
        int[] tips = {
            R.id.behavior_open_area2,
            R.id.behavior_indoors2,
                R.id.behavior_after2
        };
        int[] toggles = {
                R.id.behavior_toggle,
                R.id.behavior_toggle2,
                R.id.behavior_toggle3
        };

        for (int i= 0; i < cases.length; i++){
            final TextView tipsAnswer = findViewById(tips[i]);
            final RelativeLayout header = findViewById(cases[i]);
            final FontTextView toggle = findViewById(toggles[i]);

            final int index = i;

            header.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(visibilities[index]){
                        tipsAnswer.setVisibility(View.GONE);
                        toggle.setText(R.string.fa_caret_down_solid);
                    } else {
                        tipsAnswer.setVisibility(View.VISIBLE);
                        toggle.setText(R.string.fa_caret_up_solid);
                    }
                    visibilities[index] = !visibilities[index];
                }
            });
        }
    }



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
