package com.example.dmitriysamoilov.filipapp.model;

import com.example.dmitriysamoilov.filipapp.keys.UserJsonKeys;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;


public class UserContactListModel implements UserJsonKeys {
    @SerializedName("id")
    public String id;

    @SerializedName("sender_id")
    public String sender_id;

    @SerializedName("sender_type")
    public String sender_type;

    @SerializedName("recipient_id")
    public String recipient_id;

    @SerializedName("recipient_type")
    public String recipient_type;

    @SerializedName("status")
    public String status;

    @SerializedName("created_at")
    public String created_at;

    @SerializedName("updated_at")
    public String updated_at;

    @SerializedName("account")
    public UserAccountModel accountModel;

    public UserContactListModel() {
    }

    public UserContactListModel(JSONObject obj) throws JSONException {
        id = obj.getString(CON_ID);
        sender_id = obj.getString(CON_SENDER_ID);
        sender_type = obj.getString(CON_SENDER_TYPE);
        recipient_id = obj.getString(CON_RECIPIENT_ID);
        recipient_type = obj.getString(CON_RECIPIENT_TYPE);
        status = obj.getString(CON_STATUS);
        created_at = obj.getString(CON_CREATED_AT);
        updated_at = obj.getString(CON_UPDATED_AT);
        accountModel = new UserAccountModel(obj);
    }
}
