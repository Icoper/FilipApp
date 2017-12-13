package com.example.dmitriysamoilov.filipapp.model;


import com.example.dmitriysamoilov.filipapp.keys.UserJsonKeys;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class UserAccountModel implements UserJsonKeys {

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

    public UserAccountModel(JSONObject obj) throws JSONException {
        id = obj.getString(ACC_ID);
        name = obj.getString(ACC_NAME);
        lastname = obj.getString(ACC_LAST_NAME);
        email = obj.getString(ACC_EMAIL);
        type = obj.getString(ACC_TYPE);
        balance = ""; // No 'balance' and 'uid' in json
        birthday = obj.getString(ACC_BIRTHDAY);
        activated = obj.getString(ACC_ACTIVATED);
        parent = "";
        invite = "";
        notification = "";
        banner = "";
        belongs = "";
        avatar = obj.getString(ACC_AVATAR);
        cc_1 = obj.getString(ACC_CC_1);
        cc_2 = obj.getString(ACC_CC_2);
        num_1 = obj.getString(ACC_NUM_1);
        num_2 = obj.getString(ACC_NUM_2);
        created_at = obj.getString(ACC_CREATED_AT);
        updated_at = obj.getString(ACC_UPDATED_AT);
        role_id = obj.getString(ACC_ROLE_ID);
        level = "";
        gender = obj.getString(ACC_GENDER);
        confirmed = obj.getString(ACC_CONFIRMED);
        time = "";
        number = "";
        note_type = "";
        note_till = "";
        note = "";
        online = obj.getString(ACC_ONLINE);
        online_last = obj.getString(ACC_ONLINE_LAST);
        lang = "";
        last_activity = obj.getString(ACC_LAST_ACT);
        link = obj.getString(ACC_LINK);
        href = obj.getString(ACC_HREF);
        available_number = obj.getString(ACC_AVAILABLE_NUM);
        full_name = obj.getString(ACC_FULL_NAME);
        phone = obj.getString(ACC_PHONE);
        phone_privacy = obj.getString(ACC_PHONE_PRIVACY);
        computer_note = "";
        is_online = obj.getString(ACC_IS_ONLINE);
    }
}
