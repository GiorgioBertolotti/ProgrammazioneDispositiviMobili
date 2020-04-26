package it.unimib.quakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout;

public class FAQ extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        getSupportActionBar().setTitle("FAQ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

///////////QUESTION1
       final TextView ans1 = findViewById(R.id.answer1);
        final ImageButton upToDown = findViewById(R.id.up_to_down);
        final ImageButton downToUp = findViewById(R.id.down_to_up);

     upToDown.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
             upToDown.setVisibility(View.GONE);
             downToUp.setVisibility(View.VISIBLE);
             ans1.setVisibility(View.VISIBLE);
         }
     });
     downToUp.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown.setVisibility(View.VISIBLE);
                downToUp.setVisibility(View.GONE);
                ans1.setVisibility(View.GONE);
            }
        });
/////////QUESTION2
        final TextView ans2 = findViewById(R.id.answer2);
        final ImageButton upToDown2 = findViewById(R.id.up_to_down2);
        final ImageButton downToUp2 = findViewById(R.id.down_to_up2);

        upToDown2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown2.setVisibility(View.GONE);
                downToUp2.setVisibility(View.VISIBLE);
                ans2.setVisibility(View.VISIBLE);
            }
        });
        downToUp2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown2.setVisibility(View.VISIBLE);
                downToUp2.setVisibility(View.GONE);
                ans2.setVisibility(View.GONE);
            }
        });
////////QUESTION3
        final TextView ans3 = findViewById(R.id.answer3);
        final ImageButton upToDown3 = findViewById(R.id.up_to_down3);
        final ImageButton downToUp3 = findViewById(R.id.down_to_up3);

        upToDown3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown3.setVisibility(View.GONE);
                downToUp3.setVisibility(View.VISIBLE);
                ans3.setVisibility(View.VISIBLE);
            }
        });
        downToUp3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown3.setVisibility(View.VISIBLE);
                downToUp3.setVisibility(View.GONE);
                ans3.setVisibility(View.GONE);
            }
        });

//////QUESTION4

        final TableLayout ans4 = findViewById(R.id.answer4);
        final ImageButton upToDown4 = findViewById(R.id.up_to_down4);
        final ImageButton downToUp4 = findViewById(R.id.down_to_up4);

        upToDown4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown4.setVisibility(View.GONE);
                downToUp4.setVisibility(View.VISIBLE);
                ans4.setVisibility(View.VISIBLE);
            }
        });
        downToUp4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown4.setVisibility(View.VISIBLE);
                downToUp4.setVisibility(View.GONE);
                ans4.setVisibility(View.GONE);
            }
        });
/////QUESTION5
        final TextView ans5 = findViewById(R.id.answer5);
        final ImageButton upToDown5 = findViewById(R.id.up_to_down5);
        final ImageButton downToUp5 = findViewById(R.id.down_to_up5);
        final ImageView diagram = findViewById(R.id.diagram);

        upToDown5.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown5.setVisibility(View.GONE);
                downToUp5.setVisibility(View.VISIBLE);
                ans5.setVisibility(View.VISIBLE);
                diagram.setVisibility(View.VISIBLE);
            }
        });
        downToUp5.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown5.setVisibility(View.VISIBLE);
                downToUp5.setVisibility(View.GONE);
                ans5.setVisibility(View.GONE);
                diagram.setVisibility(View.GONE);
            }
        });
///////////QUESTION6
        final TextView ans6 = findViewById(R.id.answer6);
        final ImageButton upToDown6 = findViewById(R.id.up_to_down6);
        final ImageButton downToUp6 = findViewById(R.id.down_to_up6);

        upToDown6.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown6.setVisibility(View.GONE);
                downToUp6.setVisibility(View.VISIBLE);
                ans6.setVisibility(View.VISIBLE);
            }
        });
        downToUp6.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown6.setVisibility(View.VISIBLE);
                downToUp6.setVisibility(View.GONE);
                ans6.setVisibility(View.GONE);
            }
        });
///////////QUESTION7
        final TextView ans7 = findViewById(R.id.answer7);
        final ImageButton upToDown7 = findViewById(R.id.up_to_down7);
        final ImageButton downToUp7 = findViewById(R.id.down_to_up7);

        upToDown7.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown7.setVisibility(View.GONE);
                downToUp7.setVisibility(View.VISIBLE);
                ans7.setVisibility(View.VISIBLE);
            }
        });
        downToUp7.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown7.setVisibility(View.VISIBLE);
                downToUp7.setVisibility(View.GONE);
                ans7.setVisibility(View.GONE);
            }
        });
///////////QUESTION8
        final TextView ans8 = findViewById(R.id.answer8);
        final ImageButton upToDown8 = findViewById(R.id.up_to_down8);
        final ImageButton downToUp8 = findViewById(R.id.down_to_up8);

        upToDown8.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown8.setVisibility(View.GONE);
                downToUp8.setVisibility(View.VISIBLE);
                ans8.setVisibility(View.VISIBLE);
            }
        });
        downToUp8.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                upToDown8.setVisibility(View.VISIBLE);
                downToUp8.setVisibility(View.GONE);
                ans8.setVisibility(View.GONE);
            }
        });


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
