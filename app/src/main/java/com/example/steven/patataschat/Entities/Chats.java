package com.example.steven.patataschat.Entities;

/**
 * Created by steven on 17/12/2017.
 */

public class Chats {

    private String chat_name;
    private int chat_icon;
    private int min_rank;
    //List<String> membersID;

    public Chats(){

    }

    public Chats(String chat_name, int iconID, int rank){
        this.chat_name = chat_name;
        this.chat_icon = iconID;
        this.min_rank = rank;
    }

    public int getMin_rank() {
        return min_rank;
    }

    public void setMin_rank(int min_rank) {
        this.min_rank = min_rank;
    }

    public void setChat_icon(int chat_icon) {
        this.chat_icon = chat_icon;
    }

    public void setChat_name(String chat_name) {
        this.chat_name = chat_name;
    }

    public int getChat_icon() {
        return chat_icon;
    }

    public String getChat_name() {
        return chat_name;
    }

}
