package com.rebecasarai.mysoulmate.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.rebecasarai.mysoulmate.R;


public class MainViewModel extends AndroidViewModel {

    private MutableLiveData<Bitmap> mLastSoulMate = new MutableLiveData<>();
    private MutableLiveData<Boolean> mHeartButtonVisibility = new MutableLiveData<>();
    private MutableLiveData<Bitmap> ultimo = new MutableLiveData<>();
    private MutableLiveData<Boolean> activarCameraFargment = new MutableLiveData<>();
    private MutableLiveData<Uri> urlDeUltimoBitmap = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        Bitmap icon = BitmapFactory.decodeResource(application.getApplicationContext().getResources(), R.drawable.ic_favorite_red_24dp);
        ultimo.postValue(icon);
        activarCameraFargment.setValue(true);
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
        this.mLastSoulMate.setValue(mLastSoulMate);
    }


    public void setActivarCameraFargment(Boolean activarCameraFargment) {
        this.activarCameraFargment.setValue(activarCameraFargment);
    }

    public MutableLiveData<Boolean> getActivarCameraFragmentLive() {
        return activarCameraFargment;
    }


    public Uri getUrlDeUltimoBitmap() {
        return urlDeUltimoBitmap.getValue();
    }

    public void setUrlDeUltimoBitmap(Uri urlDeUltimoBitmap) {
        this.urlDeUltimoBitmap.setValue(urlDeUltimoBitmap);
    }
}
