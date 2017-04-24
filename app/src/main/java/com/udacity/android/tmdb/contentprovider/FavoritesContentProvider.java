package com.udacity.android.tmdb.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.udacity.android.tmdb.data.FavoritesDbHelper;

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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
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

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
