package com.example.dmitriysamoilov.filipapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmitriysamoilov.filipapp.api.Server;
import com.example.dmitriysamoilov.filipapp.model.UserDataModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String SERVER_URL = "http://taxiservice-gronau.de/";
    private static final String LOG_TAG = "LoginActivity";

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmailValid(mEmailView.getText().toString())
                        && isPasswordValid(mPasswordView.getText().toString())) {
                    singIn();
                }else {
                    Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    private void singIn() {
        UserDataModel userDataModel = new UserDataModel(
                "password",
                "2",
                "nQzpNLAuzc44IaHPdhKeiBqnIsW5dPhKUs2pz296",
                mEmailView.getText().toString(),
                mPasswordView.getText().toString());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        Server service = retrofit.create(Server.class);
        Call<UserDataModel> call = service.user(userDataModel);
        call.enqueue(new Callback<UserDataModel>() {
            @Override
            public void onResponse(Call<UserDataModel> call, Response<UserDataModel> response) {
                if (response.isSuccessful()) {
                    // запрос выполнился успешно, сервер вернул Status 200
                    Log.d(LOG_TAG,"response is successful");
                    // запускаем активити с сайтом
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                } else {
                    // сервер вернул ошибку
                    Log.d(LOG_TAG,response.message());
                }
            }

            @Override
            public void onFailure(Call<UserDataModel> call, Throwable t) {
                // ошибка во время выполнения запроса
                Log.d(LOG_TAG,t.getMessage());

            }
        });
    }
}

