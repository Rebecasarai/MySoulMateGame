package com.rebecasarai.mysoulmate.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;


/**
 * Some screenshot utils.
 */
public class ScreenshotUtils {

    /**
     * Método que devolverá Bitmap después de tomar la captura de pantalla. Tenemos que pasar la vista que queremos tomar captura de pantalla.
     * @param view
     * @return
     */
    public static Bitmap getScreenShot(View view) {

        View screenView = view;
        screenView.setDrawingCacheEnabled(true);

        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);

        return bitmap;
    }


    /**
     * Crea un directorio donde se guardará la captura de pantalla para compartir.
     * Aquí usaremos getExternalFilesDir y cada vez que la aplicación se desinstale, las imágenes se eliminarán automáticamente....
     * @param context
     * @return
     */
    public static File getMainDirectoryName(Context context) {
        File mainDir = new File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "");

        //Si no está presente se crea el directorio
        if (!mainDir.exists()) {
            if (mainDir.mkdir())
                Log.d("Directorio creado", "Directorio creado en: " + mainDir);
        }
        return mainDir;
    }

    /**
     * Almacena capturas de pantalla en la dirección creada anteriormente
     * @param bm
     * @param fileName
     * @param saveFilePath
     * @return
     */
    public static File store(Bitmap bm, String fileName, File saveFilePath) {
        File dir = new File(saveFilePath.getAbsolutePath());
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(saveFilePath.getAbsolutePath(), fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
