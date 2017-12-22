package com.example.dmitriysamoilov.filipapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dmitriysamoilov.filipapp.R;
import com.example.dmitriysamoilov.filipapp.ReservedName;
import com.example.dmitriysamoilov.filipapp.api.Server;
import com.example.dmitriysamoilov.filipapp.database.BaseDataMaster;
import com.example.dmitriysamoilov.filipapp.model.UserModel;
import com.example.dmitriysamoilov.filipapp.util.DeCryptor;
import com.example.dmitriysamoilov.filipapp.util.EnCryptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

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

public class WebActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = "WebActivity";
    private static final String SERVER_URL = "http://taxiservice-gronau.de";
    private static final String MALE_CODE = "2";
    private static final String FEMALE_CODE = "1";

    private WebView mWebView;
    private ProgressBar progressBar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private LinearLayout linearLayout;
    private ImageView userAvatarIV;
    private TextView userNameTV;
    private TextView userEmailTV;
    private ImageButton closeDrawerPanel;

    private String enterToken = "";
    private SharedPreferences preferences;
    private EnCryptor encryptor;
    private DeCryptor decryptor;
    private UserModel loginUser;
    private BaseDataMaster dataMaster;

    // To get User picture
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        // Initi all view's elements;
        if (!isOnline()) {
            Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(WebActivity.this, ChatActivity.class));
        }
        dataMaster = BaseDataMaster.getDataMaster(getApplicationContext());

        checkUserAuth();
        if (!enterToken.isEmpty()) {
            showWeb();
        }
        encryptor = new EnCryptor();
        try {
            decryptor = new DeCryptor();
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
                IOException e) {
            e.printStackTrace();
        }


    }

    private void initializeView() {

        progressBar = (ProgressBar) findViewById(R.id.web_progress);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);


        linearLayout = (LinearLayout) findViewById(R.id.web_preloader);
        linearLayout.setVisibility(View.VISIBLE);

        userAvatarIV = (ImageView) hView.findViewById(R.id.nav_header_pic);
        userNameTV = (TextView) hView.findViewById(R.id.nav_header_name);
        userEmailTV = (TextView) hView.findViewById(R.id.nav_header_email);
        closeDrawerPanel = (ImageButton) findViewById(R.id.nv_close_panel);
        closeDrawerPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        loginUser = dataMaster.getUserFromDB();
        String avatar_url = "";
        if (loginUser != null) {
            try {
                if (loginUser.getAvatar() != null) {
                    avatar_url = loginUser.getAvatar();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            // Setup navigation drawer header
            if (avatar_url.isEmpty()) {
                if (loginUser.getGender().equals(MALE_CODE)) {
                    userAvatarIV.setBackgroundResource(R.drawable.di_avatar_male);
                } else userAvatarIV.setBackgroundResource(R.drawable.di_avatar_female);
            } else Glide.with(WebActivity.this).load(loginUser.getAvatar()).into(userAvatarIV);
            userEmailTV.setText(loginUser.getEmail());
            userNameTV.setText(loginUser.getFull_name());
        }


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

            // Обновляем наши контакты TODO
//            LocalUserContactsData lucd = new LocalUserContactsData(getApplicationContext());
//            lucd.saveUserFriendData(preferences.getString(ReservedName.USER_TOKEN_NAME, ""));
            getUserData();

        } else {
            startActivity(new Intent(WebActivity.this, LoginActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null || intent.getData() == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    private void showWeb() {
        mWebView = (WebView) findViewById(R.id.ma_webview);
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(WebActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(0);
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mWebView.setWebViewClient(new MyCallback());


        mWebView.loadUrl(SERVER_URL + "/api/enter?" + ReservedName.USER_TOKEN_NAME + "=" + enterToken);

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            //For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                WebActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
            }

            // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                WebActivity.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FCR);
            }

            //For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                WebActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), WebActivity.FCR);
            }

            //For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }
                mUMA = filePathCallback;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(WebActivity.this.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCM);
                    } catch (IOException ex) {
                        Log.e(LOG_TAG, "Image file creation failed", ex);
                    }
                    if (photoFile != null) {
                        mCM = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, FCR);
                return true;
            }
        });

    }

    public class MyCallback extends WebViewClient {
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
    }

    // Create an image file
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private String decryptText(String key) {
        try {
            return (decryptor.decryptData(key, encryptor.getEncryption(), encryptor.getIv()));
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchPaddingException | NoSuchProviderException |
                IOException | InvalidKeyException e) {
            Log.e(LOG_TAG, "decryptData() called with: " + e.getMessage(), e);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_import_contact) {
            startActivity(new Intent(WebActivity.this, PhoneBookActivity.class));
            // Handle the camera action
        } else if (id == R.id.nav_logout) {
            Toast.makeText(WebActivity.this, getString(R.string.action_ok), Toast.LENGTH_SHORT).show();
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getUserData() {
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create(gson)) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .client(httpClient.build())
                .build();


        Server service = retrofit.create(Server.class);
        Call<UserModel> call = service.getUserData("Bearer " + enterToken);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    // запрос выполнился успешно, сервер вернул Status 200

                    loginUser = new UserModel(response.body().getFull_name(),
                            response.body().getEmail(), response.body().getAvatar(),
                            response.body().getGender());
                    dataMaster.insertDataOnDB(loginUser);

                    initializeView();

                    Log.d(LOG_TAG, "getUserData " + String.valueOf(response.code()));

                } else {
                    // сервер вернул ошибку
                    Log.d(LOG_TAG, "getUserData " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                // ошибка во время выполнения запроса
                Log.d(LOG_TAG, "getUserData fail " + t.getMessage());
            }
        });

    }

    private void logoutUser() {
        ArrayList<String> tokens = new ArrayList<>();
        if (preferences == null) {
            preferences = getSharedPreferences(ReservedName.USER_TOKEN_NAME, MODE_PRIVATE);
        }

        Map<String, ?> allPreferences = preferences.getAll();
        for (Object s : allPreferences.values()) {
            tokens.add(s.toString());
        }

        ArrayList<String> decryptToken = new ArrayList<>();
        // TODO : need to decrypt token first
        decryptToken = tokens;


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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create(gson)) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .client(httpClient.build())
                .build();

        for (String token : decryptToken) {

            Server service = retrofit.create(Server.class);
            Call<String> call = service.logoutAPI(token);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        // запрос выполнился успешно, сервер вернул Status 200

                        Log.d(LOG_TAG, "logout" + String.valueOf(response.code()));

                    } else {
                        // сервер вернул ошибку
                        Log.d(LOG_TAG, "logout" + String.valueOf(response.code()));
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // ошибка во время выполнения запроса
                    Log.d(LOG_TAG, "logout" + t.getMessage());
                }
            });
        }

        File file = new File("/data/data/" + ReservedName.APP_PACKAGE_NAME + "/shared_prefs/token.xml");
        file.delete();
        WebStorage.getInstance().deleteAllData();
        Intent i = new Intent(WebActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public class MyWebViewClient extends WebViewClient {
        private static final String URL = "www.taxiservice-gronau.de";


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(LOG_TAG, "shouldOverrideUrlLoading: " + url);
            if (url.contains("tel:")) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                return true;
            } else if (url.startsWith("whatsapp:")
                    || url.startsWith("viber:")
                    || url.startsWith("telegram:")) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                } catch (android.content.ActivityNotFoundException e) {
                    Log.e(LOG_TAG, "Error with " + url + ": " + e.toString());
                    Toast.makeText(getApplicationContext(), getString(R.string.app_not_install), Toast.LENGTH_SHORT).show();
                } catch (Exception q) {
                    q.printStackTrace();
                }
                return true;
            } else if (url.startsWith("https://t.me/share/url?url=http://taxiservice-gronau.de")) {
                Toast.makeText(getApplicationContext(), getString(R.string.app_not_install), Toast.LENGTH_SHORT).show();
                return true;
            }
            if (Uri.parse(url).getHost().equals(URL)) {
                // This is my web site, so do not override; let my WebView load the page
                return super.shouldOverrideUrlLoading(view, url);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (view.getProgress() == 100) {
                linearLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                mWebView.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(LOG_TAG, "onPageStarted");

        }

    }


}
