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
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.giusti.jeremy.androidcar.Activity.SettingActivity;
import com.giusti.jeremy.androidcar.Commands.AppCmdExecutor;
import com.giusti.jeremy.androidcar.Commands.CmdInterpretor;
import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.Constants.ISettingChangeListener;
import com.giusti.jeremy.androidcar.MusicPlayer.IAudioPlayer;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.ScreenOverlay.CmdButton;
import com.giusti.jeremy.androidcar.ScreenOverlay.ScreenMapper;
import com.giusti.jeremy.androidcar.SpeechRecognition.ISpeechResultListener;
import com.giusti.jeremy.androidcar.SpeechRecognition.SpeechListener;

import java.util.ArrayList;

/**
 * Created by jgiusti on 19/10/2015.
 * service that run on background will show a persistant notification if activated
 * May show the grid if asked
 */
public class ACService extends Service implements ISpeechResultListener, ISettingChangeListener, IFloatingButtonClickListener {

    private static final String TAG = ACService.class.getSimpleName();
    private static ACService instance = null;
    private ScreenMapper mScreenMapper;
    private CmdButton mCmdButton;
    private static final int CANCEL_SERVICE = 5345;
    private static final String BCAST_CONFIGCHANGED = "android.intent.action.CONFIGURATION_CHANGED";
    private CmdInterpretor cmdInterpretor;
    private SpeechListener speechListener;

    private IAudioPlayer mAudioPlayer = null;
    private boolean mAudioPlayerPaused = false;


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, R.string.overlay_forbiden, Toast.LENGTH_SHORT).show();
            new AppCmdExecutor(this).openSettingActivity();
            this.stopSelf();
            return;
        }
        displayCmdButton();
        displayGridOverlay();
        displayNotification();
        startOrientationChangeListener();
        cmdInterpretor = new CmdInterpretor(this, mScreenMapper);
        speechListener = new SpeechListener(this, this);
        ACPreference.addListener(this);
        instance = this;
    }

    /**
     * listen to screen orientation chage to re display the grid if needed
     */
    private void startOrientationChangeListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BCAST_CONFIGCHANGED);
        this.registerReceiver(mBroadcastReceiver, filter);
    }

    /**
     * show persistant notif wich open setting if clicked
     */
    private void displayNotification() {

        Intent notificationIntent = new Intent(this, SettingActivity.class);


        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);


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

    private void displayCmdButton() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mCmdButton = new CmdButton(this, this);
        mCmdButton.showInWindow(wm);
    }


    /**
     * @param potentialCmdList
     */
    public void onInputCmd(ArrayList<String> potentialCmdList) {

        cmdInterpretor.managePotentialCmdList(potentialCmdList);
    }

    public void changelisteningMode() {
        if (speechListener.isShouldBeListening()) {
            speechListener.setListeningSpeech(false, 0);
            Toast.makeText(this, "Speech Listener off", Toast.LENGTH_SHORT).show();

        } else {
            speechListener.setListeningSpeech(true, SpeechListener.DEFAULT_RETRY_NUMBER);
            Toast.makeText(this, "Speech Listener on", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public void onSpeechResult(ArrayList<String> speechResult) {
        cmdInterpretor.managePotentialCmdList(speechResult);
    }

    @Override
    public void onStartListening() {
        if (this.mAudioPlayer != null) {
            mAudioPlayer.pause();
            mAudioPlayerPaused = true;
        }
    }

    @Override
    public void onStopListening() {
        if (this.mAudioPlayer != null && mAudioPlayerPaused) {
            mAudioPlayer.start();
            mAudioPlayerPaused = false;
        }
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

        if (mCmdButton != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mCmdButton);
            mCmdButton = null;
        }
        speechListener.setListeningSpeech(false, 0);
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
        switch (settingId) {
            case ACPreference.SHOW_GRID_ID:
                mScreenMapper.setGridVisibility(ACPreference.getShowGrid(this));
                break;
            case ACPreference.USE_TRIGGER_ID:
            case ACPreference.TRIGGER_WORD_ID:
                cmdInterpretor.triggerChanged();
                break;
            default:
                break;
        }
    }

    //--------------------- service Floating button events ----------------
    @Override
    public void onprimaryClick() {
        if (!speechListener.isShouldBeListening()) {
            speechListener.setListeningSpeech(true, SpeechListener.DEFAULT_RETRY_NUMBER);
        }
    }

    @Override
    public void onSecondaryClick() {
        stopService(new Intent(this, ACService.class));
    }

    public void audioPlayerLaunched(IAudioPlayer audioPlayer) {
        this.mAudioPlayer = audioPlayer;
    }

    public void audioPlayerDestroyed() {
        this.mAudioPlayer = null;
    }

    public IAudioPlayer getAudioPlayer() {
        return mAudioPlayer;
    }
}
