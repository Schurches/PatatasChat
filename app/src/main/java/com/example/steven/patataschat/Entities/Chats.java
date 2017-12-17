package com.example.steven.patataschat.Entities;

import java.util.List;

/**
 * Created by steven on 17/12/2017.
 */

public class Chats {

    String chatName;
    int chat_icon;
    //List<String> membersID;

    public Chats(String chatName, int iconID){
        this.chatName = chatName;
        this.chat_icon = iconID;
    }

    public void setChat_icon(int chat_icon) {
        this.chat_icon = chat_icon;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}
