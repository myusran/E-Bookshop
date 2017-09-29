package com.emotech.e_bookshop.Internet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.emotech.e_bookshop.adapter.ImageItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";
    private static final String BUKU_BARU = "BukuBaru";
    private static final String BEST_SELLER = "BestSeller";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_IDAKUN = "idAkun";
    private static final String KEY_NAME = "name";
    private static final String KEY_FIRST = "firstname";
    private static final String KEY_LAST = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_Phone = "phone";
    private static final String KEY_ADDRES = "alamat";

    //Table Buku Baru Columns names
    private static final String KEY_CODE = "code";
    private static final String KEY_TITLE = "title";
    private static final String KEY_PRICE = "price";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_UPDATEON = "updateOn";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IDAKUN + " INTEGER," + KEY_NAME + " TEXT,"
                + KEY_FIRST + " TEXT," + KEY_LAST + " TEXT," + KEY_EMAIL + " TEXT UNIQUE," + KEY_Phone + " TEXT," + KEY_ADDRES + " TEXT" +")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_BUKUBARU_TABLE = "CREATE TABLE " + BUKU_BARU + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CODE + " TEXT," + KEY_TITLE + " TEXT,"
                + KEY_PRICE + " TEXT," + KEY_IMAGE + " TEXT," + KEY_UPDATEON + " TEXT"+")";
        db.execSQL(CREATE_BUKUBARU_TABLE);

        String CREATE_BESTSELLER_TABLE = "CREATE TABLE " + BEST_SELLER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CODE + " TEXT," + KEY_TITLE + " TEXT,"
                + KEY_PRICE + " TEXT," + KEY_IMAGE + " TEXT," + KEY_UPDATEON + " TEXT"+")";
        db.execSQL(CREATE_BESTSELLER_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + BUKU_BARU);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String idakun, String username, String first, String last, String email, String phone, String alamat) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IDAKUN, idakun); //id akun
        values.put(KEY_NAME, username); // username
        values.put(KEY_FIRST, first); //first name
        values.put(KEY_LAST, last); //last name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_Phone, phone); //phone
        values.put(KEY_ADDRES, alamat); //alamat

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Storing bukubaru details in database
     */

    public void addBukuBaru(String code, String title, String price, String image, String updateOn){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CODE, code);
        values.put(KEY_TITLE, title);
        values.put(KEY_PRICE, price);
        values.put(KEY_IMAGE, image);
        values.put(KEY_UPDATEON, updateOn);

        // Inserting Row
        long id = db.insert(BUKU_BARU, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New book inserted into sqlite: " + id);
    }

    public int getDateCount(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());

        String selectQuery = "SELECT * FROM " + BUKU_BARU + " WHERE " + date + " > updateOn";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int cnt = cursor.getCount();
        cursor.close();

        return cnt;
    }

    public int getRowCount(){
        String selectQuery = "SELECT * FROM " + BUKU_BARU;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int cnt = cursor.getCount();
        cursor.close();

        return cnt;
    }

    /**
     * Storing Best Seller details in database
     */

    public void addBestSeller(String code, String title, String price, String image, String updateOn){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CODE, code);
        values.put(KEY_TITLE, title);
        values.put(KEY_PRICE, price);
        values.put(KEY_IMAGE, image);
        values.put(KEY_UPDATEON, updateOn);

        // Inserting Row
        long id = db.insert(BEST_SELLER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New book inserted into sqlite: " + id);
    }

    public int getBestDateCount(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());

        String selectQuery = "SELECT * FROM " + BEST_SELLER + " WHERE " + date + " > updateOn";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int cnt = cursor.getCount();
        cursor.close();

        return cnt;
    }

    public int getBestRowCount(){
        String selectQuery = "SELECT * FROM " + BEST_SELLER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int cnt = cursor.getCount();
        cursor.close();

        return cnt;
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(1));
            user.put("username", cursor.getString(2));
            user.put("firstname", cursor.getString(3));
            user.put("lastname", cursor.getString(4));
            user.put("email", cursor.getString(5));
            user.put("phone", cursor.getString(6));
            user.put("alamat", cursor.getString(7));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public ArrayList<ImageItem> getDataBukuBaru (){
        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + BUKU_BARU;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        do{
            imageItems.add(new ImageItem(cursor.getString(4),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(1)));
        }while(cursor.moveToNext());

        return imageItems;
    }

    public ArrayList<ImageItem> getDataBestSeller (){
        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + BEST_SELLER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        do{
            imageItems.add(new ImageItem(cursor.getString(4),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(1)));
        }while(cursor.moveToNext());

        return imageItems;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
