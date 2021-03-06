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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
    /*Constants*/
    private final ArrayList<Messages> last50Messages = new ArrayList<>();
    private final int GALLERY_PICK_CODE = 2;
    private final String IMAGES_STORAGE_BASE_ROUTE = "ImagesSent/";
    private final int IMAGE_MESSAGE_CODE = 2;
    /*Database and auth*/
    private FirebaseUser currentUser;
    private FirebaseAuth authentication_service;
    private DatabaseReference CHATROOM;
    private DatabaseReference usersReference;
    private ChildEventListener childListener;
    private ChildEventListener usersChildListener;
    private ValueEventListener initialMessageLoadListener;
    private ValueEventListener usersValueListener;
    /*Widgets*/
    private RecyclerView messages_view;
    private Toolbar toolbar;
    private MessagesAdapter adapter;
    private ImageButton imageButton;
    private ImageButton sendButton;
    private ImageButton iEmotionsButton;
    private EditText textField;
    /*Variables*/
    private int loadAmount;
    private boolean wereAllMessagesLoaded;
    private MediaPlayer bubble_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        messages_view = findViewById(R.id.recyclerV);
        messages_view.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        imageButton = findViewById(R.id.more_opt_button);
        sendButton = findViewById(R.id.send_button);
        iEmotionsButton = findViewById(R.id.reactions_button);
        textField = findViewById(R.id.message_textfield);
        textFieldTextChange();
        authentication_service = FirebaseAuth.getInstance();
        currentUser = authentication_service.getCurrentUser();
        Bundle datos = getIntent().getExtras();
        CURRENT_CHAT_NAME = datos.getString("chat_name");
        loadAmount = datos.getInt("unread_count");
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
        Users u = obtainUser(currentUser.getUid());
        if(u!=null){
            if(u.getRank() == 1){
                menu.findItem(R.id.action_users).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Users current = obtainUser(currentUser.getUid());
        Intent iniActivity = null;
        switch(id){
            case R.id.action_poll:
                iniActivity = new Intent(getApplicationContext(),PollActivity.class);
                break;
            case R.id.action_users:
                iniActivity = new Intent(getApplicationContext(),ManageUserActivity.class);
                break;
        }
        iniActivity.putExtra("chat_name",CURRENT_CHAT_NAME);
        iniActivity.putExtra("user_name",current.getUsername());
        startActivity(iniActivity);
        return super.onOptionsItemSelected(item);
    }

    public void textFieldTextChange(){
        this.textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence string, int start, int count, int length) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 20){
                    iEmotionsButton.setVisibility(View.GONE);
                }else{
                    iEmotionsButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void iniIEmotions(View view){
        Users current = obtainUser(currentUser.getUid());
        Intent iniIEmotionsActivity = new Intent(getApplicationContext(),IEmotionsActivity.class);
        iniIEmotionsActivity.putExtra("chat_name",CURRENT_CHAT_NAME);
        iniIEmotionsActivity.putExtra("user_name",current.getUsername());
        startActivity(iniIEmotionsActivity);
    }

    public void iniToolbar(){
        toolbar.setTitle(CURRENT_CHAT_NAME);
        Users u = obtainUser(currentUser.getUid());
        String text = String.format(getResources().getString(R.string.chatRoom_subtitle_template),u.getUsername());
        toolbar.setSubtitle(text);
        switch(u.getRank()){
            case 2:
                toolbar.setBackgroundTintList(getColorForElements(R.color.color_rank_TRUSTED));
                imageButton.setBackgroundTintList(getColorForElements(R.color.color_rank_TRUSTED));
                sendButton.setBackgroundTintList(getColorForElements(R.color.color_rank_TRUSTED));
                iEmotionsButton.setBackgroundTintList(getColorForElements(R.color.color_rank_TRUSTED));
                break;
            case 3:
                toolbar.setBackgroundTintList(getColorForElements(R.color.color_rank_MOD));
                imageButton.setBackgroundTintList(getColorForElements(R.color.color_rank_MOD));
                sendButton.setBackgroundTintList(getColorForElements(R.color.color_rank_MOD));
                iEmotionsButton.setBackgroundTintList(getColorForElements(R.color.color_rank_MOD));
                break;
            case 4:
                toolbar.setBackgroundTintList(getColorForElements(R.color.color_rank_ADMIN));
                imageButton.setBackgroundTintList(getColorForElements(R.color.color_rank_ADMIN));
                sendButton.setBackgroundTintList(getColorForElements(R.color.color_rank_ADMIN));
                iEmotionsButton.setBackgroundTintList(getColorForElements(R.color.color_rank_ADMIN));
                break;
            case 5:
                toolbar.setBackgroundTintList(getColorForElements(R.color.color_rank_ROOT));
                imageButton.setBackgroundTintList(getColorForElements(R.color.color_rank_ROOT));
                sendButton.setBackgroundTintList(getColorForElements(R.color.color_rank_ROOT));
                iEmotionsButton.setBackgroundTintList(getColorForElements(R.color.color_rank_ROOT));
                break;
            default:
                toolbar.setBackgroundTintList(getColorForElements(R.color.color_rank_USER));
                imageButton.setBackgroundTintList(getColorForElements(R.color.color_rank_USER));
                sendButton.setBackgroundTintList(getColorForElements(R.color.color_rank_USER));
                iEmotionsButton.setBackgroundTintList(getColorForElements(R.color.color_rank_USER));

                break;
        }
    }

    public ColorStateList getColorForElements(int id){
        return ColorStateList.valueOf(ContextCompat.getColor(this,id));
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
                    Users u = obtainUser(currentUser.getUid());
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
                int load = obtainUser(currentUser.getUid()).getLoad_messages();
                if(loadAmount < load){
                    loadMessages(load);
                }else if(loadAmount > load && loadAmount <= 500){
                    loadMessages(loadAmount);
                }else{
                    loadMessages(load);
                }
                establishWritingAbilities(obtainUser(currentUser.getUid()));
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

    public void establishWritingAbilities(Users muted_user){
        boolean enable = muted_user.isMuted();
        this.textField.setEnabled(!enable);
        if(enable){
            this.textField.setHint(R.string.editText_hint_mute);
            this.iEmotionsButton.setVisibility(View.GONE);
            this.sendButton.setVisibility(View.GONE);
            this.imageButton.setVisibility(View.GONE);
        }else{
            this.textField.setHint(R.string.editText_hint_normal);
            this.iEmotionsButton.setVisibility(View.VISIBLE);
            this.sendButton.setVisibility(View.VISIBLE);
            this.imageButton.setVisibility(View.VISIBLE);
        }
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
                if(currentUser != null){ //if current user hasn't been banned
                    int size = ALL_USERS.size();
                    for(int i=0; i<size ; i++){
                        if(ALL_USERS.get(i).getUser_id().equals(dataSnapshot.getKey())){
                            Users changedUser = dataSnapshot.getValue(Users.class);
                            if(changedUser.getUser_id().equals(currentUser.getUid())){
                                if(changedUser.isBanned()){
                                    authentication_service.signOut();
                                    Toast.makeText(getApplicationContext(),R.string.user_banned_message,Toast.LENGTH_SHORT).show();
                                    finishAffinity();
                                }else{
                                    establishWritingAbilities(changedUser);
                                }
                            }
                            ALL_USERS.set(i,changedUser);
                            iniToolbar();
                            invalidateOptionsMenu();
                            return;
                        }
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
        iniUsersChildListener();
        iniUsersValueListener();
        usersReference.addChildEventListener(this.usersChildListener);
        usersReference.addListenerForSingleValueEvent(this.usersValueListener);
    }

    public ArrayList<Messages> loadMessages(int count){
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

    public void sendMessage(View view){
        String text = textField.getText().toString();
        if(!text.isEmpty() && !text.trim().isEmpty()){
            Messages.sendMessage(obtainUser(currentUser.getUid()).getUsername(),
                    text,
                    Messages.getDateFormatted(new Date()),CURRENT_CHAT_NAME,0);
        }
        textField.setText("");
    }

    public void uploadImage(View view){
        selectImage();
    }

    public String getDateForSentImage(Date date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        return dateFormat.format(date);
    }

    public void selectImage(){
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
            final String current = obtainUser(currentUser.getUid()).getUsername();
            final String route = IMAGES_STORAGE_BASE_ROUTE+
                    CURRENT_CHAT_NAME+"/"+
                    current+"-"+getDateForSentImage(date);
            StorageReference image = FirebaseStorage.getInstance().getReference().child(route);
            image.putFile(image_to_upload).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Messages.sendMessage(route,current,Messages.getDateFormatted(date),CURRENT_CHAT_NAME,IMAGE_MESSAGE_CODE);
                }
            });
        }
    }

    public void saveLastMessageOnDevice(){
        int size = last50Messages.size();
        if(size != 0){
            SharedPreferences appInternalData = getSharedPreferences("last_messages",Context.MODE_PRIVATE);
            SharedPreferences.Editor dataEditor = appInternalData.edit();
            String message = last50Messages.get(size-1).toString();
            dataEditor.putString(CURRENT_CHAT_NAME,message);
            dataEditor.apply();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveLastMessageOnDevice();
        CURRENT_CHAT_NAME = "";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveLastMessageOnDevice();
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
