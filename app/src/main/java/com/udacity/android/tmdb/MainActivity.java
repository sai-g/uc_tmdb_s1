package com.udacity.android.tmdb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import info.movito.themoviedbapi.TmdbMovies;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, which adds menu items to the action bar (res/menu/sort_category)
        getMenuInflater().inflate(R.menu.sort_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MainFragment mainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.fragment);

        // Now prepare menu item to take actions
        // Here, menu item can do sorting based on most popular movies or highest rated movies
        switch (item.getItemId()) {
            case R.id.sort_highest_rated:
                mainFragment.resetMovieMethod(TmdbMovies.MovieMethod.top_rated);
                break;
            case R.id.sort_most_popular:
                mainFragment.resetMovieMethod(TmdbMovies.MovieMethod.popular);
                break;
            default:
                mainFragment.resetMovieMethod(TmdbMovies.MovieMethod.now_playing);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
