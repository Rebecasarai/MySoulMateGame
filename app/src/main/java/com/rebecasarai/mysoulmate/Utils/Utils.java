package com.rebecasarai.mysoulmate.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Random;

/**
 * Created by michael on 10/2/18.
 */

public class Utils {
    int mEncontradosActual;


    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }


    public static boolean isYoutSoulMate(Context pContext){
        Random r = new Random();
        final int DIFICULTAD_MAXIMA = 50;
        //Obtine y actualiza shared
        boolean findedSoulMate = false;
        int encontradosActual = getProbabilty(pContext);
        updateProbability(pContext, encontradosActual++);

        int prob = r.nextInt(DIFICULTAD_MAXIMA - 49);
        Log.v("Probabilidad a", prob + "");

        Log.v("Encontrados actual a", encontradosActual + "");

        if(prob==0){
            findedSoulMate = true;
            Log.v("Probabilidad a true con", prob+"");
        }

        return findedSoulMate;

    }

    public static void updateProbability(Context pContext, int encontradosActual){
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt("NUM_DETECTADOS_ACTUAL", encontradosActual++);
        editor.commit();
    }

    public static int getProbabilty(Context pContext){
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
        int encontradosActual = mSharedPref.getInt("NUM_DETECTADOS_ACTUAL",48);

        return encontradosActual;
    }


}
