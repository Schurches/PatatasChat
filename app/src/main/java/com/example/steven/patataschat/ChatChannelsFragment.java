package com.example.steven.patataschat;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by steven on 16/12/2017.
 */

public class ChatChannelsFragment extends Fragment {

    public static final String TITLE = "ChatChannel";
    private final List<Integer> chatChannelsID = new ArrayList<>();
    private LinearLayout chatList;
    private DatabaseReference database;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.chat_channels_fragment,container,false);
        return currentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        chatList = getActivity().findViewById(R.id.chats_container);
        chatChannelsID.add(View.generateViewId());
        chatChannelsID.add(View.generateViewId());
        chatChannelsID.add(View.generateViewId());
        loadChatChannels(chatList);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void loadChatChannels(LinearLayout chats){
        List<Integer> imageID = Arrays.asList(1,1,1);
        List<String> channels = Arrays.asList("Main","Mierda","OpenSource");
        for (int i = 0; i < channels.size(); i++){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout layout = new LinearLayout(getContext());
            int margins = (int) getResources().getDimension(R.dimen.chatList_layout_margins);
            params.setMargins(margins,margins,margins,margins);
            layout.setLayoutParams(params);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.addView(setChatIconAttributes(1));
            layout.addView(setChatTitleAttributes(channels.get(i)));
            layout.addView(setChatCountAttributes(69));
            layout.setId(chatChannelsID.get(i));
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(),view.getId()+"",Toast.LENGTH_SHORT).show();
                }
            });
            chats.addView(layout);
        }

    }


    public ImageView setChatIconAttributes(int iconID){
        ImageView imagen1 = new ImageView(getContext());
        imagen1.setImageResource(R.drawable.ic_chat_black_24dp);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START;
        imagen1.setLayoutParams(params);
        return imagen1;
    }

    public TextView setChatTitleAttributes(String Title){
        TextView titulo = new TextView(getContext());
        titulo.setText(Title);
        titulo.setTextSize((int) getResources().getDimension(R.dimen.chatList_title_size));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.rightMargin = (int) getResources().getDimension(R.dimen.chatList_name_margin_H);
        params.leftMargin = (int) getResources().getDimension(R.dimen.chatList_name_margin_H);
        titulo.setLayoutParams(params);
        return titulo;
    }

    public TextView setChatCountAttributes(int Count){
        TextView counter = new TextView(getContext());
        counter.setText(Count+"");
        counter.setTextSize((int) getResources().getDimension(R.dimen.chatList_count_size));
        counter.setTextColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.rightMargin = (int) getResources().getDimension(R.dimen.chatList_message_count_margin);
        params.leftMargin = (int) getResources().getDimension(R.dimen.chatList_name_margin_H);
        counter.setLayoutParams(params);
        return counter;
    }



}
