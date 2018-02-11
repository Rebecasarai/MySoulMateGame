package com.rebecasarai.mysoulmate.Utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Random;

/**
 * Created by michael on 10/2/18.
 */

public class Utils {


    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }


    public static boolean isYoutSoulMate(){
        final int DIFICULTAD_MAXIMA = 100;
        int encontradosActual = 10;
        Random r = new Random();
        boolean findedSoulMate = false;
        int prob = r.nextInt(DIFICULTAD_MAXIMA - encontradosActual);
        Log.v("Probabilidad a", prob+"");

        //if(random.next(DIFICULTAD_MAXIMA - encontradosActual)==0={}
        if(prob==0){
            findedSoulMate = true;
            Log.v("Probabilidad a true con", prob+"");
        }

        return findedSoulMate;

    }

}
