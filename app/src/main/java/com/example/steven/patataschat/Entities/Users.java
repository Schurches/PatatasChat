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
    private String deviceID;
    private int rank;
    private boolean PP;
    private String nickname;
    private boolean isMuted;
    private boolean isBanned;
    private boolean isActive;
    //Map<String,Object> values;

    public Users(){

    }

    public Users(String userID, String username, String password,String email, int rank, boolean PP,
                 boolean isMuted, boolean isBanned, String deviceID, boolean isActive){
        this.user_id = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.rank = rank;
        this.PP = PP;
        this.nickname = username;
        this.isMuted = isMuted;
        this.isBanned = isBanned;
        this.deviceID = deviceID;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
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

    public void setBanned(boolean banned) {
        this.isBanned = banned;
    }

    public void setMuted(boolean muted) {
        this.isMuted = muted;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRankEquivalent(){
        switch(getRank()){
            case 1:
                return "User";
            case 2:
                return "Trusted";
            case 3:
                return "Mod";
            case 4:
                return "Admin";
            case 5:
                return "Root";
            default:
                return "Unknown";
        }
    }

    @Override
    public String toString() {
        return getUser_id()+"%%%%&&"+getUsername()+"%%%%&&"+getPassword()+"%%%%&&"+getEmail()+
                "%%%%&&"+getRank()+"%%%%&&"+getPP()+"%%%%&&"+isMuted()+"%%%%&&"+
                isBanned()+"%%%%&&"+getDeviceID()+"%%%%&&"+isActive();
    }
}
