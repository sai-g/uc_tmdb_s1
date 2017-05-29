package com.udacity.android.tmdb.tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.udacity.android.tmdb.listener.AsyncTaskCompleteListener;
import com.udacity.android.tmdb.model.MovieInfo;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

import static com.udacity.android.tmdb.adapter.MovieAdapter.MovieAdapterOnClickHandler.TMDB_API;

/**
 * Async task to update movie list upon scrolling or selection
 */
public class FetchResultsTask extends AsyncTask<Object, Void, List<MovieInfo>> {

    private Context context;
    private AsyncTaskCompleteListener asyncTaskCompleteListener;

    public FetchResultsTask(Context context, AsyncTaskCompleteListener asyncTaskCompleteListener) {
        this.context = context;
        this.asyncTaskCompleteListener = asyncTaskCompleteListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<MovieInfo> doInBackground(Object... params) {

        try {
            TmdbMovies tmdbMovies = TMDB_API.getMovies();

            MovieResultsPage movieResultsPage;

            TmdbMovies.MovieMethod currentMovieMethod = (TmdbMovies.MovieMethod) params[0];
            int currentPage = Integer.parseInt(String.valueOf(params[1]));
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
    protected void onPostExecute(List<MovieInfo> movieInfos) {
        super.onPostExecute(movieInfos);
        asyncTaskCompleteListener.onTaskComplete(movieInfos);
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
