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
    private String rank;
    private String PP;
    //Map<String,Object> values;

    public Users(){

    }

    public Users(String userID, String username, String password,String email, String rank, String PP){
        /*
        this.values = new HashMap<>();
        this.values.put("username",username);
        this.values.put("email",email);
        this.values.put("password",password);
        this.values.put("userID",userID);
        this.values.put("rank",rank);
        this.values.put("PP",PP);
        */
        this.user_id = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.rank = rank;
        this.PP = PP;
    }

    /*
    public Users(Map<String,Object> hash){
        this.values = hash;
    }

    public Map<String, Object> getUserInformation() {
        return values;
    }

    public Object getValueFromKey(String key){
        return this.values.get(key);
    }
    */

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPP() {
        return PP;
    }

    public String getRank() {
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

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPP(String PP) {
        this.PP = PP;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
