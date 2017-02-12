package com.udacity.android.tmdb;

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

import com.udacity.android.tmdb.adapter.MovieAdapter;
import com.udacity.android.tmdb.model.MovieInfo;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import info.movito.themoviedbapi.Utils;

import static com.udacity.android.tmdb.utilities.StringUIUtil.DECIMAL_FORMAT;
import static com.udacity.android.tmdb.utilities.StringUIUtil.getFinalString;
import static com.udacity.android.tmdb.utilities.StringUIUtil.getFriendlyDateString;
import static com.udacity.android.tmdb.utilities.StringUIUtil.isEmpty;
import static com.udacity.android.tmdb.utilities.StringUIUtil.setImageResource;
import static com.udacity.android.tmdb.utilities.StringUIUtil.setStringResource;


public class MovieInfoFragment extends Fragment {

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

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_info, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        /*
         * Get the intent which started this activity
         */
        Intent intentThatStartedThisActivity = getActivity().getIntent();

        /*
         * Make sure there is some data present in Intent before starting this activity
         */
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

            MovieInfo movieInfo = (MovieInfo) intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);

            // Set image to ImageView
            URL imageUrl = Utils.createImageUrl(MovieAdapter.MovieAdapterOnClickHandler.TMDB_API, movieInfo.getPosterPath(), getResources().getString(R.string.movie_info_image_size));
            // Alternate image will be shown when movie poster image not present
            int[] alternateImageOptions = {R.drawable.image_not_found, getResources().getInteger(R.integer.alt_info_image_width), getResources().getInteger(R.integer.alt_info_image_height)};
            setImageResource(getActivity(), mMoviePoster, imageUrl, alternateImageOptions);
            mMoviePoster.setVisibility(View.VISIBLE);

            // set textview with string formatted from strings xml
            setStringResource(mMovieTitle, R.string.movie_title, movieInfo.getOriginalTitle(), movieInfo.getReleaseDate().split("-")[0]);

            // set overview text
            if (!isEmpty(movieInfo.getOverview())) {
                // show overview label/title when overview text exists
                rootView.findViewById(R.id.movie_info_overview_title).setVisibility(View.VISIBLE);
                String overviewTxt = getFinalString(mMovieOverview, R.string.movie_info_overview, movieInfo.getOverview());
                mMovieOverview.setText(Html.fromHtml(overviewTxt));
            }

            // movie user rating
            setStringResource(mMovieRating, R.string.movie_user_rating, DECIMAL_FORMAT.format(movieInfo.getVoteAverage()));

            // movie popularity
            setStringResource(mMoviePopularity, R.string.movie_popularity, DECIMAL_FORMAT.format(movieInfo.getPopularity()));

            // set movie release date
            setStringResource(mMovieReleaseDate, R.string.movie_release_date, getFriendlyDateString(movieInfo.getReleaseDate()));

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
}
