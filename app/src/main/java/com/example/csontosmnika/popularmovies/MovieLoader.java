package com.example.csontosmnika.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

import com.example.csontosmnika.popularmovies.models.MovieModel;

public class MovieLoader extends AsyncTaskLoader<List<MovieModel>> {

    //Tag for log messages
    private static final String LOG_TAG = MovieLoader.class.getName();

    //Query URL
    private String url;

    public MovieLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<MovieModel> loadInBackground() {
        if (url == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of movies
        return QueryUtils.fetchMovieData(url);
    }
}