package com.rebecasarai.mysoulmate.Views.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.rebecasarai.mysoulmate.Models.Screenshot;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Views.PhotoHolder;
import com.rebecasarai.mysoulmate.Views.RecyclerItemClickListener;
import com.squareup.picasso.Picasso;

public class PhotoAdapter extends FirebaseRecyclerAdapter<Screenshot, PhotoHolder> {


    private RecyclerItemClickListener mRecyclerItemClickListener;

    public PhotoAdapter(FirebaseRecyclerOptions<Screenshot> options, RecyclerItemClickListener recyclerItemClickListener) {
        super(options);
        mRecyclerItemClickListener = recyclerItemClickListener;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        // Called when the necessary data has been retrieved (may be of use when using a loading spinner)
    }

    @Override
    public void onError(DatabaseError error) {
        super.onError(error);
        // Couldn't retrieve the data, update UI accordingly
    }

    @Override
    protected void onBindViewHolder(PhotoHolder holder, int position, Screenshot model) {
        Picasso.with(holder.itemView.getContext()).load(model.getImageURL()).into(holder.mSoulmateImage);
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);

        int height = parent.getMeasuredHeight() / 4;
       // int width = parent.getMeasuredWidth() / 4;
        itemView.setMinimumHeight(height);
       // itemView.setMinimumWidth(width);
        return new PhotoHolder(itemView, mRecyclerItemClickListener);
    }
}
