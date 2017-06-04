package com.udacity.android.tmdb;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import info.movito.themoviedbapi.model.Reviews;

public class MyReviewRecyclerViewAdapter extends RecyclerView.Adapter<MyReviewRecyclerViewAdapter.ViewHolder> {

    private List<Reviews> mReviews;

    public MyReviewRecyclerViewAdapter() {
        super();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mReview = mReviews.get(position);
        holder.mIdView.setText(mReviews.get(position).getAuthor());
        holder.mContentView.setText(mReviews.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews != null ? mReviews.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Reviews mReview;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


    public void setReviews(List<Reviews> reviews) {

        if (mReviews == null) {
            mReviews = reviews;
            this.notifyDataSetChanged();
        }
        else {
            mReviews.addAll(reviews);
            this.notifyItemRangeInserted(mReviews.size(), reviews.size());
        }
    }

}
