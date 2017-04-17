package com.udacity.android.tmdb;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.android.tmdb.adapter.MovieAdapter;
import com.udacity.android.tmdb.dummy.DummyContent;
import com.udacity.android.tmdb.loader.AdditionalMovieInfoLoader;
import com.udacity.android.tmdb.model.MovieInfo;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import info.movito.themoviedbapi.Utils;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Reviews;

import static com.udacity.android.tmdb.utilities.StringUIUtil.DECIMAL_FORMAT;
import static com.udacity.android.tmdb.utilities.StringUIUtil.getFinalString;
import static com.udacity.android.tmdb.utilities.StringUIUtil.getFriendlyDateString;
import static com.udacity.android.tmdb.utilities.StringUIUtil.isEmpty;
import static com.udacity.android.tmdb.utilities.StringUIUtil.setImageResource;
import static com.udacity.android.tmdb.utilities.StringUIUtil.setStringResource;


public class MovieInfoFragment extends Fragment implements LoaderManager.LoaderCallbacks<MovieDb>{

    @BindView(R.id.movie_info_title)
    TextView mMovieTitle;
    @BindView(R.id.movie_info_poster)
    ImageView mMoviePoster;
    @BindView(R.id.movie_info_overview)
    TextView mMovieOverview;
    @BindView(R.id.movie_info_rating)
    TextView mMovieRating;
    @BindView(R.id.movie_info_popularity)
    TextView mMoviePopularity;
    @BindView(R.id.movie_release_date)
    TextView mMovieReleaseDate;

    @BindView(R.id.movie_info_overview_title)
    TextView mMovieInfoOverviewTitle;

    private RecyclerView mReviewsRecyclerView;
    private Unbinder unbinder;

    private MyReviewRecyclerViewAdapter mReviewsAdapter;

    private final static int ADDITIONAL_MOVIE_INFO_LOADER = 111;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_info, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        mReviewsAdapter = new MyReviewRecyclerViewAdapter(DummyContent.ITEMS);
        /*
         * Get the intent which started this activity
         */
        Intent intentThatStartedThisActivity = getActivity().getIntent();

        /*
         * Make sure there is some data present in Intent before starting this activity
         */
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

            MovieInfo movieInfo = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);

            // TODO add trailer segment

            //TODO add reviews segment

            Bundle movieBundle = new Bundle();
            movieBundle.putInt("MOVIE_ID", movieInfo.getId());
            LoaderManager loaderManager = getActivity().getLoaderManager();
            Loader<List> movieInfoLoader = loaderManager.getLoader(ADDITIONAL_MOVIE_INFO_LOADER);
            if (movieInfoLoader == null) {
                loaderManager.initLoader(ADDITIONAL_MOVIE_INFO_LOADER, movieBundle, this);
            } else {
                loaderManager.restartLoader(ADDITIONAL_MOVIE_INFO_LOADER, movieBundle, this);
            }

            mReviewsRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_new);
            mReviewsRecyclerView.setAdapter(mReviewsAdapter);

        }

        return rootView;
    }

    public MovieInfoFragment() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public Loader<MovieDb> onCreateLoader(int id, Bundle args) {
        return new AdditionalMovieInfoLoader(getContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<MovieDb> loader, MovieDb data) {

        if (data != null) {

            for (Reviews reviews : data.getReviews()) {
                //mMovieReviewText.setText(reviews.getContent());
                //mMovieReviews.addView(mMovieReviewText);
            }

            // set textview with string formatted from strings xml
            setStringResource(mMovieTitle, R.string.movie_title, data.getOriginalTitle(), data.getReleaseDate().split("-")[0]);


            // movie user rating
            setStringResource(mMovieRating, R.string.movie_user_rating, DECIMAL_FORMAT.format(data.getVoteAverage()));

            // movie popularity
            setStringResource(mMoviePopularity, R.string.movie_popularity, DECIMAL_FORMAT.format(data.getPopularity()));

            // set movie release date
            setStringResource(mMovieReleaseDate, R.string.movie_release_date, getFriendlyDateString(data.getReleaseDate()));

            // set overview text
            if (!isEmpty(data.getOverview())) {
                // show overview label/title when overview text exists

                mMovieInfoOverviewTitle.setVisibility(View.VISIBLE);
                String overviewTxt = getFinalString(mMovieOverview, R.string.movie_info_overview, data.getOverview());
                mMovieOverview.setText(Html.fromHtml(overviewTxt));
            }

            // Set image to ImageView
            URL imageUrl = Utils.createImageUrl(MovieAdapter.MovieAdapterOnClickHandler.TMDB_API, data.getPosterPath(), getResources().getString(R.string.movie_info_image_size));
            // Alternate image will be shown when movie poster image not present
            int[] alternateImageOptions = {R.drawable.image_not_found, getResources().getInteger(R.integer.alt_info_image_width), getResources().getInteger(R.integer.alt_info_image_height)};
            setImageResource(getActivity(), mMoviePoster, imageUrl, alternateImageOptions);
            mMoviePoster.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieDb> loader) {

    }
}
