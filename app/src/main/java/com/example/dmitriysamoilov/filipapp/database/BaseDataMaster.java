package com.example.dmitriysamoilov.filipapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.dmitriysamoilov.filipapp.model.UserModel;


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

    public void insertDataOnDB(UserModel userModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BaseDataHelper.User.USER_FULL_NAME, userModel.getFull_name());
        contentValues.put(BaseDataHelper.User.USER_AVATAR_URL, userModel.getAvatar());
        contentValues.put(BaseDataHelper.User.USER_GENDER, userModel.getGender());
        contentValues.put(BaseDataHelper.User.USER_EMAIL, userModel.getEmail());

        database.insert(BaseDataHelper.User.TABLE_NAME, null, contentValues);
    }


    public UserModel getUserFromDB() {
        String query = "SELECT * FROM " +
                BaseDataHelper.User.TABLE_NAME;

        Cursor cursor = database.rawQuery(query, null);

        UserModel userModel = new UserModel();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            userModel.setAvatar(cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.User.USER_AVATAR_URL)));
            userModel.setFull_name(cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.User.USER_FULL_NAME)));
            userModel.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.User.USER_EMAIL)));
            userModel.setGender(cursor.getString(cursor.getColumnIndexOrThrow(BaseDataHelper.User.USER_GENDER)));
            cursor.moveToNext();
        }
        return userModel;
    }
}
