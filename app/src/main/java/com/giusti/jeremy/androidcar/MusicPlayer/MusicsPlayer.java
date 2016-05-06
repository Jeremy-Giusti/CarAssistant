package com.giusti.jeremy.androidcar.MusicPlayer;

import android.content.Context;
import android.widget.Toast;

import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.R;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jérémy on 06/05/2016.
 */
public class MusicsPlayer implements IMusicsPlayer, IAudioPlayerListener {

    private static MusicsPlayer instance = null;
    private ArrayList<IMusicsPlayerEventListener> listenersList = new ArrayList<>();
    private PlaylistManager mPlmanager;
    private AudioPlayer mAudioPLayer;
    private Context context;


    public static MusicsPlayer getInstance(Context context) {
        if (instance == null) {
            instance = new MusicsPlayer(context, null);
        }
        return instance;
    }


    public MusicsPlayer(Context context, IMusicsPlayerEventListener listener) {
        this.context = context;
        if (listener != null) addListener(listener);
        boolean random = ACPreference.getAudioPlayerRandom(context);
        mPlmanager = new PlaylistManager(context, random);
        mAudioPLayer = new AudioPlayer(this);
    }

    /**
     * init thing and notify listeners
     * <br> auto launched when adding a listener
     */
    public void onStart() {
        manageRandomAndRepeat();
        notifyAllListeners(musicsEvent.CHANGED);
    }

    private void manageRandomAndRepeat() {
        boolean random = ACPreference.getAudioPlayerRandom(context);
        this.mPlmanager.setPlayRandom(random);
        notifyAllListeners(musicsEvent.RANDOM);

        boolean repeat = ACPreference.getAudioPlayerRepeat(context);
        this.mPlmanager.setPlayRepeat(repeat);
        notifyAllListeners(musicsEvent.REPEAT);
    }

    @Override
    public void start() {
        if (mAudioPLayer.isStarted()) {
            mAudioPLayer.play();
            notifyAllListeners(musicsEvent.PLAY);
        } else {
            try {
                mAudioPLayer.start(mPlmanager.getCurrentSong());
                notifyAllListeners(musicsEvent.START);
            } catch (IOException e) {
                Toast.makeText(context, "error while starting:" + mPlmanager.getCurrentSong().getTitle(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void onCurrentMusicChanged(MusicFile currentMusic) {
        if (currentMusic != null) {
            try {
                mAudioPLayer.start(currentMusic);
                notifyAllListeners(musicsEvent.START);

            } catch (IOException e) {
                Toast.makeText(context, "error while starting:" + mPlmanager.getCurrentSong().getTitle(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        } else {
            notifyAllListeners(musicsEvent.STOP);
        }
    }


    @Override
    public void pause() {
        mAudioPLayer.pause();
        notifyAllListeners(musicsEvent.PAUSE);
    }

    @Override
    public void next() {
        MusicFile currentMusic = mPlmanager.nextSong();
        onCurrentMusicChanged(currentMusic);
    }

    @Override
    public void previous() {
        MusicFile currentMusic = mPlmanager.previousSong();
        onCurrentMusicChanged(currentMusic);
    }

    @Override
    public void play(String audioName) {
        MusicFile currentSong = mPlmanager.setCurrentSong(audioName);
        onCurrentMusicChanged(currentSong);
    }

    @Override
    public void play(MusicFile data) {
        mPlmanager.setCurrentSong(data);
        onCurrentMusicChanged(data);
    }

    @Override
    public void stop() {
        mAudioPLayer.stop();
        notifyAllListeners(musicsEvent.STOP);
    }

    @Override
    public boolean isPlaying() {
        return mAudioPLayer.isPlaying();
    }

    @Override
    public void destroy() {
        this.mAudioPLayer.destroy();
    }

    @Override
    public ArrayList<MusicFile> getDisplayablePlaylist() {
        return mPlmanager.getDisplayablePlaylist();
    }


    @Override
    public void seekTo(int position) {
        try {
            this.mAudioPLayer.seekTo(position);
        } catch (IOException e) {
            Toast.makeText(context, R.string.error_seeking, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void random(boolean random) {
        ACPreference.setAudioPlayerRandom(context, random);
        mPlmanager.setPlayRandom(random);
        notifyAllListeners(musicsEvent.RANDOM);
    }

    @Override
    public void repeat(boolean repeat) {
        ACPreference.setAudioPlayerRepeat(context, repeat);
        mPlmanager.setPlayRepeat(repeat);
        notifyAllListeners(musicsEvent.REPEAT);
    }

    @Override
    public void onMusicPlaying(final int msec) {
        for (IMusicsPlayerEventListener listener : listenersList) {
            listener.onMusicplaying(msec, (int) mPlmanager.getCurrentSong().getDuration());
        }

    }


    public void notifyAllListeners(IMusicsPlayer.musicsEvent event) {

        switch (event) {
            case PLAY:
                for (IMusicsPlayerEventListener listener : listenersList) {
                    listener.onMusicPlay();
                }
                break;
            case CHANGED:
                for (IMusicsPlayerEventListener listener : listenersList) {
                    listener.onMusicChange(mPlmanager.getCurrentSong());
                }
                break;
            case START:
                for (IMusicsPlayerEventListener listener : listenersList) {
                    listener.onMusicStart(mPlmanager.getCurrentSong());
                }
                break;
            case PAUSE:
                for (IMusicsPlayerEventListener listener : listenersList) {
                    listener.onMusicPause();
                }
                break;
            case STOP:
                for (IMusicsPlayerEventListener listener : listenersList) {
                    listener.onMusicStop();
                }
                break;
            case RANDOM:
                for (IMusicsPlayerEventListener listener : listenersList) {
                    listener.onRandomChange(mPlmanager.isPlayRandom());
                }
                break;
            case REPEAT:
                for (IMusicsPlayerEventListener listener : listenersList) {
                    listener.onRepeatChange(mPlmanager.isPlayRepeat());
                }
                break;
        }

    }

    public void addListener(IMusicsPlayerEventListener listener) {

        if (listenersList.isEmpty()) {
            //someone is listening, we start everything
            this.listenersList.add(listener);
            onStart();
        } else if (!listenersList.contains(listener)) {
            this.listenersList.add(listener);
        }
    }

    public void removeListener(IMusicsPlayerEventListener listener) {
        this.listenersList.remove(listener);
    }

    @Override
    public void onMusicEnded() {

    }


    @Override
    public void onError() {

    }
}
