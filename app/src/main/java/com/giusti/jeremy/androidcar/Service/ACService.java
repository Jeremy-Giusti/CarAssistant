package com.giusti.jeremy.androidcar.Service;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.giusti.jeremy.androidcar.Commands.AppCmdExecutor;
import com.giusti.jeremy.androidcar.Commands.CmdInterpretor;
import com.giusti.jeremy.androidcar.Commands.EmptyCommandResultListenerCommand;
import com.giusti.jeremy.androidcar.Commands.ICommandExcecutionResult;
import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.Constants.ISettingChangeListener;
import com.giusti.jeremy.androidcar.MusicPlayer.AudioPlayer;
import com.giusti.jeremy.androidcar.MusicPlayer.MusicsPlayer;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.ScreenOverlay.CmdButton;
import com.giusti.jeremy.androidcar.ScreenOverlay.ScreenMapper;
import com.giusti.jeremy.androidcar.SpeechRecognition.ISpeechResultListener;
import com.giusti.jeremy.androidcar.SpeechRecognition.SpeechListener;
import com.giusti.jeremy.androidcar.UI.AcNotifications;
import com.giusti.jeremy.androidcar.Utils.LongProximityEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jgiusti on 19/10/2015.
 * service that run on background will show a persistant notification if activated
 * May show the grid if asked
 */
public class ACService extends Service implements ISpeechResultListener, ISettingChangeListener, IFloatingButtonClickListener, ICommandExcecutionResult, LongProximityEventListener.IProximityEventListener {

    private static final String TAG = ACService.class.getSimpleName();

    private static final String ORIENTATION_CHANGE = "orientation change";

    private static ACService instance = null;
    private ScreenMapper mScreenMapper;
    private CmdButton mCmdButton;
    private static final String BCAST_CONFIGCHANGED = "android.intent.action.CONFIGURATION_CHANGED";
    private CmdInterpretor cmdInterpretor;
    private SpeechListener speechListener;

    private HashMap<String, BroadcastReceiver> broadcastList = new HashMap<>();

    private boolean mMusicPlayerPaused = false;

    private AudioPlayer audioPlayer = new AudioPlayer(AudioManager.STREAM_NOTIFICATION);


    /**
     * may be null
     *
     * @return
     */
    public static ACService getInstance() {
        return instance;
    }

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
            new AppCmdExecutor(this, new EmptyCommandResultListenerCommand()).openSettingActivity();
            this.stopSelf();
            return;
        }
        displayCmdButton();
        displayGridOverlay();
        displayNotification(AcNotifications.getDefaultNotification(this));
        startOrientationChangeListener();
        cmdInterpretor = new CmdInterpretor(this, this, mScreenMapper);
        speechListener = new SpeechListener(this, this);
        ACPreference.addListener(this);
        initProximitySensorDetection();
        instance = this;
    }


    public void displayNotification(Notification notif) {
        startForeground(AcNotifications.AC_NOTIF_ID, notif);
    }

    //-------------------------------------------------- OVERLAYS --------------------------------------


    /**
     * listen to screen orientation change to re display the grid if needed
     */
    private void startOrientationChangeListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BCAST_CONFIGCHANGED);
        registerBroadcast(ORIENTATION_CHANGE, mBroadcastReceiver, filter);
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

    //-------------------------------------------------- speech listening --------------------------------------

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
        if (MusicsPlayer.getInstance(this).isPlaying()) {
            MusicsPlayer.getInstance(this).pause();
            mMusicPlayerPaused = true;
        }
    }

    @Override
    public void onStopListening() {
        if (mMusicPlayerPaused) {
            MusicsPlayer.getInstance(this).start();
            mMusicPlayerPaused = false;
        }
    }


//-------------------------------------------------- setting changed --------------------------------------


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

    // ------------------------ PROXIMITY sensor events--------------------------

    private void initProximitySensorDetection() {
        SensorManager mySensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        Sensor myProximitySensor = mySensorManager.getDefaultSensor(
                Sensor.TYPE_PROXIMITY);
        if (myProximitySensor != null) {
            mySensorManager.registerListener(proximitySensorEventListener,
                    myProximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    SensorEventListener proximitySensorEventListener
            = new LongProximityEventListener(this);

    @Override
    public void onProximityEvent() {
        if (!speechListener.isShouldBeListening()) {
            speechListener.setListeningSpeech(true, SpeechListener.DEFAULT_RETRY_NUMBER);
        }
    }


    //--------------------- RESULT LISTENING ----------------

    @Override
    public void onResult(EResult result, String commandKey, String details) {
        showCommandResult(result, commandKey, details);
    }

    @Override
    public void onResult(EResult result, int commandKey, String details) {
        showCommandResult(result, getString(commandKey), details);
    }

    @Override
    public void onResult(EResult result, int commandKey, int details) {
        showCommandResult(result, getString(commandKey), getString(details));
    }

    private void showCommandResult(EResult result, String key, String detail) {
        try {
            switch (result) {
                case SUCCESS:
                    if (!TextUtils.isEmpty(detail)) {
                        Toast.makeText(this, detail, Toast.LENGTH_LONG).show();
                    }
                    audioPlayer.start(this, R.raw.success);
                    break;
                case FAIL:
                    Toast.makeText(this, getString(R.string.command_failed) + key + "\n" + detail, Toast.LENGTH_LONG).show();
                    audioPlayer.start(this, R.raw.failed);
                    break;
                case MALFORMED:
                    Toast.makeText(this, /*getString(R.string.command_malformed)*/"" + key + "\n" + detail, Toast.LENGTH_LONG).show();
                    audioPlayer.start(this, R.raw.error);
                    break;
            }
        } catch (IOException e) {
            Log.e(TAG, "error playing CMD result", e);
        }
    }


    // ---------------------- MANUAL COMMAND INPUT --------------------

    public void showWriteCommandDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.write_a_command);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> result = new ArrayList<String>();
                result.add(input.getText().toString());
                onInputCmd(result);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }
// ------------------------------------------- BroadCast registering --------------------

    public void registerBroadcast(String receverKey, BroadcastReceiver receiver, IntentFilter filter) {
        if (!broadcastList.containsKey(receverKey)) {
            broadcastList.put(receverKey, receiver);
            registerReceiver(receiver, filter);
        }
    }

//-------------------------------------------------- DESTROY --------------------------------------

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

        // this.unregisterReceiver(mBroadcastReceiver);
        for (BroadcastReceiver receveir : broadcastList.values()) {
            this.unregisterReceiver(receveir);
        }
        audioPlayer.destroy();
        instance = null;
    }


}
