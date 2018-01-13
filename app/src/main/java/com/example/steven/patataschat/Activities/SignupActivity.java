package com.example.steven.patataschat.Activities;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.steven.patataschat.Entities.Banlist;
import com.example.steven.patataschat.Entities.Users;
import com.example.steven.patataschat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth authentication_service;
    private DatabaseReference database_service;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView profile_image;
    private EditText user_field;
    private EditText mail_field;
    private EditText password_field;
    private Button signup_button;
    private final int GALLERY_PICK_CODE = 2;
    private final int RANK_USER = 1;
    private Uri selected_profile_pic = null;
    private DatabaseReference ban_reference;
    private ArrayList<Banlist> ban_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        user_field = findViewById(R.id.user_textField);
        mail_field = findViewById(R.id.mail_textField);
        password_field = findViewById(R.id.password_textField);
        signup_button = findViewById(R.id.signup_button);
        profile_image = findViewById(R.id.profile_pic);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("profilePictures");
        database_service = FirebaseDatabase.getInstance().getReference().child("users");
        ban_reference = FirebaseDatabase.getInstance().getReference("banned_list");
        iniUsersReference();
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create_new_user();
            }
        });
        authentication_service = FirebaseAuth.getInstance();
    }

    public void upload_image(String userID){
        StorageReference newProfilePicture = storageReference.child(userID);
        newProfilePicture.putFile(selected_profile_pic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SignupActivity.this,R.string.success_picture,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void select_image(View view){
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
        pickImageIntent.setType("image/+");
        startActivityForResult(pickImageIntent,GALLERY_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PICK_CODE && resultCode==RESULT_OK){
            selected_profile_pic = data.getData();
            profile_image.setImageURI(selected_profile_pic);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////Account functions///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void create_new_user(){
        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if(detectBannedID(android_id)){
            Toast.makeText(this,R.string.login_banned,Toast.LENGTH_LONG).show();
        }else{
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
                                DatabaseReference rj_user = database_service.child(current_user.getUid());
                                boolean hasProfilePic;
                                if(selected_profile_pic != null){
                                    hasProfilePic = true;
                                    upload_image(current_user.getUid());
                                }else{
                                    hasProfilePic = false;
                                }
                                Users newUser = new Users(current_user.getUid(),user, password,email,RANK_USER,hasProfilePic,false,false,android_id,true);
                                rj_user.setValue(newUser);
                                Toast.makeText(SignupActivity.this,R.string.signup_success,
                                        Toast.LENGTH_SHORT).show();
                                proceed_to_chat_interface();
                            }
                        }
                    });
        }
    }

    public boolean detectBannedID(String thisID){
        int size = ban_list.size();
        for (int i = 0; i < size; i++){
            if(ban_list.get(i).getDeviceID().equals(thisID)){
                return true;
            }
        }
        return false;
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
