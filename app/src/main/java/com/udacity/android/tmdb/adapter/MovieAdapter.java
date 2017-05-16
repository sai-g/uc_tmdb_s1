package com.udacity.android.tmdb.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.udacity.android.tmdb.BuildConfig;
import com.udacity.android.tmdb.R;
import com.udacity.android.tmdb.model.MovieInfo;

import java.net.URL;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.Utils;

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

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mMoviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
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
