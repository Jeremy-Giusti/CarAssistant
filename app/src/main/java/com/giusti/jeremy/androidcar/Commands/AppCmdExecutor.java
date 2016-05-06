package com.giusti.jeremy.androidcar.Commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.giusti.jeremy.androidcar.Activity.CommandsListActivity;
import com.giusti.jeremy.androidcar.Activity.AudioPlayerActivity;
import com.giusti.jeremy.androidcar.Activity.SettingActivity;
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
        if (ACService.getInstance().getAudioPlayer() != null) {
            switch (requestedState) {
                case PLAY:
                    ACService.getInstance().getAudioPlayer().start();
                    break;
                case PAUSE:
                    ACService.getInstance().getAudioPlayer().pause();
                    break;
                case STOP:
                    ACService.getInstance().getAudioPlayer().stop();
                    break;
                case NEXT:
                    ACService.getInstance().getAudioPlayer().next();
                    break;
                case PREVIOUS:
                    ACService.getInstance().getAudioPlayer().previous();
                    break;
            }
        } else if (requestedState == MusicState.PLAY) {
            Intent startMusicIntent = new Intent(context, AudioPlayerActivity.class);
            startMusicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startMusicIntent.putExtra(AudioPlayerActivity.STARTPLAYER_EXTRA_KEY, AudioPlayerActivity.PLAY_ANYTHING);
            ACService.getInstance().startActivity(startMusicIntent);
        } else {
            Toast.makeText(context, R.string.music_player_off, Toast.LENGTH_LONG).show();
        }
    }

    public void playMusic(String music) {
        if (ACService.getInstance().getAudioPlayer() != null) {
            ACService.getInstance().getAudioPlayer().play(music);
        } else {
            Intent startMusicIntent = new Intent(context, AudioPlayerActivity.class);
            startMusicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startMusicIntent.putExtra(AudioPlayerActivity.STARTPLAYER_EXTRA_KEY, music);
            ACService.getInstance().startActivity(startMusicIntent);
        }
    }


    public enum MusicState {
        PLAY,
        PAUSE,
        STOP,
        NEXT,
        PREVIOUS
    }
}
