package com.example.csontosmnika.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.csontosmnika.popularmovies.R;
import com.example.csontosmnika.popularmovies.models.TrailerModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position, TrailerModel trailer, ImageView imageView);
    }

    private Context context;
    private List<TrailerModel> trailers;
    private OnItemClickListener listener;

    public TrailerAdapter(List<TrailerModel> trailers) {
        this.trailers = trailers;
    }

    @Override
    public TrailerAdapter.TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_item, viewGroup, false);

        return new TrailerAdapterViewHolder(view);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final TrailerAdapterViewHolder holder, int position) {
        TrailerModel trailer = trailers.get(position);

        String nameString = String.valueOf(trailer.getName());
        String thumbnailUrl = "http://img.youtube.com/vi/" + trailer.getKey() + "/1.jpg";
        Picasso.with(holder.itemView.getContext())
                .load(thumbnailUrl)
                .placeholder(R.drawable.star_off) //todo placeholder image
                .into(holder.trailerView);
        holder.trailerName.setText(nameString);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        if (null == trailers) return 0;
        return trailers.size();
    }

    // Define viewholder
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardView;
        ImageView trailerView;
        TextView trailerName;

        public TrailerAdapterViewHolder(final View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cv_trailer_item);
            trailerView = itemView.findViewById(R.id.iv_trailer);
            trailerName = itemView.findViewById(R.id.tv_trailer_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int pos = getAdapterPosition();
                TrailerModel trailer = trailers.get(pos);
                listener.onItemClick(pos, trailer, trailerView);
            }
        }
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        this.listener = listener;
    }

    // Helper method to set the actual trailer list into the recyclerview on the activity
    public void setTrailerList(List<TrailerModel> trailerList) {
        trailers = trailerList;
        notifyDataSetChanged();
    }
}