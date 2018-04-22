package com.example.csontosmnika.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.csontosmnika.popularmovies.R;
import com.example.csontosmnika.popularmovies.models.ReviewModel;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position, ReviewModel review, CardView textView);
    }

    private Context context;
    private List<ReviewModel> reviews;
    private OnItemClickListener listener;

    public ReviewAdapter(List<ReviewModel> reviews) {
        this.reviews = reviews;
    }

    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, viewGroup, false);

        return new ReviewAdapterViewHolder(view);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final ReviewAdapterViewHolder holder, int position) {
        ReviewModel review = reviews.get(position);

        holder.authorView.setText(review.getAuthor());
        holder.contentView.setText(review.getContent());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        if (null == reviews) return 0;
        if (reviews.size() > 3)
            return 3;
        else
            return reviews.size();
    }

    // Define viewholder
    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardView;
        TextView authorView;
        TextView contentView;

        public ReviewAdapterViewHolder(final View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cv_review_item);
            authorView = itemView.findViewById(R.id.tv_review_author);
            contentView = itemView.findViewById(R.id.tv_review_content);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int pos = getAdapterPosition();
                ReviewModel review = reviews.get(pos);
                listener.onItemClick(pos, review, cardView);
            }
        }
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        this.listener = listener;
    }

    // Helper method to set the actual review list into the recyclerview on the activity
    public void setReviewList(List<ReviewModel> reviewList) {
        reviews = reviewList;
        notifyDataSetChanged();
    }
}