package com.cabel.ducatusapp;

public class ImageClass {
    private String imageurl;
    private String userID;

    public ImageClass() {

    }

    public ImageClass(String imageurl, String userID) {
        this.imageurl = imageurl;
        this.userID = userID;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
