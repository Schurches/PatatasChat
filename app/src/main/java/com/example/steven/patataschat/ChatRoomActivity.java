package com.example.steven.patataschat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.steven.patataschat.Entities.Messages;
import com.example.steven.patataschat.Entities.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatRoomActivity extends AppCompatActivity {

    public static final ArrayList<Users> ALL_USERS = new ArrayList<>();
    private final int USER_MESSAGE_CODE = 0;
    private final int ANNOUNCE_MESSAGE_CODE = 1;
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
        String chatroom_name = datos.getString("chat_name");
        CHATROOM = FirebaseDatabase.getInstance().getReference(chatroom_name).child("Messages");
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
        if(!text.isEmpty() && !text.trim().isEmpty()){
            Users userInformation = obtainUser(current_user.getUid());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String message_sent_date = dateFormat.format(date);
            Messages newMessage = new Messages(userInformation.getUsername(),text,message_sent_date,USER_MESSAGE_CODE);
            String messageID = CHATROOM.push().getKey();
            CHATROOM.child(messageID).setValue(newMessage);
            textfield.setText("");
        }
    }

}
