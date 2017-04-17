package com.udacity.android.tmdb.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.udacity.android.tmdb.model.MovieInfo;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

import static com.udacity.android.tmdb.adapter.MovieAdapter.MovieAdapterOnClickHandler.TMDB_API;

/**
 * Async loader to update movie list upon scrolling or selection
 */

public class FetchResultsLoader extends AsyncTaskLoader<List<MovieInfo>> {

    private Bundle mCurrentMovieBundle;

    // variable to cache data
    private List<MovieInfo> mData;

    public FetchResultsLoader(Context context, Bundle movieBundle) {
        super(context);
        this.mCurrentMovieBundle = movieBundle;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mCurrentMovieBundle == null) {
            return;
        }
        // check for cached result, return if exists
        if (mData != null) {
            deliverResult(mData);
        } else {
            // forces an asynchronous load
            forceLoad();
        }
    }

    @Override
    public List<MovieInfo> loadInBackground() {

        TmdbMovies.MovieMethod currentMethod = (TmdbMovies.MovieMethod) mCurrentMovieBundle.getSerializable("CURRENT_MOVIE_METHOD");
        int currentPage = mCurrentMovieBundle.getInt("CURRENT_PAGE");

        if (currentMethod == null) {
            return null;
        }
        try {
            TmdbMovies tmdbMovies = TMDB_API.getMovies();

            MovieResultsPage movieResultsPage;

            TmdbMovies.MovieMethod currentMovieMethod = currentMethod;
            if (TmdbMovies.MovieMethod.top_rated == currentMovieMethod) {
                movieResultsPage = tmdbMovies.getTopRatedMovies(null, currentPage);
            }
            else if (TmdbMovies.MovieMethod.popular == currentMovieMethod) {
                movieResultsPage = tmdbMovies.getPopularMovies(null, currentPage);
            }
            else {
                movieResultsPage = tmdbMovies.getNowPlayingMovies(null, currentPage);
            }

            if(movieResultsPage != null) {
                return convertMovieDbToInfo(movieResultsPage.getResults());
            }
        } catch (Throwable ex) {
            // TmDB API throw MovieDbException, but Async task catches it and throw a Throwable. Because of this app is crashing
            // Tried to override onCancelled by catching MovieDbException here, but didn't work since Aysnc task still throw a Throwable
            Log.e("Movie DB Exception", "No Internet Connectivity");
        }
        return null;
    }

    @Override
    public void deliverResult(List<MovieInfo> data) {
        mData = data;
        super.deliverResult(data);
    }

    /**
     * handles a request to completely reset the loader
     * https://developer.android.com/reference/android/content/AsyncTaskLoader.html
     */
    @Override
    protected void onReset() {
        super.onReset();

        // making sure the loader is stopped
        onStopLoading();

        // nullify cached data
        mData = null;
    }

    /**
     * to convert MovieDb(Api object) to model MovieInfo
     * @param movieDbs
     * @return
     */
    private List<MovieInfo> convertMovieDbToInfo(List<MovieDb> movieDbs) {

        List<MovieInfo> movieInfos = null;
        if (movieDbs != null) {

            movieInfos = new ArrayList<>(1);
            for (MovieDb movieDb : movieDbs) {

                MovieInfo movieInfo = new MovieInfo();
                // Builder pattern can be used, but polluting MovieInfo class with more lines of code
                movieInfo.setId(movieDb.getId());
                movieInfo.setOriginalTitle(movieDb.getOriginalTitle());
                movieInfo.setTitle(movieDb.getTitle());
                movieInfo.setPopularity(movieDb.getPopularity());
                movieInfo.setOverview(movieDb.getOverview());
                movieInfo.setPosterPath(movieDb.getPosterPath());
                movieInfo.setUserRating(movieDb.getUserRating());
                movieInfo.setVoteAverage(movieDb.getVoteAverage());
                movieInfo.setReleaseDate(movieDb.getReleaseDate());
                movieInfos.add(movieInfo);
            }
        }
        return movieInfos;
    }
}
