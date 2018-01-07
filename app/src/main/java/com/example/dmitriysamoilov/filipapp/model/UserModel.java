package com.example.dmitriysamoilov.filipapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmitriysamoilov on 19.12.17.
 */

public class UserModel {
    @SerializedName("full_name")
    String full_name;

    @SerializedName("email")
    String email;

    @SerializedName("avatar")
    String avatar;

    @SerializedName("gender")
    String gender;

    @SerializedName("link")
    String link;

    String token;

    public UserModel(String full_name, String email, String avatar, String gender, String link, String token) {
        this.gender = gender;
        this.full_name = full_name;
        this.email = email;
        this.avatar = avatar;
        this.link = link;
        this.token = token;
    }

    public UserModel() {
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
