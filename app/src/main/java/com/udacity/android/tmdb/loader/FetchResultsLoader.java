package com.udacity.android.tmdb.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.udacity.android.tmdb.contentprovider.FavoritesContentProvider;
import com.udacity.android.tmdb.model.MovieInfo;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

import static com.udacity.android.tmdb.adapter.MovieAdapter.MovieAdapterOnClickHandler.TMDB_API;
import static com.udacity.android.tmdb.constants.BundleConstants.CURRENT_PAGE;
import static com.udacity.android.tmdb.constants.BundleConstants.MOVIES_LIST;
import static com.udacity.android.tmdb.constants.BundleConstants.SORT_OPTION;

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

        List<MovieInfo> movieInfos = null;
        TmdbMovies.MovieMethod currentMethod = (TmdbMovies.MovieMethod) mCurrentMovieBundle.getSerializable(SORT_OPTION);
        int currentPage = mCurrentMovieBundle.getInt(CURRENT_PAGE);

        boolean loadFavorites = mCurrentMovieBundle.getBoolean("LOAD_FAVORITES");

        if (mCurrentMovieBundle.containsKey(MOVIES_LIST)) {
            return mCurrentMovieBundle.getParcelableArrayList(MOVIES_LIST);
        }

        try {
            TmdbMovies tmdbMovies = TMDB_API.getMovies();

            MovieResultsPage movieResultsPage = null;

            if (loadFavorites) {
                Uri queryUri = FavoritesContentProvider.CONTENT_URI;
                Cursor cursor = getContext().getContentResolver().query(queryUri, null, null, null, null);
                return convertDbInfoToMovieInfo(cursor);
            }
            else if (TmdbMovies.MovieMethod.top_rated == currentMethod) {
                movieResultsPage = tmdbMovies.getTopRatedMovies(null, currentPage);
            }
            else if (TmdbMovies.MovieMethod.popular == currentMethod) {
                movieResultsPage = tmdbMovies.getPopularMovies(null, currentPage);
            }
            else {
                movieResultsPage = tmdbMovies.getNowPlayingMovies(null, currentPage);
            }

            if(movieResultsPage != null) {
                movieInfos = convertMovieDbToInfo(movieResultsPage.getResults());
            }
        } catch (Exception ex) {
            // TmDB API throw MovieDbException, but Async task catches it and throw a Throwable. Because of this app is crashing
            // Tried to override onCancelled by catching MovieDbException here, but didn't work since Aysnc task still throw a Throwable
            Log.e("Movie DB Exception", "No Internet Connectivity "+ex);
        }
        return movieInfos;
    }

    @Override
    public void deliverResult(List<MovieInfo> data) {
        super.deliverResult(data);
        mData = data;
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

    private List<MovieInfo> convertDbInfoToMovieInfo(Cursor cursor) {

        List<MovieInfo> movieInfos = new ArrayList<>(1);
        while (cursor.moveToNext()) {
            MovieInfo movieInfo = new MovieInfo();
            movieInfo.setId(cursor.getInt(0));
            movieInfo.setTitle(cursor.getString(1));
            movieInfo.setOriginalTitle(cursor.getString(2));
            movieInfo.setReleaseDate(cursor.getString(3));
            movieInfo.setUserRating(cursor.getFloat(4));
            movieInfo.setPopularity(cursor.getFloat(5));
            movieInfo.setOverview(cursor.getString(6));
            movieInfo.setPosterPath(cursor.getString(7));

            movieInfos.add(movieInfo);
        }

        return movieInfos;
    }
}
