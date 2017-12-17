package com.example.steven.patataschat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth authentication_service;
    private FirebaseAuth.AuthStateListener as_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        authentication_service = FirebaseAuth.getInstance();
        as_listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    proceed_to_chat_interface();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        authentication_service.addAuthStateListener(as_listener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (as_listener != null) {
            authentication_service.removeAuthStateListener(as_listener);
        }
    }

    public void show_registration(View view)
    {
        Intent start_register_activity = new Intent(this,SignupActivity.class);
        startActivity(start_register_activity);
    }

    public void proceed_to_chat_interface()
    {
        Intent ini_chat_activity = new Intent(this,ChatInterfaceActivity.class);
        startActivity(ini_chat_activity);
    }

}
