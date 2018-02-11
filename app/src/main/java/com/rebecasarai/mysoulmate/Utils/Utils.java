package com.rebecasarai.mysoulmate.Utils;

import android.content.Context;
import android.util.DisplayMetrics;

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


}
