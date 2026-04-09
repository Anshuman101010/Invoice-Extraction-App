package com.example.incoiceextraction.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "InvoiceDB", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email TEXT UNIQUE," +
                "password TEXT)");

        db.execSQL("CREATE TABLE folders(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "user_id INTEGER)");

        db.execSQL("CREATE TABLE scans(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "invoice TEXT," +
                "date TEXT," +
                "total TEXT," +
                "folder_id INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS folders");
        db.execSQL("DROP TABLE IF EXISTS scans");

        onCreate(db);
    }

    // 🔥 CHECK LOGIN
    public boolean checkUser(String email, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE email=? AND password=?",
                new String[]{email, password}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();

        return exists;
    }
    public boolean insertFolder(String name) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.execSQL("INSERT INTO folders(name) VALUES(?)", new Object[]{name});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Cursor getAllFolders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM folders", null);
    }

    // 🔥 GET USER ID
    public int getUserId(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE email=?",
                new String[]{email}
        );

        int userId = -1;

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }

        cursor.close();
        return userId;
    }

    // 🔥 REGISTER USER (for signup)
    public boolean registerUser(String email, String password) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            db.execSQL(
                    "INSERT INTO users(email,password) VALUES(?,?)",
                    new Object[]{email, password}
            );

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}