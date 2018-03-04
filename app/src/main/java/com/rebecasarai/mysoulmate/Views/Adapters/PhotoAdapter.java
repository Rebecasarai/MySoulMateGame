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

/**
 * Clase Adaptador de las fotos, usando FirebaseRecyclerAdapater
 */
public class PhotoAdapter extends FirebaseRecyclerAdapter<Screenshot, PhotoHolder> {


    private RecyclerItemClickListener mRecyclerItemClickListener;

    public PhotoAdapter(FirebaseRecyclerOptions<Screenshot> options, RecyclerItemClickListener recyclerItemClickListener) {
        super(options);
        mRecyclerItemClickListener = recyclerItemClickListener;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }

    @Override
    public void onError(DatabaseError error) {
        super.onError(error);
    }

    @Override
    protected void onBindViewHolder(PhotoHolder holder, int position, Screenshot model) {
        Picasso.with(holder.itemView.getContext()).load(model.getImageURL()).into(holder.mSoulmateImage);
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);

        int height = parent.getMeasuredHeight() / 4;
        itemView.setMinimumHeight(height);
        return new PhotoHolder(itemView, mRecyclerItemClickListener);
    }
}
