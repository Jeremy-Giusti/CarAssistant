package com.giusti.jeremy.androidcar.Utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;

/**
 * Created by jérémy on 18/06/2016.
 */
public class LongProximityEventListener implements SensorEventListener {
    private static final float PROXIMITY_NEAR = 0;
    private Handler handler =new Handler();
    private Runnable timerEvent = new Runnable() {
        @Override
        public void run() {
            listener.onProximityEvent();
        }
    };
    private int PROXIMITY_EVENT_LENGTH = 500;
    private IProximityEventListener listener;

    public LongProximityEventListener(IProximityEventListener listener) {
        this.listener=listener;
    }

    public LongProximityEventListener(IProximityEventListener listener, int proximityEventMinLenght) {
        this(listener);
        this.PROXIMITY_EVENT_LENGTH = proximityEventMinLenght;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (PROXIMITY_NEAR == event.values[0]) {
                startTimer();
            }else{
                cancelTimer();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void startTimer(){
        handler.removeCallbacks(timerEvent);
        handler.postDelayed(timerEvent, PROXIMITY_EVENT_LENGTH);
    }

    private void cancelTimer(){
        handler.removeCallbacks(timerEvent);
    }



    public interface IProximityEventListener {
        public void onProximityEvent();
    }
}
