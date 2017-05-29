package com.udacity.android.tmdb.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

import static com.udacity.android.tmdb.adapter.MovieAdapter.MovieAdapterOnClickHandler.TMDB_API;

/**
 * Created by sai_g on 4/16/17.
 */

public class AdditionalMovieInfoLoader extends AsyncTaskLoader<MovieDb> {

    private Bundle mMovieBundle;
    public AdditionalMovieInfoLoader(Context context, Bundle movieBundle) {
        super(context);
        this.mMovieBundle = movieBundle;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        forceLoad();
    }

    @Override
    public MovieDb loadInBackground() {

        int movieId = mMovieBundle.getInt("MOVIE_ID");
        MovieDb movieDb = TMDB_API.getMovies().getMovie(movieId, "", TmdbMovies.MovieMethod.reviews, TmdbMovies.MovieMethod.videos);

        /*// online failed to get data
        if (movieDb == null || movieDb.getTitle() == null) {

            ContentValues values = new ContentValues();
            values.put(FavoritesTable.COLUMN_ID, movieId);

            Uri queryUri = Uri.parse(FavoritesContentProvider.CONTENT_URI + "/" + movieId);

            Cursor cursor = getContext().getContentResolver().query(queryUri, null, null, null, null);
            while (cursor.moveToNext()) {
                movieDb = new MovieDb();
                movieDb.setId(cursor.getInt(0));
                movieDb.setTitle(cursor.getString(1));
                movieDb.setReleaseDate(cursor.getString(2));
                movieDb.setUserRating(cursor.getFloat(3));
                movieDb.setPopularity(cursor.getFloat(4));
                movieDb.setOverview(cursor.getString(5));
                movieDb.setPosterPath(cursor.getString(6));

                movieInfos.add(movieInfo);
            }
        }*/
        return movieDb;
    }

}
