package com.example.steven.patataschat.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.steven.patataschat.Activities.ChatRoomActivity;
import com.example.steven.patataschat.Adapters.ChannelsAdapter;
import com.example.steven.patataschat.Entities.Chats;
import com.example.steven.patataschat.Entities.Messages;
import com.example.steven.patataschat.Utility.UnreadMessagesDetector;
import com.example.steven.patataschat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by steven on 16/12/2017.
 */

public class ChatChannelsFragment extends Fragment{

    public static final String TAG = "ChatChannel";
    private final ArrayList<Messages> LAST_MESSAGE_ON_EACH_CHANNEL = new ArrayList<>();
    private final ArrayList<Chats> chatChannels = new ArrayList<>();
    private final String sharedPreferenceFileName = "last_messages";
    private final String REGEX_TEMPLATE = "°°!!%%&&";
    private final String EMPTY_MESSAGE_TEMPLATE = "none"+REGEX_TEMPLATE+"none"+REGEX_TEMPLATE+"none"+REGEX_TEMPLATE+"0";
    private final int ADD_CHANNEL_CODE = 1001;
    private final int NO_CHANNEL_CODE = 2000;
    private UnreadMessagesDetector incomingMessagesDetector;
    private boolean isADMINOrROOT;
    private boolean initialLoadFinished;
    private DatabaseReference database;
    private RecyclerView channelRecyclerView;
    private ChannelsAdapter channelList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.chat_channels_fragment,container,false);
        this.isADMINOrROOT = getArguments().getBoolean("isAdmin");
        this.channelRecyclerView = rootView.findViewById(R.id.chatsHolder);
        this.channelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        this.channelList = new ChannelsAdapter(chatChannels,rootView.getContext());
        this.channelRecyclerView.setAdapter(channelList);
        this.initialLoadFinished = false;
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance().getReference("chats");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(initialLoadFinished){
                    int length = chatChannels.size();
                    if(length==1){
                        int icon = chatChannels.get(0).getChat_icon();
                        if(icon==NO_CHANNEL_CODE || icon == ADD_CHANNEL_CODE){
                            chatChannels.remove(0);
                        }
                    }else{
                        if(chatChannels.get(length-1).getChat_icon() == ADD_CHANNEL_CODE){
                            chatChannels.remove(length-1);
                        }
                    }
                }
                Chats chat = dataSnapshot.getValue(Chats.class);
                chatChannels.add(chat);
                channelList.notifyDataSetChanged();
                addEmptyMessage();
                if(initialLoadFinished){
                    resetDetector();
                    addExtraChannelInformation(isADMINOrROOT);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int size = chatChannels.size();
                boolean found = false;
                int i = 0;
                while(i<size && !found){
                    if(chatChannels.get(i).getChat_name().equals(dataSnapshot.getKey())){
                        Chats modifiedChat = dataSnapshot.getValue(Chats.class);
                        chatChannels.set(i,modifiedChat);
                        found = true;
                    }else{
                        i++;
                    }
                }
                channelList.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int size = chatChannels.size();
                boolean found = false;
                int i = 0;
                while(i<size && !found){
                    if(chatChannels.get(i).getChat_name().equals(dataSnapshot.getKey())){
                        chatChannels.remove(i);
                        found = true;
                    }else{
                        i++;
                    }
                }
                channelList.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                initialLoadFinished = true;
                addExtraChannelInformation(isADMINOrROOT);
                loadLastMessageStoredForEachChat();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveLastMessageStoredForEachChat();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLastMessageStoredForEachChat();
    }

    public void addEmptyMessage(){
        Messages EMPTY = new Messages("none","none","none",0);
        LAST_MESSAGE_ON_EACH_CHANNEL.add(EMPTY);
    }

    public void saveLastMessageStoredForEachChat(){
        //https://developer.android.com/training/data-storage/shared-preferences.html
        SharedPreferences appInternalData = getActivity().getSharedPreferences(sharedPreferenceFileName,Context.MODE_PRIVATE);
        SharedPreferences.Editor dataEditor = appInternalData.edit();
        int size = LAST_MESSAGE_ON_EACH_CHANNEL.size();
        for (int i=0; i < size; i++) {
            Messages M = LAST_MESSAGE_ON_EACH_CHANNEL.get(i);
            String chatName = chatChannels.get(i).getChat_name();
            if(M != null){
                String lastMessage = M.toString();
                dataEditor.putString(chatName,lastMessage);
            }else{
                dataEditor.putString(chatName,EMPTY_MESSAGE_TEMPLATE);
            }
        }
        dataEditor.apply();
        incomingMessagesDetector.outputMessagesCount();
    }

    public void loadLastMessageStoredForEachChat(){
        LAST_MESSAGE_ON_EACH_CHANNEL.clear();
        SharedPreferences appInternalData = getActivity().getSharedPreferences(sharedPreferenceFileName,Context.MODE_PRIVATE);
        int size;
        if(isADMINOrROOT){
            size = chatChannels.size()-1;
        }else{
            size = chatChannels.size();
        }
        for(int i = 0; i < size ; i++){
            String chatName = chatChannels.get(i).getChat_name();
            String lastMessageAsString = appInternalData.getString(chatName,EMPTY_MESSAGE_TEMPLATE);
            if(lastMessageAsString.equals(EMPTY_MESSAGE_TEMPLATE)){
                addEmptyMessage();
            }else{
                String[] message = lastMessageAsString.split(REGEX_TEMPLATE);
                String username = message[0];
                String messageSent = message[1];
                String date = message[2];
                int type = Integer.parseInt(message[3]);
                Messages M = new Messages(username,messageSent,date,type);
                LAST_MESSAGE_ON_EACH_CHANNEL.add(M);
            }
        }
        resetDetector();
    }

    public void OnRankChanged(boolean isAdmin) {
        /*
        *------ NOTE -----
        * If length equals 1, one of the following cases apply:
        * 1) the "chat channel" is the "add a channel" message
        * 2) the "chat channel" is the "no channels available" message
        * 3) the "chat channel" is actually a chat channel BUT the user is not admin
        *------/NOTE -----
        * */
        int length = chatChannels.size();
        if(isADMINOrROOT && !isAdmin){  //if WAS admin and NOW ISN'T
            if(length == 1){ //if there's only 1 channel
                //The "add new channel" message is replaced with "no channels available"
                chatChannels.set(1,new Chats("noChat",NO_CHANNEL_CODE));
            }else{ //if there are more
                //The "add new channel" message is removed
                chatChannels.remove(length-1);
            }
        }else if(!isADMINOrROOT && isAdmin){ //if WASN'T admin but NOW IS
            if(length == 1){ //if there's only one channel
                //the "no chats available" message is replaced with "add a channel"
                chatChannels.set(1,new Chats("addChat",ADD_CHANNEL_CODE));
            }else{ //if there's more than
                addExtraChannelInformation(isAdmin);
            }
        }
        channelList.notifyDataSetChanged();
        resetDetector();
        isADMINOrROOT = isAdmin;
    }

    public void addExtraChannelInformation(boolean isCurrentlyAdmin){
        if(isCurrentlyAdmin){
            chatChannels.add(new Chats("addChat",ADD_CHANNEL_CODE));
        }else{
            if(chatChannels.size()==0){
                chatChannels.add(new Chats("noChat",NO_CHANNEL_CODE));
            }
        }
    }

    public void resetDetector(){
        if(incomingMessagesDetector != null){
            destroyDetector();
        }
        incomingMessagesDetector = new UnreadMessagesDetector(this,LAST_MESSAGE_ON_EACH_CHANNEL,chatChannels);
    }

    public void destroyDetector(){
        incomingMessagesDetector.removeListeners();
        incomingMessagesDetector = null;
    }

    public UnreadMessagesDetector getDetector(){
        return incomingMessagesDetector;
    }

    public void updateMessageCount(int position, int amount){
        ChannelsAdapter.ChannelHolder channel = (ChannelsAdapter.ChannelHolder)
                channelRecyclerView.findViewHolderForAdapterPosition(position);
        if(channel != null){
            channel.setMessagesCounter(amount);
        }
    }

}
