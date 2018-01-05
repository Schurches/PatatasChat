package com.example.steven.patataschat.Entities;

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
        return getUsername()+","+getMessage()+","+getDate()+","+getType();
    }
}
