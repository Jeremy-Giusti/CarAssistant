package com.giusti.jeremy.androidcar.ScreenOverlay;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.giusti.jeremy.androidcar.Commands.IMotionEventCmdListener;
import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.Constants.Constants;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Utils.Utils;

import java.util.ArrayList;

/**
 * Created by jgiusti on 19/10/2015.
 */
public class ScreenMapper extends RelativeLayout implements IMotionEventCmdListener {

    private GridView grid;
    private CustomGridAdapter gridAdapter;
    private CoordinateConverter coordCvt;
    private Context context;

    public ScreenMapper(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.screen_map, this, true);

        coordCvt = new CoordinateConverter(context);

        grid = (GridView) findViewById(R.id.grid);
        this.context=context;
        initGrid(context);
    }

    private void initGrid(final Context context) {

        // grid item size calculation
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        height = height - (ACPreference.getStatusBarHeight(context));

        int itemWidth = 45;
        int itemHeight = 45;

        if (height < width) {

            if (width % itemWidth != 0 || (width / itemWidth > 26)) {
                itemWidth = Utils.FindBestDiviser(width, itemWidth, 26);
            }
            itemHeight = itemWidth;
            if (height % itemHeight != 0) {
                itemHeight = Utils.FindBestDiviser(height, itemHeight, -1);
            }
        } else {
            if (height % itemHeight != 0 || (height / itemHeight > 26)) {
                itemHeight = Utils.FindBestDiviser(height, itemHeight, 26);
            }
            itemWidth = itemHeight;
            if (width % itemWidth != 0) {
                itemWidth = Utils.FindBestDiviser(width, itemWidth, -1);
            }
        }


        //grid content generation
        ArrayList<AlphaNumCoord> gridContent = generateGridContent(width, height, itemHeight, itemWidth);

        // grid display
        displayGrid(context, width, height, itemHeight, itemWidth, gridContent);

    }

    @NonNull
    private ArrayList<AlphaNumCoord> generateGridContent(int width, int height, int itemHeight, int itemWidth) {
        ArrayList<AlphaNumCoord> gridContent = new ArrayList<>();
        for (int i = 0; i < (height / itemHeight); i++) {
            for (int j = 0; j < (width / itemWidth); j++) {
                gridContent.add(new AlphaNumCoord(i, j));
            }
        }
        return gridContent;
    }

    private void displayGrid(Context context, int width, int height, int itemHeight, int itemWidth, ArrayList<AlphaNumCoord> gridContent) {
        int paddingleft = width % itemWidth;
        grid.setPadding(paddingleft, 0, 0, height % itemHeight);
        int nbColumn = ((width - paddingleft) / itemWidth);
        grid.setNumColumns(nbColumn);
        gridAdapter = new CustomGridAdapter(context, itemWidth, itemHeight, gridContent);
        this.grid.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();

        ACPreference.setGridColumnNb(context, nbColumn);
        ACPreference.setGridItemHeight(context, itemHeight);
        ACPreference.setGridItemWidth(context, itemWidth);
        ACPreference.setGridPaddingLeft(context, paddingleft);
    }


    @Override
    public void onMotionEventCmd(final int eventType, final Point... eventCoordinates) {
        switch (eventType) {
            case Constants.EVENT_CLICK:
                gridAdapter.eventOntItem((coordCvt.getAlphaNumFromXY(eventCoordinates[0].x, eventCoordinates[0].y)).toString(), eventType);
                break;
            case Constants.EVENT_LONGCLICK:
                gridAdapter.eventOntItem((coordCvt.getAlphaNumFromXY(eventCoordinates[0].x, eventCoordinates[0].y)).toString(), eventType);
                break;
            case Constants.EVENT_SWIPE:
                gridAdapter.eventOntItem(coordCvt.getAlphaNumFromXY(eventCoordinates[0].x, eventCoordinates[0].y).toString(), eventType);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gridAdapter.eventOntItem(coordCvt.getAlphaNumFromXY(eventCoordinates[1].x, eventCoordinates[1].y).toString(), eventType);
                    }
                }, 200);
                break;
            default:
                break;
        }
    }


    public void setGridVisibility(boolean visibility) {
        this.gridAdapter.showGridOnBorder(visibility);
    }
}
