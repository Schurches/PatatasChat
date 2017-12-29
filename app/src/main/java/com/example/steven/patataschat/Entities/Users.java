package com.example.steven.patataschat.Entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steven on 17/12/2017.
 */

public class Users {

    private String user_id;
    private String username;
    private String password;
    private String email;
    private int rank;
    private boolean PP;
    private String nickname;
    //Map<String,Object> values;

    public Users(){

    }

    public Users(String userID, String username, String password,String email, int rank, boolean PP){
        this.user_id = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.rank = rank;
        this.PP = PP;
        this.nickname = username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean getPP() {
        return PP;
    }

    public int getRank() {
        return rank;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPP(boolean PP) {
        this.PP = PP;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
