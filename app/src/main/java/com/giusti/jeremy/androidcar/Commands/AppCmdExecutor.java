package com.giusti.jeremy.androidcar.Commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.giusti.jeremy.androidcar.Activity.CommandsListActivity;
import com.giusti.jeremy.androidcar.Activity.SettingActivity;
import com.giusti.jeremy.androidcar.MusicPlayer.MusicsPlayer;
import com.giusti.jeremy.androidcar.R;

/**
 * Created by jérémy on 05/05/2016.
 * excecute command from a module of this application
 * <br> see also {@link ApiCmdExecutor} and {@link TerminalCmdExecutor}
 */
public class AppCmdExecutor {
    private final ICommandExcecutionResult resultListener;
    private Context context;


    public AppCmdExecutor(Context context, ICommandExcecutionResult commandResultListener) {
        this.context = context;
        this.resultListener = commandResultListener;
    }


    public void openCmdListActivity(String[] strings) {
        Intent intent = new Intent(context, CommandsListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle b = new Bundle();
        b.putStringArray(CommandsListActivity.COMMAND_LIST_STR, strings);
        intent.putExtras(b);
        context.startActivity(intent);
        resultListener.onResult(ICommandExcecutionResult.EResult.SUCCESS, R.string.open_cmd_list_key, null);
    }

    public void openSettingActivity() {
        Intent intent = new Intent(context, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        resultListener.onResult(ICommandExcecutionResult.EResult.SUCCESS, R.string.open_stg_key, null);
    }

    public void changeMusicState(MusicState requestedState) {
        int key = 0;
        switch (requestedState) {
            case PLAY:
                key = R.string.play_music_key;
                MusicsPlayer.getInstance(context).start();
                break;
            case PAUSE:
                key = R.string.pause_music_key;
                MusicsPlayer.getInstance(context).pause();
                break;
            case STOP:
                key = R.string.stop_music_key;
                MusicsPlayer.getInstance(context).stop();
                break;
            case NEXT:
                key = R.string.next_music_key;
                MusicsPlayer.getInstance(context).next();
                break;
            case PREVIOUS:
                key = R.string.prev_music_key;
                MusicsPlayer.getInstance(context).previous();
                break;
        }
        resultListener.onResult(ICommandExcecutionResult.EResult.SUCCESS, key, null);

    }

    public void playMusic(String music) {
        MusicsPlayer.getInstance(context).play(music);
        resultListener.onResult(ICommandExcecutionResult.EResult.SUCCESS, R.string.play_music_key, null);
    }


    public enum MusicState {
        PLAY,
        PAUSE,
        STOP,
        NEXT,
        PREVIOUS
    }
}
