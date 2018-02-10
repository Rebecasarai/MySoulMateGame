package com.rebecasarai.mysoulmate.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.rebecasarai.mysoulmate.Database.FileManager;
import com.rebecasarai.mysoulmate.Exif;
import com.rebecasarai.mysoulmate.Graphics.FaceGraphic;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Repository.UserRepository;
import com.rebecasarai.mysoulmate.Screenshot.ScreenshotType;
import com.rebecasarai.mysoulmate.Screenshot.ScreenshotUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;




import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;





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

    private View rootView, view;
    private MediaPlayer mediaPlayer;
    ImageView mPhotoPeep;
    Context context;
    ImageButton mCatchSoulMateButton;
    ConstraintLayout mToplayout;


    Exif exif;
    Bitmap bitmapPicture = null;



    private SurfaceView SurView;
    private SurfaceHolder camHolder;
    private boolean previewRunning;
    private Button button1;
    public static Camera camera = null;
    private ImageView camera_image;
    private Bitmap bmp,bmp1;
    private ByteArrayOutputStream bos;
    private BitmapFactory.Options options,o,o2;
    private FileInputStream fis;
    ByteArrayInputStream fis2;
    private FileOutputStream fos;
    private File dir_image2,dir_image;
    private RelativeLayout CamView;









    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();

        view =  lf.inflate(com.rebecasarai.mysoulmate.R.layout.fragment_camera, container, false);


        rootView = getActivity().getWindow().getDecorView().findViewById(R.id.fragmentTopLayout);

        mPreview = (CameraPreview) view.findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) view.findViewById(R.id.faceOverlay);
        mToplayout = (ConstraintLayout) view.findViewById(R.id.fragmentTopLayout);


        //TODO: esto no debería hacerse así.
        //Chequeo permisos e inicializo la cameraSource
        int rc = ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.rebecatech);
        mPhotoPeep = (ImageView) view.findViewById(R.id.photoPerson);

        mCatchSoulMateButton = (ImageButton) view.findViewById(R.id.catchSoulMateButton);
        mCatchSoulMateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {

                        Log.v(TAG, " Foto tomada.");
                        capturar(bytes);
                    }
                });

                mCameraSource.getPreviewSize();
            }
        });

        return view;//inflater.inflate(R.layout.fragment_camera, container, false);
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
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),   source.getHeight(), matrix,true);
    }


    /**
     * Metodo que captura la foto, llamado desde CameraSource
     * @param bytes
     */
    private void capturar(byte[] bytes) {

        int orientation = exif.getOrientation(bytes);


        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        switch(orientation) {
            case 90:
                bitmapPicture= rotateImage(bitmap, 90);

                break;
            case 180:
                bitmapPicture= rotateImage(bitmap, 180);

                break;
            case 270:
                bitmapPicture= rotateImage(bitmap, 270);

                break;
            case 0:

            default:
                break;
        }


        // Coloca la imagen de la cameraSource a la vista
        mPhotoPeep.setImageBitmap(bitmapPicture);

        try {
            File mainDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"");
            Log.v("dir",mainDir.getAbsolutePath());
            //File mainDir = new  File(Environment.getExternalStorageDirectory()+ File.separator+"My Custom Folder");
            if (!mainDir.exists()) {
                if (mainDir.mkdir())
                    Log.e("Create Directory", "Main Directory Created: " + mainDir);
            }
            File captureFile = new File(mainDir, "prueba" + getPhotoTime() + ".png");
            if (!captureFile.exists())
                Log.d("CAPTURE_FILE_PATH", captureFile.createNewFile() ? "Success" : "Failed");

            //FileOutputStream stream = new FileOutputStream(captureFile);
            FileOutputStream stream = new FileOutputStream(captureFile);
            bitmapPicture.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            Log.v("dir",captureFile.getAbsolutePath());
            stream.write(bytes);
            stream.flush();
            stream.close();

            //savebitmap(getPhotoTime());

            takeScreenshot(ScreenshotType.FULL);



        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    private void savebitmap(String filename) {
        /*String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + getPhotoTime()+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapPicture.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }


    public void TakeScreenshot(){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int nu = preferences.getInt("image_num",0);
        nu++;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("image_num",nu);
        editor.commit();
        mToplayout.setDrawingCacheEnabled(true);
        mToplayout.buildDrawingCache(true);
        bmp = Bitmap.createBitmap(mToplayout.getDrawingCache());
        mToplayout.setDrawingCacheEnabled(false);
        bos = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();
        fis2 = new ByteArrayInputStream(bitmapdata);

        String picId=String.valueOf(nu);
        String myfile="MyImage"+picId+".jpeg";

        dir_image = new  File(Environment.getExternalStorageDirectory()+
                File.separator+"My Custom Folder");
        dir_image.mkdirs();

        try {
            File tmpFile = new File(dir_image,myfile);
            fos = new FileOutputStream(tmpFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = fis2.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fis2.close();
            fos.close();

            Toast.makeText(context,
                    "The file is saved at :/My Custom Folder/"+"MyImage"+picId+".jpeg",Toast.LENGTH_LONG).show();

            bmp1 = null;
            mPhotoPeep.setImageBitmap(bmp1);
            camera.startPreview();
            button1.setClickable(true);
            button1.setVisibility(View.VISIBLE);//<----UNHIDE HER
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public Bitmap decodeFile(File f) {
        Bitmap b = null;
        try {
            // Decode image size
            o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            int IMAGE_MAX_SIZE = 1000;
            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(
                        2,
                        (int) Math.round(Math.log(IMAGE_MAX_SIZE
                                / (double) Math.max(o.outHeight, o.outWidth))
                                / Math.log(0.5)));
            }

            // Decode with inSampleSize
            o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }





    /**
     * Fecha actual
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
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                context/*getContext()*/);
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
        switch (v.getId()){
            case R.id.catchSoulMateButton:
                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        Log.v(TAG, " Foto tomada.");
                        capturar(bytes);
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
            mFaceGraphic = new FaceGraphic(overlay, getContext());



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



