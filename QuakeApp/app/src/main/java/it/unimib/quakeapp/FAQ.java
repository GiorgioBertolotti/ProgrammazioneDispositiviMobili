package it.unimib.quakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout;

import info.androidhive.fontawesome.FontTextView;

public class FAQ extends AppCompatActivity {

    private boolean visibilityFaq4 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        getSupportActionBar().setTitle("FAQ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final boolean[] visibilities = {
                false,
                false,
                false,
                false,
                false,
                false,
                false
        };
        int[] headers = {
                R.id.faq_header1,
                R.id.faq_header2,
                R.id.faq_header3,
                R.id.faq_header5,
                R.id.faq_header6,
                R.id.faq_header7,
                R.id.faq_header8,
        };
        int[] answers = {
                R.id.faq_answer1,
                R.id.faq_answer2,
                R.id.faq_answer3,
                R.id.faq_answer5,
                R.id.faq_answer6,
                R.id.faq_answer7,
                R.id.faq_answer8,
        };
        int[] toggles = {
                R.id.faq_toggle1,
                R.id.faq_toggle2,
                R.id.faq_toggle3,
                R.id.faq_toggle5,
                R.id.faq_toggle6,
                R.id.faq_toggle7,
                R.id.faq_toggle8,
        };

        for(int i = 0; i < answers.length; i++) {
            final TextView answer = findViewById(answers[i]);
            final RelativeLayout header = findViewById(headers[i]);
            final FontTextView toggle = findViewById(toggles[i]);

            final int index = i;

            header.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if(visibilities[index]){
                        answer.setVisibility(View.GONE);
                        toggle.setText(R.string.fa_caret_down_solid);

                        if(index == 3) {
                            final ImageView imageView = findViewById(R.id.faq_diagram5);
                            imageView.setVisibility(View.GONE);
                        }
                    } else {
                        answer.setVisibility(View.VISIBLE);
                        toggle.setText(R.string.fa_caret_up_solid);

                        if(index == 3) {
                            final ImageView imageView = findViewById(R.id.faq_diagram5);
                            imageView.setVisibility(View.VISIBLE);
                        }
                    }
                    visibilities[index] = !visibilities[index];
                }
            });
        }

        /* --- FAQ 4 --- */
        final TableLayout answer4 = findViewById(R.id.faq_answer4);
        final RelativeLayout header4 = findViewById(R.id.faq_header4);
        final FontTextView toggle4 = findViewById(R.id.faq_toggle4);

        header4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(visibilityFaq4){
                    answer4.setVisibility(View.GONE);
                    toggle4.setText(R.string.fa_caret_down_solid);
                } else {
                    toggle4.setText(R.string.fa_caret_up_solid);
                    answer4.setVisibility(View.VISIBLE);
                }
                visibilityFaq4 = !visibilityFaq4;
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
