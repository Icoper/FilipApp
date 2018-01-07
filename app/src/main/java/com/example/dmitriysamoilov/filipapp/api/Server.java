package com.example.dmitriysamoilov.filipapp.api;

import com.example.dmitriysamoilov.filipapp.model.PhoneContactModelList;
import com.example.dmitriysamoilov.filipapp.model.TokenModel;
import com.example.dmitriysamoilov.filipapp.model.UserContactListModel;
import com.example.dmitriysamoilov.filipapp.model.SendUserData;
import com.example.dmitriysamoilov.filipapp.model.UserModel;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface Server {

    @FormUrlEncoded
    @POST("/api/switch")
    Call<TokenModel> getTokenByID(@Header("Authorization") String token, @Field("id") String id);

    @POST("/oauth/token")
    Call<SendUserData> user(@Body SendUserData user);

    @GET("/api/contacts")
    Call<List<UserContactListModel>> getUserContacts(@Header("Authorization") String token);

    @GET("/api/user")
    Call<UserModel> getUserData(@Header("Authorization") String token);

    @POST("/api/import")
    Call<PhoneContactModelList> sendPhoneBookApi(@Header("Authorization") String token, @Body PhoneContactModelList list);

    @POST("/api/revoke")
    Call<String> logoutAPI(@Header("Authorization") String token);

}
