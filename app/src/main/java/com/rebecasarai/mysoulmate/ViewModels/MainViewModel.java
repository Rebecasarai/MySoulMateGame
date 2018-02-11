package com.rebecasarai.mysoulmate.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;

import com.rebecasarai.mysoulmate.Repository.UserRepository;

/**
 * Created by macbookpro on 28/1/18.
 */

public class MainViewModel extends ViewModel {
    private UserRepository mRepository;
    private LiveData<Bitmap> mLastSoulMate;
    private MutableLiveData<Boolean> mHeartButtonVisibility = new MutableLiveData<>();


    public MainViewModel() {
        mRepository = new UserRepository();
    }

    public MutableLiveData<Boolean> getHeartButtonVisibility() {
        return mHeartButtonVisibility;
    }

    public void setHeartButtonVisibility(Boolean mHeartButtonVisibility) {
        this.mHeartButtonVisibility.postValue(mHeartButtonVisibility);
    }

    public LiveData<Bitmap> getLastSoulMate() {
        return mLastSoulMate;
    }

    public void setLastSoulMate(LiveData<Bitmap> mLastSoulMate) {
        this.mLastSoulMate = mLastSoulMate;
    }
}
