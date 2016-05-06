package com.giusti.jeremy.androidcar.MusicPlayer;

/**
 * Created by jérémy on 06/05/2016.
 */
public interface IMusicsPlayerEventListener {

    void onMusicStart(MusicFile currentFile);
    void onMusicPlay();
    void onMusicPause();
    void onMusicStop();
    void onMusicChange(MusicFile currentFile);
    void onRandomChange(boolean random);
    void onRepeatChange(boolean repeat);
    void onMusicplaying(int position, int duration);


}
