package com.example.steven.patataschat.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steven.patataschat.Adapters.UsersAdapter;
import com.example.steven.patataschat.Entities.Banlist;
import com.example.steven.patataschat.Entities.Messages;
import com.example.steven.patataschat.Entities.Users;
import com.example.steven.patataschat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class ManageUserActivity extends AppCompatActivity {

    private final ArrayList<Users> users_list = new ArrayList<>();
    private final int ACTION_NICKNAME = 1;
    private final int ACTION_RANK = 2;
    private final int ACTION_MUTE = 3;
    private final int ACTION_BAN = 4;
    private final int ANNOUNCE_CODE = 1;
    private final int MAX_NICKNAME_SIZE = 40;
    private final String REGEX_PATTERN = "^[a-zA-Z0-9:.()_,]*$";
    private DatabaseReference userReference;
    private DatabaseReference chatReference;
    private ValueEventListener usersValueListener;
    private ChildEventListener usersChildListener;
    private FirebaseAuth current_user;

    private Button nickButton;
    private Button rankButton;
    private Button muteButton;
    private Button banButton;
    private Button actionButton;
    private EditText actionEdittext;
    private TextView actionTextview;
    private Spinner rankSpinner;

    private UsersAdapter usersAdapter;
    private RecyclerView recyclerView;
    private int current_selected;
    private int current_action;
    private String chat_name;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        getIntentData(getIntent().getExtras());
        setViews();
        setDatabaseReference();
        current_selected = -1;
        current_action = 0;
        current_user = FirebaseAuth.getInstance();
        loadActionViews();
    }

    public void getIntentData(Bundle data){
        this.chat_name = data.getString("chat_name");
        this.username = data.getString("user_name");
    }

    public void setViews(){
        this.recyclerView = findViewById(R.id.users_view);
        this.rankSpinner = findViewById(R.id.rank_spinner);
        ArrayList<String> rank_list = new ArrayList<>();
        rank_list.add(getResources().getString(R.string.manage_spinner_item_user));
        rank_list.add(getResources().getString(R.string.manage_spinner_item_trusted));
        rank_list.add(getResources().getString(R.string.manage_spinner_item_mod));
        rank_list.add(getResources().getString(R.string.manage_spinner_item_admin));
        rank_list.add(getResources().getString(R.string.manage_spinner_item_root));
        ArrayAdapter<String> rankSpinerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rank_list);
        rankSpinerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        this.rankSpinner.setAdapter(rankSpinerAdapter);
        this.actionButton = findViewById(R.id.button_action);
        this.actionEdittext = findViewById(R.id.edit_text);
        this.actionTextview = findViewById(R.id.text_info);
        this.nickButton = findViewById(R.id.nick_button);
        this.rankButton = findViewById(R.id.rank_button);
        this.muteButton = findViewById(R.id.mute_button);
        this.banButton = findViewById(R.id.ban_button);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        this.usersAdapter = new UsersAdapter(users_list,getApplicationContext());
        this.recyclerView.setAdapter(usersAdapter);
    }

    public void setDatabaseReference(){
        this.chatReference = FirebaseDatabase.getInstance().getReference(this.chat_name);
        this.userReference = FirebaseDatabase.getInstance().getReference("users");
        iniChildListener();
        iniValueListener();
        userReference.addChildEventListener(this.usersChildListener);
        userReference.addListenerForSingleValueEvent(this.usersValueListener);
    }

    public void loadUserInfo(int position){
        Users u = this.users_list.get(position);
        UsersAdapter.UsersHolder holder = (UsersAdapter.UsersHolder) recyclerView.findViewHolderForAdapterPosition(current_selected);
        holder.setBubbleColor(u.isBanned(),true);
        if(u.isBanned()){
            this.banButton.setText(R.string.manage_button_un_ban);
        }else{
            this.banButton.setText(R.string.manage_button_ban);
        }
        if(u.isMuted()){
            this.muteButton.setText(R.string.manage_button_un_mute);
            holder.setColorOnStatus(R.color.color_text_rank_MOD,ACTION_MUTE);
        }else{
            this.muteButton.setText(R.string.manage_button_mute);
            holder.setColorOnStatus(R.color.colorChatRoom_icons,0);
        }
        if(current_action==ACTION_NICKNAME){
            if(u!=null){
                String username_template = String.format(getResources().getString(R.string.manage_username_template),u.getUsername());
                this.actionTextview.setText(username_template);
                this.actionEdittext.setHint(u.getNickname());
            }
        }else if(current_action==ACTION_BAN){
            if(u!=null){
                String name = users_list.get(current_selected).getUsername();
                String message;
                this.actionEdittext.setText("");
                if(u.isBanned()){
                    this.actionEdittext.setVisibility(View.GONE);
                    message = String.format(getResources().getString(R.string.manage_confirm_unban),name);
                }else{
                    message = String.format(getResources().getString(R.string.manage_confirm_ban),name);
                    this.actionEdittext.setVisibility(View.VISIBLE);
                    this.actionEdittext.setHint(R.string.manage_ban_reason_empty);
                }
                this.actionTextview.setText(message);
            }
        }
    }

    public void changeNickname(){
        String new_nick = this.actionEdittext.getText().toString();
        if(new_nick.isEmpty() || new_nick.trim().trim().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.manage_nickname_alert_empty,Toast.LENGTH_SHORT).show();
        }else{
            if(Pattern.matches(REGEX_PATTERN,new_nick)){
                int length = new_nick.length();
                if(length <= MAX_NICKNAME_SIZE){
                    setNickname(new_nick);
                }else{
                    String alert = String.format(getResources().getString(R.string.manage_nickname_alert_size),MAX_NICKNAME_SIZE,length);
                    Toast.makeText(getApplicationContext(),alert,Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),R.string.manage_nickname_alert_invalid_char,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void changeRank(){
        if(current_selected != -1){
            Users current = obtainUser(current_user.getCurrentUser().getUid());
            int current_rank = current.getRank();
            int rank_selected = this.rankSpinner.getSelectedItemPosition()+1;
            if(current_rank == 5){
                setRank(rank_selected);
            }else{
                if(current_rank > rank_selected){
                    setRank(rank_selected);
                }else{
                    Toast.makeText(this,R.string.manage_change_rank_invalid,Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void changeBanStatus(){
        if(current_selected != -1){
            Users u = users_list.get(current_selected);
            if(u!=null){
                Users current = obtainUser(current_user.getCurrentUser().getUid());
                if(u.isBanned()){
                    unBan(u);
                }else{
                    if(current.getRank() >= u.getRank()){
                        setBan(u);
                    }else{
                        Toast.makeText(this,getResources().getString(R.string.manage_ban_invalid),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public void setMute(Users u){
        String key = u.getUser_id();
        DatabaseReference changed_user_ref = FirebaseDatabase.getInstance().getReference("users/"+key);
        u.setMuted(true);
        changed_user_ref.setValue(u);
        String message = String.format(getResources().getString(R.string.manage_notify_mute),username,u.getUsername());
        String date = getDateFormatted(new Date());
        sendAnnounce(username,message,date);
        Toast.makeText(this,R.string.manage_mute_success,Toast.LENGTH_SHORT).show();
    }

    public void unmute(Users u){
        String key = u.getUser_id();
        DatabaseReference changed_user_ref = FirebaseDatabase.getInstance().getReference("users/"+key);
        u.setMuted(false);
        changed_user_ref.setValue(u);
        String message = String.format(getResources().getString(R.string.manage_notify_un_mute),u.getUsername());
        String date = getDateFormatted(new Date());
        sendAnnounce(username,message,date);
        Toast.makeText(this,R.string.manage_un_mute_success,Toast.LENGTH_SHORT).show();
    }

    public void changeMute(View view){
        if(current_selected != -1){
            Users u = users_list.get(current_selected);
            if(u!=null){
                if(u.isMuted()){
                    if(u.getUser_id().equals(current_user.getCurrentUser().getUid())){
                        Toast.makeText(this,R.string.manage_unmute_failed,Toast.LENGTH_SHORT).show();
                    }else{
                        unmute(u);
                    }
                }else{
                    setMute(u);
                }
            }
        }
    }

    public Users obtainUser(String ID){
        for (Users U : users_list) {
            if(U.getUser_id().equals(ID))
            {
                return U;
            }
        }
        return null;
    }

    public void establishAvailableOptions(){
        Users u = obtainUser(current_user.getCurrentUser().getUid());
        int rank = u.getRank();
        if(rank == 3){
            enableActionButtons(true,true,false,false);
        }else if(rank == 2){
            enableActionButtons(true,false,false,false);
        }else if(rank < 2 || rank > 5){
            enableActionButtons(false,false,false,false);
        }
    }

    public void enableActionButtons(boolean showNick, boolean showMute, boolean showRank, boolean showBan){
        this.nickButton.setEnabled(showNick);
        this.muteButton.setEnabled(showMute);
        this.rankButton.setEnabled(showRank);
        this.banButton.setEnabled(showBan);
    }

    public void setBan(Users bannedUser){
        bannedUser.setBanned(true);
        String description = this.actionEdittext.getText().toString();
        String message_success = String.format(getResources().getString(R.string.manage_ban_success),bannedUser.getUsername());
        String message = String.format(getResources().getString(R.string.manage_notify_ban),username,bannedUser.getUsername());
        String date = getDateFormatted(new Date());
        sendAnnounce(username,message,date);

        DatabaseReference banReference = FirebaseDatabase.getInstance().getReference("banned_list");
        Banlist ban = new Banlist(bannedUser.getUser_id(),bannedUser.getDeviceID(),username,description);
        banReference.child(bannedUser.getUser_id()).setValue(ban);

        banReference = FirebaseDatabase.getInstance().getReference("users/"+bannedUser.getUser_id());
        banReference.setValue(bannedUser);
        Toast.makeText(this,message_success,Toast.LENGTH_LONG).show();
        this.actionEdittext.setText("");
    }

    public void setRank(int new_rank){
        Users u = users_list.get(current_selected);
        int past_rank = u.getRank();
        if(past_rank!=new_rank){
            u.setRank(new_rank);
            DatabaseReference affectedUser = FirebaseDatabase.getInstance().getReference("users/"+u.getUser_id());
            affectedUser.setValue(u);
            String status_change;
            if(past_rank > new_rank){
                status_change = getResources().getString(R.string.manage_demote);
            }else{
                status_change = getResources().getString(R.string.manage_promote);
            }
            String message = String.format(getResources().getString(R.string.manage_notify_rank)
                    ,u.getUsername(),
                    status_change,
                    rankSpinner.getSelectedItem().toString(),
                    username);
            String date = getDateFormatted(new Date());
            sendAnnounce(username,message,date);
            Toast.makeText(getApplicationContext(),R.string.manage_rank_success,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),R.string.manage_rank_failure,Toast.LENGTH_SHORT).show();
        }

    }

    public void setNickname(String nickname){
        if(current_selected != -1){
            Users user_to_change = users_list.get(current_selected);
            if(user_to_change != null){
                String key = user_to_change.getUser_id();
                DatabaseReference changed_user_ref = FirebaseDatabase.getInstance().getReference("users/"+key);
                user_to_change.setNickname(nickname);
                changed_user_ref.setValue(user_to_change);
                String message = String.format(getResources().getString(R.string.manage_nickname_changed_announce),this.username,user_to_change.getUsername(),nickname);
                String date = getDateFormatted(new Date());
                sendAnnounce(this.username,message,date);
                this.actionEdittext.setText("");
                Toast.makeText(getApplicationContext(),R.string.manage_nickname_success,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void unBan(final Users u){
        u.setBanned(false);
        DatabaseReference banlistReference = FirebaseDatabase.getInstance().getReference("banned_list");
        banlistReference.child(u.getUser_id()).removeValue();
        String message = String.format(getResources().getString(R.string.manage_notify_unban),u.getUsername());
        String date = getDateFormatted(new Date());
        sendAnnounce(username,message,date);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/"+u.getUser_id());
        userRef.setValue(u);
        Toast.makeText(getApplicationContext(),R.string.manage_unban_success,Toast.LENGTH_SHORT).show();
    }

    public void doAction(View view){
        switch(current_action){
            case ACTION_NICKNAME:
                changeNickname();
                break;
            case ACTION_RANK:
                changeRank();
                break;
            case ACTION_MUTE:
                //changeMute();
                break;
            case ACTION_BAN:
                changeBanStatus();
                break;
        }
    }

    public void setAction(View b){
        switch(b.getId()){
            case R.id.nick_button:
                current_action = ACTION_NICKNAME;
                break;
            case R.id.rank_button:
                current_action = ACTION_RANK;
                break;
            case R.id.mute_button:
                current_action = ACTION_MUTE;
                break;
            case R.id.ban_button:
                current_action = ACTION_BAN;
                break;
        }
        loadActionViews();
    }

    public void loadActionViews(){
        switch (current_action){
            case ACTION_NICKNAME:
                if(current_selected == -1){
                    this.actionTextview.setText(getString(R.string.manage_username_name_empty));
                    this.actionEdittext.setHint(getString(R.string.manage_username_name_empty));
                    this.actionEdittext.setText("");
                }else{
                    loadUserInfo(current_selected);
                }
                setViewVisibility(true,true,false,true);
                this.actionButton.setText(getResources().getString(R.string.manage_button_change));
                break;
            case ACTION_RANK:
                this.actionTextview.setText(getResources().getString(R.string.manage_select_rank));
                this.actionEdittext.setText("");
                this.actionEdittext.setHint("");
                setViewVisibility(true,false,true,true);
                this.actionButton.setText(getResources().getString(R.string.manage_button_change));
                break;
            case ACTION_MUTE:
                //this.actionTextview.setText(getResources().getString(R.string.manage_set_time));
                //this.actionEdittext.setText("");
                //this.actionEdittext.setHint(getResources().getString(R.string.manage_time_hint));
                //setViewVisibility(true,true,false,true);
                //this.actionButton.setText(getResources().getString(R.string.manage_button_mute));
                break;
            case ACTION_BAN:
                if(current_selected == -1){
                    this.actionTextview.setText(getResources().getString(R.string.manage_username_name_empty));
                }else{
                    loadUserInfo(current_selected);
                }
                setViewVisibility(true,true,false,true);
                this.actionButton.setText(getResources().getString(R.string.manage_button_confirm));
                if(current_selected!=-1){
                    loadUserInfo(current_selected);
                }
                break;
            default:
                setViewVisibility(false,false,false,false);
                break;
        }
    }

    public void setViewVisibility(boolean showTextview, boolean showEdittext, boolean showSpinner, boolean showButton){
        if(showTextview){
            this.actionTextview.setVisibility(View.VISIBLE);
        }else{
            this.actionTextview.setVisibility(View.GONE);
        }
        if(showEdittext){
            this.actionEdittext.setVisibility(View.VISIBLE);
        }else{
            this.actionEdittext.setVisibility(View.GONE);
        }
        if(showSpinner){
            this.rankSpinner.setVisibility(View.VISIBLE);
        }else{
            this.rankSpinner.setVisibility(View.GONE);
        }
        if(showButton){
            this.actionButton.setVisibility(View.VISIBLE);
        }else{
            this.actionButton.setVisibility(View.GONE);
        }
    }

    public void sendAnnounce(String username, String message, String date){
        String announceID = chatReference.push().getKey();
        Messages announce = new Messages(username,message,date,ANNOUNCE_CODE);
        chatReference.child(announceID).setValue(announce);
    }

    public String getDateFormatted(Date date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public void setRecyclerOnTouch(){
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View touchedUser = rv.findChildViewUnder(e.getX(),e.getY());
                if(touchedUser!=null){
                    UsersAdapter.UsersHolder holder;
                    if(current_selected != -1){
                        holder = (UsersAdapter.UsersHolder) recyclerView.findViewHolderForAdapterPosition(current_selected);
                        Users u = users_list.get(current_selected);
                        holder.setBubbleColor(u.isBanned(),false);
                    }
                    current_selected = rv.getChildAdapterPosition(touchedUser);
                    loadUserInfo(current_selected);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    public void iniValueListener(){
        this.usersValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setRecyclerOnTouch();
                establishAvailableOptions();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public void iniChildListener(){
        this.usersChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Users user = dataSnapshot.getValue(Users.class);
                users_list.add(user);
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(current_user != null){
                    Users user = dataSnapshot.getValue(Users.class);
                    if(user.getUser_id().equals(current_user.getCurrentUser().getUid())){
                        if(user.isBanned()){
                            current_user.signOut();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_banned_message), Toast.LENGTH_SHORT).show();
                            finishAffinity();
                        }
                    }else{
                        int size = users_list.size();
                        for(int i = 0; i < size; i++){
                            if(users_list.get(i).getUser_id().equals(user.getUser_id())){
                                users_list.set(i,user);
                                if(current_selected == i){
                                    loadUserInfo(i);
                                }
                            }
                        }
                        usersAdapter.notifyDataSetChanged();
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

    @Override
    protected void onStop() {
        userReference.removeEventListener(this.usersValueListener);
        userReference.removeEventListener(this.usersChildListener);
        super.onStop();
    }

}
