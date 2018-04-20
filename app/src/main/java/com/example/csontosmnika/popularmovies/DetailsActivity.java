package com.example.csontosmnika.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csontosmnika.popularmovies.adapters.ReviewAdapter;
import com.example.csontosmnika.popularmovies.adapters.TrailerAdapter;
import com.example.csontosmnika.popularmovies.data.MovieContract;
import com.example.csontosmnika.popularmovies.models.MovieModel;
import com.example.csontosmnika.popularmovies.models.ReviewModel;
import com.example.csontosmnika.popularmovies.models.TrailerModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.csontosmnika.popularmovies.data.MovieProvider.haveDeletedAnItem;

// Parceler guideline: https://guides.codepath.com/android/Using-Parceler, https://github.com/codepath/android_guides/wiki/Using-Parceler
// Autofit column: https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
// Animation: https://stackoverflow.com/questions/8720626/android-fade-in-and-fade-out-with-imageview

// videó lejátszás, LAra: https://stackoverflow.com/questions/1572107/android-intent-for-playing-video

//Mentor suggestion: http://jakewharton.github.io/butterknife/


public class DetailsActivity extends AppCompatActivity  implements  LoaderManager.LoaderCallbacks  {

    @BindView(R.id.tv_original_title)
    TextView originalTitleView;
    @BindView(R.id.tv_release_date)
    TextView releaseDateView;
    @BindView(R.id.iv_detail_movie_poster)
    ImageView backdropView;
    @BindView(R.id.tv_vote_average)
    TextView voteAverageView;
    @BindView(R.id.bar_rating)
    RatingBar voteAverageBar;
    @BindView(R.id.tv_overview)
    TextView overviewView;
    @BindView(R.id.Button_Favorite)
    FloatingActionButton AddToFavoriteFloatingActionButton;

    public RecyclerView ReviewRecyclerView;
    public RecyclerView TrailerRecyclerView;

    public String MOVIE_ID;

    private int currentLoaderId;
    private static final int ID_REVIEW_LOADER = 1;
    private static final int ID_TRAILER_LOADER = 2;

    static final String DETAILS = "details";

    private Context mContext;
    SQLiteDatabase mSqLiteDatabase;
    private boolean mIsFavoriteMovie;
    public MovieModel MovieDetails;

    private Uri mCurrentProductUri;

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        // Review/Trailer set
        ReviewRecyclerView = findViewById(R.id.rv_reviews);
        TrailerRecyclerView = findViewById(R.id.rv_trailers);

        reviewAdapter = new ReviewAdapter(new ArrayList<ReviewModel>());
        trailerAdapter = new TrailerAdapter(new ArrayList<TrailerModel>());

        ReviewRecyclerView.setAdapter(reviewAdapter);
        TrailerRecyclerView.setAdapter(trailerAdapter);

        ReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        TrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        trailerAdapter.setOnItemClickListener(new TrailerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TrailerModel trailer, ImageView imageView) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v="  + trailer.getKey()));
                startActivity(intent);

            }
        });

        // Unwrapping the Parcel, get detail movie datas
        MovieDetails = (MovieModel) getIntent().getParcelableExtra(DETAILS);

        mContext = this;

        MOVIE_ID = String.valueOf(MovieDetails.getId());

        if (isFavoriteMovie(MovieDetails.getId(), mContext)) {
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
                mIsFavoriteMovie = !mIsFavoriteMovie;
                if (backdropView.getDrawable() instanceof BitmapDrawable) {
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


        // review/trailer loader
       getLoaderManager().initLoader(ID_REVIEW_LOADER, null, this);
       getLoaderManager().initLoader(ID_TRAILER_LOADER, null, this);

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


    public static boolean isFavoriteMovie(int movieId, Context context) {
           String[] moviIdString = new String[]{String.valueOf(movieId)};

        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, "movie_id = ?", moviIdString, null);
        if (cursor.getCount() != 0) {

               return true;
        }
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
        Toast.makeText(this, getString(R.string.editor_insert_movie_successful),
                Toast.LENGTH_SHORT).show();
    }


    private void deleteMovieFromFavorites() {
        // Only perform the delete if this is an existing movie.

        //  if (MovieContract.MovieEntry.CONTENT_URI != null) {
        // Call the ContentResolver to delete the product at the given content URI.
        // Pass in null for the selection and selection args because the mCurrentProductUri
        // content URI already identifies the product that we want.
        int rowsDeleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendEncodedPath(MOVIE_ID).build(), null, null);
        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            haveDeletedAnItem = false;
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_movie_failed),
                    Toast.LENGTH_SHORT).show();
        } else {

            haveDeletedAnItem = true;
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_movie_successful),
                    Toast.LENGTH_SHORT).show();
        }
        //  }

    }


    private static class TrailerLoader extends AsyncTaskLoader<List<TrailerModel>> {

        private String url;

        public TrailerLoader(Context context, String url) {
            super(context);
            this.url = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<TrailerModel> loadInBackground() {
            if (url == null) {
                return null;
            }

            // Perform the network request, parse the response, and extract a list of movies
            return QueryUtils.fetchTrailerData(url);


        }
    }

    public static class ReviewLoader extends AsyncTaskLoader<List<ReviewModel>> {


        String url;

        public ReviewLoader(Context context, String url) {
            super(context);
            this.url = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<ReviewModel> loadInBackground() {
            if (url == null) {
                return null;
            }

            // Perform the network request, parse the response, and extract a list of movies
            return QueryUtils.fetchReviewData(url);

        }
    }


    public Loader onCreateLoader(int id, Bundle args) {

        //Query URL todo: kiszedni thMovieApiba
        String url_review = "https://api.themoviedb.org/3/movie/" + String.valueOf(MovieDetails.getId()) + "/reviews?api_key=b2fad85553a59df1194eb4851cdc2b6e&language=en-US&page=1";
        String url_trailer = "https://api.themoviedb.org/3/movie/" + String.valueOf(MovieDetails.getId()) + "/videos?api_key=b2fad85553a59df1194eb4851cdc2b6e&language=en-US";

        if (id == ID_REVIEW_LOADER) {
            return new ReviewLoader(this, url_review);
        } else if (id == ID_TRAILER_LOADER) {
            return new TrailerLoader(this, url_trailer);
        }
        return null;
    }

    public void onLoadFinished(Loader loader, Object o) {
        int id = loader.getId();

        if (id == ID_REVIEW_LOADER) {
            reviewAdapter.setReviewList((List<ReviewModel>) o);
        } else if (id == ID_TRAILER_LOADER) {
            trailerAdapter.setTrailerList((List<TrailerModel>) o);
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {
       reviewAdapter.setReviewList(null);
       trailerAdapter.setTrailerList(null);
    }


}

