package com.example.csontosmnika.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.csontosmnika.popularmovies.utils.EndlessScrollListener;
import com.example.csontosmnika.popularmovies.utils.ScreenColumnCalculator;

//import org.parceler.Parcels;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

// link to themoviedb.org
import static com.example.csontosmnika.popularmovies.TheMovieDbApi.TheMovieApiDbConstants.POPULAR_MOVIES;
import static com.example.csontosmnika.popularmovies.TheMovieDbApi.TheMovieApiDbConstants.TOP_RATED_MOVIES;
import static com.example.csontosmnika.popularmovies.TheMovieDbApi.TheMovieApiDbConstants.page;


// Used guidelines:
// Android Card View and Recycler View - Delaroy Studios video, https://www.youtube.com/watch?v=7Fe1jigV5Qs, https://github.com/delaroy/AndroidCardView
// Endless Scroll https://gist.github.com/pratikbutani/dc6b963aa12200b3ad88aecd0d103872 and Carlos (Udacity mentor)
// Parceler:
// https://guides.codepath.com/android/Using-Parceler,
// https://github.com/codepath/android_guides/wiki/Using-Parceler
// https://guides.codepath.com/android/Using-Parcelable#creating-a-parcelable-the-manual-way (Passing Data Between Intents)
// Save list element position:
// https://eliasbland.wordpress.com/2011/07/28/how-to-save-the-position-of-a-scrollview-when-the-orientation-changes-in-android/

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<MovieModel>> {


    private String THEMOVIEDB_URL;
    private static final int ID_THEMOVIEDB_LOADER = 1;

    private List<MovieModel> movies = new ArrayList<>();

    private MovieAdapter movieAdapter;
    public RecyclerView RecyclerView;
    private ProgressBar progressBar;
    private TextView emptyStateTextView;
    private TextView originalTitle;

    static final String DETAILS = "details";

    GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_main);

        //onRestoreInstanceState(savedInstanceState);

        if (savedState == null) {
            THEMOVIEDB_URL = POPULAR_MOVIES;
        }

        //Assign the views
        RecyclerView = findViewById(R.id.rv_movies);
        progressBar = findViewById(R.id.progress_bar);
        emptyStateTextView = findViewById(R.id.tv_empty_state);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        originalTitle = findViewById(R.id.tv_original_title);

        // Screen mode span settings
        int orientation = this.getResources().getConfiguration().orientation;
        int spanCount = 2;

        /*if (orientation == 2) {
            //code for landscape mode
            spanCount = 3;
        } else {
            //code for portrait mode
            spanCount = 2;
        }*/

        spanCount = ScreenColumnCalculator.calculateNoOfColumns(getApplicationContext());


        layoutManager = new GridLayoutManager(this, spanCount);

        RecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, dpToPx(4), true));
        RecyclerView.setLayoutManager(layoutManager);

        // Listener for onClick Movie

       MovieAdapter.OnItemClickListener listener = new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieModel movie) {
                Intent movieDetailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                // Wrapping Up the Parcel, put data for details
                //movieDetailsIntent.putExtra(DETAILS, Parcels.wrap(movie));
                movieDetailsIntent.putExtra(DETAILS, movie);

                //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, originalTitle, "transition");
                //ActivityCompat.startActivity(MainActivity.this, movieDetailsIntent, options.toBundle());
                startActivity(movieDetailsIntent);
            }
        };

       movieAdapter = new MovieAdapter(this, movies, listener);
        RecyclerView.setAdapter(movieAdapter);

        // Endless list
        RecyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                // Load the next page, and append it to list (page variable is incremented in EndlessScrollListener)
                loadNextDataFromApi(current_page);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        assert connMgr != null; //line suggested by Lint
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(ID_THEMOVIEDB_LOADER, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{RecyclerView.getScrollX(), RecyclerView.getScrollY()});
        outState.putString("SORT_URL", THEMOVIEDB_URL);
        outState.putString("PAGE", String.valueOf(page));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        final int[] position = savedState.getIntArray("ARTICLE_SCROLL_POSITION");
        if (position != null)
            RecyclerView.post(new Runnable() {
                public void run() {
                    RecyclerView.scrollTo(position[0], position[1]);
                }
            });

        THEMOVIEDB_URL = savedState.getString("SORT_URL");
        page = Integer.parseInt(savedState.getString("PAGE"));
    }


    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        page = offset;
        getLoaderManager().restartLoader(0, null, MainActivity.this);
    }


    // Converting dp to pixel
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    @Override
    public Loader<List<MovieModel>> onCreateLoader(int id, Bundle args) {

        return new MovieLoader(this, THEMOVIEDB_URL + page);
    }

    @Override
    public void onLoadFinished(Loader<List<MovieModel>> loader, List<MovieModel> movies) {

        //Hides the progress bar
        progressBar.setVisibility(View.GONE);

        // If the recent page is the first, clear the adapter of previous movie data. Every other case: the loader will add elements to the list (endless scroll up/down)
        if (page == 1) {
            movieAdapter.setMovieList(null);
        }
        // If there is a valid list of movies, then add them to the adapter's data set.
        if (movies != null && !movies.isEmpty()) {
            emptyStateTextView.setVisibility(View.GONE);
            movieAdapter.setMovieList(movies);
            movieAdapter.notifyDataSetChanged();

        } else {
            // Set empty state text
            emptyStateTextView.setText(R.string.no_movies);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MovieModel>> loader) {
        movieAdapter.setMovieList(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Change the dataset sort order popular or top rated
        // and load the first page of new list
        switch (item.getItemId()) {
            case R.id.popular_menu_item:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    // Popular Movies
                    page = 1;
                    THEMOVIEDB_URL = POPULAR_MOVIES + page;
                }

                getLoaderManager().restartLoader(ID_THEMOVIEDB_LOADER, null, this);
                return true;

            case R.id.top_rated_menu_item:
                if (item.isChecked()) {
                    item.setChecked(false);

                } else {
                    item.setChecked(true);

                    // Top Rated Movies
                    page = 1;
                    THEMOVIEDB_URL = TOP_RATED_MOVIES + page;
                }

                getLoaderManager().restartLoader(ID_THEMOVIEDB_LOADER, null, this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // RecyclerView item decoration
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;
                if (position < spanCount) {
                    outRect.top = spacing; // top edge
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing * spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }

    }

}
