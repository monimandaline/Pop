package com.example.csontosmnika.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
//import org.parceler.Parcel;

// link to themoviedb.org
import static com.example.csontosmnika.popularmovies.TheMovieDbApi.TheMovieApiDbConstants.POSTER_BASE_URL;
import static com.example.csontosmnika.popularmovies.TheMovieDbApi.TheMovieApiDbConstants.POSTER_BACKDROP_URL;

// Movie model class
// Parceler guideline: https://guides.codepath.com/android/Using-Parceler, https://github.com/codepath/android_guides/wiki/Using-Parceler


public class MovieModel implements Parcelable{
    // fields must be package private
    /*private int id;
    private String originalTitle;
    private String posterPath;
    private String backdropPath;
    private String releaseDate;
    private double voteAverage;
    private String overview;*/

    private int id;
    private double voteAverage;
    private String title;
    private String posterPath;
    private String originalTitle;
    private String backdropPath;
    private String overview;
    private String releaseDate;

    // This empty constructor is needed by the Parceler library
    public MovieModel() {
    }

    public MovieModel(int id, String originalTitle, String posterPath, String backdropPath, String releaseDate, double voteAverage, String overview) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getBackdropUriString() {
        Uri baseUri = Uri.parse(POSTER_BACKDROP_URL);
        Uri.Builder backdropUri = baseUri.buildUpon();
        backdropUri.appendEncodedPath(backdropPath);
        String backdropUriString = backdropUri.toString();
        return backdropUriString;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getImageUriString() {
        Uri baseUri = Uri.parse(POSTER_BASE_URL);
        Uri.Builder imageUri = baseUri.buildUpon();
        imageUri.appendEncodedPath(posterPath);
        String imageUriString = imageUri.toString();
        return imageUriString;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int describeContents () {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(originalTitle);
        //parcel.writeString(title);
        parcel.writeString(posterPath);
        parcel.writeString(backdropPath);
        parcel.writeString(releaseDate);
        parcel.writeDouble(voteAverage);
        parcel.writeString(overview);
        //parcel.writeInt( registered ? 1 :0 );
    }

    // Static field used to regenerate object, individually or as arrays /
    public static final Parcelable.Creator<MovieModel> CREATOR = new Parcelable.Creator<MovieModel>() {
        public MovieModel createFromParcel(Parcel pc) {
            return new MovieModel(pc);
        }
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    /**Ctor from Parcel, reads back fields IN THE ORDER they were written */
    protected MovieModel(Parcel pc){
        id = pc.readInt();
        originalTitle = pc.readString();
        // title =  pc.readString();
        posterPath = pc.readString();
        backdropPath = pc.readString();
        releaseDate = pc.readString();
        voteAverage = pc.readDouble();
        overview = pc.readString();
        // registered = ( pc.readInt() == 1 );
    }


}

