package com.example.csontosmnika.popularmovies.adapters;
        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
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

        import com.squareup.picasso.Picasso;
        import com.example.csontosmnika.popularmovies.DetailsActivity;
        import com.example.csontosmnika.popularmovies.R;
        import com.example.csontosmnika.popularmovies.QueryUtils;
        import com.example.csontosmnika.popularmovies.data.MovieContract.MovieEntry;
        import com.example.csontosmnika.popularmovies.models.MovieModel;

        import java.util.List;

        import butterknife.BindView;
        import butterknife.ButterKnife;


public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    private static final String EXTRA_MOVIE = "movie";
    private Context context;
    private Cursor cursor;

    public interface OnItemClickListener {
        void onItemClick(MovieModel item);
    }

    private final OnItemClickListener listener;

    public FavouriteAdapter(Context context, FavouriteAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_main_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouriteAdapter.ViewHolder holder, int position) {
        if (cursor == null || cursor.getCount() == 0) {
            return;
        }
        cursor.moveToPosition(position);
        String posterUriString = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_POSTER_PATH));
        String originaltitle = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_TITLE));

        holder.movieTitleView.setText(originaltitle);
        final MovieModel movie = getCurrentMovie(position);

        Picasso.with(context)
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
                showPopupMenu(view);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        if (cursor != null) {
            notifyDataSetChanged();
        }
    }


    // Define viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView moviePosterView;
        public TextView movieTitleView;
        public ImageView movieOverflowView;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cv_movie_item);
            moviePosterView = itemView.findViewById(R.id.iv_movie_poster);
            movieTitleView = itemView.findViewById(R.id.tv_movie_title);
            movieOverflowView = itemView.findViewById(R.id.iv_overflow);



        }

    }




//    public class ViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.iv_movie_poster) ImageView posterIv;
//        @BindView(R.id.tv_original_title) TextView OriginalTitle;
//
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//
//           /* itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, DetailsActivity.class);
//                    Movie movie = getCurrentMovie(getAdapterPosition());
//                    intent.putExtra(EXTRA_MOVIE, movie);
//                    context.startActivity(intent);
//                }
//            });*/
//        }
//    }

    private MovieModel getCurrentMovie(int adapterPosition) {

        cursor.moveToPosition(adapterPosition);
        MovieModel movie = new MovieModel();
        String movieId = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_ID));
        movie.setId(Integer.valueOf(movieId));

        String movieTitle = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_TITLE));
        movie.setOriginalTitle(movieTitle);

        String moviePosterUriString = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_POSTER_PATH));
        movie.setPosterPath(moviePosterUriString);

        String movieBackgroundUriString = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_BACKDROP_PATH));
        movie.setBackdropPath(movieBackgroundUriString);

        String movieReleaseData = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_RELEASE_DATE));
        movie.setReleaseDate(movieReleaseData);

        String movieRate = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_USER_RATING));
        movie.setVoteAverage(Float.valueOf(movieRate));

        String movieOverview = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_OVERVIEW));
        movie.setOverview(movieOverview);

        return movie;
    }


    // Showing popup menu when tapping on 3 dots
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_movie, popup.getMenu());
        popup.setOnMenuItemClickListener(new FavouriteAdapter.MyMenuItemClickListener());
        popup.show();
    }

    // Click listener for popup menu items
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(context, "Here will be the Favourite menu", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }


}
