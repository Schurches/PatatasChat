package com.example.steven.patataschat.Entities;

/**
 * Created by steven on 17/12/2017.
 */

public class Users {

    String username;
    String email;
    String password;
    String userID;
    String rank;

    public Users(String username, String email, String password, String userID, String rank){
        this.username = username;
        this.email = email;
        this.password = password;
        this.userID = userID;
        this.rank = rank;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
