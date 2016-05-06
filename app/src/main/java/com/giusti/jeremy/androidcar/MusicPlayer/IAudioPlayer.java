package com.giusti.jeremy.androidcar.MusicPlayer;

/**
 * Created by jérémy on 05/05/2016.
 */
public interface IAudioPlayer {

    void start();

    void pause();

    void next();

    void previous();

    void play(String audioName);

    void stop();

    boolean isPlaying();
}
