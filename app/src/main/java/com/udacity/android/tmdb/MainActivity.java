package com.udacity.android.tmdb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.android.tmdb.constants.SortBy;

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

        // Now prepare menu item to take actions899
        // Here, menu item can do sorting based on most popular movies or highest rated movies
        switch (item.getItemId()) {
            case R.id.highest_rated:
                mainFragment.resetMovieMethod(SortBy.TOP_RATED);
                break;
            case R.id.most_popular:
                mainFragment.resetMovieMethod(SortBy.POPULAR);
                break;
            case R.id.favorites:
                // null is passed to handle favorites movie, true for favorite movies from db
                mainFragment.resetMovieMethod(SortBy.FAVORITES);
                break;
            default:
                mainFragment.resetMovieMethod(SortBy.NOW_PLAYING);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
