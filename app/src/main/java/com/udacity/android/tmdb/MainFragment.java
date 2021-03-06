package com.udacity.android.tmdb;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.android.tmdb.adapter.MovieAdapter;
import com.udacity.android.tmdb.constants.SortBy;
import com.udacity.android.tmdb.loader.FetchResultsLoader;
import com.udacity.android.tmdb.model.MovieInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.udacity.android.tmdb.constants.BundleConstants.CURRENT_PAGE;
import static com.udacity.android.tmdb.constants.BundleConstants.CURRENT_POSITION;
import static com.udacity.android.tmdb.constants.BundleConstants.MOVIES_LIST;
import static com.udacity.android.tmdb.constants.BundleConstants.SORT_OPTION;
import static com.udacity.android.tmdb.utilities.StringUIUtil.calculateNoOfColumns;


public class MainFragment extends Fragment implements MovieAdapter.MovieAdapterOnClickHandler, LoaderManager.LoaderCallbacks<List<MovieInfo>>{

    private GridLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    private MovieAdapter mMovieAdapter;
    private ProgressBar mLoadingIndicator;

    private TextView mErrorDisplayView;
    private TextView mNoFavoritesView;

    private int mCurrentPage;
    private int mCurrentVisiblePosition;

    private String mCurrentMovieMethod = SortBy.NOW_PLAYING;
    // constant int to uniquely identify the loader
    private static final int FETCH_RESULTS_LOADER = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        /*
        Using findViewById to get RecyclerView from activity_main xml
         */
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_movies);

        mErrorDisplayView = (TextView) rootView.findViewById(R.id.movie_error_message_display);

        mNoFavoritesView = (TextView) rootView.findViewById(R.id.no_favorites);

        // Recycler view to use LinearLayout Manager with VERTICAL orientation
        // Here Layout starts from top to bottom, so reverse layout set to false
        // Based on display size, column count will be decided
        //mLayoutManager = new GridLayoutManager(this.getContext(), calculateNoOfColumns(this.getContext()));
        mLayoutManager = new GridLayoutManager(this.getContext(), calculateNoOfColumns(this.getContext()));

        mRecyclerView.setLayoutManager(mLayoutManager);

        // Experiment with this value, when child layout size varies
        mRecyclerView.setHasFixedSize(true);

        /*
        Using adapter to display data with the Views
         */
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        setRecyclerViewScrollListener();

        mLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.movie_loading_indicator);

        List<MovieInfo> movieInfos = null;
        // retrieve movie method from Bundle
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CURRENT_POSITION)) {
                mCurrentVisiblePosition = savedInstanceState.getInt(CURRENT_POSITION);
            }
            if (savedInstanceState.containsKey(CURRENT_PAGE)) {
                mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE);
            }
            if (savedInstanceState.containsKey(SORT_OPTION)) {
                mCurrentMovieMethod = savedInstanceState.getString(SORT_OPTION);

                if (savedInstanceState.containsKey(MOVIES_LIST)) {
                    movieInfos = savedInstanceState.getParcelableArrayList(MOVIES_LIST);
                    loadMovieDbData(movieInfos);
                }
            }

        } else {
            // mCurrentMovieMethod = TmdbMovies.SortBy.now_playing;
            // reset current page whenever new layout is created
            resetCurrentPage();
            loadMovieDbData(null);
        }
        // considering default movie method as now playing movies

        return rootView;
    }

    private void loadMovieDbData(List<MovieInfo> movieInfos) {

        showMovieDbDataView();

        mLoadingIndicator.setVisibility(View.VISIBLE);

        // loader takes bundle as input
        Bundle currentMovieBundle = new Bundle();
        currentMovieBundle.putString(SORT_OPTION, mCurrentMovieMethod);
        currentMovieBundle.putInt(CURRENT_PAGE, mCurrentPage);

        if (movieInfos != null) {
            currentMovieBundle.putParcelableArrayList(MOVIES_LIST, (ArrayList<? extends Parcelable>) movieInfos);
        }

        LoaderManager loaderManager = getActivity().getLoaderManager();
        Loader<List> fetchResultsLoader = loaderManager.getLoader(FETCH_RESULTS_LOADER);
        if (fetchResultsLoader == null) {
            loaderManager.initLoader(FETCH_RESULTS_LOADER, currentMovieBundle, this);
        } else {
            loaderManager.restartLoader(FETCH_RESULTS_LOADER, currentMovieBundle, this);
        }

    }

    /**
     * to show recycler view and hide error message
     */
    private void showMovieDbDataView() {
        mNoFavoritesView.setVisibility(View.GONE);
        mErrorDisplayView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * to show error message and hide recycler view
     */
    private void showErrorMessageView() {

        mRecyclerView.setVisibility(View.GONE);
        if (Objects.equals(mCurrentMovieMethod, SortBy.FAVORITES)) {
            mNoFavoritesView.setVisibility(View.VISIBLE);
        } else {
            mErrorDisplayView.setVisibility(View.VISIBLE);
        }
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

                //check to see if we reached current threshold, request more movies when reaching current threshold
                if(totalMoviesCount == getLastVisibleItemPosition() + 1) {
                    mCurrentVisiblePosition = 0;
                    loadMovieDbData(null);
                }

            }
        });
    }

    @Override
    public void onClick(int adapterPosition, MovieInfo selectedMovie) {

        mCurrentVisiblePosition = adapterPosition;
        Context context = getActivity();
        Class destinationActivity = MovieInfoActivity.class;

        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, selectedMovie);

        startActivity(startChildActivityIntent);
    }

    public void resetMovieMethod(String sortBy) {
        mCurrentMovieMethod = sortBy;
        // reset page information
        resetCurrentPage();
        mMovieAdapter.clearMovieData();
        // call API to get movies
        loadMovieDbData(null);
    }

    private void resetCurrentPage() {
        mCurrentPage = 1;
    }

    @Override
    public Loader<List<MovieInfo>> onCreateLoader(int id, final Bundle args) {
        return new FetchResultsLoader(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<MovieInfo>> loader, List<MovieInfo> data) {

        mMovieAdapter.setMovieData(data);

        int adapterItemCount = mMovieAdapter.getItemCount();
        if(data != null) {
            //increment current page when movies are returned
            mCurrentPage = (adapterItemCount / 20) + 1;
            showMovieDbDataView();
        }
        // clear data in adapter and show error message when there is no response from api
        if (adapterItemCount == 0) {
            showErrorMessageView();
        }

        // scroll to current visible position on rotation
        if (mCurrentVisiblePosition > 0) {
            mRecyclerView.scrollToPosition(mCurrentVisiblePosition);
        }

        mLoadingIndicator.setVisibility(View.GONE);

    }

    @Override
    public void onLoaderReset(Loader<List<MovieInfo>> loader) {
        Log.d("LOADER", "onLoaderReset");
        mMovieAdapter.setMovieData(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // movie info added to bundle
        if (mMovieAdapter.getMovieData() != null) {
            outState.putParcelableArrayList(MOVIES_LIST, (ArrayList<? extends Parcelable>) mMovieAdapter.getMovieData());
        }
        outState.putString(SORT_OPTION, mCurrentMovieMethod);
        outState.putInt(CURRENT_PAGE, mCurrentPage);
        outState.putInt(CURRENT_POSITION, mCurrentVisiblePosition > 0 ? mCurrentVisiblePosition : getLastVisibleItemPosition());
    }


    @Override
    public void onResume() {
        super.onResume();

        //refresh the page in case of favorites
        if (Objects.equals(mCurrentMovieMethod, SortBy.FAVORITES)) {
            mMovieAdapter.clearMovieData();
            loadMovieDbData(null);
        }
    }


}
