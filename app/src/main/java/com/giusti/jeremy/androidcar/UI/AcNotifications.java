package com.giusti.jeremy.androidcar.UI;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import com.giusti.jeremy.androidcar.Activity.AudioPlayerActivity;
import com.giusti.jeremy.androidcar.Activity.SettingActivity;
import com.giusti.jeremy.androidcar.MusicPlayer.MusicFile;
import com.giusti.jeremy.androidcar.MusicPlayer.MusicIntentListenerService;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Service.ACService;
import com.giusti.jeremy.androidcar.Utils.Utils;

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

    public static Notification getMusicNotification(Context context, MusicFile music, boolean playing) {
        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        Intent intent = new Intent(context, AudioPlayerActivity.class);
        PendingIntent showIntent = PendingIntent.getActivity(context, MusicIntentListenerService.INTENT_REQUEST_SHOW, intent, 0);

        String duration = Utils.getDisplayableTime(music.getDuration());
        builder.setSmallIcon(R.drawable.ic_music_player)
                .setContentTitle(music.getTitle())
                .setContentText(String.valueOf(duration))
                .setContentIntent(showIntent)
                .addAction(getMusicPrevAction(context))
                .addAction(getMusicPlayPauseAction(context, !playing))
                .addAction(getMusicNextAction(context)).build();


        return builder.build();
    }

    private static Notification.Action getMusicPrevAction(Context context) {
        Intent intent = new Intent(context, ACService.class);
        intent.putExtra(MusicIntentListenerService.INTENT_REQUEST, MusicIntentListenerService.INTENT_REQUEST_PREV);
        PendingIntent prevIntent = PendingIntent.getService(context, MusicIntentListenerService.INTENT_REQUEST_PREV, intent, 0);
        Notification.Action.Builder actionBuilder = new Notification.Action.Builder(R.drawable.ic_previous_small, "previous", prevIntent);
        return actionBuilder.build();
    }

    private static Notification.Action getMusicNextAction(Context context) {
        Intent intent = new Intent(context, MusicIntentListenerService.class);
        intent.putExtra(MusicIntentListenerService.INTENT_REQUEST, MusicIntentListenerService.INTENT_REQUEST_NEXT);
        PendingIntent nextIntent = PendingIntent.getService(context, MusicIntentListenerService.INTENT_REQUEST_NEXT, intent, 0);
        Notification.Action.Builder actionBuilder = new Notification.Action.Builder(R.drawable.ic_next_small, "next", nextIntent);
        return actionBuilder.build();
    }

    private static Notification.Action getMusicPlayPauseAction(Context context, boolean play) {
        int ic;
        String title;
        int request;

        if (play) {
            ic = R.drawable.ic_play_small;
            title = "play";
            request = MusicIntentListenerService.INTENT_REQUEST_PLAY;
        } else {
            ic = R.drawable.ic_pause_small;
            title = "pause";
            request = MusicIntentListenerService.INTENT_REQUEST_PAUSE;
        }
        Intent intent = new Intent(context, MusicIntentListenerService.class);
        intent.putExtra(MusicIntentListenerService.INTENT_REQUEST, request);
        PendingIntent pauseIntent = PendingIntent.getService(context, request, intent, 0);
        Notification.Action.Builder actionBuilder = new Notification.Action.Builder(ic, title, pauseIntent);
        return actionBuilder.build();
    }

}
