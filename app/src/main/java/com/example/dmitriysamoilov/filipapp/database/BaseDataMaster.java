package com.example.dmitriysamoilov.filipapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;

import com.example.dmitriysamoilov.filipapp.model.UserModel;

import java.util.ArrayList;


public class BaseDataMaster {
    private SQLiteDatabase database;
    private BaseDataHelper dbCreator;

    private static BaseDataMaster dataMaster;

    private BaseDataMaster(Context context) {
        dbCreator = new BaseDataHelper(context);
        if (database == null || !database.isOpen()) {
            database = dbCreator.getWritableDatabase();
        }
    }

    public static BaseDataMaster getDataMaster(Context context) {
        if (dataMaster == null) {
            dataMaster = new BaseDataMaster(context);
        }
        return dataMaster;
    }

    // Делаем запись о пользователе в БД
    public void insertDataOnDB(UserModel userModel) {
        if (database == null || !database.isOpen()) {
            database = dbCreator.getWritableDatabase();
        }
        ContentValues contentValues = new ContentValues();
        ArrayList<UserModel> users = getUserFromDB();
        boolean userFound = false;

        // Проверяем, есть ли в базе запись с таким же пользователем
        for (UserModel user : users) {
            if (user.getLink().contains(userModel.getLink())) {
                // нашли пользователя
                userFound = true;
                break;
            }
        }
        contentValues.put(BaseDataHelper.User.USER_FULL_NAME, userModel.getFull_name());
        contentValues.put(BaseDataHelper.User.USER_AVATAR_URL, userModel.getAvatar());
        contentValues.put(BaseDataHelper.User.USER_GENDER, userModel.getGender());
        contentValues.put(BaseDataHelper.User.USER_EMAIL, userModel.getEmail());
        contentValues.put(BaseDataHelper.User.USER_LINK, userModel.getLink());
        contentValues.put(BaseDataHelper.User.USER_TOKEN, userModel.getToken());

        // Обновляем или записываем данные пользователя
        if (userFound) {
            database.update(BaseDataHelper.User.TABLE_NAME, contentValues, " link = ?", new String[]{userModel.getLink()});
        } else {
            database.insert(BaseDataHelper.User.TABLE_NAME, null, contentValues);

        }

    }


    public ArrayList<UserModel> getUserFromDB() {
        ArrayList<UserModel> users = new ArrayList<>();
        String query = "SELECT * FROM " +
                BaseDataHelper.User.TABLE_NAME;

        if (database == null || !database.isOpen()) {
            database = dbCreator.getWritableDatabase();
        }
        Cursor cursor = database.rawQuery(query, null);

        try {
            UserModel userModel;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                userModel = new UserModel();
                userModel.setAvatar(cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.User.USER_AVATAR_URL)));
                userModel.setFull_name(cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.User.USER_FULL_NAME)));
                userModel.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.User.USER_EMAIL)));
                userModel.setGender(cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.User.USER_GENDER)));
                userModel.setLink(cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.User.USER_LINK)));

                userModel.setToken(cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.User.USER_TOKEN)));
                users.add(userModel);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (SQLiteDiskIOException e) {
            e.printStackTrace();
        }

        return users;
    }


    public void dropAllTables() {
        database.execSQL("DROP TABLE " + BaseDataHelper.User.TABLE_NAME);
    }
}
