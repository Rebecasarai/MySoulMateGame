package com.rebecasarai.mysoulmate.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.rebecasarai.mysoulmate.R;

/**
 * Clase con el ViewModel de la aplicación.
 * Obtiene el ultimo soul mate recien capturado en ese momento, por la cámara. Ya que Firebase tarda un tiempo en recargar desde almacenamiento.
 * Permite manejar un booleano la visibilidad del boton para capturar alma gemela, solo cuando detecta ese rostro.
 * Dependiedno del fragmento en el que este, pausa o reinicia la camara de la app.
 * Ultima Url de una imagen alma gemela.
 */
public class MainViewModel extends AndroidViewModel {

    private MutableLiveData<Bitmap> mLastSoulMate = new MutableLiveData<>();
    private MutableLiveData<Boolean> mHeartButtonVisibility = new MutableLiveData<>();
    private MutableLiveData<Bitmap> mUltimo = new MutableLiveData<>();
    private MutableLiveData<Boolean> mActivarCameraFragment = new MutableLiveData<>();
    private MutableLiveData<Uri> mUrlDeUltimoBitmap = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        Bitmap icon = BitmapFactory.decodeResource(application.getApplicationContext().getResources(), R.drawable.ic_favorite_red_24dp);
        mUltimo.postValue(icon);
        mActivarCameraFragment.setValue(true);
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
        this.mActivarCameraFragment.setValue(activarCameraFargment);
    }

    public MutableLiveData<Boolean> getActivarCameraFragmentLive() {
        return mActivarCameraFragment;
    }


    public Uri getUrlDeUltimoBitmap() {
        return mUrlDeUltimoBitmap.getValue();
    }

    public void setUrlDeUltimoBitmap(Uri urlDeUltimoBitmap) {
        this.mUrlDeUltimoBitmap.setValue(urlDeUltimoBitmap);
    }
}
