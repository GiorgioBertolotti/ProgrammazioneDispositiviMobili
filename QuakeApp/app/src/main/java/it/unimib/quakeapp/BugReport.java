package it.unimib.quakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BugReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);
        getSupportActionBar().setTitle(getString(R.string.menu_bugs));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText input = findViewById(R.id.bug_edit_text);
        Button send = findViewById(R.id.bug_button);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Editable body = input.getText();
                sendEmail(body);
                input.setText("");
            }
        });
    }

    private void sendEmail(Editable body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String[] recipient = {"try@example.lol"};
        // To be sure that the intent will open only email clients
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, recipient);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Bug Report");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        // The user gets back to the app after sending the mail
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }
}