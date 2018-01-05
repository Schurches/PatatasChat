package com.example.steven.patataschat.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.steven.patataschat.R;
import com.firebase.client.Firebase;

public class MainActivity extends AppCompatActivity {

    private Firebase fbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        String link =  getResources().getString(R.string.firebaseConectionLink);
        fbRef = new Firebase(link);
    }
}
