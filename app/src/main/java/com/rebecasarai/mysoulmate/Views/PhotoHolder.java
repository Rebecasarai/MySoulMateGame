package com.rebecasarai.mysoulmate.Views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.rebecasarai.mysoulmate.R;

/**
 * Created by rebecagonzalez on 8/2/18.
 */

public class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public ImageView mSoulmateImage;
    public RecyclerItemClickListener recyclerItemClickListener;

    public PhotoHolder(View itemView, RecyclerItemClickListener recyclerItemClickListener) {
        super(itemView);
        mSoulmateImage = itemView.findViewById(R.id.soulmateImage);
        this.recyclerItemClickListener = recyclerItemClickListener;
        mSoulmateImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        recyclerItemClickListener.OnItemClick(getAdapterPosition(), view);
    }
}
