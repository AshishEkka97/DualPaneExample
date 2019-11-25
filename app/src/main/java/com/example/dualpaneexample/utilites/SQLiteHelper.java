package com.example.dualpaneexample.utilites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dualpaneexample.model.Item;

import java.io.File;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LocalImages.db";
    private static final String TABLE_NAME = "images_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "ALBUM_ID";
    private static final String COL_3 = "TITLE";
    private static final String COL_4 = "URL";
    private static final String COL_5 = "THUMBNAIL_URL";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query ="CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + "(" +
                COL_1 + " INTEGER PRIMARY KEY, " +
                COL_2 + " INTEGER, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT" +
                ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }

    public void addImages(List<Item> images) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Item image : images) {
                values.put(COL_1, image.getId());
                values.put(COL_2, image.getAlbumId());
                values.put(COL_3, image.getTitle());
                values.put(COL_4, image.getUrl());
                values.put(COL_5, image.getThumbUrl());
                db.insert(TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getData(int limit, int offset) {
       SQLiteDatabase db = this.getWritableDatabase();
       String query = "SELECT * FROM " + TABLE_NAME + " LIMIT " + limit + " OFFSET " + offset;
       Cursor res = db.rawQuery(query, null);
       return res;
    }

    public boolean checkDatabase() {
        Cursor cursor = getData(10, 0);
        if (cursor != null && cursor.getCount() > 0) return true;
        return false;
    }

    public long numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return count;
    }
}
