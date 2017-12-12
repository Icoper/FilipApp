package com.example.dmitriysamoilov.filipapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dmitriysamoilov.filipapp.R;
import com.example.dmitriysamoilov.filipapp.ReservedName;
import com.example.dmitriysamoilov.filipapp.api.Server;
import com.example.dmitriysamoilov.filipapp.database.LocalUserContactsData;
import com.example.dmitriysamoilov.filipapp.model.UserDataModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String SERVER_URL = "http://taxiservice-gronau.de";
    private static final String LOG_TAG = "LoginActivity";

    private SharedPreferences preferences;
    private Retrofit retrofit;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create(gson)) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmailValid(mEmailView.getText().toString())
                        && isPasswordValid(mPasswordView.getText().toString())) {
                    singIn();
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    // Выполняем вход
    private void singIn() {
        progressBar.setVisibility(View.VISIBLE);

        final UserDataModel userDataModel = new UserDataModel(
                ReservedName.CLIENT_GRANT_TYPE,
                ReservedName.CLIENT_ID,
                ReservedName.CLIENT_SECRET,
                mEmailView.getText().toString(),
                mPasswordView.getText().toString());


        Server service = retrofit.create(Server.class);
        Call<UserDataModel> call = service.user(userDataModel);
        call.enqueue(new Callback<UserDataModel>() {
            @Override
            public void onResponse(Call<UserDataModel> call, Response<UserDataModel> response) {
                if (response.isSuccessful()) {
                    // запрос выполнился успешно, сервер вернул Status 200
                    Log.d(LOG_TAG + "singIn", String.valueOf(response.code()));

                    // Сохраняем токен пользователя
                    saveUserToken(response.body().getAccess_token());

                    // Сохраняем контакты пользователя
                    LocalUserContactsData localUserContactsData = new LocalUserContactsData(getApplicationContext());
                    localUserContactsData.saveUserFriendData(response.body().getAccess_token());

                    // запускаем активити с сайтом
                    Intent uIntent = new Intent(LoginActivity.this, WebActivity.class);
                    startActivity(uIntent);

                } else {
                    // сервер вернул ошибку
                    Log.d(LOG_TAG + "singIn", String.valueOf(response.code()));
                    Toast.makeText(getApplicationContext(), "Incorrect email or pass",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<UserDataModel> call, Throwable t) {
                // ошибка во время выполнения запроса
                Log.d(LOG_TAG + "singIn", t.getMessage());
            }
        });


    }

    public void saveUserToken(String token) {
        preferences = getSharedPreferences(ReservedName.USER_TOKEN_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ReservedName.USER_TOKEN_NAME, token);
        editor.commit();

        Log.d(LOG_TAG, "saveNewToken()");
    }


}

