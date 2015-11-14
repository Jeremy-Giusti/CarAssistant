package com.giusti.jeremy.androidcar.Commands;

import android.graphics.Point;

/**
 * Created by jgiusti on 21/10/2015.
 */
public interface IMotionEventCmdListener {
    void onMotionEventCmd(int eventType, Point... eventCoordinates);
}
