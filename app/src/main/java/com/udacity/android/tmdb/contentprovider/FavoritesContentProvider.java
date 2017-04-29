package com.udacity.android.tmdb.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.udacity.android.tmdb.data.FavoritesDbHelper;
import com.udacity.android.tmdb.data.FavoritesTable;

public class FavoritesContentProvider extends ContentProvider {

    // database helper class
    private FavoritesDbHelper dbHelper;

    // UriMatcher ids
    private static final int FAVORITES = 100;
    private static final int FAVORITES_ID = 110;

    private static final String AUTHORITY = "com.udacity.android.tmdb.contentprovider";

    private static final String BASE_PATH = "favorites";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/movies";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/movie";

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH, FAVORITES);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH, FAVORITES_ID);
    }

    @Override
    public boolean onCreate() {
        // to initialize your content provider on startup.
        dbHelper = new FavoritesDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        // using query builder to create sql query
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // validate all columns

        queryBuilder.setTables(FavoritesTable.TABLE_NAME);

        int uriTypes = URI_MATCHER.match(uri);
        switch (uriTypes) {
            case FAVORITES_ID:
                // add details for all columns
                queryBuilder.appendWhere(FavoritesTable.COLUMN_ID + "=" +uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : "+ uri);
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // to handle requests to delete one or more rows.
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int id = 0;
        switch (uriType) {
            case FAVORITES_ID:
                id = database.delete(FavoritesTable.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return id;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // to handle requests to insert a new row.
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case FAVORITES_ID:
                id = database.insert(FavoritesTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
