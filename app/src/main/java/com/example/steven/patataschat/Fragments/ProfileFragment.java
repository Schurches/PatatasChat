package com.example.steven.patataschat.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.steven.patataschat.Entities.Users;
import com.example.steven.patataschat.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * Created by steven on 16/12/2017.
 */

public class ProfileFragment extends Fragment {

    public static final String TITLE = "Profile";
    private ImageView profile_image;
    private TextView username;
    private TextView nickname;
    private TextView rank;
    private RadioGroup group1;
    private RadioGroup group2;
    private Users current;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.profile_fragment,container,false);
        profile_image = container.findViewById(R.id.profile_picture);
        username = container.findViewById(R.id.profile_username);
        nickname = container.findViewById(R.id.profile_nick);
        rank = container.findViewById(R.id.profile_rank);
        group1 = container.findViewById(R.id.messages_group);
        group2 = container.findViewById(R.id.notification_group);
        create_user(getArguments().getString("user"));
        loadInformation();
        return currentView;
    }

    public void create_user(String user){
        String[] info = user.split("%%%%&&");
        int rank = Integer.parseInt(info[4]);
        boolean PP = Boolean.parseBoolean(info[5]);
        boolean muted = Boolean.parseBoolean(info[6]);
        boolean banned = Boolean.parseBoolean(info[7]);
        boolean active = Boolean.parseBoolean(info[9]);
        current = new Users(info[0],info[1],info[2],info[3],rank,PP,muted,banned,info[8],active);
    }

    public void loadInformation(){
        StorageReference storage = FirebaseStorage.getInstance().getReference("profilePictures/"+current.getUser_id());
        Glide.with(getContext())
                .using(new FirebaseImageLoader())
                .load(storage)
                .into(profile_image);
        this.username.setText( "Username: "+current.getUsername());
        this.nickname.setText("Nickname: "+current.getNickname());
        this.rank.setText("Rank: "+current.getRankEquivalent());
    }

}


