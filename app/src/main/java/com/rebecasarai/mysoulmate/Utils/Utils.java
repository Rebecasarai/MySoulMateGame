package com.rebecasarai.mysoulmate.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.rebecasarai.mysoulmate.R;

import java.util.Random;

/**
 * Metodos estaticos Utils
 */

public class Utils {


    private static final String TAG = Utils.class.getSimpleName();

    public static float getScreenDpWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels / displayMetrics.density;
    }


    /**
     * Calcula la probabilidad de que la siguiente persona sea el alma gemela del usuario
     * LA posibilidad es calculada con un numero al azar entre el rango de numero resultado de la resta de NumoMaximo(Difilcutad) y caras detectatdas hasta ahora
     * @param context
     * @return
     */
    public static boolean isYourSoulMate(Context context) {

        int snapshotsTaken = getSnapshotsTaken(context);
        if (snapshotsTaken > Constants.MAX_NUM_SCREENSHOTS_TO_SOULMATE) {
            updateSnapshotsTaken(context, Constants.MAX_NUM_SCREENSHOTS_TO_SOULMATE / 2);
        }
        snapshotsTaken = getSnapshotsTaken(context);
        snapshotsTaken++;
        updateSnapshotsTaken(context, snapshotsTaken);

        int chance = new Random().nextInt(Constants.MAX_NUM_SCREENSHOTS_TO_SOULMATE - snapshotsTaken);
        Log.d(TAG, "Probability: " + (Constants.MAX_NUM_SCREENSHOTS_TO_SOULMATE - snapshotsTaken));
        Log.d(TAG, "SnapshotsTaken " + snapshotsTaken);
        Log.d(TAG, "Chance " + chance);

        if (chance == 0) return true;

        return false;
    }

    /**
     *
     * @param context
     * @param snapshotsTaken
     */
    private static void updateSnapshotsTaken(Context context, int snapshotsTaken) {
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(context.getString(R.string.pref_num_snapshots_taken), snapshotsTaken);
        editor.apply();
    }

    /**
     *
     * @param pContext
     * @return
     */
    public static int getSnapshotsTaken(Context pContext) {
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        int encontradosActual = mSharedPref.getInt(pContext.getString(R.string.pref_num_snapshots_taken), 49);
        Log.d(TAG, encontradosActual + "");
        return encontradosActual;
    }


}
