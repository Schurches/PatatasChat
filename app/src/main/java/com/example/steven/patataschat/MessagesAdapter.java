package com.example.steven.patataschat;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.steven.patataschat.Entities.Messages;
import com.example.steven.patataschat.Entities.Users;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by steven on 21/12/2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Messages> mensajes;
    private final int TYPE_MESSAGE = 0;
    private final int TYPE_ANNOUNCE = 1;
    private final FirebaseAuth auth_service = FirebaseAuth.getInstance();
    private final FirebaseUser current_user = auth_service.getCurrentUser();
    private Context context;


    public MessagesAdapter(ArrayList<Messages> messages_list, Context app_context){
        this.mensajes = messages_list;
        this.context = app_context;
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder message_body = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType){
            case TYPE_MESSAGE:
                View messageBox = inflater.inflate(R.layout.message_item,null,false);
                message_body = new MessagesHolder(messageBox);
                break;
            case TYPE_ANNOUNCE:
                View announceBox = inflater.inflate(R.layout.announce_item,null,false);
                message_body = new AnnouncesHolder(announceBox);
                break;
            default:
                break;
        }
        return message_body;
    }

    public Users obtainUserInfo(String username){
        ArrayList<Users> users_list = ChatRoomActivity.ALL_USERS;
        for (Users user:users_list) {
            if(user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()){
            case TYPE_MESSAGE:
                MessagesHolder messagesHolder = (MessagesHolder) holder;
                messagesHolder.loadMessage(mensajes.get(position));
                Users user = obtainUserInfo(mensajes.get(position).getUsername());
                if(user != null){
                    if(user.getUser_id().equals(current_user.getUid())){
                        messagesHolder.LayoutMessage.setBackgroundTintList(
                                ColorStateList.valueOf(
                                        ContextCompat.getColor(context,R.color.colorMessageSentME)));
                    }else{
                        messagesHolder.LayoutMessage.setBackgroundTintList(
                                ColorStateList.valueOf(
                                        ContextCompat.getColor(context,R.color.colorMessageSentOTHER)));
                    }
                }
                break;
            case TYPE_ANNOUNCE:
                AnnouncesHolder announcesHolder = (AnnouncesHolder) holder;
                announcesHolder.loadMessage(mensajes.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return this.mensajes.size();
    }

    @Override
    public long getItemId(int position) {
        Messages message = mensajes.get(position);
        return (message.getUsername()+message.getMessage()+message.getDate()).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return this.mensajes.get(position).getType();
    }

    public class AnnouncesHolder extends RecyclerView.ViewHolder{

        private TextView AnnounceMessage;

        public AnnouncesHolder(View itemView) {
            super(itemView);
            this.AnnounceMessage = itemView.findViewById(R.id.announce_message);
        }

        public void loadMessage(Messages message){
            String date = message.getDate();
            int dateSize = date.length();
            date = date.substring(dateSize-8,dateSize-3);
            this.AnnounceMessage.setText(message.getMessage()+" - "+date);
        }

    }

    public class MessagesHolder extends RecyclerView.ViewHolder{

        private LinearLayout LayoutMessage;
        private TextView SentMessage;
        private TextView UserMessage;
        private TextView DateMessage;
        private TextView NickMessage;
        private ImageView ImageMessage;

        public MessagesHolder(View itemView) {
            super(itemView);
            this.SentMessage = itemView.findViewById(R.id.message_sent);
            this.UserMessage = itemView.findViewById(R.id.message_user);
            this.DateMessage = itemView.findViewById(R.id.message_time);
            this.ImageMessage = itemView.findViewById(R.id.message_image);
            this.NickMessage = itemView.findViewById(R.id.message_nickname);
            this.LayoutMessage = itemView.findViewById(R.id.message_layout);
        }

        public void loadMessage(Messages message){
            Users user = obtainUserInfo(message.getUsername());
            if(user.getPP()){
                StorageReference imageReferences = FirebaseStorage.getInstance().getReference();
                imageReferences = imageReferences.child("profilePictures/"+user.getUser_id());
                Glide.with(this.itemView.getContext().getApplicationContext())
                        .using(new FirebaseImageLoader())
                        .load(imageReferences)
                        .into(this.ImageMessage);
            }else{
                this.ImageMessage.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));
            }
            this.UserMessage.setText(message.getUsername());
            switch(user.getRank()){
                case 0:
                    this.UserMessage.setTextColor(ContextCompat.getColor(context,R.color.color_rank_USER));
                    break;
                case 3:
                    this.UserMessage.setTextColor(ContextCompat.getColor(context,R.color.color_rank_MOD));
                    break;
                case 4:
                    this.UserMessage.setTextColor(ContextCompat.getColor(context,R.color.color_rank_ADMIN));
                    break;
                case 5:
                    this.UserMessage.setTextColor(ContextCompat.getColor(context,R.color.color_rank_ROOT));
                        break;
            }
            this.NickMessage.setText(user.getNickname());
            String date = message.getDate();
            int dateSize = date.length();
            date = date.substring(dateSize-8,dateSize-3);
            this.DateMessage.setText(date);
            this.SentMessage.setText(message.getMessage());
        }

    }
}
