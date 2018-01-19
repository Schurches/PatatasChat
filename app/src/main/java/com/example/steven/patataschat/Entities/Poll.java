package com.example.steven.patataschat.Entities;

import java.util.ArrayList;

/**
 * Created by steven on 14/01/2018.
 */

public class Poll {

    private String pollID;
    private String title;
    private boolean isActive;
    private ArrayList<String> options;
    private ArrayList<Integer> option_counter;
    private ArrayList<String> voted_users_list;

    public Poll(){

    }

    public Poll(String pollID, String title, boolean isActive, ArrayList<String> options, ArrayList<Integer> option_counter, ArrayList<String> voted_users_list) {
        this.pollID = pollID;
        this.title = title;
        this.isActive = isActive;
        this.options = options;
        this.option_counter = option_counter;
        this.voted_users_list = voted_users_list;
    }

    public static ArrayList<Integer> create_options_count(int size, int selected){
        ArrayList<Integer> count = new ArrayList<>();
        for (int i = 0; i < size; i++){
            if(i==selected){
                count.add(1);
            }else{
                count.add(0);
            }
        }
        return count;
    }

    public String getPollID() {
        return pollID;
    }

    public void setPollID(String pollID) {
        this.pollID = pollID;
    }

    public ArrayList<Integer> getOption_counter() {
        return option_counter;
    }

    public void setOption_counter(ArrayList<Integer> option_counter) {
        this.option_counter = option_counter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public ArrayList<String> getVoted_users_list() {
        return voted_users_list;
    }

    public void setVoted_users_list(ArrayList<String> voted_users_list) {
        this.voted_users_list = voted_users_list;
    }

    public String getOptionTitle(int position){
        return this.options.get(position);
    }

    public void increment_vote(int position){
        this.option_counter.set(position,getOptionVotedCount(position)+1);
    }

    public int getOptionVotedCount(int position){
        return this.option_counter.get(position);
    }

    public void addUserID(String user){
        this.voted_users_list.add(user);
    }

    public boolean hasUserVoted(String userID){
        for(String user:voted_users_list){
            if(user.equals(userID)){
                return true;
            }
        }
        return false;
    }

}
