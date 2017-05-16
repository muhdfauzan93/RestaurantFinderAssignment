package com.example.muhdfauzan.myrestaurant.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.muhdfauzan.myrestaurant.model.ModelChart;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper{

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "chart";

    // Contacts table name
    private static final String TABLE_CHARTS = "charts";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_RES_ID = "rid";
    private static final String KEY_MENU_NAME = "name";
    private static final String KEY_MENU_TYPE = "type";
    private static final String KEY_MENU_PRICE = "price";
    private static final String KEY_MENU_DETAIL = "detail";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CHARTS  + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_RES_ID + " TEXT,"
                + KEY_MENU_NAME + " TEXT," + KEY_MENU_TYPE + " TEXT," + KEY_MENU_PRICE + " TEXT," + KEY_MENU_DETAIL + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHARTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addMenu(ModelChart menu) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RES_ID, menu.getId());
        values.put(KEY_MENU_NAME, menu.getMenuName());
        values.put(KEY_MENU_PRICE, menu.getMenuPrice());
        values.put(KEY_MENU_DETAIL, menu.getMenuDetail());
        values.put(KEY_MENU_TYPE, menu.getMenuType());

        // Inserting Row
        db.insert(TABLE_CHARTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    ModelChart getMenu(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CHARTS, new String[] { KEY_ID, KEY_RES_ID,
                        KEY_MENU_NAME,KEY_MENU_TYPE,KEY_MENU_PRICE,KEY_MENU_DETAIL }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

       ModelChart chart = new ModelChart(cursor.getString(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5));
        // return contact
        return chart;
    }

    // Getting All Contacts
    public List<ModelChart> getAllChart() {
        List<ModelChart> chartList = new ArrayList<ModelChart>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CHARTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ModelChart chart = new ModelChart();
                chart.setId(cursor.getString(0));
                chart.setRestId(cursor.getString(1));
                chart.setMenuName(cursor.getString(2));
                chart.setMenuType(cursor.getString(3));
                chart.setMenuPrice(cursor.getString(4));
                chart.setMenuDetail(cursor.getString(5));

                // Adding contact to list
               chartList.add(chart);
            } while (cursor.moveToNext());
        }

        // return contact list
        return chartList;
    }

    // Updating single contact
    public int updateContact(ModelChart chart) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RES_ID, chart.getRestId());
        values.put(KEY_MENU_NAME, chart.getMenuName());
        values.put(KEY_MENU_TYPE, chart.getMenuType());
        values.put(KEY_MENU_PRICE, chart.getMenuPrice());
        values.put(KEY_MENU_DETAIL, chart.getMenuDetail());

        // updating row
        return db.update(TABLE_CHARTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(chart.getId()) });
    }

    // Deleting single contact
    public void deleteChart(ModelChart chart) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHARTS, KEY_ID + " = ?",
                new String[] { String.valueOf(chart.getId()) });
        db.close();
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_CHARTS);
        db.close();
    }


    // Getting contacts Count
    public int getChartCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CHARTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }


}