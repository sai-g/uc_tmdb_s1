package com.udacity.android.popularmoviess1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.android.popularmoviess1.BuildConfig;
import com.udacity.android.popularmoviess1.R;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.Utils;
import info.movito.themoviedbapi.model.MovieDb;

import static com.udacity.android.popularmoviess1.utilities.StringUIUtil.setImageResource;

/**
 * Used to create recycler view list of movies
 * Created by sai_g on 2/1/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{

    public final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.0");

    private List<MovieDb> mMovieData;

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {

        String API_KEY = BuildConfig.TMDB_API_KEY;
        TmdbApi TMDB_API = new TmdbApi(API_KEY);

        void onClick(MovieDb selectedMovie);
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
        MovieDb currentMovie = mMovieData.get(position);
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
            MovieDb selectedMovie = mMovieData.get(adapterPosition);
            mClickHandler.onClick(selectedMovie);
        }

        void bind(MovieDb movieDb) {

            // Set image to ImageView
            URL imageUrl = Utils.createImageUrl(MovieAdapterOnClickHandler.TMDB_API, movieDb.getPosterPath(), "w300");
            setImageResource(mMoviePoster.getContext(), mMoviePoster, imageUrl, R.drawable.image_not_found);
            mMoviePoster.setVisibility(View.VISIBLE);

            // Movie Title
            String movieTitle = mMovieTitle.getResources().getString(R.string.movie_title, movieDb.getTitle(), movieDb.getReleaseDate().split("-")[0]);
            mMovieTitle.setText(movieTitle);

            // Movie popularity
            String popularity = DECIMAL_FORMAT.format(movieDb.getPopularity());
            popularity = mMoviePopularity.getResources().getString(R.string.movie_popularity, popularity);
            mMoviePopularity.setText(popularity);

            // Movie user rating
            String avgRating = DECIMAL_FORMAT.format(movieDb.getVoteAverage());
            avgRating = mMovieRating.getResources().getString(R.string.movie_user_rating, avgRating);
            mMovieRating.setText(avgRating);

        }
    }

    public void setMovieData(List<MovieDb> movieData) {

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
