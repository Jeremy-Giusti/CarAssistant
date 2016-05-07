package com.giusti.jeremy.androidcar.Commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.giusti.jeremy.androidcar.Activity.CommandsListActivity;
import com.giusti.jeremy.androidcar.Activity.AudioPlayerActivity;
import com.giusti.jeremy.androidcar.Activity.SettingActivity;
import com.giusti.jeremy.androidcar.MusicPlayer.MusicsPlayer;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Service.ACService;

/**
 * Created by jérémy on 05/05/2016.
 * excecute command from a module of this application
 * <br> see also {@link ApiCmdExecutor} and {@link TerminalCmdExecutor}
 */
public class AppCmdExecutor {
    private Context context;


    public AppCmdExecutor(Context context) {
        this.context = context;
    }


    public void openCmdListActivity(String[] strings) {
        Intent intent = new Intent(context, CommandsListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle b = new Bundle();
        b.putStringArray(CommandsListActivity.COMMAND_LIST_STR, strings);
        intent.putExtras(b);
        context.startActivity(intent);
    }

    public void openSettingActivity() {
        Intent intent = new Intent(context, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void changeMusicState(MusicState requestedState) {
//        if (ACService.getInstance().getAudioPlayer() != null) {
            switch (requestedState) {
                case PLAY:
                    MusicsPlayer.getInstance(context).start();
                    break;
                case PAUSE:
                    MusicsPlayer.getInstance(context).pause();
                    break;
                case STOP:
                    MusicsPlayer.getInstance(context).stop();
                    break;
                case NEXT:
                    MusicsPlayer.getInstance(context).next();
                    break;
                case PREVIOUS:
                    MusicsPlayer.getInstance(context).previous();
                    break;
            }
//        } else if (requestedState == MusicState.PLAY) {
//            Intent startMusicIntent = new Intent(context, AudioPlayerActivity.class);
//            startMusicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startMusicIntent.putExtra(AudioPlayerActivity.STARTPLAYER_EXTRA_KEY, AudioPlayerActivity.PLAY_ANYTHING);
//            ACService.getInstance().startActivity(startMusicIntent);
//        } else {
//            Toast.makeText(context, R.string.music_player_off, Toast.LENGTH_LONG).show();
//        }
    }

    public void playMusic(String music) {
//        if (ACService.getInstance().getAudioPlayer() != null) {
            MusicsPlayer.getInstance(context).play(music);
//        } else {
//            Intent startMusicIntent = new Intent(context, AudioPlayerActivity.class);
//            startMusicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startMusicIntent.putExtra(AudioPlayerActivity.STARTPLAYER_EXTRA_KEY, music);
//            ACService.getInstance().startActivity(startMusicIntent);
//        }
    }


    public enum MusicState {
        PLAY,
        PAUSE,
        STOP,
        NEXT,
        PREVIOUS
    }
}
