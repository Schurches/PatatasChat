package com.example.steven.patataschat.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.steven.patataschat.R;

public class BannedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banned);
        TextView banGiverText = findViewById(R.id.text_giver);
        TextView banReasonText = findViewById(R.id.text_reason);
        banGiverText.setText(String.format(getString(R.string.ban_giver_template),
                getIntent().getExtras().getString("ban_giver")));
        banReasonText.setText(String.format(getString(R.string.ban_reason_template),
                getIntent().getExtras().getString("description")));
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
