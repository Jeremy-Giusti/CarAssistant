package com.giusti.jeremy.androidcar.MusicPlayer;

/**
 * Created by jérémy on 05/05/2016.
 */
public class MusicFile {
    private long id;
    private String artist;
    private String title;
    private String filePath;
    private String fileName;
    private long duration;

    public MusicFile(long id, String artist, String title, String filePath, String fileName, long duration) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.filePath = filePath;
        this.fileName = fileName;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
