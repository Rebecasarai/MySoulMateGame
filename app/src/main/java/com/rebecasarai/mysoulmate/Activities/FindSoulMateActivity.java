/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rebecasarai.mysoulmate.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.rebecasarai.mysoulmate.FileManager;
import com.rebecasarai.mysoulmate.Graphics.FaceGraphic;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Screenshot.ScreenshotType;
import com.rebecasarai.mysoulmate.Screenshot.ScreenshotUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class FindSoulMateActivity extends AppCompatActivity {
    private static final String TAG = "FaceTracker";

    private CameraSource mCameraSource = null;

    private CameraPreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    View rootView;

    MediaPlayer mediaPlayer;

    ImageView mPhotoPeep;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_find_soul_mate);

        rootView = getWindow().getDecorView().findViewById(R.id.topLayout);

        mPreview = (CameraPreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Chequeo permisos e inicializo la cameraSource
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.rebecatech);


    }


    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
        mediaPlayer.stop();
    }

    /**
     * Libera recursos, la fuente de la cámara, el face detector
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }

        mediaPlayer.stop();
        mediaPlayer.release();
    }


    /**
     * Permisos
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
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }


        final Activity thisActivity = this;

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

        Context context = getApplicationContext();
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
                //.setRequestedPreviewSize(640, 480)
                //.setRequestedPreviewSize(940, 480)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    takeScreenshot(ScreenshotType.FULL);

                    return true;
                case R.id.navigation_dashboard:

                    mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] bytes) {
                            Log.v("foto", "tomada");
                            capturar(bytes);

                        }

                        private void capturar(byte[] bytes) {

                            try {

                                File mainDir = new File(
                                        getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "prueba");

                                if (!mainDir.exists()) {
                                    if (mainDir.mkdir())
                                        Log.e("Create Directory", "Main Directory Created : " + mainDir);
                                }
                                File captureFile = new File(mainDir + "prueba" + getPhotoTime() + ".png");
                                if (!captureFile.exists())
                                    Log.d("CAPTURE_FILE_PATH", captureFile.createNewFile() ? "Success" : "Failed");
                                FileOutputStream stream = new FileOutputStream(captureFile);

                                stream.write(bytes);

                                stream.flush();
                                stream.close();

                                Bitmap bitmap = BitmapFactory.decodeFile(captureFile.getAbsolutePath());

                                mPhotoPeep.setImageBitmap(bitmap);
                                mPhotoPeep.setRotation(90);

                                //shareScreenshot(captureFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            takeScreenshot(ScreenshotType.FULL);
                        }

                        private String getPhotoTime(){
                            SimpleDateFormat sdf=new SimpleDateFormat("ddMMyy_hhmmss");
                            return sdf.format(new Date());
                        }
                    });

                    return true;
                case R.id.navigation_notifications:


                    return true;
            }
            return false;
        }
    };

    /**
     * Inicia o reinicia la fuente de la cámara, si existe. Si la fuente de la cámara aún no existe
     * (Como Porque se invocó a OnResume antes de que se creara la fuente de la cámara), se volverá a
     * llamar cuando se cree la fuente de la cámara.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
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
            mFaceGraphic = new FaceGraphic(overlay, getApplicationContext());

            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.rebecatech);
                }

                mediaPlayer.start();
            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);

            }

        }



        /**
         * Comienza a trackear dentro del overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);

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
            Log.v("missing","" );
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
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.rebecatech);
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
                bitmap = ScreenshotUtils.getScreenShot(rootView);
                break;
            case CUSTOM:
                //Puedo hacer invisible o visible lo que me parezca

                break;
        }

        //Si el bitmap no es nulo
        if (bitmap != null) {

            FileManager.uploadScreenshot(FirebaseAuth.getInstance(), bitmap, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //TODO: maybe say something else
                    Toast.makeText(FindSoulMateActivity.this, "Image uploaded.", Toast.LENGTH_SHORT).show();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //TODO: treat
                    Toast.makeText(FindSoulMateActivity.this, "Uploading failed.", Toast.LENGTH_SHORT).show();

                }
            });
            /*File saveFile = ScreenshotUtils.getMainDirectoryName(this);//el directoria para guardar
            File file = ScreenshotUtils.store(b, "screenshot" + screenshotType + ".jpg", saveFile);//save the screenshot to selected path
            shareScreenshot(file);//finally share screenshot*/
        } else {
            //Si es nulo
            Toast.makeText(this, R.string.screenshot_take_failed, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Muestra screenshot Bitmap si quiero
     */
    private void showScreenShotImage(Bitmap b) {
        //imageView.setImageBitmap(b);
    }

    /**
     * Comparte Screenshot
     */
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

}



