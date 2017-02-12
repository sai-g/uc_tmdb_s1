package com.udacity.android.tmdb.listener;

/**
 * reference : http://www.jameselsey.co.uk/blogs/techblog/extracting-out-your-asynctasks-into-separate-classes-makes-your-code-cleaner/
 */

public interface AsyncTaskCompleteListener<T> {

    public void onTaskComplete(T result);
}
