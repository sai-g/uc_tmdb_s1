package com.udacity.android.tmdb.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sai_g on 4/23/17.
 */

public class FavoritesDbHelper extends SQLiteOpenHelper{

    // name of the database
    private static final String DATABASE_NAME = "favorites.db";
    // database version, whenever there is a change in dbversion(increment or decrement) appropriate helper method will be called
    // increment onUpgrade, decrement onDowngrade
    private static final int DATABASE_VERSION = 4;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        FavoritesTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        FavoritesTable.onUpgrade(db, oldVersion, newVersion);
    }
}
