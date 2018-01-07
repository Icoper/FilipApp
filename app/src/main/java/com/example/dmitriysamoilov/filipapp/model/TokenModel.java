package com.example.dmitriysamoilov.filipapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmitriysamoilov on 26.12.17.
 */

public class TokenModel {

    @SerializedName("token")
    String token;

    public TokenModel(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
