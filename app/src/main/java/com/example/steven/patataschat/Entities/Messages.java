package com.example.steven.patataschat.Entities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by steven on 20/12/2017.
 */

public class Messages {

    String username;
    String message;
    String date;
    int type;

    public Messages(){

    }

    /**
     *
     * @param username user that sent the message
     * @param message the message
     * @param date date that the message was sent
     * @param type message type (2 for image | 1 for announce | 0 for user)
     */
    public Messages(String username, String message, String date, int type){
        this.username = username;
        this.message = message;
        this.date = date;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public int getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getUsername()+"°°!!%%&&"+getMessage()+"°°!!%%&&"+getDate()+"°°!!%%&&"+getType();
    }

    public static void sendMessage(String username, String message, String date, String chat_name, int message_type){
        DatabaseReference chat = FirebaseDatabase.getInstance().getReference(chat_name);
        String announceID = chat.push().getKey();
        Messages announce = new Messages(username,message,date,message_type);
        chat.child(announceID).setValue(announce);
    }

    public static String getDateFormatted(Date date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(date);
    }

}
