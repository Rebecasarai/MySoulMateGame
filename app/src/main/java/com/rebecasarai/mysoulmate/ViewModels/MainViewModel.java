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
    public MutableLiveData<Boolean> getmHeartButtonVisibility() {
        return mHeartButtonVisibility;
    }

    public void setmHeartButtonVisibility(MutableLiveData<Boolean> mHeartButtonVisibility) {
        this.mHeartButtonVisibility = mHeartButtonVisibility;

    }

    public void setmHeartButtonVisibilityValue(Boolean mHeartButtonVisibility) {
        this.mHeartButtonVisibility.setValue(mHeartButtonVisibility);
    }

    public Boolean getmHeartButtonVisibilityValue() {
        return mHeartButtonVisibility.getValue();
    }

    public LiveData<Bitmap> getmLastSoulMate() {
        return mLastSoulMate;
    }

    public void setmLastSoulMate(LiveData<Bitmap> mLastSoulMate) {
        this.mLastSoulMate = mLastSoulMate;
    }
}
