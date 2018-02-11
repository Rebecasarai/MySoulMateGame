package com.rebecasarai.mysoulmate.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.rebecasarai.mysoulmate.Camera.CameraPreview;
import com.rebecasarai.mysoulmate.Camera.GraphicOverlay;
import com.rebecasarai.mysoulmate.Graphics.FaceGraphic;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Screenshot.ScreenshotType;
import com.rebecasarai.mysoulmate.Screenshot.ScreenshotUtils;
import com.rebecasarai.mysoulmate.Utils.ExifUtils;
import com.rebecasarai.mysoulmate.Utils.FileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
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
    private Context context;
    private ImageButton mCatchSoulMateButton;

    private Bitmap mBitmapPicture;

    private int mProbability;
    private SharedPreferences mSharedPref;


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

        //TODO: Cambiar request de permisos, tratar con on permissionRerquest etc. Usa mi clase permissionUtils
        //Chequeo permisos e inicializo la cameraSource
        int rc = ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.rebecatech);
        mPhotoPeep = (ImageView) mRootView.findViewById(R.id.photoPerson);

        mCatchSoulMateButton = (ImageButton) mRootView.findViewById(R.id.catchSoulMateButton);
        mCatchSoulMateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(final byte[] bytes) {
                        Log.v(TAG, " Foto Tomada.");
                        takeSnapshot(bytes);
                        setBitmap();
                       // new FasterScreenshotAsyncTask().execute(bytes);
                       /* new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                takeSnapshot(bytes);
                            }
                        });*/
                    }
                });

            }
        });


        return mRootView;//inflater.inflate(R.layout.fragment_camera, container, false);
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
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
     * Libera recursos, la fuente de la cámara, el face detector
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


    /**
     * Permisos
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

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
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
        Log.w(TAG, "El permiso de la cámara no se concedió");

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

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = getContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {

            Log.w(TAG, "Detector de caras no funciona");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    /**
     * Metodo que captura la foto, llamado desde CameraSource.
     *
     * @param bytes Bytes de la imagen tomada.
     */
    private void takeSnapshot(byte[] bytes) {
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



        try {
            File mainDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "");
            Log.v("dir", mainDir.getAbsolutePath());
            if (!mainDir.exists()) {
                if (mainDir.mkdir())
                    Log.e("Create Directory", "Main Directory Created: " + mainDir);
            }
            File captureFile = new File(mainDir, "prueba" + getPhotoTime() + ".png");
            if (!captureFile.exists())
                Log.d("CAPTURE_FILE_PATH", captureFile.createNewFile() ? "Success" : "Failed");

            FileOutputStream stream = new FileOutputStream(captureFile);
            //mBitmapPicture.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            Log.v("dir", captureFile.getAbsolutePath());
            stream.write(bytes);
            stream.flush();
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void setBitmap(){
        mPhotoPeep.setImageBitmap(mBitmapPicture);
        takeScreenshot(ScreenshotType.FULL);
    }


    /**
     * Fecha actual
     *
     * @return
     */
    private String getPhotoTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy_hhmmss");
        return sdf.format(new Date());
    }

    /**
     * Inicia o reinicia la fuente de la cámara, si existe. Si la fuente de la cámara aún no existe
     * (Como Porque se invocó a OnResume antes de que se creara la fuente de la cámara), se volverá a
     * llamar cuando se cree la fuente de la cámara.
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.catchSoulMateButton:
                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        Log.v(TAG, " Foto tomada.");
                        takeSnapshot(bytes);
                    }
                });
                break;

        }
    }


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

            int numMax = 60;
            int fotosTomadas = 40;


            //TODO: Probability, if(random((MAX)) - FotosTomadas del Shared == 0)
            //TODO: Tener dos variables de totalDeFotos y otra para la prob
            int mProbability = new Random().nextInt(50);
            if(mProbability >= 35) {
                mFaceGraphic = new FaceGraphic(overlay, getContext(), 1);
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = MediaPlayer.create(getContext(), R.raw.rebecatech);
                    }

                    mediaPlayer.start();
                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);

                }
            }

        }


        /**
         * Comienza a trackear dentro del overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
            //TODO:

        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        /**
         * Oculta el gráfico cuando no se detectó la cara.
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
            Log.v("missing", "");
        }

        /**
         * Llamado cuando se supone que la cara no se detecta definitvamente. Elimina el gráfica del overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
            Log.v("done", "");
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = MediaPlayer.create(getContext(), R.raw.rebecatech);
                }

            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);

            }
        }
    }

    /**
     * Método que tomará una captura de pantalla en base al tipo de captura de pantalla ENUM
     */
    private void takeScreenshot(ScreenshotType screenshotType) {
        Bitmap bitmap = null;

        switch (screenshotType) {
            case FULL:
                bitmap = ScreenshotUtils.getScreenShot(mPreview);
                break;
            case CUSTOM:
                //Puedo hacer invisible o visible lo que me parezca
                break;
        }

        //Si el bitmap no es nulo
        if (bitmap != null) {

            //TODO: pasarlo al repository
            FileManager.uploadScreenshot(FirebaseAuth.getInstance(), bitmap, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //TODO: maybe say something else
                    Toast.makeText(getActivity(), "Image uploaded.", Toast.LENGTH_SHORT).show();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //TODO: treat
                    Toast.makeText(getActivity(), "Uploading failed.", Toast.LENGTH_SHORT).show();
                }
            });

            File saveFile = ScreenshotUtils.getMainDirectoryName(getContext());//el directoria para guardar
            File file = ScreenshotUtils.store(bitmap, "screenshot" + screenshotType + ".jpg", saveFile);//save the screenshot to selected path
            shareScreenshot(file);

        } else {
            //Si es nulo
            Toast.makeText(getContext(), R.string.screenshot_take_failed, Toast.LENGTH_SHORT).show();
        }

    }


    private void shareScreenshot(File file) {
        Uri uri = Uri.fromFile(file);//Convert file path into Uri for sharing
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.sharing_text));
        intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
    }


    public class FasterScreenshotAsyncTask extends AsyncTask<byte[], Void, Void>{



        @Override
        protected Void doInBackground(byte[]... bytes) {
            takeSnapshot(bytes[0]);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setBitmap();
        }


    }


    public void setProbability(){
        mSharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt("prob", 60);
        editor.commit();

    }


    public int getProbability(){
        mSharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        //int prob = getResources().getInteger("s");
        int prob = 5;
        return prob;

    }


}



