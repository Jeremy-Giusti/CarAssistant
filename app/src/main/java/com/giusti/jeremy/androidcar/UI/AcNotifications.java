package com.giusti.jeremy.androidcar.UI;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import com.giusti.jeremy.androidcar.Activity.SettingActivity;
import com.giusti.jeremy.androidcar.MusicPlayer.IMusicsPlayer;
import com.giusti.jeremy.androidcar.R;

/**
 * Created by jérémy on 06/05/2016.
 */
public class AcNotifications {
    public static final int AC_NOTIF_ID = 1336;

    /**
     * get persistant notif wich open setting if clicked
     */
    public static Notification getDefaultNotification(Context context) {

        Intent notificationIntent = new Intent(context, SettingActivity.class);


        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);


        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_action_speech)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(res.getString(R.string.notif_title))
                .setContentText(res.getString(R.string.notif_text));


        return builder.build();
    }

    public static Notification getMusicNotification(Context context, IMusicsPlayer audioPlayer) {
        return null;
        //TODO
    }
}
