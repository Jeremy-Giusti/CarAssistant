package com.giusti.jeremy.androidcar.MusicPlayer;

/**
 * Created by jérémy on 05/05/2016.
 */
public interface IMusicPlayBackListener {
    void onMusicEnded();

    void onMusicPlaying(int msec);

    void onError();
}
