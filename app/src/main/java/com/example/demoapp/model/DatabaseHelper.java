package com.example.demoapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.demoapp.common.Constants;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "database.db";
    private static final int SCHEMA = 1;
    private static volatile DatabaseHelper sDatabaseHelper = null;
    private SQLiteDatabase mDatabase = null;

    public synchronized static DatabaseHelper getInstance(Context context) {
        if (sDatabaseHelper == null) {
            sDatabaseHelper = new DatabaseHelper(context.getApplicationContext());
        }
        return sDatabaseHelper;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table, defining table & column headings
        db.execSQL("CREATE TABLE " + Constants.TABLE + "("
                + "_id INTEGER PRIMARY KEY, "
                + Constants.ITEM_ID + " INTEGER, "
                + Constants.ITEM_TITLE + " TEXT, "
                + Constants.ITEM_DESCRIPTION + " TEXT"
                +  ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("onUpgrade not setup"); // FIXME
    }

    private SQLiteDatabase getDb(Context context) {
        if(mDatabase == null) {
            mDatabase = getInstance(context).getWritableDatabase();
        }
        return mDatabase;
    }

    // insert Item
    public void insertTaskItem(Context context, long itemID, String title, String description) {
        // Timber.i("%s: inserting item into the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        ContentValues cv = new ContentValues();
        cv.put(Constants.ITEM_ID, itemID);
        cv.put(Constants.ITEM_TITLE, title);
        cv.put(Constants.ITEM_DESCRIPTION, description);
        db.insert(Constants.TABLE, Constants.ITEM_ID, cv);
    }

    // load item
    public Cursor loadItem(Context context, long itemId) {
        // Timber.i("%s: loading item from the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        return (db.rawQuery("SELECT * FROM " + Constants.TABLE + " where " + Constants.ITEM_ID +"='" + itemId + "'", null));
    }

    // load items
    public Cursor loadItems(Context context) {
        // Timber.i("%s: loading items from the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        return  (db.rawQuery("SELECT * FROM " + Constants.TABLE + " ORDER BY " + Constants.ITEM_ID + " DESC", null));
    }

    // delete item
    public void deleteTaskItem(Context context, long itemId) {
        // Timber.i("%s: deleting item from the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        String selection = Constants.ITEM_ID + " = ?";
        String[] args = {String.valueOf(itemId)};
        db.delete(Constants.TABLE, selection, args);
    }


    // update item
    public void updateTaskItem(Context context, ContentValues values){
        // Timber.i("%s: updating item in the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        db.update(Constants.TABLE, values, Constants.ITEM_ID + " = " + values.getAsString(Constants.ITEM_ID), null);
    }


}
