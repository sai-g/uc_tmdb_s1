package com.udacity.android.tmdb.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by sai_g on 4/23/17.
 */

public class FavoritesTable {

    // table name
    public static final String TABLE_NAME = "favorite_movies";
    // table columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MOVIE_TITLE = "title";
    public static final String COLUMN_MOVIE_YEAR = "year";
    public static final String COLUMN_USER_RATING = "user_rating";
    public static final String COLUMN_POPULARITY = "popularity";
    public static final String COLUMN_OVERVIEW = "overview";

    // sql for database creation
    private static final String DATABASE_CREATE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key,"
            + COLUMN_MOVIE_TITLE + " text not null,"
            + COLUMN_MOVIE_YEAR + " text not null,"
            + COLUMN_USER_RATING + " text,"
            + COLUMN_POPULARITY + " text,"
            + COLUMN_OVERVIEW + " text"
            + ");";

    // sql to drop table
    private static final String DATABASE_DROP = "DROP TABLE IF EXISTS "+TABLE_NAME;

    /**
     * this method will be called from DbHelper class to create table
     * @param database
     */
    public static void onCreate (SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    /**
     * this method will be called from DbHelper on upgrading db
     * @param database
     * @param oldVersion
     * @param newVersion
     */
    public static void onUpgrade (SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(DATABASE_DROP);
        onCreate(database);
    }
}
