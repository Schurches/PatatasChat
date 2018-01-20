package com.example.steven.patataschat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.steven.patataschat.Activities.AddChannelActivity;
import com.example.steven.patataschat.Activities.ChatRoomActivity;
import com.example.steven.patataschat.Entities.Chats;
import com.example.steven.patataschat.R;

import java.util.ArrayList;

/**
 * Created by steven on 2/01/2018.
 */

public class ChannelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Chats> chats;
    private Context context;
    private final int ADD_CHANNEL_CODE = 1001;
    private final int NORMAL_CHANNEL_CODE = 1000;
    private final int NO_CHANNEL_CODE = 2000;

    public ChannelsAdapter(ArrayList<Chats> chats, Context appContext){
        this.chats = chats;
        this.context = appContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder channelLayout = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType){
            case NORMAL_CHANNEL_CODE:
                View channelDesign = inflater.inflate(R.layout.channel_item,parent,false);
                channelLayout = new ChannelHolder(channelDesign);
                break;
            case ADD_CHANNEL_CODE:
                View channelDesign2 = inflater.inflate(R.layout.channel_add_item,parent,false);
                channelLayout = new AddChannelHolder(channelDesign2);
                break;
            default:
                View channelDesign3 = inflater.inflate(R.layout.channel_add_item,parent,false);
                channelLayout = new AddChannelHolder(channelDesign3);
                break;
        }
        return channelLayout;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String title = "";
        int id;
        switch(holder.getItemViewType()){
            case NORMAL_CHANNEL_CODE:
                title = chats.get(position).getChat_name();
                id = chats.get(position).getChat_icon();
                ((ChannelHolder) holder).loadChannelInfo(title,"...",id);
                break;
            case ADD_CHANNEL_CODE:
                title = context.getResources().getString(R.string.add_chat);
                ((AddChannelHolder) holder).loadInformation(title);
                break;
            default:
                title = context.getResources().getString(R.string.no_chats);
                ((AddChannelHolder) holder).loadInformation(title);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch(chats.get(position).getChat_icon()){
            case ADD_CHANNEL_CODE:
                return ADD_CHANNEL_CODE;
            case NO_CHANNEL_CODE:
                return NO_CHANNEL_CODE;
            default:
                return NORMAL_CHANNEL_CODE;
        }
    }

    public class ChannelHolder extends RecyclerView.ViewHolder{

        private LinearLayout channelLayout;
        private ImageView channelImage;
        private TextView channelTitle;
        private TextView channelMessageCount;
        private int ICONID;

        public ChannelHolder(View itemView){
            super(itemView);
            this.channelLayout = itemView.findViewById(R.id.channel_layout);
            this.channelImage = itemView.findViewById(R.id.channel_image);
            this.channelTitle = itemView.findViewById(R.id.channel_title);
            this.channelMessageCount = itemView.findViewById(R.id.channel_count);
            this.channelLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openChatRoom_Intent = new Intent(view.getContext(),ChatRoomActivity.class);
                    openChatRoom_Intent.putExtra("chat_name",channelTitle.getText().toString());
                    openChatRoom_Intent.putExtra("chat_icon",ICONID);
                    int unread = Integer.parseInt(channelMessageCount.getText().toString());
                    openChatRoom_Intent.putExtra("unread_count",unread);
                    view.getContext().startActivity(openChatRoom_Intent);
                }
            });
        }

        public void setICONID(int id){
            this.ICONID = id;
        }

        public void loadChannelInfo(String title, String count, int id){
            setICONID(id);
            this.channelTitle.setText(title);
            this.channelMessageCount.setText(count);
            this.channelImage.setImageResource(obtainChatIcon());
        }

        public void setMessagesCounter(int unread) {
            String amount = unread+"";
            this.channelMessageCount.setText(amount);
            if(unread == 0){
                this.channelMessageCount.setTextColor(context.getResources().getColor(R.color.colorMessageSentME));
                this.channelMessageCount.setTextSize(context.getResources().getDimension(R.dimen.chatList_count_size));
            }else{
                this.channelMessageCount.setTextColor(context.getResources().getColor(R.color.color_rank_ADMIN));
                this.channelMessageCount.setTextSize(context.getResources().getDimension(R.dimen.chatList_count_up_size));
            }
        }

        public int obtainChatIcon(){
            switch(this.ICONID){
                case 1:
                    return R.drawable.ic_chat_black_24dp;
                case 2:
                    return R.drawable.ic_account_circle_black_24dp;
                case 3:
                    return R.drawable.ic_iemotions_alert;
                case 4:
                    return R.drawable.ic_iemotions_24dp;
                case 5:
                    return R.drawable.ic_member_black_2_24dp;
                default:
                    return R.drawable.ic_settings_applications_black_24dp;
            }
        }
    }

    public class AddChannelHolder extends RecyclerView.ViewHolder{
        private LinearLayout addLayout;
        private ImageView addImage;
        private TextView addText;

        public AddChannelHolder(View itemView){
            super(itemView);
            this.addLayout = itemView.findViewById(R.id.add_layout);
            this.addImage = itemView.findViewById(R.id.add_image);
            this.addText = itemView.findViewById(R.id.add_message);
        }

        public void loadInformation(String message){
            this.addText.setText(message);
            this.addImage.setImageResource(obtainAddIcon());
            this.addLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addNewChannel_Intent = new Intent(view.getContext(),AddChannelActivity.class);
                    view.getContext().startActivity(addNewChannel_Intent);
                }
            });
        }

        public int obtainAddIcon(){
            return R.drawable.ic_add_circle_black_24dp;
        }

    }




}
