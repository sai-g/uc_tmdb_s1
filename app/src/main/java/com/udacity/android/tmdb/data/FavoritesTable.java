package com.udacity.android.tmdb.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by sai_g on 4/23/17.
 */

public class FavoritesTable {

    private FavoritesTable() {}

    // table name
    public static final String TABLE_NAME = "favorite_movies";
    // table columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MOVIE_TITLE = "title";
    public static final String COLUMN_MOVIE_ORIGINAL_TITLE = "original_title";
    public static final String COLUMN_MOVIE_YEAR = "release_date";
    public static final String COLUMN_USER_RATING = "rating";
    public static final String COLUMN_POPULARITY = "popularity";
    public static final String COLUMN_OVERVIEW = "overview";
    public static final String COLUMN_POSTERPATH = "poster_path";

    public static final String COLUMN_REVIEWS = "reviews";
    public static final String COLUMN_VIDEOS = "videos";

    private static final String TEXT_NOT_NULL = " text not null,";
    private static final String TEXT = " text,";

    // sql for database creation
    private static final String DATABASE_CREATE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key,"
            + COLUMN_MOVIE_TITLE + TEXT_NOT_NULL
            + COLUMN_MOVIE_ORIGINAL_TITLE + TEXT_NOT_NULL
            + COLUMN_MOVIE_YEAR + TEXT_NOT_NULL
            + COLUMN_USER_RATING + TEXT
            + COLUMN_POPULARITY + TEXT
            + COLUMN_OVERVIEW + TEXT
            + COLUMN_POSTERPATH + TEXT
            + COLUMN_REVIEWS + " blob,"
            + COLUMN_VIDEOS + " blob"
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
