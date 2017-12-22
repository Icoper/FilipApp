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

    public UserModel(String full_name, String email, String avatar, String gender) {
        this.gender = gender;
        this.full_name = full_name;
        this.email = email;
        this.avatar = avatar;
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
}
