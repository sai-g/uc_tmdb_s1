package com.udacity.android.tmdb.utilities;

import com.udacity.android.tmdb.BuildConfig;

import info.movito.themoviedbapi.TmdbApi;

/**
 * Created by sai_g on 5/28/17.
 */

public class NetworkUtil {

    public static TmdbApi getTmdbApi() {
        return new TmdbApi(BuildConfig.TMDB_API_KEY);
    }
}
