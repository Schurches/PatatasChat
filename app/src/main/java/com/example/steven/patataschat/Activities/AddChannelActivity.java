package com.example.steven.patataschat.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.steven.patataschat.Entities.Chats;
import com.example.steven.patataschat.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddChannelActivity extends AppCompatActivity {

    private final String EMPTY_MESSAGE_TEMPLATE = "none°°!!%%&&none°°!!%%&&none°°!!%%&&0";
    private EditText channelName;
    private Spinner iconSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_channel);

        channelName = findViewById(R.id.channel_title);
        iconSelector = findViewById(R.id.icon_selector_list);

        ArrayList<String> icons_list = new ArrayList<>();
        icons_list.add(getString(R.string.icon_1));
        icons_list.add(getString(R.string.icon_2));
        icons_list.add(getString(R.string.icon_3));
        icons_list.add(getString(R.string.icon_4));
        icons_list.add(getString(R.string.icon_5));
        ArrayAdapter<String> spinerListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, icons_list);
        spinerListAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        iconSelector.setAdapter(spinerListAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void sendNewChannelDetails(View v){
        String title = channelName.getText().toString();
        if(!title.isEmpty() && !title.trim().isEmpty()){
            int option = iconSelector.getSelectedItemPosition()+1;
            Chats newChat = new Chats(title,option,1);
            DatabaseReference newChannel = FirebaseDatabase.getInstance().getReference().child("chats");
            newChannel.child(title).setValue(newChat);
            SharedPreferences sharedPreferences = getSharedPreferences("last_messages", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(title,EMPTY_MESSAGE_TEMPLATE);
            editor.apply();
            Toast.makeText(this,R.string.chatChannel_new_added,Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(this, R.string.chatChannel_failed_empty, Toast.LENGTH_SHORT).show();
        }
    }

}
