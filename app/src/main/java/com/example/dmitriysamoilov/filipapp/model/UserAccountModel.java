package com.example.dmitriysamoilov.filipapp.model;


import com.google.gson.annotations.SerializedName;

public class UserAccountModel {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("lastname")
    public String lastname;

    @SerializedName("email")
    public String email;

    @SerializedName("type")
    public String type;

    @SerializedName("balance")
    public String balance;

    @SerializedName("uid")
    public String uid;

    @SerializedName("birthday")
    public String birthday;

    @SerializedName("activated")
    public String activated;

    @SerializedName("parent")
    public String parent;

    @SerializedName("belongs")
    public String belongs;

    @SerializedName("invite")
    public String invite;

    @SerializedName("notification")
    public String notification;

    @SerializedName("avatar")
    public String avatar;

    @SerializedName("banner")
    public String banner;

    @SerializedName("cc_1")
    public String cc_1;

    @SerializedName("cc_2")
    public String cc_2;

    @SerializedName("cc_3")
    public String cc_3;

    @SerializedName("num_1")
    public String num_1;

    @SerializedName("num_2")
    public String num_2;

    @SerializedName("num_3")
    public String num_3;

    @SerializedName("created_at")
    public String created_at;

    @SerializedName("updated_at")
    public String updated_at;

    @SerializedName("role_id")
    public String role_id;

    @SerializedName("level")
    public String level;

    @SerializedName("gender")
    public String gender;

    @SerializedName("confirmed")
    public String confirmed;

    @SerializedName("time")
    public String time;

    @SerializedName("number")
    public String number;

    @SerializedName("note_type")
    public String note_type;

    @SerializedName("note_till")
    public String note_till;

    @SerializedName("last_activity")
    public String last_activity;

    @SerializedName("note")
    public String note;

    @SerializedName("online")
    public String online;

    @SerializedName("online_last")
    public String online_last;

    @SerializedName("lang")
    public String lang;

    @SerializedName("link")
    public String link;

    @SerializedName("href")
    public String href;

    @SerializedName("available_number")
    public String available_number;

    @SerializedName("full_name")
    public String full_name;

    @SerializedName("phone")
    public String phone;

    @SerializedName("phone_privacy")
    public String phone_privacy;

    @SerializedName("computer_note")
    public String computer_note;

    @SerializedName("is_online")
    public String is_online;

    public UserAccountModel() {
    }
}
