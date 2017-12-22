package com.example.dmitriysamoilov.filipapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmitriysamoilov.filipapp.R;
import com.example.dmitriysamoilov.filipapp.ReservedName;
import com.example.dmitriysamoilov.filipapp.api.Server;
import com.example.dmitriysamoilov.filipapp.database.BaseDataMaster;
import com.example.dmitriysamoilov.filipapp.model.SendUserData;
import com.example.dmitriysamoilov.filipapp.model.UserModel;
import com.example.dmitriysamoilov.filipapp.util.EnCryptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {
    private static final String SERVER_URL = "http://taxiservice-gronau.de";
    private static final String LOG_TAG = "LoginActivity";
    private static final String SAMPLE_ALIAS = "MYALIAS";

    private String token;
    private SharedPreferences preferences;
    private Retrofit retrofit;


    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private ProgressBar progressBar;
    private Button mEmailSignInButton;
    private Button mRegisterButton;
    private TextView mForgotPassTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging); // <-- this is the important line!


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create(gson)) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .client(httpClient.build())
                .build();

        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        mForgotPassTV = (TextView) findViewById(R.id.forgot_pass_tv);
        mForgotPassTV.setOnClickListener(this);
        mRegisterButton = (Button) findViewById(R.id.registration_button);
        mRegisterButton.setOnClickListener(this);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(this);

    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void showAlertDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.sendcontact_alertdialog, null);
        AlertDialog.Builder builder = new AlertDialog
                .Builder(new ContextThemeWrapper(LoginActivity.this, R.style.Theme_AppCompat_Dialog));
        builder.setView(view);
        builder.setCancelable(false)
                .setNegativeButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(LoginActivity.this, PhoneBookActivity.class));

                    }
                }).setNeutralButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                startWebActivity();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("");
        alertDialog.show();
    }

    // Выполняем вход
    private void singIn() {
        progressBar.setVisibility(View.VISIBLE);

        final SendUserData sendUserData = new SendUserData(
                ReservedName.CLIENT_GRANT_TYPE,
                ReservedName.CLIENT_ID,
                ReservedName.CLIENT_SECRET,
                mEmailView.getText().toString(),
                mPasswordView.getText().toString());


        Server service = retrofit.create(Server.class);
        Call<SendUserData> call = service.user(sendUserData);
        call.enqueue(new Callback<SendUserData>() {
            @Override
            public void onResponse(Call<SendUserData> call, Response<SendUserData> response) {
                if (response.isSuccessful()) {
                    // запрос выполнился успешно, сервер вернул Status 200
                    Log.d(LOG_TAG + "singIn", String.valueOf(response.code()));

                    // Сохраняем токен пользователя
                    saveUserToken(response.body().getAccess_token());
                    token = response.body().getAccess_token();
                    progressBar.setVisibility(View.INVISIBLE);

//                    getUserData(token);

                    startWebActivity();

                } else {
                    // сервер вернул ошибку
                    Log.d(LOG_TAG + "singIn", String.valueOf(response.code()));
                    Toast.makeText(getApplicationContext(), getString(R.string.wrong_data),
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    setEnableView(true);
                }
            }

            @Override
            public void onFailure(Call<SendUserData> call, Throwable t) {
                // ошибка во время выполнения запроса
                Log.d(LOG_TAG + "singIn", t.getMessage());
                Toast.makeText(getApplicationContext(), getString(R.string.unknown_error),
                        Toast.LENGTH_SHORT).show();
                setEnableView(true);
            }
        });


    }

    private void getUserData(String token) {


        Server service = retrofit.create(Server.class);
        Call<UserModel> call = service.getUserData("Bearer " + token);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    // запрос выполнился успешно, сервер вернул Status 200
                    Log.d(LOG_TAG, "getUserData" + String.valueOf(response.code()));

                } else {
                    // сервер вернул ошибку
                    Log.d(LOG_TAG, "getUserData" + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                // ошибка во время выполнения запроса
                Log.d(LOG_TAG, "getUserData" + t.getMessage());
            }
        });


    }

    private void startWebActivity() {
        if (token.isEmpty()) {
            token = " ";
        }
        Intent intent = new Intent(LoginActivity.this, WebActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ReservedName.USER_TOKEN_NAME, token);
        startActivity(intent);
    }

    private String encryptText(String s) {

        EnCryptor encryptor = new EnCryptor();

        try {
            final byte[] encryptedText = encryptor
                    .encryptText(SAMPLE_ALIAS, s);
            return (Base64.encodeToString(encryptedText, Base64.DEFAULT));
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException e) {
            Log.e(LOG_TAG, "onClick() called with: " + e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException | SignatureException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void saveUserToken(String token) {
        preferences = getSharedPreferences(ReservedName.USER_TOKEN_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

//        String key = encryptText(token);

        editor.putString(ReservedName.USER_TOKEN_NAME, token);
        editor.commit();

        Log.d(LOG_TAG, "saveNewToken()");
    }

    private void setEnableView(boolean status) {
        mEmailView.setEnabled(status);
        mPasswordView.setEnabled(status);
        mEmailSignInButton.setClickable(status);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.email_sign_in_button:
                if (isEmailValid(mEmailView.getText().toString())
                        && isPasswordValid(mPasswordView.getText().toString())) {
                    singIn();
                    setEnableView(false);
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.forgot_pass_tv:
                Toast.makeText(getApplicationContext(), "In develop", Toast.LENGTH_SHORT).show();
                break;
            case R.id.registration_button:
                Toast.makeText(getApplicationContext(), "In develop", Toast.LENGTH_SHORT).show();
                break;

        }

    }
}

