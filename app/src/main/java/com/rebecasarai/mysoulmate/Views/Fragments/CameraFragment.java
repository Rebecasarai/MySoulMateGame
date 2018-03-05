package com.rebecasarai.mysoulmate.Views.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.rebecasarai.mysoulmate.Repository.FileManager;
import com.rebecasarai.mysoulmate.Views.CameraPreview;
import com.rebecasarai.mysoulmate.Views.GraphicOverlay;
import com.rebecasarai.mysoulmate.Graphics.FaceGraphic;
import com.rebecasarai.mysoulmate.Models.Heart;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Utils.ScreenshotType;
import com.rebecasarai.mysoulmate.Utils.ScreenshotUtils;
import com.rebecasarai.mysoulmate.Utils.ExifUtils;
import com.rebecasarai.mysoulmate.Utils.Utils;
import com.rebecasarai.mysoulmate.ViewModels.MainViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Fragmento en el que se crea el recurso camara para detectar caras, mostrar filtros y reproducir musica al capturar almas gemelas.
 * La camara se detine al navegar a otros fragmentos del ViewPager y se restaura al volver.
 * Se utiliza tracking facial para identificar un rostro y mientras lo tenga en camara mostrar el filtro y poder capturarlo.
 * Se piden y verifican permisos de camra y almacenamiento.
 * Se toman capturas de pantallas para tomar foto de los efectos.
 */
public class CameraFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = CameraFragment.class.getSimpleName();

    private CameraSource mCameraSource = null;
    private CameraPreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private View mRootView;
    private MediaPlayer mediaPlayer;
    private ImageView mPhotoPeep;
    private ImageButton mCatchSoulMateButton;
    private Bitmap mBitmapPicture;
    private FaceDetector detector;
    private MainViewModel mViewmodel;



    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(com.rebecasarai.mysoulmate.R.layout.fragment_camera, container, false);
        mPreview = (CameraPreview) mRootView.findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) mRootView.findViewById(R.id.faceOverlay);
        mViewmodel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        int rc = ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();

        } else {
            requestCameraPermission();
        }

        mediaPlayer = MediaPlayer.create(getContext(), R.raw.rebecatech);
        mPhotoPeep = (ImageView) mRootView.findViewById(R.id.photoPerson);

        mCatchSoulMateButton = (ImageButton) mRootView.findViewById(R.id.catchSoulMateButton);
        mCatchSoulMateButton.setVisibility(View.INVISIBLE);
        mCatchSoulMateButton.setOnClickListener(this);


        mViewmodel.getHeartButtonVisibility().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    mCatchSoulMateButton.setVisibility(View.VISIBLE);
                } else {
                    mCatchSoulMateButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        mViewmodel.getActivarCameraFragmentLive().observe(this, new Observer<Boolean>() {
            public void onChanged(@Nullable Boolean aBoolean) {
                if(!aBoolean){
                    mPreview.stop();
                }else{
                    startCameraSource();
                }
            }
        });

        return mRootView;
    }


    /*--------------------------------
            Override methods
     ---------------------------------*/

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTheme(R.style.AppTheme_NoActionBar);
        mCatchSoulMateButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPreview.stop();
        mediaPlayer.stop();
    }

    /**
     * Libera recursos, la cámara, el face detector
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }

        mediaPlayer.stop();
        mediaPlayer.release();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.catchSoulMateButton:
                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(final byte[] bytes) {
                        Log.v(TAG, "Foto Tomada.");
                        takeSnapshot(bytes);
                        setBitmap();
                    }
                });
                break;

        }
    }


    /*--------------------------------
                Face Methods
     ---------------------------------*/


    /**
     * Fábrica para crear el tracker de cara para asociarlo con una nueva cara. El multiprocesador usa esta fábrica para crear rastreadores de caras según sea necesario, uno para cada persona.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Tracker para cada individuo detectado. Esto mantiene un gráfico de cara dentro de la superposición de cara asociada a la aplicación.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;

            if (Utils.isYourSoulMate(getContext())) {

                ArrayList<Heart> hearts = new ArrayList<>();
                //top
                hearts.add(new Heart(5, 5, 5, 10));
                //right
                hearts.add(new Heart(1100, 100, 10, 10));
                //bottom
                hearts.add(new Heart(200, 1300, 10, 10));
                //left
                hearts.add(new Heart(20, 400, 10, 10));


                mFaceGraphic = new FaceGraphic(overlay, getContext(),hearts);

                mViewmodel.setHeartButtonVisibility(true);
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = MediaPlayer.create(getContext(), R.raw.rebecatech);
                    }
                    mediaPlayer.start();
                } catch (Exception e) {

                    Log.d(TAG,"Error en el graphic: "+e);
                }
            }

        }



        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            if (mFaceGraphic != null) {
                mOverlay.add(mFaceGraphic);
                mFaceGraphic.updateFace(face);
            }
        }

        /**
         * Oculta el gráfico cuando no se detecta la cara por unos milisegundos.
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            if (mFaceGraphic != null) {
                mOverlay.remove(mFaceGraphic);
            }
        }

        /**
         * Llamado cuando se supone que la cara no se detecta más después de unos segundos, la oculta de forma definitiva. Elimina el gráfica del overlay.
         * Pone fin a esa detección facial.
         */
        @Override
        public void onDone() {

            if (mFaceGraphic != null) {
                mOverlay.remove(mFaceGraphic);
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = MediaPlayer.create(getContext(), R.raw.rebecatech);
                        mViewmodel.setHeartButtonVisibility(false);
                    }

                } catch (Exception e) {

                    Log.d(TAG,"Error en el done: "+e);
                }
            }


        }
    }


    /*--------------------------------
             Pemmission Methods
     ---------------------------------*/


    /**
     * Metodo calback de los permisos otorgados
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.d(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //finish();
                //getActivity().getFragmentManager().beginTransaction().remove(this).commit();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Face")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Maneja la solicitud del permiso de la cámara. Muestra un mensaje de
     * Snackbar
     */
    private void requestCameraPermission() {

        final String[] permissions = new String[]{android.Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                android.Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final FragmentActivity thisActivity = getActivity();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();

    }




    /*--------------------------------
                Other Methods
     ---------------------------------*/


    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {


        Context context = getContext();
        detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .setMaxGapFrames(0)
                        .build());


        if (!detector.isOperational()) {

            Log.d(TAG, "Detector de caras no funciona");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                //.setRequestedFps(15.0f)
                .build();

    }


    /**
     * Metodo que captura la foto, llamado desde CameraSource.
     *
     * @param bytes Bytes de la imagen tomada.
     */
    private void takeSnapshot(byte[] bytes) {

        setOrientacion(bytes);
        saveImage(mBitmapPicture, getPhotoTime());

    }

    /**
     * Establece la orientaciñon adecuada de una imagen capturada, para enderzarla si esta volteada.
     * si el dispositivo tiene por default camara Land Horizontal y toma las fotos volteadas
     * como es el caso de algunos Samsung
     * @param bytes representan los bytes de la imagen
     */
    private void setOrientacion(byte[] bytes){

        int orientation = ExifUtils.getOrientation(bytes);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        switch (orientation) {
            case 90:
                mBitmapPicture = rotateImage(bitmap, 90);
                break;
            case 180:
                mBitmapPicture = rotateImage(bitmap, 180);
                break;
            case 270:
                mBitmapPicture = rotateImage(bitmap, 270);
                break;

            default:
                mBitmapPicture = bitmap;
                break;
        }
    }

    /**
     * Rota un bitmap
     * @param source representa el bitmap
     * @param angle el angulo a voltear
     * @return
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    /**
     * Plasma el bitmap y captura el conjunto de vistas para establecer la imagen de alma gemela
     */
    public void setBitmap() {
        mPhotoPeep.setImageBitmap(mBitmapPicture);
        takeScreenshot(ScreenshotType.FULL);
        mPhotoPeep.setVisibility(View.INVISIBLE);
    }


    /**
     * Fecha actual
     *
     * @return String con fecha actual
     */
    private String getPhotoTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy_hhmmss");
        return sdf.format(new Date());
    }

    /**
     * Inicia o reinicia la fuente de la cámara, si existe. Si la fuente de la cámara aún no existe
     * (Como Porque se invocó a OnResume antes de que se creara la fuente de la cámara), se volverá a
     * llamar cuando se cree la camerasource.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getView().getContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    /**
     * Método que tomará una captura de pantalla en base al tipo de captura de pantalla ENUM
     */
    private void takeScreenshot(ScreenshotType screenshotType) {
        Bitmap bitmap = null;
        File mScreenShotFile;

        switch (screenshotType) {
            case FULL:
                bitmap = ScreenshotUtils.getScreenShot(mPreview);
                Log.d(TAG,"Bitmap "+bitmap.toString());
                break;
            case CUSTOM:

                break;
        }

        if (bitmap != null) {

            FileManager.uploadScreenshot(FirebaseAuth.getInstance(), bitmap, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getActivity(), "Image subida.", Toast.LENGTH_SHORT).show();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Fallo al subir imagen.", Toast.LENGTH_SHORT).show();
                }
            });

            File saveFile = ScreenshotUtils.getMainDirectoryName(mRootView.getContext());
            mScreenShotFile = ScreenshotUtils.store(bitmap, "screenshot" + screenshotType + ".jpg", saveFile);//save the screenshot to selected path

            Uri uri = Uri.fromFile(mScreenShotFile);

            mViewmodel.setLastSoulMate(bitmap);
            mViewmodel.setUrlDeUltimoBitmap(uri);

        } else {
            Toast.makeText(getContext(), R.string.screenshot_take_failed, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Guarda imagen en el almacenamiento externo del dispositivo
     * @param finalBitmap
     * @param image_name
     */
    private void saveImage(Bitmap finalBitmap, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.d("LOAD", root + fname);


        if(isStoragePermissionGranted()){
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    /**
     * Verifica si han dado permisos
     * @return Booleano que representa si se han dado permisos
     */
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG,"Permiso concedido");
                return true;
            } else {

                Log.d(TAG,"Permiso revocado");
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            Log.v(TAG,"Permiso concedido");
            return true;
        }
    }


}



