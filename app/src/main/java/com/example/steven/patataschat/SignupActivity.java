package com.example.steven.patataschat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth authentication_service;
    private DatabaseReference database_service;

    private EditText user_field;
    private EditText mail_field;
    private EditText password_field;
    private Button signup_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        user_field = findViewById(R.id.user_textField);
        mail_field = findViewById(R.id.mail_textField);
        password_field = findViewById(R.id.password_textField);
        signup_button = findViewById(R.id.signup_button);

        database_service = FirebaseDatabase.getInstance().getReference().child("users");

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create_new_user();
            }
        });

        authentication_service = FirebaseAuth.getInstance();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////Account functions///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void create_new_user(){
        final String user = user_field.getText().toString();
        final String email = mail_field.getText().toString();
        final String password = password_field.getText().toString();
        String username = user+"@gmail.com";

        authentication_service.createUserWithEmailAndPassword(username,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseUser current_user = authentication_service.getCurrentUser();
                    DatabaseReference rj_user = database_service.child(user);
                    rj_user.child("userID").setValue(current_user.getUid());
                    rj_user.child("email").setValue(email);
                    rj_user.child("password").setValue(password);
                    rj_user.child("Rank").setValue("User");
                    rj_user.child("profile_picture").setValue("Nothing for now...");
                    Toast.makeText(SignupActivity.this,R.string.signup_success,
                            Toast.LENGTH_SHORT).show();
                    proceed_to_chat_interface();
                }
            }
        });

    }

    public void redirect_to_login()
    {
        Intent redirect_login = new Intent(this,LoginActivity.class);
        startActivity(redirect_login);
    }

    public void proceed_to_chat_interface()
    {
        Intent ini_chat_activity = new Intent(this,ChatInterfaceActivity.class);
        startActivity(ini_chat_activity);
    }




}