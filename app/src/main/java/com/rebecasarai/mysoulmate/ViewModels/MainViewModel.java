package com.rebecasarai.mysoulmate.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Repository.UserRepository;

/**
 * Created by macbookpro on 28/1/18.
 */

public class MainViewModel extends AndroidViewModel {
    private UserRepository mRepository;
    private MutableLiveData<Bitmap> mLastSoulMate;
    private MutableLiveData<Boolean> mHeartButtonVisibility = new MutableLiveData<>();
    private LiveData<Bitmap> ultimo;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserRepository();
 //       Bitmap icon = BitmapFactory.decodeResource(application.getApplicationContext().getResources(), R.drawable.ic_favorite_red_24dp);
//        mLastSoulMate.postValue(icon);
    }

    public MutableLiveData<Boolean> getHeartButtonVisibility() {
        return mHeartButtonVisibility;
    }

    public void setHeartButtonVisibility(Boolean mHeartButtonVisibility) {
        this.mHeartButtonVisibility.postValue(mHeartButtonVisibility);
    }

    public MutableLiveData<Bitmap> getLastSoulMate() {
        return mLastSoulMate;
    }

    public void setLastSoulMate(Bitmap mLastSoulMate) {
        this.mLastSoulMate.postValue(mLastSoulMate);
    }

    public LiveData<Bitmap> getUltimo() {
        return ultimo;
    }

    public void setUltimo(LiveData<Bitmap> ultimo) {
        this.ultimo = ultimo;
    }
}
