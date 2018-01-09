package com.example.steven.patataschat.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.steven.patataschat.Adapters.UsersAdapter;
import com.example.steven.patataschat.Entities.Users;
import com.example.steven.patataschat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageUserActivity extends AppCompatActivity {

    private final ArrayList<Users> users_list = new ArrayList<>();
    private RecyclerView recyclerView;
    private Button changeButton;
    private EditText nicknameEdittext;
    private TextView usernameTextview;
    private DatabaseReference userReference;
    private ValueEventListener usersValueListener;
    private ChildEventListener usersChildListener;
    private UsersAdapter usersAdapter;
    private boolean firstRead;
    private int current_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        setViews();
        setDatabaseReference();
    }

    public void setViews(){
        this.recyclerView = findViewById(R.id.users_view);
        this.changeButton = findViewById(R.id.button_change);
        this.nicknameEdittext = findViewById(R.id.textbox_new_nickname);
        this.usernameTextview = findViewById(R.id.text_username);
        this.firstRead = true;
    }

    public void setDatabaseReference(){
        this.userReference = FirebaseDatabase.getInstance().getReference("users");
        iniChildListener();
        iniValueListener();
        this.userReference.addListenerForSingleValueEvent(this.usersValueListener);
        this.userReference.addChildEventListener(this.usersChildListener);
    }

    public void loadUserInfo(int position){
        Users u = this.users_list.get(position);
        if(u!=null){
            String username_template = String.format(getResources().getString(R.string.manage_username_template),u.getUsername());
            this.usernameTextview.setText(username_template);
            this.nicknameEdittext.setHint(u.getNickname());
        }
    }

    public void setRecyclerView(){
        this.usersAdapter = new UsersAdapter(this.users_list,getApplicationContext());
        this.recyclerView.setAdapter(this.usersAdapter);
        this.recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View touchedUser = rv.findChildViewUnder(e.getX(),e.getY());
                if(touchedUser!=null){
                    int position = rv.getChildAdapterPosition(touchedUser);
                    current_selected = position;
                    loadUserInfo(position);
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
                setRecyclerView();
                firstRead = false;
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
                if(!firstRead){
                    usersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Users user = dataSnapshot.getValue(Users.class);
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


}
