package com.example.csontosmnika.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csontosmnika.popularmovies.DetailsActivity;
import com.example.csontosmnika.popularmovies.R;
import com.example.csontosmnika.popularmovies.data.AddFavourite;
import com.example.csontosmnika.popularmovies.models.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.csontosmnika.popularmovies.TheMovieDbApi.TheMovieApiDbConstants.page;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MovieModel item);
    }

    private Context mContext;
    private List<MovieModel> mMovies;
    private final OnItemClickListener listener;

    // Define viewholder
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView moviePosterView;
        public TextView movieTitleView;
        public ImageView movieOverflowView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cv_movie_item);
            moviePosterView = itemView.findViewById(R.id.iv_movie_poster);
            movieTitleView = itemView.findViewById(R.id.tv_movie_title);
            movieOverflowView = itemView.findViewById(R.id.iv_overflow);
        }

    }


    public MovieAdapter(Context context, List<MovieModel> movies, OnItemClickListener listener) {
        this.mContext = context;
        this.mMovies = movies;
        this.listener = listener;
    }

    @Override
    public MovieAdapter.MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.activity_main_item, viewGroup, false);

        return new MovieAdapterViewHolder(view);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        final MovieModel movie = mMovies.get(position);
        String originalTitle = String.valueOf(movie.getOriginalTitle());
        String voteString = String.valueOf(movie.getVoteAverage());

        holder.movieTitleView.setText(originalTitle);

        Picasso.with(mContext)
                .load(movie.getImageUriString())
                .into(holder.moviePosterView);


        holder.moviePosterView.setOnClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {
                                                          listener.onItemClick(movie);
                                                      }
                                                  }
        );


        holder.movieTitleView.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {
                                                         listener.onItemClick(movie);
                                                     }
                                                 }
        );


        holder.movieOverflowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, movie.getId());
            }
        });


    }


    // Returns the total count of items
    @Override
    public int getItemCount() {
        if (null == mMovies) return 0;
        return mMovies.size();
    }

    // Set the actual movie list into the recyclerview on the activity
    public void setMovieList(List<MovieModel> movieList) {
        if (mMovies == null || page == 1) {
            mMovies = new ArrayList<>();
        }
        if (movieList != null) {
            mMovies.addAll(movieList);
        }

        notifyDataSetChanged();
    }

    // Showing popup menu when tapping on 3 dots
    private void showPopupMenu(View view, int MovieId) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_movie, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(MovieId));
        popup.show();
    }

    // Click listener for popup menu items
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int movieId;

        public MyMenuItemClickListener(int MovieId) {
            movieId = MovieId;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    if (AddFavourite.isFavoriteMovie(movieId, mContext)) {
                        Toast.makeText(mContext, "Here will be the Favourite menu", Toast.LENGTH_SHORT).show();
                    }
                    else
                       if (AddFavourite.addMovieToFavorites(MovieDetails, getContentResolver())) {
                        Toast.makeText(MovieAdapter.this, getString(R.string.editor_insert_movie_successful),
                                Toast.LENGTH_SHORT).show();
                       }

                    return true;
                default:
            }
            return false;
        }
    }

}
