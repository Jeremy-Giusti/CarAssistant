package com.giusti.jeremy.androidcar.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Pair;

import com.giusti.jeremy.androidcar.Constants.ACPreference;

import java.util.concurrent.TimeUnit;

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
     *
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

        if (maxDivResult > 0 && (divised / nearDiviser) > maxDivResult) {
            nearDiviser = diviser;
            while ((divised % nearDiviser) > (diviser / 4) || (divised / nearDiviser) > maxDivResult) {
                nearDiviser++;
            }
        }


        return nearDiviser;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static void vibrate(Context context, int duration, int intensity) {
        long[] pattern = {0, intensity, 0, duration};
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(pattern, 1);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getSoftbuttonsbarHeight(Activity activity) {
        // getRealMetrics is only available with API 17 and +
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;

    }

    public static String getDisplayableTime(long millisec) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millisec),
                TimeUnit.MILLISECONDS.toSeconds(millisec) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisec))
        );
    }

    /**
     * return the screen size minus status bar height
     * @param context
     * @return
     */
    public static Pair<Integer, Integer> getDisplayableScreenSize(Context context) {
        Pair dimens ;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        height = height - (ACPreference.getStatusBarHeight(context));
        dimens=new Pair(width, height);
        return dimens;
    }

    /**
     * return the screen size
     * <br> see also getDisplayableScreenSize()
     * @param context
     * @return
     */
    public static Pair<Integer, Integer> getScreenSize(Context context) {
        Pair dimens ;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dimens=new Pair(width, height);
        return dimens;
    }
}
