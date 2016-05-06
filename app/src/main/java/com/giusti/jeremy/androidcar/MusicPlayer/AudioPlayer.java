package com.giusti.jeremy.androidcar.MusicPlayer;

import android.media.MediaPlayer;
import android.os.Handler;

import java.io.IOException;

/**
 * Created by jérémy on 05/05/2016.
 */
public class AudioPlayer {
    private static final int UPDATE_FREQUENCY_MILLISEC = 1000;
    private IAudioPlayerListener listener;
    private MediaPlayer mediaPLayer;
    private Handler mediaPostionUpdaterHandler = new Handler();
    private boolean started = false;

    public AudioPlayer(IAudioPlayerListener listener) {
        this.listener = listener;
        mediaPLayer = new MediaPlayer();
        mediaPLayer.setOnCompletionListener(completionListener);
        mediaPostionUpdaterHandler.postDelayed(updateMediaPostionRunnable, UPDATE_FREQUENCY_MILLISEC);
    }

    public void start(MusicFile audio) throws IOException {
        if (started) {
            mediaPLayer.stop();
            mediaPLayer.reset();
        }
        mediaPLayer.setDataSource(audio.getFilePath());
        mediaPLayer.prepare();
        mediaPLayer.start();
        started = true;
    }

    public void pause() {
        mediaPLayer.pause();
    }

    public void play() {
        mediaPLayer.start();
    }

    public void stop() {
        if (started && mediaPLayer.isPlaying()) {
            mediaPLayer.stop();
            mediaPLayer.reset();
        }
        started = false;
    }

    public void destroy() {
        stop();
        mediaPLayer.release();
        mediaPostionUpdaterHandler.removeCallbacks(updateMediaPostionRunnable);
    }

    public void seekTo(long msec) throws IOException {
        mediaPLayer.seekTo((int) msec);
    }

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            listener.onMusicEnded();
        }
    };

    private Runnable updateMediaPostionRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPLayer.isPlaying()) {
                listener.onMusicPlaying(mediaPLayer.getCurrentPosition());
            }
            mediaPostionUpdaterHandler.postDelayed(this, UPDATE_FREQUENCY_MILLISEC);
        }
    };

    public boolean isStarted() {
        return started;
    }

    public boolean isPlaying() {
        return mediaPLayer.isPlaying();
    }
}
