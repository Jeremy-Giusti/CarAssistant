package com.giusti.jeremy.androidcar.MusicPlayer;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jérémy on 05/05/2016.
 * manage a list of song (currently all song at once)
 * allow to get current song and manage next and previous song
 */
public class PlaylistManager {

    private List<MusicFile> songs = new ArrayList<>();
    private MusicFile currentSong;
    private List<MusicFile> previousSongs;
    private List<MusicFile> nextSongs;
    private boolean playRandom = false;
    private boolean playRepeat = false;

    public PlaylistManager(Context context, boolean random) {
        playRandom = random;
        initMusicList(context);
        initPlaylist();
    }


    /**
     * add song to next song list ,select a first one, and init previous songs list to empty
     */
    private void initPlaylist() {
        if (songs != null && !songs.isEmpty()) {

            nextSongs = new ArrayList<>();
            previousSongs = new ArrayList<>();

            nextSongs.addAll(songs);

            int currentSongIndex = 0;
            if (playRandom) {
                currentSongIndex = (int) (Math.random() * nextSongs.size());
            }
            currentSong = nextSongs.get(currentSongIndex);
            nextSongs.remove(currentSong);
        }
    }

    /**
     * get all music from media store and fill songs
     *
     * @param context
     */
    private void initMusicList(Context context) {
        //Some audio may be explicitly marked as not being music
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        while (cursor.moveToNext()) {
            songs.add(new MusicFile(Long.parseLong(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    Long.parseLong(cursor.getString(5))));
        }
    }


    /**
     * set the next song as currentSong, update nextList and previous list
     *
     * @return
     */
    public MusicFile nextSong() {
        if (songs == null || songs.isEmpty()) {
            return null;
        }
        previousSongs.add(currentSong);
        if (nextSongs.size() == 0) {
            if (playRepeat) {
                initPlaylist();
            } else {
                return null;
            }
        }
        int currentSongIndex = 0;
        if (playRandom) {
            currentSongIndex = (int) (Math.random() * nextSongs.size());
        }
        currentSong = nextSongs.get(currentSongIndex);
        nextSongs.remove(currentSong);
        return currentSong;
    }

    /**
     * set the previous song as currentSong, update nextList and previous list
     *
     * @return
     */
    public MusicFile previousSong() {
        if (songs == null || songs.isEmpty() || previousSongs.isEmpty()) {
            return null;
        }
        nextSongs.add(currentSong);
        currentSong = previousSongs.get(previousSongs.size() - 1);
        previousSongs.remove(currentSong);
        return currentSong;
    }


    public MusicFile getCurrentSong() {
        return currentSong;
    }

    public boolean isPlayRandom() {
        return playRandom;
    }

    public void setPlayRandom(boolean playRandom) {
        this.playRandom = playRandom;
    }

    public boolean isPlayRepeat() {
        return playRepeat;
    }

    public void setPlayRepeat(boolean playRepeat) {
        this.playRepeat = playRepeat;
    }

    /**
     * search in the song list if a song match the title
     *
     * @param title
     * @return the music if found else null
     */
    public MusicFile setCurrentSong(String title) {
        for (MusicFile music : songs) {
            if (StringUtils.containsIgnoreCase(music.getTitle(), title)) {
                setCurrentSong(music);
                return currentSong;
            }
        }
        return null;
    }

    public void setCurrentSong(MusicFile song) {
        previousSongs.add(currentSong);
        currentSong = song;
        nextSongs.remove(currentSong);
        previousSongs.remove(currentSong);
    }

    public ArrayList<MusicFile> getDisplayablePlaylist() {
        if (songs == null || songs.isEmpty()) {
            return null;
        }
        ArrayList<MusicFile> displayableList = new ArrayList<>();
        displayableList.addAll(nextSongs);
        displayableList.addAll(previousSongs);
        return displayableList;
    }

}
