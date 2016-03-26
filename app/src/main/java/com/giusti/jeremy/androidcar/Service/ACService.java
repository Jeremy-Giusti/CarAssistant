package com.giusti.jeremy.androidcar.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.giusti.jeremy.androidcar.Commands.CmdInterpretor;
import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.Constants.ISettingChangeListener;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.ScreenOverlay.ScreenMapper;
import com.giusti.jeremy.androidcar.SpeechRecognition.ISpeechResultListener;
import com.giusti.jeremy.androidcar.SpeechRecognition.SpeechListener;

import java.util.ArrayList;

/**
 * Created by jgiusti on 19/10/2015.
 */
public class ACService extends Service implements ISpeechResultListener, ISettingChangeListener{

    private static final String TAG = ACService.class.getSimpleName();
    private static ACService instance;
    private ScreenMapper mScreenMapper;
    private static final int CANCEL_SERVICE = 5345;
    private static final String BCAST_CONFIGCHANGED = "android.intent.action.CONFIGURATION_CHANGED";
    private CmdInterpretor cmdInterpretor;
    private SpeechListener speechListener;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, START_STICKY, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        displayGridOverlay();
        displayNotifivation();
        startOrientationChangeListener();
        cmdInterpretor = new CmdInterpretor(this, mScreenMapper);
        speechListener = new SpeechListener(this, this);
        ACPreference.addListener(this);
        //TODO prov pendant l'utilisation de l'activity test
        //speechListener.setListeningSpeech(true);
        instance = this;
    }

    private void startOrientationChangeListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BCAST_CONFIGCHANGED);
        this.registerReceiver(mBroadcastReceiver, filter);
    }

    /**
     * show persistant notif in order to make the service unkillable
     */
    private void displayNotifivation() {

        Intent notificationIntent = new Intent(this, ACService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                CANCEL_SERVICE, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = this.getResources();
        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_action_speech)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(res.getString(R.string.notif_title))
                .setContentText(res.getString(R.string.notif_text));
        Notification notif = builder.build();

        startForeground(1337, notif);
    }

    /**
     * show grid overlay
     */
    private void displayGridOverlay() {
        if (mScreenMapper != null) {
            mScreenMapper.removeAllViews();
        }

        mScreenMapper = new ScreenMapper(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.BOTTOM;
        params.setTitle("Load Average");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mScreenMapper, params);
        if (cmdInterpretor != null) {
            cmdInterpretor.addListener(mScreenMapper);
        }
    }


    /**
     * todo prov
     *
     * @param potentialCmdList
     */
    public void onInputCmd(ArrayList<String> potentialCmdList) {

        cmdInterpretor.managePotentialCmdList(potentialCmdList);
    }

    public void changelisteningMode() {
        if (speechListener.isShouldBeListening()) {
            speechListener.setListeningSpeech(false);
            Toast.makeText(this, "Speech Listener off", Toast.LENGTH_SHORT).show();

        } else {
            speechListener.setListeningSpeech(true);
            Toast.makeText(this, "Speech Listener on", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public void onSpeechResult(ArrayList<String> speechResult) {
        cmdInterpretor.managePotentialCmdList(speechResult);
    }

    /**
     * may be null
     *
     * @return
     */
    public static ACService getInstance() {
        return instance;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        cmdInterpretor.endAllConnections();
        cmdInterpretor.removeListener(mScreenMapper);
        if (mScreenMapper != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mScreenMapper);
            mScreenMapper = null;
        }
        speechListener.setListeningSpeech(false);
        speechListener.removeListener(this);
        ACPreference.removeListener(this);
        this.unregisterReceiver(mBroadcastReceiver);
        instance = null;
    }

    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent myIntent) {

            if (myIntent.getAction().equals(BCAST_CONFIGCHANGED)) {
                Log.d(TAG, "received->" + BCAST_CONFIGCHANGED);
                cmdInterpretor.removeListener(mScreenMapper);
                displayGridOverlay();

            }
        }
    };


    @Override
    public void onSettingChanged(int settingId) {
        switch (settingId){
            case ACPreference.SHOW_GRID_ID:
                mScreenMapper.setGridVisibility(ACPreference.getShowGrid(this));
                break;
            case ACPreference.TRIGGER_WORD_ID:
                cmdInterpretor.triggerChanged();
                break;
            default:
                break;
        }
    }
}
