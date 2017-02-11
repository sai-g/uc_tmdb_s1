package com.udacity.android.tmdb.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.android.tmdb.BuildConfig;
import com.udacity.android.tmdb.R;
import com.udacity.android.tmdb.model.MovieInfo;

import java.net.URL;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.Utils;

import static com.udacity.android.tmdb.utilities.StringUIUtil.DECIMAL_FORMAT;
import static com.udacity.android.tmdb.utilities.StringUIUtil.setImageResource;

/**
 * Used to create recycler view list of movies
 * Created by sai_g on 2/1/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{

    private List<MovieInfo> mMovieData;

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {

        String API_KEY = BuildConfig.TMDB_API_KEY;
        TmdbApi TMDB_API = new TmdbApi(API_KEY);

        void onClick(MovieInfo selectedMovie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    /**
     * To create new list item in order to fill the visible screen space
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForItem = R.layout.movies_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = layoutInflater.inflate(layoutIdForItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * Call to actually show the data inside list item
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        MovieInfo currentMovie = mMovieData.get(position);
        holder.bind(currentMovie);
    }

    @Override
    public int getItemCount() {
        if(null == mMovieData)
            return 0;
        return mMovieData.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView mMoviePoster;
        public final TextView mMovieTitle;
        public final TextView mMovieRating;
        public final TextView mMoviePopularity;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);

            mMovieTitle = (TextView) itemView.findViewById(R.id.movie_title);
            mMoviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            mMovieRating = (TextView) itemView.findViewById(R.id.movie_avg_rating);
            mMoviePopularity = (TextView) itemView.findViewById(R.id.movie_popularity);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieInfo selectedMovie = mMovieData.get(adapterPosition);
            mClickHandler.onClick(selectedMovie);
        }

        void bind(MovieInfo movieInfo) {

            // Set image to ImageView
            Resources resources = mMoviePoster.getResources();
            URL imageUrl = Utils.createImageUrl(MovieAdapterOnClickHandler.TMDB_API, movieInfo.getPosterPath(), resources.getString(R.string.grid_image_size));
            // Alternate image will be shown when movie poster image not present
            int[] alternateImageOptions = {R.drawable.image_not_found, resources.getInteger(R.integer.alt_image_width), resources.getInteger(R.integer.alt_image_height)};
            setImageResource(mMoviePoster.getContext(), mMoviePoster, imageUrl, alternateImageOptions);
            mMoviePoster.setVisibility(View.VISIBLE);

            // Movie Title
            String movieTitle = mMovieTitle.getResources().getString(R.string.movie_title, movieInfo.getTitle(), movieInfo.getReleaseDate().split("-")[0]);
            mMovieTitle.setText(movieTitle);

            // Movie popularity
            String popularity = DECIMAL_FORMAT.format(movieInfo.getPopularity());
            popularity = mMoviePopularity.getResources().getString(R.string.movie_popularity, popularity);
            mMoviePopularity.setText(popularity);

            // Movie user rating
            String avgRating = DECIMAL_FORMAT.format(movieInfo.getVoteAverage());
            avgRating = mMovieRating.getResources().getString(R.string.movie_user_rating, avgRating);
            mMovieRating.setText(avgRating);

        }
    }

    public void setMovieData(List<MovieInfo> movieData) {

        if (mMovieData == null) {
            mMovieData = movieData;
            notifyDataSetChanged();
        }
        else {
            mMovieData.addAll(movieData);
            notifyItemRangeInserted(mMovieData.size(), movieData.size());
        }

    }

    public void clearMovieData() {
        mMovieData = null;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
