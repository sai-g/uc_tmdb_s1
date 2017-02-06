package com.udacity.android.popularmoviess1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.android.popularmoviess1.adapter.MovieAdapter;

import java.net.URL;

import info.movito.themoviedbapi.Utils;
import info.movito.themoviedbapi.model.MovieDb;

import static com.udacity.android.popularmoviess1.adapter.MovieAdapter.DECIMAL_FORMAT;
import static com.udacity.android.popularmoviess1.utilities.StringUIUtil.IMAGE_WIDTH;
import static com.udacity.android.popularmoviess1.utilities.StringUIUtil.getFinalString;
import static com.udacity.android.popularmoviess1.utilities.StringUIUtil.getFriendlyDateString;
import static com.udacity.android.popularmoviess1.utilities.StringUIUtil.setImageResource;
import static com.udacity.android.popularmoviess1.utilities.StringUIUtil.setStringResource;


public class MovieInfoFragment extends Fragment {

    private TextView mMovieTitle;
    private ImageView mMoviePoster;
    private TextView mMovieOverview;
    private TextView mMovieRating;
    private TextView mMoviePopularity;
    private TextView mMovieReleaseDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_info, container, false);


        mMovieTitle = (TextView) rootView.findViewById(R.id.movie_info_title);

        mMoviePoster = (ImageView) rootView.findViewById(R.id.movie_info_poster);

        mMovieOverview = (TextView) rootView.findViewById(R.id.movie_info_overview);

        mMovieRating = (TextView) rootView.findViewById(R.id.movie_info_rating);

        mMoviePopularity = (TextView) rootView.findViewById(R.id.movie_info_popularity);

        mMovieReleaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);

        /*
         * Get the intent which started this activity
         */
        Intent intentThatStartedThisActivity = getActivity().getIntent();

        /*
         * Make sure there is some data present in Intent before starting this activity
         */
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

            MovieDb movieDb = (MovieDb) intentThatStartedThisActivity.getSerializableExtra(Intent.EXTRA_TEXT);

            // Set image to ImageView
            URL imageUrl = Utils.createImageUrl(MovieAdapter.MovieAdapterOnClickHandler.TMDB_API, movieDb.getPosterPath(), IMAGE_WIDTH);
            setImageResource(getActivity(), mMoviePoster, imageUrl, R.drawable.image_not_found);
            mMoviePoster.setVisibility(View.VISIBLE);

            // set textview with string formatted from strings xml
            setStringResource(mMovieTitle, R.string.movie_title, movieDb.getOriginalTitle(), movieDb.getReleaseDate().split("-")[0]);

            // set overview text
            String overviewTxt = getFinalString(mMovieOverview, R.string.movie_info_overview, movieDb.getOverview());
            mMovieOverview.setText(Html.fromHtml(overviewTxt));

            // movie user rating
            setStringResource(mMovieRating, R.string.movie_user_rating, DECIMAL_FORMAT.format(movieDb.getVoteAverage()));

            // movie popularity
            setStringResource(mMoviePopularity, R.string.movie_popularity, DECIMAL_FORMAT.format(movieDb.getPopularity()));

            // set movie release date
            setStringResource(mMovieReleaseDate, R.string.movie_release_date, getFriendlyDateString(movieDb.getReleaseDate()));

        }

        return rootView;
    }

    public MovieInfoFragment() {

    }

}
