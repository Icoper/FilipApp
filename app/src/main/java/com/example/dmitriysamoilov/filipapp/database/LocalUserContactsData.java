package com.example.dmitriysamoilov.filipapp.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.dmitriysamoilov.filipapp.ReservedName;
import com.example.dmitriysamoilov.filipapp.api.Server;
import com.example.dmitriysamoilov.filipapp.model.UserContactListModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LocalUserContactsData extends ReservedName {
    private static final String LOG_TAG = "LocUsContData";
    private SharedPreferences sharedPreferences;
    private Context context;
    private String spName;
    private Retrofit retrofit;


    public LocalUserContactsData(Context context) {
        this.context = context;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create(gson)) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

    }

    public List<UserContactListModel> getUserLocalContactsData() {
        List<UserContactListModel> listModels = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(ReservedName.USER_LOCAL_CONTACTS_DATA,
                context.MODE_PRIVATE);

        String res = sharedPreferences.getString("json", "");

        try {
            JSONArray arr = new JSONArray(res);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                UserContactListModel model = new UserContactListModel(obj);
                listModels.add(model);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON: ", e);
        }
        return listModels;
    }

    public void saveUserFriendData(final String token) {

        Server service = retrofit.create(Server.class);
        Call<List<UserContactListModel>> call = service.getUserContacts("Bearer " + token);
        call.enqueue(new Callback<List<UserContactListModel>>() {
            @Override
            public void onResponse(Call<List<UserContactListModel>> call, Response<List<UserContactListModel>> response) {
                if (response.isSuccessful()) {
                    // запрос выполнился успешно, сервер вернул Status 200
                    Log.d(LOG_TAG + "saveUser", String.valueOf(response.code()));
                    Log.d(LOG_TAG, "array - " + response.body().size());

                    // Сохраняем список контактов локально
                    String data = new Gson().toJson(response.body());
                    SharedPreferences.Editor prefEditor = context.getSharedPreferences(
                            ReservedName.USER_LOCAL_CONTACTS_DATA, Context.MODE_PRIVATE).edit();
                    prefEditor.putString("json", data);
                    prefEditor.commit();


                } else {
                    // сервер вернул ошибку
                    Log.d(LOG_TAG + "saveUser", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<UserContactListModel>> call, Throwable t) {
                // ошибка во время выполнения запроса
                Log.d(LOG_TAG + "saveUser", t.getMessage());
            }
        });
    }
}
