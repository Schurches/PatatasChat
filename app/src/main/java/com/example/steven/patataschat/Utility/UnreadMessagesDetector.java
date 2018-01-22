package com.example.steven.patataschat.Utility;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.example.steven.patataschat.Activities.ChatInterfaceActivity;
import com.example.steven.patataschat.Activities.ChatRoomActivity;
import com.example.steven.patataschat.Activities.LoginActivity;
import com.example.steven.patataschat.Entities.Chats;
import com.example.steven.patataschat.Entities.Messages;
import com.example.steven.patataschat.Fragments.ChatChannelsFragment;
import com.example.steven.patataschat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by steven on 5/01/2018.
 */

public class UnreadMessagesDetector {
    private ChatChannelsFragment chatsListFragment;
    private HashMap<String, Messages> lastMessageSavedMAP;
    private HashMap<String, Integer> chatsPositionInArray;
    private ArrayList<DatabaseReference> chatsDatabaseReference;
    private ArrayList<Integer> unreadCount;
    private ArrayList<Boolean> firstTimeRead;
    private ValueEventListener valueListener;
    private ChildEventListener childListener;
    private Context context;
    private int all_read;
    private  final int NOTIFICATION_ID = 1100;
    private NotificationCompat.Builder builder;
    private NotificationManager manager;

    public UnreadMessagesDetector(ChatChannelsFragment chatsFragment, ArrayList<Messages> messages, ArrayList<Chats> chatsName) {
        this.all_read = 0;
        this.context = chatsFragment.getContext();
        createBuilder();
        HashMap<String, Messages> messagesHashMap = new HashMap<>();
        HashMap<String, Integer> positionHashMap = new HashMap<>();
        this.chatsDatabaseReference = new ArrayList<>();
        this.unreadCount = new ArrayList<>();
        this.firstTimeRead = new ArrayList<>();
        int chatAmounts = messages.size();
        for (int i = 0; i < chatAmounts; i++) {
            String name = chatsName.get(i).getChat_name();
            messagesHashMap.put(name, messages.get(i));
            positionHashMap.put(name, i);
            this.unreadCount.add(0);
            this.firstTimeRead.add(false);
            this.chatsDatabaseReference.add(FirebaseDatabase.getInstance().getReference(name));
        }
        this.lastMessageSavedMAP = messagesHashMap;
        this.chatsPositionInArray = positionHashMap;
        this.chatsListFragment = chatsFragment;
        iniListeners();
        iniChatMessagesListeners();
    }

    public void iniChatMessagesListeners(){
        for (DatabaseReference reference:this.chatsDatabaseReference) {
            //When first set of messages has been loaded
            reference.addListenerForSingleValueEvent(this.valueListener);
            //After loading first set, keep listening for new incomes
            reference.addChildEventListener(this.childListener);
        }
    }

    public void increaseCounter(int chatPosition){
        int lastAmount = this.unreadCount.get(chatPosition);
        this.unreadCount.set(chatPosition,lastAmount+1);
    }

    public void removeListeners(){
        for (DatabaseReference reference: this.chatsDatabaseReference) {
            reference.removeEventListener(this.valueListener);
            reference.removeEventListener(this.childListener);
        }
    }

    public void resetCounter(int chatPosition){
        this.unreadCount.set(chatPosition,0);
    }

    public void iniListeners(){
        iniValueListener();
        iniChildListener();
    }

    public void iniValueListener(){
        this.valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String chatName = dataSnapshot.getKey();
                int position = getChatPosition(chatName);
                firstTimeRead.set(position,true);
                int initialAmount = getMessageCount(chatName);
                chatsListFragment.updateMessageCount(position,initialAmount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public void createBuilder(){
        String channel_ID = "notification_1";
        builder = new NotificationCompat.Builder(context,channel_ID);
        builder.setSmallIcon(R.drawable.notification_icon);
        Intent iniActivity = new Intent(context, LoginActivity.class);
        TaskStackBuilder stack = TaskStackBuilder.create(context);
        stack.addParentStack(LoginActivity.class);
        stack.addNextIntent(iniActivity);
        PendingIntent pending = stack.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pending);
        CharSequence name = context.getString(R.string.app_name);
        builder.setContentTitle(name);
        builder.setAutoCancel(true);
    }

    public void outputMessagesCount(){
        if(manager == null){
            manager = (NotificationManager) chatsListFragment.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        int total = 0;
        for (int count:unreadCount) {
            total+=count;
        }
        String description = String.format(context.getString(R.string.messages_count),total);
        builder.setContentText(description);
        manager.notify(NOTIFICATION_ID,builder.build());
    }

    public void finishNotificationSystem(){
        if(manager==null){
            manager = (NotificationManager) chatsListFragment.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        manager.cancel(NOTIFICATION_ID);
    }

    public void iniChildListener(){
        this.childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String chatName = dataSnapshot.getRef().getParent().getKey();
                String message = getMessageForChat(chatName);
                int position = getChatPosition(chatName);
                Messages newMessage = dataSnapshot.getValue(Messages.class);
                if(!newMessage.toString().equals(message)){
                    //if last message stored is different from new one, increase counter in 1
                    increaseCounter(position);
                }else{
                    //if last message stored is the same as the one found, reset the counter.
                    //From here on it will show the actual unread count
                    resetCounter(position);
                }
                if(firstTimeRead.get(position)){ //if initial set has been loaded
                    //Update the unread amount on the specific channel
                    if(chatsListFragment.isResumed()){
                        chatsListFragment.updateMessageCount(position,getMessageCount(chatName));
                    }else{
                        if(ChatRoomActivity.CURRENT_CHAT_NAME != null){
                            if(ChatRoomActivity.CURRENT_CHAT_NAME.equals(chatName)){
                                resetCounter(position);
                                //chatsListFragment.updateMessageCount(position,0);
                            }else{
                                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                outputMessagesCount();
                            }
                        }else{
                            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                            outputMessagesCount();
                        }
                    }
                }else{ //if the initial set hasn't been loaded
                    //don't update yet because there's still more messages stored.
                }
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
        };
    }

    public int getChatPosition(String chatname) {
        return this.chatsPositionInArray.get(chatname);
    }

    public int getMessageCount(String chatname) {
        return this.unreadCount.get(this.getChatPosition(chatname));
    }

    public String getMessageForChat(String chatname){
        Messages M = lastMessageSavedMAP.get(chatname);
        return M.toString();
    }

}
