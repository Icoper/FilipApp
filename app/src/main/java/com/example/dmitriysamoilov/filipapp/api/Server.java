package com.example.dmitriysamoilov.filipapp.api;

import com.example.dmitriysamoilov.filipapp.model.UserDataModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface Server {

    @POST("oauth/token")
    Call <UserDataModel> user(@Body UserDataModel user);

    @GET("api/user")
    Call <UserDataModel> userTocen(@Query("client_secret") String token);
}
