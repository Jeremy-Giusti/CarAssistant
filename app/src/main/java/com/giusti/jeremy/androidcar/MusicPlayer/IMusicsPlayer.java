package com.giusti.jeremy.androidcar.MusicPlayer;

import java.util.ArrayList;

/**
 * Created by jérémy on 05/05/2016.
 */
public interface IMusicsPlayer {

    void start();

    void pause();

    void next();

    void previous();

    void play(String audioName);

    void play(MusicFile data);

    void stop();

    void seekTo(int position);

    void random(boolean random);

    void repeat(boolean random);

    boolean isPlaying();

    void destroy();

    ArrayList<MusicFile> getDisplayablePlaylist();


    public enum musicsEvent{
        START,
        CHANGED,
        PLAY,
        PAUSE,
        STOP,
        RANDOM,
        REPEAT
    }
}
