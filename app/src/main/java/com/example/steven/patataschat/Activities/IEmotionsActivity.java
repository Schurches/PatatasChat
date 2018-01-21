package com.example.steven.patataschat.Activities;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.Date;

public class IEmotionsActivity extends AppCompatActivity {

    private final ArrayList<IEmotionButton> buttons = new ArrayList<>();
    private final ArrayList<Users> users = new ArrayList<>();
    /*Widgets*/
    private RecyclerView recyclerView;
    private ButtonsAdapter button_list;
    private EditText titleTextField;
    private Button previewButton;
    private Button colorButton;
    private ImageButton iconButton;
    /*Database and authentication*/
    private DatabaseReference IEmotionsReference;
    private DatabaseReference userReference;
    private ChildEventListener childListener;
    private ChildEventListener userChildListener;
    private ValueEventListener userValueListener;
    private FirebaseAuth auth_service;
    /*Variables*/
    private ArrayList<ColorStateList> colors;
    private ArrayList<Drawable> icons;
    private int colorPosition;
    private int iconPosition;
    private String chatName;
    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iemotions);
        this.titleTextField = findViewById(R.id.button_title);
        this.previewButton = findViewById(R.id.preview_button);
        this.colorButton = findViewById(R.id.color_button);
        this.iconButton = findViewById(R.id.icon_button);
        iniColorAndDrawables();
        setEditTextOnTextChangeEvent();
        Bundle data = getIntent().getExtras();
        this.chatName = data.getString("chat_name");
        this.userName = data.getString("user_name");
        this.auth_service = FirebaseAuth.getInstance();
        this.userReference = FirebaseDatabase.getInstance().getReference("users");
        this.IEmotionsReference = FirebaseDatabase.getInstance().getReference("IEmotions");
        iniUsersListener();
        this.recyclerView = findViewById(R.id.buttons_container);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        this.button_list= new ButtonsAdapter(buttons, userName, chatName);
        this.recyclerView.setAdapter(button_list);
    }

    public void iniColorAndDrawables(){
        colors = new ArrayList<>();
        colors.add(ColorStateList.valueOf(getResources().getColor(R.color.color_rank_USER)));
        colors.add(ColorStateList.valueOf(getResources().getColor(R.color.color_rank_TRUSTED)));
        colors.add(ColorStateList.valueOf(getResources().getColor(R.color.color_rank_MOD)));
        colors.add(ColorStateList.valueOf(getResources().getColor(R.color.color_rank_ADMIN)));
        colors.add(ColorStateList.valueOf(getResources().getColor(R.color.color_rank_ROOT)));
        colors.add(ColorStateList.valueOf(getResources().getColor(R.color.colorMessageSentME)));
        colors.add(ColorStateList.valueOf(getResources().getColor(R.color.colorMessageSentOTHER)));
        colors.add(ColorStateList.valueOf(getResources().getColor(R.color.color_IEmotions_teal)));
        colors.add(ColorStateList.valueOf(getResources().getColor(R.color.color_IEmotions_brown)));
        colors.add(ColorStateList.valueOf(getResources().getColor(R.color.color_IEmotions_black)));
        colors.add(ColorStateList.valueOf(getResources().getColor(R.color.colorChatRoom_icons)));
        colorPosition = 0;
        icons = new ArrayList<>();
        icons.add(getResources().getDrawable(R.drawable.ic_iemotions_24dp));
        icons.add(getResources().getDrawable(R.drawable.ic_iemotions_awesome));
        icons.add(getResources().getDrawable(R.drawable.ic_iemotions_death));
        icons.add(getResources().getDrawable(R.drawable.ic_iemotions_disappointed));
        icons.add(getResources().getDrawable(R.drawable.ic_iemotions_run));
        icons.add(getResources().getDrawable(R.drawable.ic_iemotions_sad));
        icons.add(getResources().getDrawable(R.drawable.ic_iemotions_child));
        icons.add(getResources().getDrawable(R.drawable.ic_email_black_24dp));
        icons.add(getResources().getDrawable(R.drawable.ic_iemotions_alert));
        icons.add(getResources().getDrawable(R.drawable.ic_chat_black_24dp));
        iconPosition = 0;
        colorButton.setBackgroundTintList(colors.get(0));
        iconButton.setImageDrawable(icons.get(0));
    }

    public void nextColor(View view){
        switch(view.getId()) {
            case R.id.previous_1:
                if (colorPosition == 0) {
                    colorPosition = colors.size() - 1;
                } else {
                    colorPosition--;
                }
                colorButton.setBackgroundTintList(colors.get(colorPosition));
                break;
            case R.id.forward_1:
                if (colorPosition == colors.size() - 1) {
                    colorPosition = 0;
                } else {
                    colorPosition++;
                }
                break;
        }
        colorButton.setBackgroundTintList(colors.get(colorPosition));
        previewButton.setBackgroundTintList(colors.get(colorPosition));
    }

    public void nextIcon(View view){
        switch (view.getId()){
            case R.id.previous_2:
                if (iconPosition == 0) {
                    iconPosition = icons.size() - 1;
                } else {
                    iconPosition--;
                }
                break;
            case R.id.forward_2:
                if (iconPosition == icons.size() - 1) {
                    iconPosition = 0;
                } else {
                    iconPosition++;
                }
                break;
        }
        iconButton.setImageDrawable(icons.get(iconPosition));
        previewButton.setCompoundDrawablesWithIntrinsicBounds(icons.get(iconPosition),null,null,null);
    }

    public void setEditTextOnTextChangeEvent(){
        titleTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                previewButton.setText(charSequence);
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

    public boolean isTitleValid(String title){
        if(title.isEmpty() || title.trim().isEmpty()){
            titleTextField.setError(getString(R.string.field_empty_error));
            return false;
        }else if(title.length() < 2){
            titleTextField.setError(getString(R.string.title_length_min_error));
            return false;
        }else{
            titleTextField.setError(null);
            return true;
        }
    }

    public void addIEmotionButton(View view){
        String title = titleTextField.getText().toString();
        if(isTitleValid(title)){
            boolean white_letter = true;
            if(colorPosition == 0 || colorPosition == 6 || colorPosition == 7|| colorPosition > 9){
                white_letter = false;
            }
            IEmotionButton IButton = new IEmotionButton(title,colorPosition+1,iconPosition+1,white_letter);
            String buttonID = IEmotionsReference.push().getKey();
            IEmotionsReference.child(buttonID).setValue(IButton);
            String announce = String.format(getResources().getString(R.string.IEmotions_created),title);
            Messages.sendMessage(userName,announce,Messages.getDateFormatted(new Date()),chatName,1);
            Toast.makeText(this, R.string.IEmotions_success, Toast.LENGTH_SHORT).show();
            titleTextField.setText("");
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
