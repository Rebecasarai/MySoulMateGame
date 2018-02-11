package com.rebecasarai.mysoulmate.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.rebecasarai.mysoulmate.R;

import java.util.Random;

/**
 * Some static utils methods.
 */

public class Utils {


    private static final String TAG = Utils.class.getSimpleName();

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }


    public static boolean isYourSoulMate(Context pContext) {

        int snapshotsTaken = getSnapshotsTaken(pContext);
        updateSnapshotsTaken(pContext, snapshotsTaken++);

        int chance = new Random().nextInt(Constants.MAX_NUM_SCREENSHOTS_TO_SOULMATE - snapshotsTaken);
        Log.v(TAG, "Probability: " + (Constants.MAX_NUM_SCREENSHOTS_TO_SOULMATE - snapshotsTaken));
        Log.v(TAG, "Encontrados actual a" + snapshotsTaken);

        if (chance == 0) return true;

        return false;
    }

    private static void updateSnapshotsTaken(Context pContext, int snapshotsTaken) {
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(pContext.getString(R.string.pref_num_snapshots_taken), snapshotsTaken++);
        editor.commit();
    }

    private static int getSnapshotsTaken(Context pContext) {
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        int encontradosActual = mSharedPref.getInt(pContext.getString(R.string.pref_num_snapshots_taken), 48);
        return encontradosActual;
    }


}
