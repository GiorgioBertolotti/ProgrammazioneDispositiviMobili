package it.unimib.quakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

public class FAQ extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        getSupportActionBar().setTitle("FAQ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

/*
       final TextView ans1 = findViewById(R.id.answer1);
        final ImageButton upToDown = findViewById(R.id.up_to_down);
        final ImageButton downToUp = findViewById(R.id.down_to_up);

     upToDown.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
             upToDown.setVisibility(View.GONE);
             downToUp.setVisibility(View.VISIBLE);
             ans1.setVisibility(View.VISIBLE);


         }
     });*/

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
