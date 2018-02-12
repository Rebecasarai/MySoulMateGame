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

    public static float getScreenDpWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels / displayMetrics.density;
    }


    public static boolean isYourSoulMate(Context context) {

        int snapshotsTaken = getSnapshotsTaken(context);
        updateSnapshotsTaken(context, snapshotsTaken);

        int chance = new Random().nextInt(Constants.MAX_NUM_SCREENSHOTS_TO_SOULMATE - snapshotsTaken);
        Log.d(TAG, "Probability: " + (Constants.MAX_NUM_SCREENSHOTS_TO_SOULMATE - snapshotsTaken));
        Log.d(TAG, "SnapshotsTaken " + snapshotsTaken);

        if (chance == 0) return true;

        return false;
    }

    private static void updateSnapshotsTaken(Context context, int snapshotsTaken) {
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(context.getString(R.string.pref_num_snapshots_taken), snapshotsTaken++);
        editor.commit();
    }

    private static int getSnapshotsTaken(Context pContext) {
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        int encontradosActual = mSharedPref.getInt(pContext.getString(R.string.pref_num_snapshots_taken), 48);
        Log.d(TAG,encontradosActual+"");
        return encontradosActual;
    }


}
