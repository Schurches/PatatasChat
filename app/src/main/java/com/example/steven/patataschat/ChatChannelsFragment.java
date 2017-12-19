package com.example.steven.patataschat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steven.patataschat.Entities.Chats;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steven on 16/12/2017.
 */

public class ChatChannelsFragment extends Fragment {

    public static final String TITLE = "ChatChannel";
    private final List<Integer> chatChannelsID = new ArrayList<>();
    private final List<Chats> chatChannels = new ArrayList<>();
    private final int ADD_CHANNEL_ID = 1001;
    private LinearLayout chatList;
    private DatabaseReference database;
    private final int ADD_CHANNEL_CODE = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.chat_channels_fragment,container,false);
        return currentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        chatList = getActivity().findViewById(R.id.chats_container);
        database = FirebaseDatabase.getInstance().getReference("chats");
        chatList.removeAllViews();
        loadChatChannels(chatList);
        database.addChildEventListener(new ChildEventListener() {
            @Override
             public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                 Chats chat = dataSnapshot.getValue(Chats.class);
                 chatChannels.add(chat);
                 chatChannelsID.add(View.generateViewId());
                 chatList.removeAllViews();
                 loadChatChannels(chatList);
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
        });
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();





        /*
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==ADD_CHANNEL_CODE){
            if(resultCode == Activity.RESULT_OK){
                Bundle datos = data.getExtras();
                String channel_title = datos.getString("ChannelTitle");
                int icon_id = datos.getInt("iconID");
                DatabaseReference newChannel = FirebaseDatabase.getInstance().getReference().child("chats");
                Chats chat = new Chats(channel_title,icon_id);
                newChannel.child(channel_title).setValue(chat);
                //newChannel.child("chatTitle").setValue(channel_title);
                //newChannel.child("iconCode").setValue(icon_id);
                Toast.makeText(getContext(),R.string.chatChannel_new_added,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public LinearLayout createChannelLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout layout = new LinearLayout(getContext());
        int margins = (int) getResources().getDimension(R.dimen.chatList_layout_margins);
        params.setMargins(margins, margins, margins, margins);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        return layout;
    }

    public void loadChatChannels(LinearLayout chatsLayout){
        int chatAmmount = chatChannels.size();
        for (int i = 0; i <= chatAmmount; i++) {
            LinearLayout layout = createChannelLayout();
            String addMessage;
            int icon;
            if(i==chatAmmount){
                if(chatAmmount==0){
                    addMessage = getResources().getString(R.string.first_channel);
                }else{
                    addMessage = getResources().getString(R.string.add_chat);
                }
                icon = 2;
                layout.setId(ADD_CHANNEL_ID);
            }else{
                icon = chatChannels.get(i).getChat_icon();
                addMessage = chatChannels.get(i).getChat_name();
                layout.setId(chatChannelsID.get(i));
            }
            layout.addView(setChatIconAttributes(icon));
            layout.addView(setChatTitleAttributes(addMessage));
            if(i!=chatAmmount){
                layout.addView(setChatCountAttributes(69));
            }
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(view.getId() == ADD_CHANNEL_ID){
                        Intent addNewChannel_Intent = new Intent(getContext(),AddChannelActivity.class);
                        startActivityForResult(addNewChannel_Intent,ADD_CHANNEL_CODE);
                    }
                }
            });
            chatsLayout.addView(layout);
        }

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


    public ImageView setChatIconAttributes(int iconID){
        ImageView imagen1 = new ImageView(getContext());
        imagen1.setImageResource(obtainChatIcon(iconID));
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
