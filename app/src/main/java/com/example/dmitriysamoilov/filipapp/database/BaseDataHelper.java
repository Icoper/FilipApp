package com.example.dmitriysamoilov.filipapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class BaseDataHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "fillip_app_db";

    public static final int DB_VERSION = 1;


    public static class User implements BaseColumns {

        public static final String TABLE_NAME = "user_data";
        public static final String USER_EMAIL = "email";
        public static final String USER_FULL_NAME = "name";
        public static final String USER_GENDER = "gender";
        public static final String USER_AVATAR_URL = "avatar_url";

    }


    static String SCRIPT_CREATE_TBL_MAIN = "CREATE TABLE " +
            User.TABLE_NAME + " ( " +
            User._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            User.USER_AVATAR_URL + " TEXT," +
            User.USER_EMAIL + " TEXT," +
            User.USER_FULL_NAME + " TEXT," +
            User.USER_GENDER + " TEXT" + ");";

    public BaseDataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCRIPT_CREATE_TBL_MAIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE " + User.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

}
