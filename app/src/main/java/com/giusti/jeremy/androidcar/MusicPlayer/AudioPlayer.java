package com.giusti.jeremy.androidcar.MusicPlayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;

import java.io.IOException;

/**
 * Created by jérémy on 05/05/2016.
 */
public class AudioPlayer {
    private static final int UPDATE_FREQUENCY_MILLISEC = 1000;
    private IAudioPlayerListener listener;
    private MediaPlayer mediaPlayer;
    private Handler mediaPostionUpdaterHandler = new Handler();
    private boolean started = false;
    private int streamType = -1;

    public AudioPlayer() {
        mediaPlayer = new MediaPlayer();
    }

    public AudioPlayer(int streamType) {
        this();
        this.streamType = streamType;
        mediaPlayer.setAudioStreamType(streamType);
    }

    public AudioPlayer(IAudioPlayerListener listener) {
        this();
        this.listener = listener;
        mediaPlayer.setOnCompletionListener(completionListener);
        mediaPostionUpdaterHandler.postDelayed(updateMediaPositionRunnable, UPDATE_FREQUENCY_MILLISEC);
    }


    public void start(MusicFile audio) throws IOException {
        if (started) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        mediaPlayer.setDataSource(audio.getFilePath());
        mediaPlayer.prepare();
        mediaPlayer.start();
        started = true;
    }

    public void start(Context context, int audioRes) throws IOException {
        if (started) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        AssetFileDescriptor afd = context.getResources().openRawResourceFd(audioRes);
        if (afd == null) return;
        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        afd.close();
        mediaPlayer.prepare();
        mediaPlayer.start();
        started = true;
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void play() {
        mediaPlayer.start();
    }

    public void stop() {
        if (started && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        started = false;
    }

    public void destroy() {
        stop();
        mediaPlayer.release();
        mediaPostionUpdaterHandler.removeCallbacks(updateMediaPositionRunnable);
    }

    public void seekTo(long msec) throws IOException {
        mediaPlayer.seekTo((int) msec);
    }

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (listener != null) {
                listener.onMusicEnded();
            }
        }
    };

    private Runnable updateMediaPositionRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying() && listener != null) {
                listener.onMusicPlaying(mediaPlayer.getCurrentPosition());
            }
            mediaPostionUpdaterHandler.postDelayed(this, UPDATE_FREQUENCY_MILLISEC);
        }
    };

    public boolean isStarted() {
        return started;
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
}
