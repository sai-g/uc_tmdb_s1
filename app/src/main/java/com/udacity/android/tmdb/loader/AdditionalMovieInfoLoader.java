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
        return movieDb;
    }
}
