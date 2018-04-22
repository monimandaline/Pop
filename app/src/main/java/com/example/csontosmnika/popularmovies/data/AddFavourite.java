package com.example.csontosmnika.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import com.example.csontosmnika.popularmovies.models.MovieModel;


public class AddFavourite {

      public static boolean addMovieToFavorites (MovieModel MovieDetails, ContentResolver MovieContentResolver){
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, MovieDetails.getId());
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, MovieDetails.getOriginalTitle());
        values.put(MovieContract.MovieEntry.COLUMN_USER_RATING, MovieDetails.getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, MovieDetails.getReleaseDate());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, MovieDetails.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, MovieDetails.getPosterPath());
        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, MovieDetails.getBackdropPath());

        Uri insertedMovie = MovieContentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, values);


        if (insertedMovie == null) {
           return false;

        } else return true;

    }

}
