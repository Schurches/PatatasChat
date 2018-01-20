package com.example.steven.patataschat.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.steven.patataschat.Entities.Banlist;
import com.example.steven.patataschat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth authentication_service;
    private FirebaseAuth.AuthStateListener as_listener;
    private EditText username;
    private EditText password;
    private DatabaseReference ban_reference;
    private ArrayList<Banlist> ban_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.user_field);
        password = findViewById(R.id.password_field);
        authentication_service = FirebaseAuth.getInstance();
        ban_reference = FirebaseDatabase.getInstance().getReference("banned_list");
        iniUsersReference();
        as_listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Banlist banned = obtain_user(user.getUid());
                    SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                    boolean isBanned = sharedPreferences.getBoolean("is_banned",false);
                    if(banned == null && !isBanned){
                        proceed_to_chat_interface();
                    }else{
                        Intent iniBannedClass = new Intent(getApplicationContext(),BannedActivity.class);
                        iniBannedClass.putExtra("ban_giver",banned.getWhoBanned_name());
                        iniBannedClass.putExtra("description",banned.getDescription());
                        startActivity(iniBannedClass);
                    }
                }
            }
        };
    }

    /**
     *
     * @param userID the banned user ID
     * @return it must return null if the user is not banned. Otherwise, returns and instance of the
     * banned template
     */
    public Banlist obtain_user(String userID){
        for (Banlist b:ban_list) {
            if(b.getUserID().equals(userID)){
                return b;
            }
        }
        return null;
    }

    public void iniUsersReference(){
        ban_reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ban_list.add(dataSnapshot.getValue(Banlist.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int size = ban_list.size();
                Banlist banned_updated = dataSnapshot.getValue(Banlist.class);
                for (int i = 0; i < size; i++){
                    if(banned_updated.getUserID().equals(ban_list.get(i).getUserID())){
                        ban_list.set(i,banned_updated);
                        return;
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int size = ban_list.size();
                Banlist banned_updated = dataSnapshot.getValue(Banlist.class);
                for (int i = 0; i < size; i++){
                    if(banned_updated.getUserID().equals(ban_list.get(i).getUserID())){
                        ban_list.remove(i);
                        return;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ban_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 3;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean isUsernameValid(String name){
        if(name.isEmpty() || name.trim().isEmpty()){
            username.setError(getString(R.string.field_empty_error));
            return false;
        }else if(name.length() < 5){
            username.setError(getString(R.string.name_length_min_error));
            return false;
        }else if(name.length() > 12){
            username.setError(getString(R.string.name_length_max_error));
            return false;
        }else{
            username.setError(null);
            return true;
        }
    }

    public boolean isPasswordValid(String pass){
        if(pass.isEmpty() || pass.trim().isEmpty()){
            password.setError(getString(R.string.field_empty_error));
            return false;
        }else{
            password.setError(null);
            return true;
        }
    }

    public void log_in(View view){
        String user = username.getText().toString();
        String pass = password.getText().toString();
        boolean validUser = isUsernameValid(user);
        boolean validPass = isPasswordValid(pass);
        if(validUser && validPass){
            user = user+"@gmail.com";
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
