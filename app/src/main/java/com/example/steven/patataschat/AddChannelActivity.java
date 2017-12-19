package com.example.steven.patataschat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddChannelActivity extends AppCompatActivity {

    private EditText channelName;
    private Spinner iconSelector;
    private Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_channel);

        channelName = findViewById(R.id.channel_title);
        iconSelector = findViewById(R.id.icon_selector_list);
        createButton = findViewById(R.id.create_channel_button);

        ArrayList<String> icons_list = new ArrayList<>();
        icons_list.add("icon_1");
        icons_list.add("icon_2");
        icons_list.add("icon_3");

        ArrayAdapter<String> spinerListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, icons_list);
        spinerListAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        iconSelector.setAdapter(spinerListAdapter);

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void sendNewChannelDetails(View v){
        Intent information = new Intent();
        String title = channelName.getText().toString();
        int option = iconSelector.getSelectedItemPosition()+1;
        information.putExtra("ChannelTitle",title);
        information.putExtra("iconID",option);
        setResult(RESULT_OK,information);
        finish();
    }


}
