package com.giusti.jeremy.androidcar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Window;

/**
 * Created by jgiusti on 19/10/2015.
 */
public class Utils {
    public static int GCD(int a, int b) {
        if (b == 0) return a;
        return GCD(b, a % b);
    }

    /**
     * calculate
     * @param divised
     * @param diviser
     * @param maxDivResult
     * @return
     */
    public static int FindBestDiviser(int divised, int diviser, int maxDivResult) {
        int nearDiviser = diviser;

        while ((divised % nearDiviser) > (diviser / 4)) {
            nearDiviser--;
        }

        if(maxDivResult>0 && (divised/nearDiviser)>maxDivResult){
            nearDiviser = diviser;
            while ((divised % nearDiviser) > (diviser / 4) || (divised/nearDiviser)>maxDivResult) {
                nearDiviser++;
            }
        }


        return nearDiviser;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
}
