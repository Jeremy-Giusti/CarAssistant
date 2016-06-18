package com.giusti.jeremy.androidcar.MusicPlayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by jérémy on 07/05/2016.
 */
public class MusicIntentListenerService extends Service {

    private static MusicIntentListenerService instance;

    public static final String INTENT_REQUEST = "request";
    public static final int INTENT_REQUEST_REMOVE = 199;
    public static final int INTENT_REQUEST_SHOW = 200;
    public static final int INTENT_REQUEST_PLAY = 201;
    public static final int INTENT_REQUEST_PAUSE = 202;
    public static final int INTENT_REQUEST_NEXT = 203;
    public static final int INTENT_REQUEST_PREV = 204;

    public static MusicIntentListenerService getInstance() {
        return instance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (instance == null) {
            instance = this;
        }

        onNewIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void onNewIntent(Intent intent) {
        int request = intent.getIntExtra(INTENT_REQUEST, -1);
        switch (request) {
            case INTENT_REQUEST_SHOW:
                //? TODO ?
                break;
            case INTENT_REQUEST_REMOVE:
                MusicsPlayer.getInstance(this).destroy();
                break;
            case INTENT_REQUEST_PLAY:
                MusicsPlayer.getInstance(this).start();
                break;
            case INTENT_REQUEST_PAUSE:
                MusicsPlayer.getInstance(this).pause();
                break;
            case INTENT_REQUEST_NEXT:
                MusicsPlayer.getInstance(this).next();
                break;
            case INTENT_REQUEST_PREV:
                MusicsPlayer.getInstance(this).previous();
                break;
            default:
                break;
        }
    }
}
