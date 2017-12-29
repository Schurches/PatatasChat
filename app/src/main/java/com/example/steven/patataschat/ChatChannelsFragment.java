package com.example.steven.patataschat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
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
import com.google.firebase.database.ValueEventListener;

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
    private final int ADD_CHANNEL_ICON = 2;
    private boolean isADMINOrROOT;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.chat_channels_fragment,container,false);
        this.isADMINOrROOT = getArguments().getBoolean("isAdmin");
        return currentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        chatList = getActivity().findViewById(R.id.chats_container);
        database = FirebaseDatabase.getInstance().getReference("chats");
        chatList.removeAllViews();
        loadChatChannels(chatList);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.removeAllViews();
                loadChatChannels(chatList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                int i = 3;
            }
        });
        database.addChildEventListener(new ChildEventListener() {
            @Override
             public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                 Chats chat = dataSnapshot.getValue(Chats.class);
                 chatChannels.add(chat);
                 chatChannelsID.add(View.generateViewId());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int size = chatChannels.size();
                for (int i=0; i<size;i++){
                    if(chatChannels.get(i).getChat_name().equals(dataSnapshot.getKey())){
                        chatChannels.remove(i);
                        chatChannelsID.remove(i);
                    }
                }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==ADD_CHANNEL_CODE){
            if(resultCode == Activity.RESULT_OK){
                Bundle datos = data.getExtras();
                String channel_title = datos.getString("ChannelTitle");
                int icon_id = datos.getInt("iconID");
                DatabaseReference newChannel = FirebaseDatabase.getInstance().getReference().child("chats");
                Chats chat = new Chats(channel_title,icon_id);
                newChannel.child(channel_title).setValue(chat);
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
        if(chatAmmount==0){
            LinearLayout layout;
            String message = "";
            if(isADMINOrROOT){
                message = getResources().getString(R.string.first_channel);
            }else{
                message = getResources().getString(R.string.no_chats);
            }
            layout = addNewChannelView(message,ADD_CHANNEL_ICON,isADMINOrROOT,-1);
            chatsLayout.addView(layout);
        }else{
            for (int i = 0; i < chatAmmount; i++) {
                String addMessage = chatChannels.get(i).getChat_name();
                int icon = chatChannels.get(i).getChat_icon();
                int channelID = chatChannelsID.get(i);
                LinearLayout layout = addNewChannelView(addMessage,icon,isADMINOrROOT,channelID);
                chatsLayout.addView(layout);
            }
            if(isADMINOrROOT){
                String message = getResources().getString(R.string.add_chat);
                LinearLayout layout = addNewChannelView(message,ADD_CHANNEL_ICON,true,-1);
                chatsLayout.addView(layout);
            }
        }

    }

    public LinearLayout addNewChannelView(String addMessage, int icon, boolean isAdmin, int channelID){
        LinearLayout layout = createChannelLayout();
        if(channelID == -1){ //If not a chat channel but either an "add channel" or "no channel" view
            if(isAdmin){
                layout.setId(ADD_CHANNEL_ID);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent addNewChannel_Intent = new Intent(getContext(),AddChannelActivity.class);
                        startActivityForResult(addNewChannel_Intent,ADD_CHANNEL_CODE);
                    }
                });
            }
        }else{ //If indeed is a chat channel
            layout.setId(channelID);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openChatRoom_Intent = new Intent(getContext(),ChatRoomActivity.class);
                    int selectedChatRoomID = findChatByID(view.getId());
                    Chats room = chatChannels.get(selectedChatRoomID);
                    openChatRoom_Intent.putExtra("chat_name",room.getChat_name());
                    openChatRoom_Intent.putExtra("chat_icon",room.getChat_icon());
                    startActivity(openChatRoom_Intent);
                }
            });

        }
        layout.addView(setChatIconAttributes(icon));
        layout.addView(setChatTitleAttributes(addMessage));
        if(channelID != -1){
            layout.addView(setChatCountAttributes(10));
        }
        return layout;
    }

    public int findChatByID(int chatID){
        int nChats = chatChannelsID.size();
        for (int i = 0; i < nChats;i++){
            if(chatChannelsID.get(i) == chatID){
                return i;
            }
        }
        return -1;
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
