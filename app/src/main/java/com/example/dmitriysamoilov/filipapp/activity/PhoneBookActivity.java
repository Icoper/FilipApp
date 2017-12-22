package com.example.dmitriysamoilov.filipapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmitriysamoilov.filipapp.R;
import com.example.dmitriysamoilov.filipapp.ReservedName;
import com.example.dmitriysamoilov.filipapp.api.Server;
import com.example.dmitriysamoilov.filipapp.database.UserPhoneBookData;
import com.example.dmitriysamoilov.filipapp.model.PhoneContactModel;
import com.example.dmitriysamoilov.filipapp.model.PhoneContactModelList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dmitriysamoilov on 14.12.17.
 */

public class PhoneBookActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private static final String SERVER_URL = "http://taxiservice-gronau.de";
    private static final String LOG_TAG = "PhoneBookActivity";
    private static final int REQUEST_CONTACTS = 101;

    private ArrayList<PhoneContactModel> allContacts;
    private ContactAdapter contactAdapter;

    private ListView listView;
    private Button sendContacts;
    private Button cansel;
    private boolean contactReadPermission = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);


        // initialize view elements

        if (contactReadPermission || EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            allContacts = new UserPhoneBookData(this).getPhoneBook();
            contactAdapter = new ContactAdapter(this, allContacts);
            listView = (ListView) findViewById(R.id.ca_listView);
            listView.setAdapter(contactAdapter);
        } else {
            Toast.makeText(PhoneBookActivity.this, getString(R.string.read_contact), Toast.LENGTH_SHORT).show();
            EasyPermissions.requestPermissions(this, getString(R.string.read_contact), REQUEST_CONTACTS, Manifest.permission.READ_CONTACTS);
        }


        sendContacts = (Button) findViewById(R.id.ca_confirmBtn);
        cansel = (Button) findViewById(R.id.ca_cancel);

        sendContacts.setOnClickListener(this);
        cansel.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.ca_confirmBtn:
                sendUserPhoneBook();
                break;

            case R.id.ca_cancel:
                Intent intent = new Intent(PhoneBookActivity.this, WebActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    private void sendUserPhoneBook() {
        // выбираем только те контакты,которые выбраны для отправки
        ArrayList<PhoneContactModel> listToSend = new ArrayList<>();

        for (PhoneContactModel contact : contactAdapter.getList()) {
            if (contact.getIsChecked()) {
                listToSend.add(contact);
            }
        }

        SharedPreferences preferences = getSharedPreferences(ReservedName.USER_TOKEN_NAME, MODE_PRIVATE);
        final String access_token = preferences.getString(ReservedName.USER_TOKEN_NAME, "");

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


        PhoneContactModelList modelList = new PhoneContactModelList(listToSend);
        Server service = retrofit.create(Server.class);
        Call<PhoneContactModelList> call = service.sendPhoneBookApi("Bearer " + access_token, modelList);
        call.enqueue(new Callback<PhoneContactModelList>() {
            @Override
            public void onResponse(Call<PhoneContactModelList> call, Response<PhoneContactModelList> response) {
                if (response.isSuccessful()) {
                    // запрос выполнился успешно, сервер вернул Status 200
                    Log.d(LOG_TAG, "-sendUserPhoneBook" + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<PhoneContactModelList> call, Throwable t) {
                // ошибка во время выполнения запроса
                Log.d(LOG_TAG, "-sendUserPhoneBook failure - " + t.getMessage());
            }
        });

        Toast.makeText(PhoneBookActivity.this, getString(R.string.action_ok), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PhoneBookActivity.this, WebActivity.class);
        intent.putExtra(ReservedName.USER_TOKEN_NAME, access_token);
        startActivity(intent);

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        contactReadPermission = true;
        recreate();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(LOG_TAG, "Permission has been denied");
        contactReadPermission = false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, PhoneBookActivity.this);
    }

    public class ContactAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lInflater;
        ArrayList<PhoneContactModel> contactListView;

        ContactAdapter(Context context, ArrayList<PhoneContactModel> products) {
            ctx = context;
            contactListView = products;
            lInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // кол-во элементов
        @Override
        public int getCount() {
            return contactListView.size();
        }

        // элемент по позиции
        @Override
        public Object getItem(int position) {
            return contactListView.get(position);
        }

        // id по позиции
        @Override
        public long getItemId(int position) {
            return position;
        }

        // пункт списка
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // используем созданные, но не используемые view
            View view = convertView;
            if (view == null) {
                view = lInflater.inflate(R.layout.contact_item, parent, false);
            }

            PhoneContactModel p = contactListView.get(position);

            ((TextView) view.findViewById(R.id.ci_name)).setText(p.getName());

            final CheckBox cbBuy = (CheckBox) view.findViewById(R.id.ci_checkBox);
            // присваиваем чекбоксу обработчик
            cbBuy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    contactListView.get(position).setChecked(isChecked);
                }
            });

            return view;
        }

        public ArrayList<PhoneContactModel> getList() {
            return contactListView;
        }

    }
}
