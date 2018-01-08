package com.example.steven.patataschat.Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.steven.patataschat.Adapters.SectionPagerAdapter;
import com.example.steven.patataschat.Fragments.ChatChannelsFragment;
import com.example.steven.patataschat.Entities.Users;
import com.example.steven.patataschat.Fragments.ProfileFragment;
import com.example.steven.patataschat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatInterfaceActivity extends AppCompatActivity {

    //CHECK: https://stackoverflow.com/questions/24188050/how-to-access-fragments-child-views-inside-fragments-parent-activity
    private final ArrayList<Users> all_users = new ArrayList<>();
    private ViewPager fragments_visualizer;
    private SectionPagerAdapter fragments_adapter;
    private BottomNavigationView nav_view;
    private DatabaseReference usersReference;
    private boolean isUserAdminOrRoot;
    private ValueEventListener usersValueListener;
    private ChildEventListener usersChildListener;
    private boolean firstResume = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_interface);
        usersReference = FirebaseDatabase.getInstance().getReference("users");
        isUserAdminOrRoot = false;
        iniUsersListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        usersReference.removeEventListener(this.usersValueListener);
        usersReference.removeEventListener(this.usersChildListener);
        firstResume = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!firstResume){
            iniValueListener();
            iniChildListener();
            usersReference.addListenerForSingleValueEvent(this.usersValueListener);
            usersReference.addChildEventListener(this.usersChildListener);
            changeCurrentUserRank(isUserAdminOrRoot);
            ((ChatChannelsFragment)fragments_adapter.getItem(0)).getDetector().finishNotificationSystem();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((ChatChannelsFragment)fragments_adapter.getItem(0)).getDetector().finishNotificationSystem();
    }

    private void iniValueListener(){
        this.usersValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //When all users has finally been loaded
                if(firstResume){
                    iniViewPager();
                    iniNavigationView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void iniChildListener(){
        this.usersChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Users userAdded = dataSnapshot.getValue(Users.class);
                if(userAdded.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    if(firstResume){
                        if(userAdded.getRank() > 3){
                            isUserAdminOrRoot = true;
                        }else{
                            isUserAdminOrRoot = false;
                        }
                    }else{
                        if(isUserAdminOrRoot && userAdded.getRank() < 4){
                            changeCurrentUserRank(false);
                        }else if(!isUserAdminOrRoot && userAdded.getRank() > 3){
                            changeCurrentUserRank(true);
                        }
                    }
                }
                all_users.add(userAdded);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int size = all_users.size();
                for(int i=0; i<size ; i++){
                    if(all_users.get(i).getUser_id().equals(dataSnapshot.getKey())){
                        Users changedUser = dataSnapshot.getValue(Users.class);
                        all_users.set(i,changedUser);
                        if(changedUser.getRank() >= 4){
                            changeCurrentUserRank(true);
                        }else{
                            changeCurrentUserRank(false);
                        }
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
        this.usersValueListener = null;
        this.usersChildListener = null;
        iniValueListener();
        iniChildListener();
        usersReference.addListenerForSingleValueEvent(this.usersValueListener);
        usersReference.addChildEventListener(this.usersChildListener);
    }

    public void changeCurrentUserRank(boolean isStillAdmin){
        isUserAdminOrRoot = isStillAdmin;
        ChatChannelsFragment fragment = (ChatChannelsFragment) fragments_adapter.getItem(0);
        fragment.OnRankChanged(isStillAdmin);
    }

    @Override
    public void onBackPressed() {

    }

    private void iniViewPager(){
        fragments_visualizer = findViewById(R.id.pager);
        setupViewPager(fragments_visualizer);
        fragments_visualizer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0: //Chat channel
                        nav_view.setSelectedItemId(R.id.chats);
                        break;
                    case 1: //Profile
                        nav_view.setSelectedItemId(R.id.profile);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void iniNavigationView(){
        nav_view = findViewById(R.id.navigation_view);
        nav_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.chats:
                        fragments_visualizer.setCurrentItem(0,true);
                        break;
                    case R.id.profile:
                        fragments_visualizer.setCurrentItem(1,true);
                        break;
                    case R.id.configuration:
                        break;
                    default:
                        break;
                }
                item.setChecked(!item.isChecked());
                return false;
            }
        });
    }

    private void setupViewPager(ViewPager visor_fragments){
        fragments_adapter = new SectionPagerAdapter(getSupportFragmentManager());
        Bundle info = new Bundle();
        info.putBoolean("isAdmin",isUserAdminOrRoot);
        ChatChannelsFragment chats = new ChatChannelsFragment();
        chats.setArguments(info);
        fragments_adapter.addFragment(chats,"ChatChannel");
        fragments_adapter.addFragment(new ProfileFragment(), "Profile");
        visor_fragments.setAdapter(fragments_adapter);
    }

}
