package com.example.xyzreader.data.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.xyzreader.data.providers.TaleProgressContract.TaleProgressEntry;

class AppDb extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "xyzreader.db";
    private static final int DATABASE_VERSION = 5;

    public AppDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + AppContentProvider.Tables.ITEMS + " ("
                + ItemsContract.ItemsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ItemsContract.ItemsColumns.SERVER_ID + " TEXT,"
                + ItemsContract.ItemsColumns.TITLE + " TEXT NOT NULL,"
                + ItemsContract.ItemsColumns.AUTHOR + " TEXT NOT NULL,"
                + ItemsContract.ItemsColumns.BODY + " TEXT NOT NULL,"
                + ItemsContract.ItemsColumns.THUMB_URL + " TEXT NOT NULL,"
                + ItemsContract.ItemsColumns.PHOTO_URL + " TEXT NOT NULL,"
                + ItemsContract.ItemsColumns.ASPECT_RATIO + " REAL NOT NULL DEFAULT 1.5,"
                + ItemsContract.ItemsColumns.PUBLISHED_DATE + " TEXT NOT NULL"
                + ")" );

        db.execSQL("CREATE TABLE " + AppContentProvider.Tables.TALE_PROGRESS + " ("
                + TaleProgressEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TaleProgressEntry.TALE_ID + " INTEGER NOT NULL UNIQUE,"
                + TaleProgressEntry.PAUSE_INDEX + " INTEGER NOT NULL DEFAULT 0"
                + ")" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AppContentProvider.Tables.ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + AppContentProvider.Tables.TALE_PROGRESS);
        onCreate(db);
    }
}