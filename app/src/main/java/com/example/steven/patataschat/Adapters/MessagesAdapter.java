package com.example.steven.patataschat.Adapters;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.steven.patataschat.Activities.ChatRoomActivity;
import com.example.steven.patataschat.Activities.FullImageActivity;
import com.example.steven.patataschat.Entities.Messages;
import com.example.steven.patataschat.Entities.Users;
import com.example.steven.patataschat.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by steven on 21/12/2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Messages> chat_messages;
    private final int TYPE_MESSAGE = 0;
    private final int TYPE_ANNOUNCE = 1;
    private final int TYPE_IMAGE = 2;
    private final FirebaseAuth auth_service = FirebaseAuth.getInstance();
    private final FirebaseUser current_user = auth_service.getCurrentUser();
    private Context context;


    public MessagesAdapter(ArrayList<Messages> messages_list, Context app_context){
        this.chat_messages = messages_list;
        this.context = app_context;
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder message_body = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType){
            case TYPE_MESSAGE:
                View messageBox = inflater.inflate(R.layout.message_item,parent,false);
                message_body = new MessagesHolder(messageBox,TYPE_MESSAGE);
                break;
            case TYPE_ANNOUNCE:
                View announceBox = inflater.inflate(R.layout.announce_item,parent,false);
                message_body = new AnnouncesHolder(announceBox);
                break;
            case TYPE_IMAGE:
                View imageBox = inflater.inflate(R.layout.message_item,parent,false);
                message_body = new MessagesHolder(imageBox,TYPE_IMAGE);
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
            case TYPE_ANNOUNCE:
                AnnouncesHolder announcesHolder = (AnnouncesHolder) holder;
                announcesHolder.loadMessage(chat_messages.get(position));
                break;
            default:
                /*
                * This default targets the cases when its a normal message or an image message.
                * Since for both cases the algorithm does the same.
                * */
                MessagesHolder messagesHolder = (MessagesHolder) holder;
                messagesHolder.loadMessage(chat_messages.get(position));
                Users user = obtainUserInfo(chat_messages.get(position).getUsername());
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
        }
    }

    @Override
    public int getItemCount() {
        return this.chat_messages.size();
    }

    @Override
    public long getItemId(int position) {
        Messages message = chat_messages.get(position);
        return (message.getUsername()+message.getMessage()+message.getDate()).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return this.chat_messages.get(position).getType();
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
            String announce = message.getMessage()+" - "+date;
            this.AnnounceMessage.setText(announce);
        }

    }

    public class MessagesHolder extends RecyclerView.ViewHolder{
        private LinearLayout LayoutMessage;
        private TextView SentMessage;
        private TextView UserMessage;
        private TextView DateMessage;
        private TextView NickMessage;
        private ImageView ImageMessage;
        private ImageView SentImage;
        private final int TYPE;

        public MessagesHolder(View itemView, int Message_TYPE) {
            super(itemView);
            this.SentMessage = itemView.findViewById(R.id.message_sent);
            this.UserMessage = itemView.findViewById(R.id.message_user);
            this.DateMessage = itemView.findViewById(R.id.message_time);
            this.ImageMessage = itemView.findViewById(R.id.message_image);
            this.NickMessage = itemView.findViewById(R.id.message_nickname);
            this.LayoutMessage = itemView.findViewById(R.id.message_layout);
            this.SentImage = itemView.findViewById(R.id.message_image_sent);
            if(Message_TYPE == TYPE_MESSAGE){
                this.SentImage.setVisibility(View.GONE);
            }else{
                this.SentMessage.setVisibility(View.GONE);
                this.SentImage.setVisibility(View.VISIBLE);
                this.LayoutMessage.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            this.TYPE = Message_TYPE;
            LayoutMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("link", SentMessage.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, R.string.message_copied, Toast.LENGTH_SHORT).show();
                }
            });
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
                case 2:
                    this.UserMessage.setTextColor(ContextCompat.getColor(context,R.color.color_text_rank_TRUSTED));
                    break;
                case 3:
                    this.UserMessage.setTextColor(ContextCompat.getColor(context,R.color.color_text_rank_MOD));
                    break;
                case 4:
                    this.UserMessage.setTextColor(ContextCompat.getColor(context,R.color.color_text_rank_ADMIN));
                    break;
                case 5:
                    this.UserMessage.setTextColor(ContextCompat.getColor(context,R.color.color_text_rank_ROOT));
                    break;
                default:
                    this.UserMessage.setTextColor(ContextCompat.getColor(context,R.color.color_text_rank_USER));
                    break;
            }
            this.NickMessage.setText(user.getNickname());
            String date = message.getDate();
            int dateSize = date.length();
            date = date.substring(dateSize-8,dateSize-3);
            this.DateMessage.setText(date);
            loadSentMessageContent(message);
        }

        public void loadSentMessageContent(Messages message){
            if(this.TYPE == TYPE_MESSAGE){
                this.SentMessage.setText(message.getMessage());
            }else{
                StorageReference imageReferences = FirebaseStorage.getInstance().getReference();
                try{
                    /*
                    * -----NOTE----
                    * Images sent to a specific chat will be stored with the following format:
                    * ImagesSent/(chat_name)/(user)-(date)
                    * -----NOTE----
                    * */
                    imageReferences = imageReferences.child(message.getMessage());
                    Glide.with(itemView.getContext().getApplicationContext())
                            .using(new FirebaseImageLoader())
                            .load(imageReferences)
                            .into(this.SentImage);
                    final StorageReference finalImageReferences = imageReferences;
                    SentImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent openImage = new Intent(context, FullImageActivity.class);
                            openImage.putExtra("reference", finalImageReferences.getPath());
                            itemView.getContext().startActivity(openImage);
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                    //Assuming that the image never loaded by any reason
                    this.SentImage.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_broken_image_black_24dp));
                    this.SentImage.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(itemView.getContext(),R.color.color_rank_ADMIN)));
                }
            }
        }

    }
}
