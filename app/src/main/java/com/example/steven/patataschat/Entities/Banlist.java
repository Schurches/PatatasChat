package com.example.steven.patataschat.Entities;

/**
 * Created by steven on 11/01/2018.
 */

public class Banlist {

    private String userID;
    private String deviceID;
    private String whoBanned_name;
    private String description;

    public Banlist(){

    }

    public Banlist(String userID, String deviceID, String whoBanned_name, String description) {
        this.userID = userID;
        this.deviceID = deviceID;
        this.whoBanned_name = whoBanned_name;
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getWhoBanned_name() {
        return whoBanned_name;
    }

    public void setWhoBanned_name(String whoBanned_name) {
        this.whoBanned_name = whoBanned_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
