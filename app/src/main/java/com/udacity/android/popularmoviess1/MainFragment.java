package com.udacity.android.popularmoviess1;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.android.popularmoviess1.adapter.MovieAdapter;

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

    private int currentPage;
    private int totalPages;
    private static TmdbMovies.MovieMethod CURRENT_MOVIE_METHOD = TmdbMovies.MovieMethod.now_playing;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        /*
        Using findViewById to get RecyclerView from activity_main xml
         */
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_movies);

        mErrorDisplayView = (TextView) rootView.findViewById(R.id.movie_error_message_display);

        // Recycler view to use LinearLayout Manager with VERTICAL orientation
        // Here Layout starts from top to bottom, so reverse layout set to false
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
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
        currentPage = 1;
        return rootView;
    }

    public MainFragment() {

    }

    private void loadMovieDbData() {

        showMovieDbDataView();

        //passing null now, will add functionality to fetch location and language info
        new FetchResultsTask().execute((String[]) null);
    }


    private void showMovieDbDataView() {
        mErrorDisplayView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

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

                Log.d("CURRENT MOVIES COUNT ", String.valueOf(totalMoviesCount));
                Log.d("CURRENT COUNT ", String.valueOf(getLastVisibleItemPosition()));

                //check to see if we reached current threshold, request more movies when reaching current threshold
                if(totalMoviesCount == getLastVisibleItemPosition() + 1) {
                    loadMovieDbData();
                }
            }
        });
    }

    @Override
    public void onClick(MovieDb selectedMovie) {
        Context context = getActivity();
        Class destinationActivity = MovieInfoActivity.class;

        Log.d("SELECTED MOVIE", selectedMovie.getOriginalTitle());

/*        if(mTempToast != null) {
            mTempToast.cancel();
        }
        mTempToast = Toast.makeText(this, "Movie Selected - "+selectedMovie.getOriginalTitle(), Toast.LENGTH_LONG);
        mTempToast.show();*/

        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, selectedMovie);

        startActivity(startChildActivityIntent);
    }

    public void resetMovieMethod(TmdbMovies.MovieMethod movieMethod) {
        CURRENT_MOVIE_METHOD = movieMethod;
        // reset page information
        resetPages();
        mMovieAdapter.clearMovieData();
        // call API to get movies
        loadMovieDbData();
    }

    private void resetPages() {
        currentPage = 1;
        totalPages = 0;
    }


    private class FetchResultsTask extends AsyncTask<String, Void, List<MovieDb>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            //mLoadingIndicator.showContextMenu();
        }

        @Override
        protected List<MovieDb> doInBackground(String... params) {

            TmdbMovies tmdbMovies = TMDB_API.getMovies();

            MovieResultsPage movieResultsPage;

            // TODO to add language based on location or input from user
            if (TmdbMovies.MovieMethod.top_rated == CURRENT_MOVIE_METHOD) {
                movieResultsPage = tmdbMovies.getTopRatedMovies(null, currentPage);
            }
            else if (TmdbMovies.MovieMethod.popular == CURRENT_MOVIE_METHOD) {
                movieResultsPage = tmdbMovies.getPopularMovies(null, currentPage);
            }
            else {
                movieResultsPage = tmdbMovies.getNowPlayingMovies(null, currentPage);
            }

            if(movieResultsPage != null) {
                // increment page number in order to fetch next set of movies from API
                currentPage++;
                totalPages = movieResultsPage.getTotalPages();
                return movieResultsPage.getResults();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieDb> movieDbs) {

            // show movie db when there is a movie list present
            if(movieDbs != null) {
                showMovieDbDataView();
                mMovieAdapter.setMovieData(movieDbs);
            }
            // show toast when there is no next page available
            else if (currentPage >= totalPages) {
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
}
