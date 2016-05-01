package com.giusti.jeremy.androidcar.Utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by jérémy on 01/05/2016.
 * can send intentBroacast to control music player
 */
public class MediaPlayerManager {
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPLAY = "play";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";

    public static void playMusic(Context context) {
        Intent i = new Intent(SERVICECMD);
        i.putExtra(CMDNAME, CMDPLAY);
        context.sendBroadcast(i);
    }

    public static void pauseMusic(Context context) {
        Intent i = new Intent(SERVICECMD);
        i.putExtra(CMDNAME, CMDPAUSE);
        context.sendBroadcast(i);
    }

    public static void stopMusic(Context context) {
        Intent i = new Intent(SERVICECMD);
        i.putExtra(CMDNAME, CMDSTOP);
        context.sendBroadcast(i);
    }

    public static void nextMusic(Context context) {
        Intent i = new Intent(SERVICECMD);
        i.putExtra(CMDNAME, CMDNEXT);
        context.sendBroadcast(i);
    }

    public static void previousMusic(Context context) {
        Intent i = new Intent(SERVICECMD);
        i.putExtra(CMDNAME, CMDPREVIOUS);
        context.sendBroadcast(i);
    }
}
