package com.udacity.android.tmdb;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.udacity.android.tmdb.contentprovider.FavoritesContentProvider;
import com.udacity.android.tmdb.data.FavoritesTable;
import com.udacity.android.tmdb.loader.AdditionalMovieInfoLoader;
import com.udacity.android.tmdb.model.MovieInfo;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import info.movito.themoviedbapi.Utils;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;

import static com.udacity.android.tmdb.adapter.MovieAdapter.MovieAdapterOnClickHandler.TMDB_API;
import static com.udacity.android.tmdb.utilities.StringUIUtil.DECIMAL_FORMAT;
import static com.udacity.android.tmdb.utilities.StringUIUtil.convertObjToBytes;
import static com.udacity.android.tmdb.utilities.StringUIUtil.getFinalString;
import static com.udacity.android.tmdb.utilities.StringUIUtil.getFriendlyDateString;
import static com.udacity.android.tmdb.utilities.StringUIUtil.isEmpty;
import static com.udacity.android.tmdb.utilities.StringUIUtil.setImageResource;
import static com.udacity.android.tmdb.utilities.StringUIUtil.setStringResource;


public class MovieInfoFragment extends Fragment implements LoaderManager.LoaderCallbacks<MovieDb>, TrailerFragment.OnListFragmentInteractionListener{

    /*@BindView(R.id.movie_info_loading_indicator)
    ProgressBar mMovieInfoLoadingIndicator;*/

    @BindView(R.id.movie_info_title)
    TextView mMovieTitle;
    @BindView(R.id.movie_info_poster)
    ImageView mMoviePoster;
    @BindView(R.id.movie_info_overview)
    TextView mMovieOverview;
    @BindView(R.id.movie_info_rating)
    RatingBar mMovieRating;
    @BindView(R.id.movie_info_popularity)
    TextView mMoviePopularity;
    @BindView(R.id.movie_release_date)
    TextView mMovieReleaseDate;

    @BindView(R.id.reviews_not_available)
    TextView mReviewsNotAvailable;
    @BindView(R.id.trailers_not_available)
    TextView mTrailersNotAvailable;

    @BindView(R.id.favorite_button)
    MaterialFavoriteButton mFavoriteButton;

    private Unbinder unbinder;

    private MyReviewRecyclerViewAdapter mReviewsAdapter;
    private MyTrailerRecyclerViewAdapter mTrailersAdapter;

    private final static int ADDITIONAL_MOVIE_INFO_LOADER = 111;

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

            MovieInfo movieInfo = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);

            Bundle movieBundle = new Bundle();
            movieBundle.putInt("MOVIE_ID", movieInfo.getId());

            LoaderManager loaderManager = getActivity().getLoaderManager();
            Loader<List> movieInfoLoader = loaderManager.getLoader(ADDITIONAL_MOVIE_INFO_LOADER);

            if (movieInfoLoader == null) {
                loaderManager.initLoader(ADDITIONAL_MOVIE_INFO_LOADER, movieBundle, this);
            } else {
                loaderManager.restartLoader(ADDITIONAL_MOVIE_INFO_LOADER, movieBundle, this);
            }

            // reviews segment
            mReviewsAdapter = new MyReviewRecyclerViewAdapter();
            RecyclerView mReviewsRecyclerView = (RecyclerView) rootView.findViewById(R.id.review_list);
            mReviewsRecyclerView.setAdapter(mReviewsAdapter);

            // trailer segment
            mTrailersAdapter = new MyTrailerRecyclerViewAdapter(this);
            RecyclerView mTrailerRecyclerView = (RecyclerView) rootView.findViewById(R.id.trailer_list);
            mTrailerRecyclerView.setAdapter(mTrailersAdapter);

            initMovieInfoFragment(movieInfo);
        }

        return rootView;
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
            // reviews section
            setReviewsView(data);

            // trailers section
            setTrailersView(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieDb> loader) {

    }

    /**
     * to open video link in youtube
     * @param item
     */
    @Override
    public void onListFragmentInteraction(Video item) {

        String key = item.getKey();
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            // try to open video in youtube app
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            // when youtube app not found, open browser
            Log.e("YOUTUBE VIDEO", "Youtube app not found, so opening video in browser "+ex);
            startActivity(webIntent);
        }
    }

    private void setFavoriteButtonStatus() {

        String info = (String) mMovieTitle.getTag();
        String[] strings = info.split("-");

        int movieId = Integer.parseInt(strings[0]);
        ContentValues values = new ContentValues();
        values.put(FavoritesTable.COLUMN_ID, movieId);

        Uri queryUri = Uri.parse(FavoritesContentProvider.CONTENT_URI + "/" + movieId);

        Cursor cursor = getContext().getContentResolver().query(queryUri, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            if (movieId == cursor.getInt(0)) {
                mFavoriteButton.toggleFavorite();
            }
        }
    }
    // private Uri favoritesUri;

    private void saveFavorite() {

        // extract id, title, original title
        String info = (String) mMovieTitle.getTag();
        String[] strings = info.split("-");

        int movieId = Integer.parseInt(strings[0]);
        String movieTitle = String.valueOf(strings[1]);
        String originalTitle = mMovieTitle.getText().toString();
        String releaseDate = (String) mMovieReleaseDate.getTag();
        String userRating = Float.toString((Float) mMovieRating.getTag());
        String popularity = Float.toString((Float) mMoviePopularity.getTag());
        String imagePath = (String) mMoviePoster.getTag();
        String overview = mMovieOverview.getText().toString();

        byte[] reviews = (byte[]) mReviewsNotAvailable.getTag();
        byte[] trailers = (byte[]) mTrailersNotAvailable.getTag();

        ContentValues values = new ContentValues();
        values.put(FavoritesTable.COLUMN_ID, movieId);
        values.put(FavoritesTable.COLUMN_MOVIE_TITLE, movieTitle);
        values.put(FavoritesTable.COLUMN_MOVIE_ORIGINAL_TITLE, originalTitle);
        values.put(FavoritesTable.COLUMN_MOVIE_YEAR, releaseDate);
        values.put(FavoritesTable.COLUMN_USER_RATING, userRating);
        values.put(FavoritesTable.COLUMN_POPULARITY, popularity);
        values.put(FavoritesTable.COLUMN_OVERVIEW, overview);

        values.put(FavoritesTable.COLUMN_REVIEWS, reviews);
        values.put(FavoritesTable.COLUMN_VIDEOS, trailers);

        if (imagePath != null) {
            String[] imagePathArray = imagePath.split("/");
            values.put(FavoritesTable.COLUMN_POSTERPATH, "/"+imagePathArray[imagePathArray.length-1]);
        }

        Uri saveUri = Uri.parse(FavoritesContentProvider.CONTENT_URI + "/" + movieId);
        getContext().getContentResolver().insert(saveUri, values);
    }

    private void deleteFavorite() {

        // extract id, title, original title
        String info = (String) mMovieTitle.getTag();
        String[] strings = info.split("-");

        int movieId = Integer.parseInt(strings[0]);

        // where part of query
        String selection = FavoritesTable.COLUMN_ID + " LIKE ?";
        // values in placeholder
        String[] selectionArgs = {String.valueOf(movieId)};

        Uri deleteUri = Uri.parse(FavoritesContentProvider.CONTENT_URI + "/" + movieId);
        getContext().getContentResolver().delete(deleteUri, selection, selectionArgs);
    }

    /**
     * initialize view with MovieInfo
     * @param movieInfo
     */
    private void initMovieInfoFragment(MovieInfo movieInfo) {

        if (movieInfo != null) {

            // Set image to ImageView
            URL imageUrl = Utils.createImageUrl(TMDB_API, movieInfo.getPosterPath(), getResources().getString(R.string.movie_info_image_size));
            // store image url as tag, it is stored in db
            if (imageUrl != null)
                mMoviePoster.setTag(imageUrl.toString());
            // Alternate image will be shown when movie poster image not present
            int[] alternateImageOptions = {R.drawable.image_not_found, getResources().getInteger(R.integer.alt_info_image_width), getResources().getInteger(R.integer.alt_info_image_height)};
            setImageResource(getActivity(), mMoviePoster, imageUrl, alternateImageOptions);
            mMoviePoster.setVisibility(View.VISIBLE);

            // set movie id
            // set tag
            mMovieTitle.setTag(movieInfo.getId() + "-" + movieInfo.getTitle() + "-" + movieInfo.getOriginalTitle());

            // set textview with string formatted from strings xml
            setStringResource(mMovieTitle, R.string.movie_title, movieInfo.getOriginalTitle());

            // movie user rating
            if (movieInfo.getUserRating() > 0) {
                mMovieRating.setTag(movieInfo.getUserRating());
                mMovieRating.setRating(movieInfo.getUserRating()/2);
            } else {
                mMovieRating.setTag(movieInfo.getVoteAverage());
                mMovieRating.setRating(movieInfo.getVoteAverage()/2);
            }

            //setStringResource(mMovieRating, R.string.movie_user_rating, DECIMAL_FORMAT.format(data.getVoteAverage()));

            // movie popularity
            mMoviePopularity.setTag(movieInfo.getPopularity());
            setStringResource(mMoviePopularity, R.string.movie_popularity, DECIMAL_FORMAT.format(movieInfo.getPopularity()));

            // set movie release date
            mMovieReleaseDate.setTag(movieInfo.getReleaseDate());
            setStringResource(mMovieReleaseDate, R.string.movie_release_date, getFriendlyDateString(movieInfo.getReleaseDate()));

            setFavoriteButtonStatus();

            // state of the favorite button
            mFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    if (favorite) {
                        saveFavorite();
                        Toast.makeText(buttonView.getContext(), "Marked as Favorite", Toast.LENGTH_LONG).show();
                    }
                    else {
                        deleteFavorite();
                        Toast.makeText(buttonView.getContext(), "Removed from Favorites", Toast.LENGTH_LONG).show();
                    }
                }
            });

            // set overview text
            if (!isEmpty(movieInfo.getOverview())) {
                // show overview label/title when overview text exists

                // mMovieInfoOverviewTitle.setVisibility(View.VISIBLE);
                String overviewTxt = getFinalString(mMovieOverview, R.string.movie_info_overview, movieInfo.getOverview());
                mMovieOverview.setText(Html.fromHtml(overviewTxt));
            }

        }
    }

    /**
     * to set reviews in movie info fragment
     */
    private void setReviewsView(MovieDb movieDb) {

        if (movieDb.getReviews() != null && !movieDb.getReviews().isEmpty()) {
            mReviewsAdapter.setReviews(movieDb.getReviews());
            mReviewsNotAvailable.setTag(convertObjToBytes(movieDb.getReviews()));
        } else {
            // show no reviews available text
            mReviewsNotAvailable.setVisibility(View.VISIBLE);
        }
    }

    /**
     * to set trailer videos in movie info fragment
     */
    private void setTrailersView(MovieDb movieDb) {

        if (movieDb.getVideos() != null && !movieDb.getVideos().isEmpty()) {
            for (Video video : movieDb.getVideos()) {
                if ("YouTube".equals(video.getSite()) && "Trailer".equals(video.getType())) {
                    mTrailersAdapter.setVideo(video);
                }
            }
            mTrailersNotAvailable.setTag(convertObjToBytes(movieDb.getVideos()));
        }

        if (mTrailersAdapter.getVideos() == null || mTrailersAdapter.getVideos().isEmpty()) {
            mTrailersNotAvailable.setVisibility(View.VISIBLE);
        }

    }

}
