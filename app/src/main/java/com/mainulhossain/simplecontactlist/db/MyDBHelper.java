package com.mainulhossain.simplecontactlist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mainulhossain.simplecontactlist.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mainul on 3/31/2017.
 */

public class MyDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager.db";

    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "phone_number";

    public MyDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Creating Table
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT" + ")";

        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void addContacts(Contact contact)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, contact.getName());
        contentValues.put(KEY_PH_NO, contact.getNumber());

        if (!checkValueExists(contact))
        {
            //Insert row
            db.insert(TABLE_CONTACTS, null, contentValues);
        }

        //Close Database
        db.close();
    }

    public List<Contact> getContacts()
    {
        List<Contact> contactList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        //Select Query
        String query = "SELECT * FROM " + TABLE_CONTACTS;
        Cursor mCursor = db.rawQuery(query, null);

        if (mCursor.moveToFirst())
        {
            do {
                Contact contact = new Contact();
                contact.setId(mCursor.getInt(0));
                contact.setName(mCursor.getString(1));
                contact.setNumber(mCursor.getString(2));

                contactList.add(contact);
            }
            while (mCursor.moveToNext());
        }
        //Close Cursor
        mCursor.close();
        //Close Database
        db.close();

        return contactList;
    }

    public boolean checkValueExists(Contact contact)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE "
                + KEY_NAME + " =?" + " AND " + KEY_PH_NO + " =?" ;
        Cursor mCursor = db.rawQuery(query, new String[]{contact.getName(), contact.getNumber()});

        if (mCursor.moveToFirst())
        {
            //Close Cursor
            mCursor.close();
            return true;
        }
        //Close Cursor
        mCursor.close();
        return false;
    }
}
