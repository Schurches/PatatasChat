package com.example.steven.patataschat.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.example.steven.patataschat.Entities.Users;
import com.example.steven.patataschat.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;


/**
 * Created by steven on 16/12/2017.
 */

public class ProfileFragment extends Fragment {

    public static final String TITLE = "Profile";
    private ArrayList<RadioButton> messages_group;
    private ArrayList<RadioButton> notifications_group;
    private ImageView profile_image;
    private TextView username;
    private TextView nickname;
    private TextView rank;
    private TextView email;
    private ImageButton changeEmailButton;
    private EditText email_field;
    private Users current;
    private Uri selectedPic;
    private String otherMail;
    private Button upload;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.profile_fragment,container,false);
        messages_group = new ArrayList<>();
        notifications_group = new ArrayList<>();
        return currentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profile_image = getView().findViewById(R.id.prof_picture);
        username = getView().findViewById(R.id.profile_username);
        nickname = getView().findViewById(R.id.profile_nick);
        rank = getView().findViewById(R.id.profile_rank);
        email = getView().findViewById(R.id.profile_email);
        email_field = getView().findViewById(R.id.change_email);
        changeEmailButton = getView().findViewById(R.id.change_email_button);
        upload = getView().findViewById(R.id.upload_button);
        messages_group.add((RadioButton)getView().findViewById(R.id.profile_rb_1));
        messages_group.add((RadioButton)getView().findViewById(R.id.profile_rb_2));
        messages_group.add((RadioButton)getView().findViewById(R.id.profile_rb_3));
        notifications_group.add((RadioButton)getView().findViewById(R.id.profile_rb_4));
        notifications_group.add((RadioButton)getView().findViewById(R.id.profile_rb_5));
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePic();
            }
        });
        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMail();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });
        for(int i = 0; i < 5; i++){
            if(i <= 2){
                messages_group.get(i).setOnClickListener(listener());
            }else{
                notifications_group.get(i-3).setOnClickListener(listener());
            }

        }
        createUser(getArguments().getString("user"));
    }

    public View.OnClickListener listener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSelected(view.getId());
            }
        };
    }

    public void checkPosition(ArrayList<RadioButton> array, int position){
        int size = array.size();
        for(int i = 0; i < size; i++){
            if(i==position){
                array.get(i).setChecked(true);
            }else{
                array.get(i).setChecked(false);
            }
        }
    }

    public int getCheckedPosition(ArrayList<RadioButton> array){
        int size = array.size();
        for(int i = 0; i < size; i++){
            if(array.get(i).isChecked()){
                return i;
            }
        }
        return 100;
    }

    public void changeSelected(int id){
        switch(id){
            case R.id.profile_rb_1:
                checkPosition(messages_group,0);
                break;
            case R.id.profile_rb_2:
                checkPosition(messages_group,1);
                break;
            case R.id.profile_rb_3:
                checkPosition(messages_group,2);
                break;
            case R.id.profile_rb_4:
                checkPosition(notifications_group,0);
                break;
            case R.id.profile_rb_5:
                checkPosition(notifications_group,1);
                break;
        }
    }

    public void uploadData(){
        if(selectedPic != null){
            uploadImage(current.getUser_id());
        }else{
            changeUserData();
        }
    }

    public void changeUserData(){
        if(otherMail != null){
            current.setEmail(otherMail);
        }
        switch(getCheckedPosition(messages_group)){
            case 0:
                current.setLoad_messages(20);
                break;
            case 2:
                current.setLoad_messages(100);
                break;
            default:
                current.setLoad_messages(50);
                break;
        }
        switch(getCheckedPosition(notifications_group)){
            case 1:
                current.setRingtone_option(2);
                break;
            default:
                current.setRingtone_option(1);
                break;
        }
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users/"+current.getUser_id());
        userReference.setValue(current);
        loadInformation();
        Toast.makeText(getContext(), R.string.profile_update_success, Toast.LENGTH_SHORT).show();
    }

    public void updateView(){
        if(otherMail != null){
            String regex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,4}";
            if(!otherMail.isEmpty() && !otherMail.trim().isEmpty() && Pattern.matches(regex,otherMail)){
                email.setText(String.format(getString(R.string.profile_email_template),otherMail));
            }else{
                otherMail = null;
                email.setText(String.format(getString(R.string.profile_email_template),current.getEmail()));
            }
        }else{
            email.setText(String.format(getString(R.string.profile_email_template),current.getEmail()));
        }
    }

    public void changeMail(){
        if(email.getVisibility() == View.VISIBLE){
            email.setVisibility(View.GONE);
            email_field.setVisibility(View.VISIBLE);
            changeEmailButton.setImageDrawable(getContext().getDrawable(R.drawable.ic_check_black_24dp));
        }else{
            otherMail = email_field.getText().toString();
            updateView();
            email.setVisibility(View.VISIBLE);
            email_field.setVisibility(View.GONE);
            changeEmailButton.setImageDrawable(getContext().getDrawable(R.drawable.ic_edit_24dp));
        }
    }

    public void uploadImage(String userID){
        StorageReference newProfilePicture = FirebaseStorage.getInstance().getReference("profilePictures/"+userID);
        newProfilePicture.putFile(selectedPic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                current.setPP(true);
                changeUserData();
            }
        });
    }

    public void changePic(){
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
        pickImageIntent.setType("image/+");
        startActivityForResult(pickImageIntent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            selectedPic = data.getData();
            profile_image.setImageURI(selectedPic);
        }
    }

    public void createUser(String user){
        String[] info = user.split("%%%%&&");
        int rank = Integer.parseInt(info[4]);
        boolean PP = Boolean.parseBoolean(info[5]);
        boolean muted = Boolean.parseBoolean(info[6]);
        boolean banned = Boolean.parseBoolean(info[7]);
        boolean active = Boolean.parseBoolean(info[9]);
        int messages = Integer.parseInt(info[10]);
        int ringtone = Integer.parseInt(info[11]);
        current = new Users(info[0],info[1],info[2],info[3],rank,PP,muted,banned,info[8],active,messages,ringtone);
        current.setNickname(info[12]);
        loadInformation();
    }

    public void loadInformation(){
        StorageReference storage = FirebaseStorage.getInstance().getReference("profilePictures/"+current.getUser_id());
        profile_image.setImageDrawable(getContext().getDrawable(R.drawable.ic_account_circle_black_24dp));
        if(current.getPP()){
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(storage)
                    .into(profile_image);
        }
        this.username.setText(String.format(getString(R.string.profile_username_template),current.getUsername()));
        this.nickname.setText(String.format(getString(R.string.profile_nick_template),current.getNickname()));
        this.rank.setText(String.format(getString(R.string.profile_rank_template),current.obtainRankEquivalent()));
        this.email.setText(String.format(getString(R.string.profile_email_template),current.getEmail()));
        switch(current.getLoad_messages()){
            case 20:
                checkPosition(messages_group,0);
                break;
            case 100:
                checkPosition(messages_group,2);
                break;
            default:
                checkPosition(messages_group,1);
                break;
        }
        switch(current.getRingtone_option()){
            case 2:
                checkPosition(notifications_group,1);
                break;
            default:
                checkPosition(notifications_group,0);
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        checkPosition(messages_group,-1);
        checkPosition(notifications_group,-1);
    }
}


