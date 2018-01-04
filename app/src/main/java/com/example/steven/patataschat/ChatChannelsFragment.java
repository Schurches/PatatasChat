package com.example.steven.patataschat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

public class ChatChannelsFragment extends Fragment{

    //private final List<Integer> chatChannelsID = new ArrayList<>();
    //private final int ADD_CHANNEL_ID = 1001;
    //private LinearLayout chatList;
    //private final int ADD_CHANNEL_CODE = 1;
    //private final int ADD_CHANNEL_ICON = 2;
    //private final int OPEN_CHAT_CODE = 3;
    public static final String TITLE = "ChatChannel";
    private final ArrayList<Chats> chatChannels = new ArrayList<>();
    private final int ADD_CHANNEL_CODE = 1001;
    private final int NO_CHANNEL_CODE = 2000;
    private DatabaseReference database;
    private boolean isADMINOrROOT;
    private boolean initialLoadFinished;
    private RecyclerView channelRecyclerView;
    private ChannelsAdapter channelList;
    private String lastMessageKEY = null;
    private boolean wasLastMessageFOUND;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_channels_fragment,container,false);
        this.isADMINOrROOT = getArguments().getBoolean("isAdmin");
        this.channelRecyclerView = rootView.findViewById(R.id.chatsHolder);
        this.channelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        channelList = new ChannelsAdapter(chatChannels,rootView.getContext());
        channelRecyclerView.setAdapter(channelList);
        initialLoadFinished = false;
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //chatList = getActivity().findViewById(R.id.chats_container);
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
                if(initialLoadFinished){
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //chatList.removeAllViews();
        /*
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

        */
        super.onActivityCreated(savedInstanceState);
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

    /*
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
        }else if(requestCode==OPEN_CHAT_CODE){
            Bundle datos = data.getExtras();
            lastMessageKEY= datos.getString("LAST_MESSAGE_KEY");
            String chatLeftNAME = datos.getString("CHAT_LEFT");
            wasLastMessageFOUND = false;
            DatabaseReference incomingMessages = FirebaseDatabase.getInstance().getReference(chatLeftNAME);
            incomingMessages.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(dataSnapshot.getKey().equals(lastMessageKEY)){
                        wasLastMessageFOUND = true;
                    }
                    if(wasLastMessageFOUND){

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
            });
        }

    }
    */
/*
    public LinearLayout createChannelLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout layout = new LinearLayout(getContext());
        int margins = (int) getResources().getDimension(R.dimen.chatList_layout_margins);
        params.setMargins(margins, margins, margins, margins);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        return layout;
    }
*/
    /*

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
    */


    /*

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
                    startActivityForResult(openChatRoom_Intent,OPEN_CHAT_CODE);
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
    */

    /*
    public int findChatByID(int chatID){
        int nChats = chatChannelsID.size();
        for (int i = 0; i < nChats;i++){
            if(chatChannelsID.get(i) == chatID){
                return i;
            }
        }
        return -1;
    }
    */

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

    /*
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
    */



}
