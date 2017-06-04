package com.udacity.android.tmdb;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.android.tmdb.TrailerFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.Video;

public class MyTrailerRecyclerViewAdapter extends RecyclerView.Adapter<MyTrailerRecyclerViewAdapter.ViewHolder> {

    private List<Video> mVideos;

    private final OnListFragmentInteractionListener mListener;

    public MyTrailerRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mVideo = mVideos.get(position);
        // holder.mIdView.setText(mVideos.get(position).getType());
        // holder.mContentView.setText(mVideos.get(position).getKey());
        holder.mContentView.setText(mVideos.get(position).getType() + " " +(position+1));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mVideo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideos != null ? mVideos.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Video mVideo;

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

    public void setVideo(Video video) {

        if (mVideos == null) {
            mVideos = new ArrayList<>(1);
        }
        // to avoid duplicate
        if (!mVideos.contains(video))
            mVideos.add(video);
        this.notifyItemRangeInserted(mVideos.size(), 1);
    }

    public List<Video> getVideos() {
        return mVideos;
    }
}
