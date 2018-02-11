package com.rebecasarai.mysoulmate.ViewModels;

import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;

import com.rebecasarai.mysoulmate.Repository.UserRepository;

/**
 * Created by macbookpro on 28/1/18.
 */

public class MainViewModel extends ViewModel {
    private UserRepository mRepository;
    private Bitmap mLastSoulMate;


    public MainViewModel() {
        mRepository = new UserRepository();

    }



}
