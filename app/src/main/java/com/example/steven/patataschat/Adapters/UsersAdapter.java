package com.example.steven.patataschat.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.steven.patataschat.Entities.Users;
import com.example.steven.patataschat.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;


/**
 * Created by steven on 8/01/2018.
 */

public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Users> users;
    private Context context;

    public UsersAdapter(ArrayList<Users> usersList, Context context){
        this.users = usersList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View user_template = layoutInflater.inflate(R.layout.user_item,parent,false);
        RecyclerView.ViewHolder user_layout = new UsersHolder(user_template);
        return user_layout;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UsersHolder usersHolder = (UsersHolder) holder;
        usersHolder.loadInformation(this.users.get(position));
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }


    public class UsersHolder extends RecyclerView.ViewHolder{

        private LinearLayout background;
        private ImageView profile_pic;
        private TextView username;
        private TextView nickname;
        private final int ACCESS_MUTE = 1;

        public UsersHolder(View itemView){
            super(itemView);
            this.profile_pic = itemView.findViewById(R.id.profile_pic_image);
            this.username = itemView.findViewById(R.id.username_text);
            this.nickname = itemView.findViewById(R.id.nickname_text);
            this.background = itemView.findViewById(R.id.background_lay);
        }

        public void setBubbleColor(boolean isBanned, boolean isSelected){
            if(isSelected){
                if(isBanned){
                    this.background.setBackgroundTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(),R.color.color_text_rank_ADMIN))
                    );
                }else{
                    this.background.setBackgroundTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(),R.color.colorMessageSentME))
                    );
                }
            }else{
                if(isBanned){
                    this.background.setBackgroundTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(),R.color.color_rank_ADMIN))
                    );
                }else{
                    this.background.setBackgroundTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(),R.color.colorMessageSentOTHER))
                    );
                }
            }
        }

        public void setColorOnStatus(int resID,int access_mode){
            ColorStateList color = ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(),resID));
            if(access_mode== ACCESS_MUTE){
                this.username.setTextColor(color);
                this.nickname.setTextColor(color);
            }else{
                this.username.setTextColor(color);
                this.nickname.setTextColor(color);
            }
        }

        public void loadInformation(Users user){
            if(user.isMuted()){
                setColorOnStatus(R.color.color_text_rank_MOD,ACCESS_MUTE);
            }else{
                setColorOnStatus(R.color.colorChatRoom_icons,0);
            }
            if(user.isBanned()){
                setBubbleColor(true,false);
            }else{
                setBubbleColor(false,false);
            }
            this.username.setText(user.getUsername());
            this.nickname.setText(user.getNickname());
            if(user.getPP()){
                StorageReference storage = FirebaseStorage.getInstance().getReference("profilePictures/"+user.getUser_id());
                Glide.with(this.itemView.getContext().getApplicationContext())
                        .using(new FirebaseImageLoader())
                        .load(storage)
                        .into(this.profile_pic);
            }else{
                this.profile_pic.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));
            }
        }

    }

}
