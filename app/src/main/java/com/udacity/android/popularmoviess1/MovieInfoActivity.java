package com.udacity.android.popularmoviess1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.android.popularmoviess1.adapter.MovieAdapter;

import java.net.URL;

import info.movito.themoviedbapi.Utils;
import info.movito.themoviedbapi.model.MovieDb;

public class MovieInfoActivity extends AppCompatActivity {

    private TextView mMovieTitle;
    private ImageView mMoviePoster;
    private TextView mMovieOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        mMovieTitle = (TextView) findViewById(R.id.movie_info_title);

        mMoviePoster = (ImageView) findViewById(R.id.movie_info_poster);

        mMovieOverview = (TextView) findViewById(R.id.movie_info_overview);


        /*
         * Get the intent which started this activity
         */
        Intent intentThatStartedThisActivity = getIntent();

        /*
         * Make sure there is some data present in Intent before starting this activity
         */
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

            MovieDb movieDb = (MovieDb) intentThatStartedThisActivity.getSerializableExtra(Intent.EXTRA_TEXT);

            mMovieTitle.setText(movieDb.getOriginalTitle());

            URL imageUrl = Utils.createImageUrl(MovieAdapter.MovieAdapterOnClickHandler.TMDB_API, movieDb.getPosterPath(), "w342");
            if(imageUrl != null) {
                Picasso.with(this).load(imageUrl.toString()).into(mMoviePoster);
                mMoviePoster.setVisibility(View.VISIBLE);
            }
            else {
                Log.e("<IMAGE NOT FOUND>", movieDb.getOriginalTitle() + " -- "+ movieDb.getImdbID() + " -- " + imageUrl);
            }


            mMovieOverview.setText(movieDb.getOverview());
        }
    }
}
