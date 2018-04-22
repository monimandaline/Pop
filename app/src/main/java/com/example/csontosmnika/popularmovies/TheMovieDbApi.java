package com.example.csontosmnika.popularmovies;

public class TheMovieDbApi {

   /* private TheMovieDbApi() {

    }*/

    public static final class TheMovieApiDbConstants {
        public static int page = 1;
        public static final String API_KEY = "";
        public static final String POPULAR_MOVIES = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY + "&language=en-US&sort_by=popularity.desc&page=";
        public static final String TOP_RATED_MOVIES = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY + "&language=en-US&sort_by=vote_average.desc&page=";
        public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";
        public static final String POSTER_BACKDROP_URL = "http://image.tmdb.org/t/p/w780/";
        public static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
        public static final String YOUTUBE_IMAGE_URL = "http://img.youtube.com/vi/";
        public static final String MOVIE_DETAILS_URL ="https://api.themoviedb.org/3/movie/";
    }
}
