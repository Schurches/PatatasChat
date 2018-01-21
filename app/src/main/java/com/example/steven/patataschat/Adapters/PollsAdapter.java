package com.example.steven.patataschat.Adapters;

import android.content.res.ColorStateList;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.steven.patataschat.Entities.Poll;
import com.example.steven.patataschat.R;

import java.util.ArrayList;

/**
 * Created by steven on 14/01/2018.
 */

public class PollsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Poll> poll_list;
    private int user_ammount;

    public PollsAdapter(ArrayList<Poll> polls, int users){
        this.poll_list = polls;
        this.user_ammount = users;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder pollHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View poll = inflater.inflate(R.layout.poll_item,parent,false);
        pollHolder = new PollHolder(poll);
        return pollHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((PollHolder)holder).loadPollInfo(poll_list.get(position),user_ammount);
    }

    @Override
    public int getItemCount() {
        return poll_list.size();
    }


    public class PollHolder extends RecyclerView.ViewHolder{

        private CardView background;
        private TextView title;
        private TextView voted_count;
        private TextView active;

        public PollHolder(View itemView){
            super(itemView);
            this.title = itemView.findViewById(R.id.poll_question);
            this.voted_count = itemView.findViewById(R.id.poll_voted_list);
            this.active = itemView.findViewById(R.id.poll_active_text);
            this.background = itemView.findViewById(R.id.card_background);
        }

        public void loadPollInfo(Poll poll, int total_users){
            this.title.setText(poll.getTitle());
            int voted_count = 0;
            if(poll.getVoted_users_list() != null){
                voted_count = poll.getVoted_users_list().size();
            }
            String total = voted_count+"/"+total_users;
            this.voted_count.setText(total);
            verifyActive(voted_count,total_users);
        }

        public void checkSelected(boolean isSelected){
            if(isSelected){
                background.setCardBackgroundColor(ColorStateList.valueOf(itemView.getResources().getColor(R.color.colorMessageSentME)));
            }else{
                background.setCardBackgroundColor(ColorStateList.valueOf(itemView.getResources().getColor(R.color.colorMessageSentOTHER)));
            }
        }

        public void verifyActive(int voted, int total){
            if(voted==total){
                this.active.setTextColor(ColorStateList.valueOf(itemView.getResources().getColor(R.color.color_rank_ADMIN)));
                this.active.setText(itemView.getResources().getString(R.string.poll_finish));
            }else{
                this.active.setTextColor(ColorStateList.valueOf(itemView.getResources().getColor(R.color.color_rank_MOD)));
                this.active.setText(itemView.getResources().getString(R.string.poll_active));
            }
        }

    }

}
