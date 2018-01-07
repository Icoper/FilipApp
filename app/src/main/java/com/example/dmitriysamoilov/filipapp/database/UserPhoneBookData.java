package com.example.dmitriysamoilov.filipapp.database;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.example.dmitriysamoilov.filipapp.model.PhoneContactModel;

import java.util.ArrayList;

public class UserPhoneBookData {

    private final String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;

    private final String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";

    private final String ORDER = String.format("%1$s COLLATE NOCASE", DISPLAY_NAME);
    private final ContentResolver contentResolver;

    @SuppressLint("InlinedApi")
    private final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
    };


    public UserPhoneBookData(final Context context) {
        contentResolver = context.getContentResolver();
    }

    public ArrayList<PhoneContactModel> getPhoneBook() {
        ArrayList<PhoneContactModel> phoneBookDataList = new ArrayList<>();

        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, FILTER, null, ORDER);
        if (cursor != null && cursor.moveToFirst()) {

            do {
                // get the contact's information
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                // get the user's email address
                String email = null;
                Cursor ce = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                if (ce != null && ce.moveToFirst()) {
                    email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    ce.close();
                }

                // get the user's phone number
                String phone = null;
                if (hasPhone > 0) {
                    Cursor cp = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (cp != null && cp.moveToFirst()) {
                        phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        cp.close();
                    }
                }

                // if the user user has an email or phone then add it to contacts
                if ((!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        && !email.equalsIgnoreCase(name)) || (!TextUtils.isEmpty(phone))) {
                    PhoneContactModel contact = new PhoneContactModel(name, email, phone, "", false);
                    phoneBookDataList.add(contact);
                }

            } while (cursor.moveToNext());

            // clean up cursor
            cursor.close();
        }

        return phoneBookDataList;
    }


}
