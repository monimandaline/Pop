package com.example.csontosmnika.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
//import org.parceler.Parcels;
import android.os.Parcel;
import android.os.Parcelable;

// Parceler guideline: https://guides.codepath.com/android/Using-Parceler, https://github.com/codepath/android_guides/wiki/Using-Parceler
// Autofit column: https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns

public class DetailsActivity extends AppCompatActivity {

    ImageView backdropView;
    TextView originalTitleView;
    TextView voteAverageView;
    RatingBar voteAverageBar;
    TextView releaseDateView;
    TextView overviewView;

    private int MOVIE_ID;

    static final String DETAILS = "details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        originalTitleView = findViewById(R.id.tv_original_title);
        releaseDateView = findViewById(R.id.tv_release_date);
        backdropView = findViewById(R.id.iv_detail_movie_poster);
        voteAverageView = findViewById(R.id.tv_vote_average);
        voteAverageBar = findViewById(R.id.bar_rating);
        overviewView = findViewById(R.id.tv_overview);

        // Unwrapping the Parcel, get detail movie datas
        //MovieModel MovieDetails = Parcels.unwrap(getIntent().getParcelableExtra(DETAILS));

        final MovieModel MovieDetails = (MovieModel) getIntent().getParcelableExtra(DETAILS);
        //MOVIE_ID = MovieModel.getId();


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

    }
}
