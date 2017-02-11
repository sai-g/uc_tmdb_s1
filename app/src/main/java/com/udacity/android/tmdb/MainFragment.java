package com.udacity.android.tmdb;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.android.tmdb.adapter.MovieAdapter;
import com.udacity.android.tmdb.model.MovieInfo;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;


public class MainFragment extends Fragment implements MovieAdapter.MovieAdapterOnClickHandler{

    private LinearLayoutManager mLayoutManager;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private ProgressBar mLoadingIndicator;
    private TextView mErrorDisplayView;

    private int mCurrentPage;
    private int mTotalPages;
    private TmdbMovies.MovieMethod mCurrentMovieMethod = TmdbMovies.MovieMethod.now_playing;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
        }else {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        /*
        Using findViewById to get RecyclerView from activity_main xml
         */
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_movies);

        mErrorDisplayView = (TextView) rootView.findViewById(R.id.movie_error_message_display);

        // Recycler view to use LinearLayout Manager with VERTICAL orientation
        // Here Layout starts from top to bottom, so reverse layout set to false
        // Based on display size, column count will be decided
        int spanCount = getResources().getInteger(R.integer.grid_count);
        if (spanCount == 1) {
            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
        else {
            mLayoutManager = new GridLayoutManager(getActivity(), spanCount, LinearLayoutManager.VERTICAL, false);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);

        // Experiment with this value, when child layout size varies
        mRecyclerView.setHasFixedSize(true);

        /*
        Using adapter to display data with the Views
         */
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.movie_loading_indicator);

        // considering default movie method as now playing movies
        loadMovieDbData();

        setRecyclerViewScrollListener();

        //check to see if this is necessary
        mCurrentPage = 1;

        return rootView;
    }

    public MainFragment() {

    }

    private void loadMovieDbData() {

        showMovieDbDataView();

        //passing null now, will add functionality to fetch location and language info
        new FetchResultsTask().execute((String[]) null);
    }

    /**
     * to show recycler view and hide error message
     */
    private void showMovieDbDataView() {
        mErrorDisplayView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * to show error message and hide recycler view
     */
    private void showErrorMessageView() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorDisplayView.setVisibility(View.VISIBLE);
    }

    /**
     * Adding scrolling support by following snippet from
     * https://www.raywenderlich.com/126528/android-recyclerview-tutorial
     *
     */
    private int getLastVisibleItemPosition() {
        return mLayoutManager.findLastVisibleItemPosition();
    }

    /**
     * Adding scroll listener inorder to load more movies upon scrolling
     */
    private void setRecyclerViewScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int totalMoviesCount = mRecyclerView.getLayoutManager().getItemCount();

                //Log.d("CURRENT MOVIES COUNT ", String.valueOf(totalMoviesCount));
                //Log.d("CURRENT COUNT ", String.valueOf(getLastVisibleItemPosition()));

                //check to see if we reached current threshold, request more movies when reaching current threshold
                if(totalMoviesCount == getLastVisibleItemPosition() + 1) {
                    loadMovieDbData();
                }

            }
        });
    }

    @Override
    public void onClick(MovieInfo selectedMovie) {
        Context context = getActivity();
        Class destinationActivity = MovieInfoActivity.class;

        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, selectedMovie);

        startActivity(startChildActivityIntent);
    }

    public void resetMovieMethod(TmdbMovies.MovieMethod movieMethod) {
        mCurrentMovieMethod = movieMethod;
        // reset page information
        resetPages();
        mMovieAdapter.clearMovieData();
        // call API to get movies
        loadMovieDbData();
    }

    private void resetPages() {
        mCurrentPage = 1;
        mTotalPages = 0;
    }

    /**
     * Async task to update movie list upon scrolling or selection
     */
    private class FetchResultsTask extends AsyncTask<String, Void, List<MovieInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            //mLoadingIndicator.showContextMenu();
        }

        @Override
        protected List<MovieInfo> doInBackground(String... params) {

            try {
                TmdbMovies tmdbMovies = TMDB_API.getMovies();

                MovieResultsPage movieResultsPage;

                // TODO to add language based on location or input from user
                if (TmdbMovies.MovieMethod.top_rated == mCurrentMovieMethod) {
                    movieResultsPage = tmdbMovies.getTopRatedMovies(null, mCurrentPage);
                }
                else if (TmdbMovies.MovieMethod.popular == mCurrentMovieMethod) {
                    movieResultsPage = tmdbMovies.getPopularMovies(null, mCurrentPage);
                }
                else {
                    movieResultsPage = tmdbMovies.getNowPlayingMovies(null, mCurrentPage);
                }

                if(movieResultsPage != null) {
                    // increment page number in order to fetch next set of movies from API
                    mCurrentPage++;
                    mTotalPages = movieResultsPage.getTotalPages();
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

            // show movie db when there is a movie list present
            if(movieInfos != null) {
                showMovieDbDataView();
                mMovieAdapter.setMovieData(movieInfos);
            }
            // show toast when there is no next page available
            else if (mTotalPages > 0 && mCurrentPage >= mTotalPages) {
                Toast.makeText(getActivity(), "No more Movies found!!", Toast.LENGTH_LONG).show();
            }
            // clear data in adapter and show error message when there is no response from api
            else {
                mMovieAdapter.clearMovieData();
                showErrorMessageView();
            }

            mLoadingIndicator.setVisibility(View.GONE);
        }
    }

    /**
     * to convert MovieDb(Api object) to model MovieInfo
     * @param movieDbs
     * @return
     */
    private static List<MovieInfo> convertMovieDbToInfo(List<MovieDb> movieDbs) {

        List<MovieInfo> movieInfos = null;
        if (movieDbs != null) {

            movieInfos = new ArrayList<>(1);
           for (MovieDb movieDb : movieDbs) {

               MovieInfo movieInfo = new MovieInfo();
               // Builder pattern can be used, but polluting MovieInfo class with more lines of code
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
