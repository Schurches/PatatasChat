package com.example.steven.patataschat.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.steven.patataschat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth authentication_service;
    private FirebaseAuth.AuthStateListener as_listener;
    private EditText username;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.email_field);
        password = findViewById(R.id.password_field);
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

    public void log_in(View view){
        String user = username.getText().toString();
        String pass = password.getText().toString();
        user = user+"@gmail.com";
        if(!user.isEmpty() && !pass.isEmpty()){
            authentication_service.signInWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),R.string.fail_login,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),R.string.success_login,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
