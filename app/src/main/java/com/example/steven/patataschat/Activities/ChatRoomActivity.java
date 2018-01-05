package com.example.steven.patataschat.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.steven.patataschat.Adapters.MessagesAdapter;
import com.example.steven.patataschat.Entities.Messages;
import com.example.steven.patataschat.Entities.Users;
import com.example.steven.patataschat.Fragments.ChatChannelsFragment;
import com.example.steven.patataschat.R;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatRoomActivity extends AppCompatActivity {

    public static final ArrayList<Users> ALL_USERS = new ArrayList<>();
    public static String CURRENT_CHAT_NAME;
    private final int GALLERY_PICK_CODE = 2;
    private final String IMAGES_STORAGE_BASE_ROUTE = "ImagesSent/";
    private final int USER_MESSAGE_CODE = 0;
    private final int ANNOUNCE_MESSAGE_CODE = 1;
    private final int IMAGE_MESSAGE_CODE = 2;
    private DatabaseReference CHATROOM;
    private DatabaseReference usersReference;
    //private LinearLayout messages_view;
    private RecyclerView messages_view;
    final ArrayList<Messages> last50Messages = new ArrayList<Messages>();
    private MessagesAdapter adapter;
    private ImageButton moreButton;
    private ImageButton sendButton;
    private EditText textfield;
    private FirebaseUser current_user;
    private FirebaseAuth authentication_service;
    private int current_message_displayed;
    private ChatChannelsFragment channelsFragment;

    public void ChatRoomActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        messages_view = findViewById(R.id.recyclerV);
        messages_view.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        moreButton = findViewById(R.id.more_opt_button);
        sendButton = findViewById(R.id.send_button);
        textfield = findViewById(R.id.message_textfield);
        authentication_service = FirebaseAuth.getInstance();
        current_user = authentication_service.getCurrentUser();
        Bundle datos = getIntent().getExtras();
        CURRENT_CHAT_NAME = datos.getString("chat_name");
        CHATROOM = FirebaseDatabase.getInstance().getReference(CURRENT_CHAT_NAME).child("Messages");
        usersReference = FirebaseDatabase.getInstance().getReference("users");
        iniUsersListener();
        load_Messages(50);
        adapter = new MessagesAdapter(last50Messages,getApplicationContext());
        messages_view.setAdapter(adapter);
        current_message_displayed = 0;
    }

    private void iniUsersListener(){
        usersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Users userAdded = dataSnapshot.getValue(Users.class);
                ALL_USERS.add(userAdded);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int size = ALL_USERS.size();
                for(int i=0; i<size ; i++){
                    if(ALL_USERS.get(i).getUser_id().equals(dataSnapshot.getKey())){
                        Users changedUser = dataSnapshot.getValue(Users.class);
                        ALL_USERS.set(i,changedUser);
                        return;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<Messages> load_Messages(int count){
        CHATROOM.limitToLast(count).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                Log.d("Message added: ", message.toString());
                last50Messages.add(message);
                adapter.notifyDataSetChanged();
                messages_view.smoothScrollToPosition(last50Messages.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return last50Messages;
    }

    public Users obtainUser(String ID){
        for (Users U : ALL_USERS) {
            if(U.getUser_id().equals(ID))
            {
                return U;
            }
        }
        return null;
    }

    public void send_message(View view){
        String text = textfield.getText().toString();
        Users userInformation = obtainUser(current_user.getUid());
        Date date = new Date();
        addMessageInformation(text,userInformation,date,USER_MESSAGE_CODE);
        textfield.setText("");
    }

    public void addMessageInformation(String text, Users user, Date date, int CODE){
        if(!text.isEmpty() && !text.trim().isEmpty()){
            String message_sent_date = getDateFormatted(date);
            Messages newMessage = new Messages(user.getUsername(),text,message_sent_date,CODE);
            String messageID = CHATROOM.push().getKey();
            CHATROOM.child(messageID).setValue(newMessage);
        }
    }

    public void uploadImage(View view){
        select_image();
    }

    public String getDateFormatted(Date date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public String getDateForSentImage(Date date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        return dateFormat.format(date);
    }

    public void select_image(){
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
        pickImageIntent.setType("image/+");
        startActivityForResult(pickImageIntent,GALLERY_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri image_to_upload;
        if(requestCode==GALLERY_PICK_CODE && resultCode==RESULT_OK){
            image_to_upload = data.getData();
            final Date date = new Date();
            final Users current = obtainUser(current_user.getUid());
            final String route = IMAGES_STORAGE_BASE_ROUTE+
                    CURRENT_CHAT_NAME+"/"+
                    current.getUsername()+"-"+getDateForSentImage(date);
            StorageReference image = FirebaseStorage.getInstance().getReference().child(route);
            image.putFile(image_to_upload).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    addMessageInformation(route,current,date,IMAGE_MESSAGE_CODE);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences appInternalData = getSharedPreferences("last_messages",Context.MODE_PRIVATE);
        SharedPreferences.Editor dataEditor = appInternalData.edit();
        int size = last50Messages.size();
        String message = last50Messages.get(size-1).toString();
        dataEditor.putString(CURRENT_CHAT_NAME,message);
        dataEditor.apply();
    }
}
