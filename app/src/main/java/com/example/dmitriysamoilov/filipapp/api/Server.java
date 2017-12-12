package com.example.dmitriysamoilov.filipapp.api;

import com.example.dmitriysamoilov.filipapp.model.UserContactListModel;
import com.example.dmitriysamoilov.filipapp.model.UserDataModel;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface Server {

    @POST("/oauth/token")
    Call <UserDataModel> user(@Body UserDataModel user);

    @GET("/api/contacts")
    Call<List<UserContactListModel>> getUserContacts(@Header("Authorization") String token);

}
