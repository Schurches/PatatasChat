package com.example.steven.patataschat.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.example.steven.patataschat.Adapters.MessagesAdapter;
import com.example.steven.patataschat.Entities.Messages;
import com.example.steven.patataschat.Entities.Users;
import com.example.steven.patataschat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private int current_chat_icon;
    private DatabaseReference CHATROOM;
    private DatabaseReference usersReference;
    private RecyclerView messages_view;
    final ArrayList<Messages> last50Messages = new ArrayList<Messages>();
    private MessagesAdapter adapter;
    private ImageButton moreButton;
    private ImageButton sendButton;
    private EditText textfield;
    private FirebaseUser current_user;
    private FirebaseAuth authentication_service;
    private MediaPlayer bubble_sound;
    private ChildEventListener childListener;
    private ChildEventListener usersChildListener;
    private ValueEventListener initialMessageLoadListener;
    private ValueEventListener usersValueListener;
    private boolean wereAllMessagesLoaded;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        moreButton = findViewById(R.id.more_opt_button);
        sendButton = findViewById(R.id.send_button);
        messages_view = findViewById(R.id.recyclerV);
        messages_view.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        moreButton = findViewById(R.id.more_opt_button);
        sendButton = findViewById(R.id.send_button);
        textfield = findViewById(R.id.message_textfield);
        authentication_service = FirebaseAuth.getInstance();
        current_user = authentication_service.getCurrentUser();
        Bundle datos = getIntent().getExtras();
        current_chat_icon = datos.getInt("chat_icon");
        CURRENT_CHAT_NAME = datos.getString("chat_name");
        CHATROOM = FirebaseDatabase.getInstance().getReference(CURRENT_CHAT_NAME);
        usersReference = FirebaseDatabase.getInstance().getReference("users");
        iniUsersListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatroom_options,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Users u = obtainUser(current_user.getUid());
        if(u!=null){
            if(u.getRank() <= 3){
                menu.findItem(R.id.action_users).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_poll:
                break;
            case R.id.action_users:
                Users current = obtainUser(current_user.getUid());
                Intent iniUserManage = new Intent(getApplicationContext(),ManageUserActivity.class);
                iniUserManage.putExtra("chat_name",CURRENT_CHAT_NAME);
                iniUserManage.putExtra("user_name",current.getUsername());
                startActivity(iniUserManage);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void iniToolbar(){
        toolbar.setTitle(CURRENT_CHAT_NAME);
        Users u = obtainUser(current_user.getUid());
        String text = String.format(getResources().getString(R.string.chatRoom_subtitle_template),u.getUsername());
        toolbar.setSubtitle(text);
        toolbar.setLogo(obtainChatIcon(current_chat_icon));
        toolbar.getLogo().setTintList(getColorForElements(R.color.colorChatRoom_icons));
        switch(u.getRank()){
            case 3:
                toolbar.setBackgroundTintList(getColorForElements(R.color.color_rank_MOD));
                moreButton.setBackgroundTintList(getColorForElements(R.color.color_rank_MOD));
                sendButton.setBackgroundTintList(getColorForElements(R.color.color_rank_MOD));
                break;
            case 4:
                toolbar.setBackgroundTintList(getColorForElements(R.color.color_rank_ADMIN));
                moreButton.setBackgroundTintList(getColorForElements(R.color.color_rank_ADMIN));
                sendButton.setBackgroundTintList(getColorForElements(R.color.color_rank_ADMIN));
                break;
            case 5:
                toolbar.setBackgroundTintList(getColorForElements(R.color.color_rank_ROOT));
                moreButton.setBackgroundTintList(getColorForElements(R.color.color_rank_ROOT));
                sendButton.setBackgroundTintList(getColorForElements(R.color.color_rank_ROOT));
                break;
            default:
                toolbar.setBackgroundTintList(getColorForElements(R.color.color_rank_USER));
                moreButton.setBackgroundTintList(getColorForElements(R.color.color_rank_USER));
                sendButton.setBackgroundTintList(getColorForElements(R.color.color_rank_USER));
                break;
        }
    }

    public ColorStateList getColorForElements(int id){
        return ColorStateList.valueOf(ContextCompat.getColor(this,id));
    }

    public int obtainChatIcon(int iconID){
        switch(iconID){
            case 1:
                return R.drawable.ic_chat_black_24dp;
            case 2:
                return R.drawable.ic_account_circle_black_24dp;
            default:
                return R.drawable.ic_settings_applications_black_24dp;
        }
    }

    private void iniValueListener(){
        this.initialMessageLoadListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                wereAllMessagesLoaded = true;
                if(last50Messages.size() > 0){
                    scroll(-1,last50Messages.size()-1,true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void iniChildListener(){
        this.childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                if(message!=null){
                    int last_visible_position = ((LinearLayoutManager)messages_view.getLayoutManager()).findLastVisibleItemPosition();
                    int last_message_position = adapter.getItemCount()-1;
                    Users u = obtainUser(current_user.getUid());
                    boolean isCurrentUserTheSender = message.getUsername().equals(u.getUsername());
                    last50Messages.add(message);
                    adapter.notifyDataSetChanged();
                    if(wereAllMessagesLoaded){
                        if(!isCurrentUserTheSender){
                            playMessageSFX();
                        }
                        scroll(last_visible_position,last_message_position,isCurrentUserTheSender);
                    }
                }
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
        };
    }

    private void iniUsersValueListener(){
        this.usersValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //When initial load finished
                iniToolbar();
                invalidateOptionsMenu();
                load_Messages(50);
                adapter = new MessagesAdapter(last50Messages,getApplicationContext());
                messages_view.setAdapter(adapter);
                bubble_sound = MediaPlayer.create(getApplicationContext(),R.raw.message_bubble_sound);
                wereAllMessagesLoaded = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void iniUsersChildListener(){
        this.usersChildListener = new ChildEventListener() {
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
                        iniToolbar();
                        invalidateOptionsMenu();
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
        };
    }

    private void iniUsersListener(){
        iniUsersValueListener();
        iniUsersChildListener();
        usersReference.addListenerForSingleValueEvent(this.usersValueListener);
        usersReference.addChildEventListener(this.usersChildListener);
    }

    public ArrayList<Messages> load_Messages(int count){
        iniChildListener();
        iniValueListener();
        CHATROOM.limitToLast(count).addChildEventListener(this.childListener);
        CHATROOM.limitToLast(count).addListenerForSingleValueEvent(this.initialMessageLoadListener);
        return last50Messages;
    }

    public void scroll(int last_visible_position, int last_message_position, boolean isCurrentUserTheSender){
        if((last_visible_position != -1 && last_visible_position == last_message_position) || isCurrentUserTheSender){
            messages_view.smoothScrollToPosition(last50Messages.size()-1);
        }
    }

    public void playMessageSFX(){
        bubble_sound.start();
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
        int size = last50Messages.size();
        if(size != 0){
            SharedPreferences appInternalData = getSharedPreferences("last_messages",Context.MODE_PRIVATE);
            SharedPreferences.Editor dataEditor = appInternalData.edit();
            String message = last50Messages.get(size-1).toString();
            dataEditor.putString(CURRENT_CHAT_NAME,message);
            dataEditor.apply();
        }
        // The <> null validations are in case of the user leaves before they've been instantiated
        // This case is possible if the users enters to a channel and leaves immediately
        // (due to a miss click)
        if(this.childListener!=null){
            CHATROOM.removeEventListener(this.childListener);
        }
        if(this.initialMessageLoadListener!=null){
            CHATROOM.removeEventListener(this.initialMessageLoadListener);
        }
        if(this.usersChildListener!=null){
            usersReference.removeEventListener(this.usersChildListener);
        }
        if(this.usersValueListener!=null){
            usersReference.removeEventListener(this.usersValueListener);
        }
        ALL_USERS.clear();
    }
}
