package com.giusti.jeremy.androidcar.ScreenOverlay;

import android.content.Context;
import android.graphics.Point;
import android.widget.Toast;

import com.giusti.jeremy.androidcar.Constants.ACPreference;

/**
 * Created by jgiusti on 20/10/2015.
 * <p/>
 * convert Alphanumerical or numericale coordinate (A1 or 720x1080)
 * using calculated dimensions from ScreenMapper creation
 */
public class CoordinateConverter {
    private Context context;

    int asciiValueA = (int) 'A';


    public CoordinateConverter(Context context) {
        this.context = context;
    }

    public AlphaNumCoord getAlphaNumFromXY(int x, int y) {
        try {
            int gridStartX = x - ACPreference.getGridPaddingLeft(context);
            int gridStartY = y - ACPreference.getStatusBarHeight(context);

            int AsciiValueAlpha = asciiValueA + (int)(gridStartX / (float)ACPreference.getGridItemWidth(context));
            char alpha = (char) AsciiValueAlpha;
            int num = (int) (gridStartY / (float)ACPreference.getGridItemHeight(context));
            return new AlphaNumCoord(num,alpha);
        } catch (Exception e) {
            Toast.makeText(context, "error while getting alphaNum from " + x + ":" + y, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }

    public Point getXYFromAlphaNum(AlphaNumCoord alphaNum) {
        int alphaValue = alphaNum.getAlphaAsValue();
        int x = (alphaValue * ACPreference.getGridItemWidth(context)) + ACPreference.getGridPaddingLeft(context) +ACPreference.getGridItemWidth(context)/2;
        int y = (alphaNum.getNum() * ACPreference.getGridItemHeight(context)) + ACPreference.getStatusBarHeight(context)+ACPreference.getGridItemHeight(context)/2;
        return new Point(x, y);
    }

}
