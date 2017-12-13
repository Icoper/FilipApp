package com.example.dmitriysamoilov.filipapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.dmitriysamoilov.filipapp.MyWebViewClient;
import com.example.dmitriysamoilov.filipapp.R;
import com.example.dmitriysamoilov.filipapp.ReservedName;
import com.example.dmitriysamoilov.filipapp.database.LocalUserContactsData;
import com.example.dmitriysamoilov.filipapp.model.UserContactListModel;

import java.util.List;


public class WebActivity extends AppCompatActivity {
    private static final String LOG_TAG = "WebActivity";
    private static final String SERVER_URL = "http://taxiservice-gronau.de";

    private WebView mWebView;

    private String enterToken;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isOnline()) {
            Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(WebActivity.this, ChatActivity.class));
        }

        checkUserAuth();

    }

    // Проверяем подключено ли устройство к сети
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void checkUserAuth() {
        if (preferences == null) {
            preferences = getSharedPreferences(ReservedName.USER_TOKEN_NAME, MODE_PRIVATE);
        }

        // Если пользователь уже был авторизован на этом устройстве, входим по токену
        if (preferences.contains(ReservedName.USER_TOKEN_NAME)) {
            enterToken = preferences.getString(ReservedName.USER_TOKEN_NAME, "");
            // Обновляем наши контакты
            LocalUserContactsData lucd = new LocalUserContactsData(getApplicationContext());
            lucd.saveUserFriendData(preferences.getString(ReservedName.USER_TOKEN_NAME, ""));

            List<UserContactListModel> list = lucd.getUserLocalContactsData();

            showWeb();
        } else {
            startActivity(new Intent(WebActivity.this, LoginActivity.class));
        }
    }

    private void showWeb() {
        mWebView = (WebView) findViewById(R.id.ma_webview);
        // включаем поддержку JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        // указываем страницу загрузки
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(SERVER_URL + "/api/enter?" + ReservedName.USER_TOKEN_NAME + "=" + enterToken);

    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
