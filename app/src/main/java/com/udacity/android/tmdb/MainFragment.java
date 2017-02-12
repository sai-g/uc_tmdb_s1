package com.udacity.android.tmdb;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.android.tmdb.adapter.MovieAdapter;
import com.udacity.android.tmdb.listener.AsyncTaskCompleteListener;
import com.udacity.android.tmdb.model.MovieInfo;
import com.udacity.android.tmdb.tasks.FetchResultsTask;

import java.util.List;

import info.movito.themoviedbapi.TmdbMovies;

import static com.udacity.android.tmdb.utilities.StringUIUtil.calculateNoOfColumns;


public class MainFragment extends Fragment implements MovieAdapter.MovieAdapterOnClickHandler{

    private GridLayoutManager mLayoutManager;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private ProgressBar mLoadingIndicator;
    private TextView mErrorDisplayView;

    private int mCurrentPage;
    private TmdbMovies.MovieMethod mCurrentMovieMethod = TmdbMovies.MovieMethod.now_playing;

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

        mLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.movie_loading_indicator);

        // reset current page whenever new layout is created
        resetCurrentPage();

        // considering default movie method as now playing movies
        loadMovieDbData();

        setRecyclerViewScrollListener();

        return rootView;
    }

    public MainFragment() {

    }

    private void loadMovieDbData() {

        showMovieDbDataView();

        mLoadingIndicator.setVisibility(View.VISIBLE);

        // passing currentmovie method to get results based on menu selection
        new FetchResultsTask(this.getContext(), new FetchResultsTaskCompleteListener()).execute(mCurrentMovieMethod, mCurrentPage);
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
        resetCurrentPage();
        mMovieAdapter.clearMovieData();
        // call API to get movies
        loadMovieDbData();
    }

    private void resetCurrentPage() {
        mCurrentPage = 1;
    }

    /**
     * this task complete listener will be called when fetch results tasks is complete(from onPostExecute)
     */
    public class FetchResultsTaskCompleteListener implements AsyncTaskCompleteListener<List<MovieInfo>> {
        @Override
        public void onTaskComplete(List<MovieInfo> movieInfos) {
            // show movie db when there is a movie list present
            if(movieInfos != null) {
                //increment current page when movies are returned
                mCurrentPage++;
                showMovieDbDataView();
                mMovieAdapter.setMovieData(movieInfos);
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
