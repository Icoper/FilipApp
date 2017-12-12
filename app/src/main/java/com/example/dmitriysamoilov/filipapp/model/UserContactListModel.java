package com.example.dmitriysamoilov.filipapp.model;

import com.google.gson.annotations.SerializedName;


public class UserContactListModel {
    @SerializedName("id")
    String id;

    @SerializedName("sender_id")
    String sender_id;

    @SerializedName("sender_type")
    String sender_type;

    @SerializedName("recipient_id")
    String recipient_id;

    @SerializedName("recipient_type")
    String recipient_type;

    @SerializedName("status")
    String status;

    @SerializedName("created_at")
    String created_at;

    @SerializedName("updated_at")
    String updated_at;

    @SerializedName("account")
    UserAccountModel accountModel;


    public UserContactListModel() {
    }

}
