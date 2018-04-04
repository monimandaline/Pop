package com.example.csontosmnika.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csontosmnika.popularmovies.models.MovieModel;
import com.example.csontosmnika.popularmovies.data.MovieContract;
import com.example.csontosmnika.popularmovies.data.MovieDbHelper;



import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

// Parceler guideline: https://guides.codepath.com/android/Using-Parceler, https://github.com/codepath/android_guides/wiki/Using-Parceler
// Autofit column: https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
// Animation: https://stackoverflow.com/questions/8720626/android-fade-in-and-fade-out-with-imageview

//Mentor suggestion: http://jakewharton.github.io/butterknife/


public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.tv_original_title) TextView originalTitleView;
    @BindView(R.id.tv_release_date) TextView releaseDateView;
    @BindView(R.id.iv_detail_movie_poster) ImageView backdropView;
    @BindView(R.id.tv_vote_average) TextView voteAverageView;
    @BindView(R.id.bar_rating) RatingBar voteAverageBar ;
    @BindView(R.id.tv_overview) TextView overviewView;
    @BindView(R.id.Button_Favorite) FloatingActionButton AddToFavoriteFloatingActionButton;

    private String MOVIE_ID;

    static final String DETAILS = "details";

    private Context mContext ;
    SQLiteDatabase mSqLiteDatabase ;
    private boolean mIsFavoriteMovie;
    private MovieModel MovieDetails;

    private Uri mCurrentProductUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        // Unwrapping the Parcel, get detail movie datas
        MovieDetails = (MovieModel) getIntent().getParcelableExtra(DETAILS);

        mContext = this;

        MOVIE_ID = String.valueOf(MovieDetails.getId());

       if(isFavoriteMovie(MovieDetails.getId(),mContext))
        {
            /*String[] projection = {
                    MovieContract.MovieEntry.COLUMN_POSTER_IMAGE
            };
            final String SELECTION = MovieContract.MovieEntry.COLUMN_ID + " = " + MovieDetails.getId();
            Cursor cursor = mSqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME,
                    projection, SELECTION, null, null, null, null
            );*/


            mIsFavoriteMovie = true;
            AddToFavoriteFloatingActionButton.setImageResource(R.drawable.star_on);
            //cursor.moveToFirst();
            //MovieDetails.setPosterImage(cursor.getBlob(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_POSTER_IMAGE)));
            //cursor.close();
        }


        String posterURL = MovieDetails.getBackdropUriString();
        Picasso.with(this)
                .load(posterURL)
                .into(backdropView);

        float stars = (float) MovieDetails.getVoteAverage();
        voteAverageView.setText(String.valueOf(MovieDetails.getVoteAverage()));
        voteAverageBar.setRating(stars);
        originalTitleView.setText(MovieDetails.getOriginalTitle());
        releaseDateView.setText(MovieDetails.getReleaseDate());
        overviewView.setText(MovieDetails.getOverview());

        animate(backdropView, false);


        AddToFavoriteFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsFavoriteMovie=!mIsFavoriteMovie;
                if(backdropView.getDrawable() instanceof BitmapDrawable) {
                    if (mIsFavoriteMovie) {
                        addMovieToFavorites();
                        AddToFavoriteFloatingActionButton.setImageResource(R.drawable.star_on);
                    } else {
                        deleteMovieFromFavorites();
                        //final String SELECTION = MovieContract.MovieEntry.COLUMN_ID + " = " + MovieDetails.getId();
                        AddToFavoriteFloatingActionButton.setImageResource(R.drawable.star_off);
                        //mSqLiteDatabase.delete(MovieContract.MovieEntry.TABLE_NAME, SELECTION, null);
                    }
                }

            }
        });
    }

    private void animate(final ImageView imageView, final boolean forever) {

        //imageView <-- The View which displays the images
        //forever <-- If equals true then after the last image it starts all over again with the first image resulting in an infinite loop. You have been warned.

        int fadeInDuration = 7500; // Configure time values here
        int timeBetween = 200;

        imageView.setVisibility(View.VISIBLE);    //Visible or invisible by default - this will apply when the animation ends

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.setRepeatCount(1);
        imageView.setAnimation(animation);
    }


    public static boolean isFavoriteMovie(int movieId, Context context)
    {
        //SQLiteDatabase sqLiteDatabase = new MovieDbHelper(context).getWritableDatabase();

        //final String SELECTION = movieId;

        String[] moviIdString = new String[]{String.valueOf(movieId)};

        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null, "movie_id = ?", moviIdString,  null);
        if(cursor.getCount()!=0)
        {

            //cursor.close();
            return true;
        }
       // cursor.close();
        return false;


    }

    private void addMovieToFavorites() {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, MovieDetails.getId());
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, MovieDetails.getOriginalTitle());
        values.put(MovieContract.MovieEntry.COLUMN_USER_RATING, MovieDetails.getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, MovieDetails.getReleaseDate());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, MovieDetails.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, MovieDetails.getPosterPath());
        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, MovieDetails.getBackdropPath());
        /*Bitmap bitmap = ((BitmapDrawable)backdropView.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        byte[] image = outputStream.toByteArray();
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_IMAGE,image);*/
        getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
    }


    private void deleteMovieFromFavorites() {
        // Only perform the delete if this is an existing product.

      //  if (MovieContract.MovieEntry.CONTENT_URI != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
           int rowsDeleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendEncodedPath(MOVIE_ID).build(), null, null);
                    // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_movie_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_movie_successful),
                        Toast.LENGTH_SHORT).show();
            }
      //  }

    }



}


