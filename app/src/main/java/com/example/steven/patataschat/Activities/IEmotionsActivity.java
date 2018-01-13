package com.example.steven.patataschat.Activities;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.steven.patataschat.Adapters.ButtonsAdapter;
import com.example.steven.patataschat.Entities.IEmotionButton;
import com.example.steven.patataschat.Entities.Messages;
import com.example.steven.patataschat.Entities.Users;
import com.example.steven.patataschat.R;
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

public class IEmotionsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Spinner icon_spinner;
    private Spinner color_spinner;
    private EditText title_textfield;
    private Button preview_button;
    private final ArrayList<IEmotionButton> buttons = new ArrayList<>();
    private final ArrayList<Users> users = new ArrayList<>();
    private final int ANNOUNCE_CODE = 1;
    private ButtonsAdapter button_list;
    private DatabaseReference IEmotionsReference;
    private DatabaseReference userReference;
    private ChildEventListener childListener;
    private ChildEventListener userChildListener;
    private ValueEventListener userValueListener;
    private FirebaseAuth auth_service;
    private String chat_name;
    private String user_name;
    private boolean spinner_1_initialized = false;
    private boolean spinner_2_initialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iemotions);
        this.icon_spinner = findViewById(R.id.icon_spinner);
        this.color_spinner = findViewById(R.id.color_spinner);
        this.title_textfield = findViewById(R.id.button_title);
        this.preview_button = findViewById(R.id.preview_button);
        iniSpinners();
        setComponentsEvent();
        Bundle data = getIntent().getExtras();
        this.chat_name = data.getString("chat_name");
        this.user_name = data.getString("user_name");
        this.auth_service = FirebaseAuth.getInstance();
        this.userReference = FirebaseDatabase.getInstance().getReference("users");
        this.IEmotionsReference = FirebaseDatabase.getInstance().getReference("IEmotions");
        iniUsersListener();
        this.recyclerView = findViewById(R.id.buttons_container);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        this.button_list= new ButtonsAdapter(buttons,user_name,chat_name);
        this.recyclerView.setAdapter(button_list);
    }

    public void iniSpinners(){
        ArrayList<String> colors_name = new ArrayList<>();
        colors_name.add(getString(R.string.IEmotions_color));
        colors_name.add(getString(R.string.IEmotions_color_user));
        colors_name.add(getString(R.string.IEmotions_color_trusted));
        colors_name.add(getString(R.string.IEmotions_color_mod));
        colors_name.add(getString(R.string.IEmotions_color_admin));
        colors_name.add(getString(R.string.IEmotions_color_root));
        colors_name.add(getString(R.string.IEmotions_color_sentME));
        colors_name.add(getString(R.string.IEmotions_color_sentOTHER));
        colors_name.add(getString(R.string.IEmotions_color_teal));
        colors_name.add(getString(R.string.IEmotions_color_brown));
        colors_name.add(getString(R.string.IEmotions_color_black));
        colors_name.add(getString(R.string.IEmotions_color_white));
        ArrayAdapter<String> color_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors_name);
        color_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        color_spinner.setAdapter(color_adapter);
        ArrayList<String> icon_name = new ArrayList<>();
        icon_name.add(getString(R.string.IEmotions_pick));
        icon_name.add(getString(R.string.IEmotions_icon_happy));
        icon_name.add(getString(R.string.IEmotions_icon_awesome));
        icon_name.add(getString(R.string.IEmotions_icon_death));
        icon_name.add(getString(R.string.IEmotions_icon_disappointment));
        icon_name.add(getString(R.string.IEmotions_icon_run));
        icon_name.add(getString(R.string.IEmotions_icon_sad));
        icon_name.add(getString(R.string.IEmotions_icon_child));
        icon_name.add(getString(R.string.IEmotions_icon_email));
        icon_name.add(getString(R.string.IEmotions_icon_warn));
        icon_name.add(getString(R.string.IEmotions_icon_message));
        ArrayAdapter<String> icon_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, icon_name);
        icon_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        icon_spinner.setAdapter(icon_adapter);
    }

    public void setComponentsEvent(){
        color_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinner_1_initialized){
                    preview_button.setBackgroundTintList(obtainColor(i));
                }else{
                    spinner_1_initialized = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        icon_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinner_2_initialized){
                    preview_button.setCompoundDrawablesWithIntrinsicBounds(obtainDrawable(i),null,null,null);
                }else{
                    spinner_2_initialized = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        title_textfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                preview_button.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void iniChildListener(){
        this.childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                buttons.add(dataSnapshot.getValue(IEmotionButton.class));
                button_list.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                IEmotionButton button = dataSnapshot.getValue(IEmotionButton.class);
                int size = buttons.size();
                for(int i = 0; i < size; i++){
                    if(buttons.get(i).getIEmotion_title().equals(dataSnapshot.getKey())){
                        buttons.set(i,button);
                        return;
                    }
                }
                button_list.notifyDataSetChanged();
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

    public void iniListener(){
        iniChildListener();
        IEmotionsReference.addChildEventListener(childListener);
    }

    public void iniUserValueListener(){
        this.userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                iniListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public void iniUserChildListener(){
        this.userChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                users.add(dataSnapshot.getValue(Users.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(auth_service != null){
                    Users changedUser = dataSnapshot.getValue(Users.class);
                    if(changedUser.getUser_id().equals(auth_service.getCurrentUser().getUid())){
                        if(changedUser.isBanned()){
                            auth_service.signOut();
                            Toast.makeText(IEmotionsActivity.this, R.string.user_banned_message, Toast.LENGTH_SHORT).show();
                            finishAffinity();
                        }
                        if(changedUser.isMuted()){
                            Toast.makeText(IEmotionsActivity.this, R.string.IEmotions_muted, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }else{
                        int size = users.size();
                        for(int i = 0; i < size; i++){
                            if(changedUser.getUser_id().equals(users.get(i).getUser_id())){
                                users.set(i,changedUser);
                                return;
                            }
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

    public void iniUsersListener(){
        iniUserChildListener();
        iniUserValueListener();
        userReference.addChildEventListener(userChildListener);
        userReference.addValueEventListener(userValueListener);
    }

    public void addIEmotionButton(View view){
        int icon_id = icon_spinner.getSelectedItemPosition();
        int color_id = color_spinner.getSelectedItemPosition();
        String title = title_textfield.getText().toString();
        if(!title.isEmpty() && !title.trim().isEmpty()){
            boolean white_letter = true;
            if(color_id == 0 || color_id == 7 || color_id == 6|| color_id > 10){
                white_letter = false;
            }
            IEmotionButton IButton = new IEmotionButton(title,color_id,icon_id,white_letter);
            String buttonID = IEmotionsReference.push().getKey();
            IEmotionsReference.child(buttonID).setValue(IButton);
            String announce = String.format(getResources().getString(R.string.IEmotions_created),title);
            sendAnnounce(user_name,announce,getDateFormatted(new Date()));
            Toast.makeText(this, R.string.IEmotions_success, Toast.LENGTH_SHORT).show();
            icon_spinner.setSelection(0);
            color_spinner.setSelection(0);
            title_textfield.setText("");
        }else{
            Toast.makeText(this, R.string.IEmotions_empty, Toast.LENGTH_SHORT).show();
        }
    }

    public void sendAnnounce(String username, String message, String date){
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference(chat_name);
        String messageID = chatReference.push().getKey();
        Messages body = new Messages(username,message,date, ANNOUNCE_CODE);
        chatReference.child(messageID).setValue(body);
    }

    public String getDateFormatted(Date date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public ColorStateList obtainColor(int colorID){
        return ContextCompat.getColorStateList(getApplicationContext(),obtainColorResourceByID(colorID));
    }

    public int obtainColorResourceByID(int colorID){
        switch(colorID){
            case 1:
                return R.color.color_rank_USER;
            case 2:
                return R.color.color_rank_TRUSTED;
            case 3:
                return R.color.color_rank_MOD;
            case 4:
                return R.color.color_rank_ADMIN;
            case 5:
                return R.color.color_rank_ROOT;
            case 6:
                return R.color.colorMessageSentME;
            case 7:
                return R.color.colorMessageSentOTHER;
            case 8:
                return R.color.color_IEmotions_teal;
            case 9:
                return R.color.color_IEmotions_brown;
            case 10:
                return R.color.color_IEmotions_black;
            default:
                return R.color.colorChatRoom_icons;
        }
    }

    public Drawable obtainDrawable(int iconID){
        return getResources().getDrawable(obtainIconByID(iconID));
    }

    public int obtainIconByID(int iconID){
        switch(iconID){
            case 1:
                return R.drawable.ic_iemotions_24dp;
            case 2:
                return R.drawable.ic_iemotions_awesome;
            case 3:
                return R.drawable.ic_iemotions_death;
            case 4:
                return R.drawable.ic_iemotions_disappointed;
            case 5:
                return R.drawable.ic_iemotions_run;
            case 6:
                return R.drawable.ic_iemotions_sad;
            case 7:
                return R.drawable.ic_iemotions_child;
            case 8:
                return R.drawable.ic_email_black_24dp;
            case 9:
                return R.drawable.ic_iemotions_alert;
            default:
                return R.drawable.ic_chat_black_24dp;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(userValueListener != null){
            this.userReference.removeEventListener(userValueListener);
        }
        if(userChildListener != null){
            this.userReference.removeEventListener(userChildListener);
        }
        if(childListener != null){
            this.IEmotionsReference.removeEventListener(childListener);
        }
    }
}
