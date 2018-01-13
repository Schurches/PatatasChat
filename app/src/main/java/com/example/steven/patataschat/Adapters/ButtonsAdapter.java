package com.example.steven.patataschat.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.steven.patataschat.Entities.IEmotionButton;
import com.example.steven.patataschat.Entities.Messages;
import com.example.steven.patataschat.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by steven on 12/01/2018.
 */

public class ButtonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int MESSAGE_CODE = 0;
    private ArrayList<IEmotionButton> button_list;
    private String username;
    private String chatname;

    public ButtonsAdapter(ArrayList<IEmotionButton> buttons, String username, String chat){
        this.button_list = buttons;
        this.username = username;
        this.chatname = chat;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder buttonHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View button_design = inflater.inflate(R.layout.button_item,parent,false);
        buttonHolder = new ButtonHolder(button_design);
        return buttonHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ButtonHolder)holder).loadButtoninfo(button_list.get(position));
    }

    @Override
    public int getItemCount() {
        return button_list.size();
    }

    public class ButtonHolder extends RecyclerView.ViewHolder{

        private Button button;

        public ButtonHolder(View itemView){
            super(itemView);
            this.button = itemView.findViewById(R.id.button);
        }

        public void loadButtoninfo(IEmotionButton iEmotionButton){
            this.button.setText(iEmotionButton.getIEmotion_title());
            this.button.setCompoundDrawablesWithIntrinsicBounds(obtainDrawable(iEmotionButton.getIEmotion_icon()),
                    null,null,null);
            this.button.setBackgroundTintList(obtainColor(iEmotionButton.getIEmotion_color()));
            if(iEmotionButton.isIEmotion_text_color_white()){
                this.button.setTextColor(itemView.getResources().getColor(R.color.colorChatRoom_icons));
            }else{
                this.button.setTextColor(itemView.getResources().getColor(R.color.color_IEmotions_black));
            }
            this.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage(username,button.getText().toString(),getDateFormatted(new Date()));
                }
            });
        }

        public void sendMessage(String username, String message, String date){
            DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference(chatname);
            String messageID = chatReference.push().getKey();
            Messages body = new Messages(username,message,date, MESSAGE_CODE);
            chatReference.child(messageID).setValue(body);
        }

        public String getDateFormatted(Date date){
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return dateFormat.format(date);
        }

        public ColorStateList obtainColor(int colorID){
            return ContextCompat.getColorStateList(itemView.getContext(),obtainColorResourceByID(colorID));
        }

        public int obtainColorResourceByID(int colorID){
            switch(colorID){
                case 1:
                    return R.color.color_rank_USER;
                case 2:
                    return R.color.color_rank_TRUSTED;
                case 3:
                    return R.color.color_rank_MOD;
                case 4:
                    return R.color.color_rank_ADMIN;
                case 5:
                    return R.color.color_rank_ROOT;
                case 6:
                    return R.color.colorMessageSentME;
                case 7:
                    return R.color.colorMessageSentOTHER;
                case 8:
                    return R.color.color_IEmotions_teal;
                case 9:
                    return R.color.color_IEmotions_brown;
                case 10:
                    return R.color.color_IEmotions_black;
                default:
                    return R.color.colorChatRoom_icons;
            }
        }

        public Drawable obtainDrawable(int iconID){
            return itemView.getResources().getDrawable(obtainIconByID(iconID));
        }

        public int obtainIconByID(int iconID){
            switch(iconID){
                case 1:
                    return R.drawable.ic_iemotions_24dp;
                case 2:
                    return R.drawable.ic_iemotions_awesome;
                case 3:
                    return R.drawable.ic_iemotions_death;
                case 4:
                    return R.drawable.ic_iemotions_disappointed;
                case 5:
                    return R.drawable.ic_iemotions_run;
                case 6:
                    return R.drawable.ic_iemotions_sad;
                case 7:
                    return R.drawable.ic_iemotions_child;
                case 8:
                    return R.drawable.ic_email_black_24dp;
                case 9:
                    return R.drawable.ic_iemotions_alert;
                default:
                    return R.drawable.ic_chat_black_24dp;
            }
        }

    }

}
