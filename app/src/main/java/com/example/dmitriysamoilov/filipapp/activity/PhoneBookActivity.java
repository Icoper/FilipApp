package com.example.dmitriysamoilov.filipapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ProgressBar;
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
    private static final String LOG_TAG = "PhoneBookActivity";
    private static final int REQUEST_CONTACTS = 101;

    private ArrayList<PhoneContactModel> allContacts;
    private ContactAdapter contactAdapter;

    private ListView listView;
    private Button sendContacts;
    private Button cancel;
    private CheckBox selectorCB;
    private ProgressBar progressBar;
    private boolean contactReadPermission = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // initialize view elements
        selectorCB = (CheckBox) findViewById(R.id.ca_checkBox);
        selectorCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                progressBar.setVisibility(View.VISIBLE);
                if (b) {
                    for (int i = 0; i < allContacts.size(); i++) {
                        allContacts.get(i).setChecked(true);
                    }

                } else {
                    for (int i = 0; i < allContacts.size(); i++) {
                        allContacts.get(i).setChecked(false);
                    }
                }
                contactAdapter = new ContactAdapter(PhoneBookActivity.this, allContacts);
                listView.setAdapter(contactAdapter);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        sendContacts = (Button) findViewById(R.id.ca_confirmBtn);
        cancel = (Button) findViewById(R.id.ca_cancel);
        progressBar = (ProgressBar) findViewById(R.id.ca_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        sendContacts.setOnClickListener(this);
        cancel.setOnClickListener(this);

        setupListView();

    }

    private void setupListView() {

        if (contactReadPermission || EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            progressBar.setVisibility(View.VISIBLE);
            listView = (ListView) findViewById(R.id.ca_listView);

            LoadContactsToListView longTask = new LoadContactsToListView();
            longTask.execute();

        } else {
            Toast.makeText(PhoneBookActivity.this, getString(R.string.read_contact), Toast.LENGTH_SHORT).show();
            EasyPermissions.requestPermissions(this, getString(R.string.read_contact), REQUEST_CONTACTS, Manifest.permission.READ_CONTACTS);
        }

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
                .baseUrl(ReservedName.SERVER_URL) // Адрес сервера
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Url", ReservedName.SERVER_URL + "/connect/directly/all");
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

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
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

            CheckBox cbBuy = (CheckBox) view.findViewById(R.id.ci_checkBox);
            cbBuy.setChecked(p.isChecked);
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

    class LoadContactsToListView extends AsyncTask<Void, Void, ArrayList<PhoneContactModel>> {

        @Override
        protected ArrayList<PhoneContactModel> doInBackground(Void... noargs) {
            return new UserPhoneBookData(PhoneBookActivity.this).getPhoneBook();
        }

        @Override
        protected void onPostExecute(ArrayList<PhoneContactModel> list) {
            allContacts = list;
            contactAdapter = new ContactAdapter(PhoneBookActivity.this, allContacts);
            listView.setAdapter(contactAdapter);
            progressBar.setVisibility(View.INVISIBLE);


        }
    }

}
