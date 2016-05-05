package com.giusti.jeremy.androidcar.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.MusicPlayer.IAudioPlayer;
import com.giusti.jeremy.androidcar.MusicPlayer.IMusicPlayBackListener;
import com.giusti.jeremy.androidcar.MusicPlayer.MusicFile;
import com.giusti.jeremy.androidcar.MusicPlayer.MusicListAdapter;
import com.giusti.jeremy.androidcar.MusicPlayer.MusicPlayer;
import com.giusti.jeremy.androidcar.MusicPlayer.PlaylistManager;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Service.ACService;
import com.giusti.jeremy.androidcar.Utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jérémy on 05/05/2016.<br>
 * add extra with key {@value STARTPLAYER_EXTRA_KEY} and song name as value to launch with song
 * <br> or with value {@value PLAY_ANYTHING} to launch with anything playing
 */
public class MusicPlayerActivity extends AppCompatActivity implements IMusicPlayBackListener, MusicListAdapter.IItemEventListener, IAudioPlayer {
    public static final String STARTPLAYER_EXTRA_KEY = "startPlayer";
    public static final String PLAY_ANYTHING = "play anything";

    private PlaylistManager mPlmanager;
    private MusicPlayer mMusicPLayer;

    private RecyclerView mMusics_rv;
    private ImageButton mPlayButton;
    private TextView mDuration_tv;
    private TextView mMusicTitle_tv;
    private SeekBar mDuration_sb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        boolean random = ACPreference.getAudioPlayerRandom(this);
        mPlmanager = new PlaylistManager(this,random);
        mMusicPLayer = new MusicPlayer(this);

        initViews();

        if (ACService.getInstance() != null) {
            ACService.getInstance().audioPlayerLaunched(this);
        }

        manageExtra();


    }


    private void initViews() {
        mMusics_rv = (RecyclerView) findViewById(R.id.am_music_list_rv);
        mPlayButton = (ImageButton) findViewById(R.id.am_play_pause_bt);
        mDuration_tv = (TextView) findViewById(R.id.am_duration_tv);
        mMusicTitle_tv = (TextView) findViewById(R.id.am_music_name_tv);
        mDuration_sb = (SeekBar) findViewById(R.id.am_music_duration_sb);

        mDuration_sb.setOnSeekBarChangeListener(seekBarListener);
        mMusics_rv.setHasFixedSize(true);
        mMusics_rv.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        MusicListAdapter adapter = new MusicListAdapter(new ArrayList<MusicFile>(), this);
        mMusics_rv.setAdapter(adapter);

        fillViews();
    }

    private void fillViews() {
        manageRandomAndRepeat();
        MusicFile currentSong = mPlmanager.getCurrentSong();
        mDuration_tv.setText("00:00/" + Utils.getDisplayableTime(currentSong.getDuration()));
        mMusicTitle_tv.setText(currentSong.getTitle() + " - " + currentSong.getArtist());
        mDuration_sb.setMax((int) currentSong.getDuration());
        mDuration_sb.setProgress(0);
        ((MusicListAdapter) mMusics_rv.getAdapter()).updateList(mPlmanager.getDisplayablePlaylist());

    }

    private void manageRandomAndRepeat() {
        boolean random = ACPreference.getAudioPlayerRandom(this);
        this.mPlmanager.setPlayRandom(random);
        findViewById(R.id.am_random_bt).setSelected(random);

        boolean repeat = ACPreference.getAudioPlayerRepeat(this);
        this.mPlmanager.setPlayRepeat(repeat);
        findViewById(R.id.am_repeat_bt).setSelected(repeat);

    }

    /**
     * manage the extra:<br>
     * the activity may have een started to play a specifique song or to immediatly start
     */
    private void manageExtra() {
        if (getIntent().getExtras() != null) {
            String extra = getIntent().getExtras().getString(STARTPLAYER_EXTRA_KEY);
            if (PLAY_ANYTHING.equals(extra)) {
                this.clickPlayPause(mPlayButton);
            } else {
                this.play(extra);
            }
        }
    }

    private void setStopped() {
        this.mPlayButton.setSelected(false);
        this.mDuration_sb.setEnabled(false);
    }

    private void setStarted() {
        this.mPlayButton.setSelected(true);
        this.mDuration_sb.setEnabled(true);
    }

    @Override
    public void onItemClick(MusicListAdapter.ViewHolder item) {
        mPlmanager.setCurrentSong(item.data);
        onCurrentMusicChanged(item.data);

    }

    public void clickPlayPause(View v) {
        if (v.isSelected()) {
            v.setSelected(false);
            this.pause();
        } else {
            this.start();
            setStarted();
        }
    }

    public void clickRandom(View v) {
        boolean random = !v.isSelected();
        v.setSelected(random);
        this.mPlmanager.setPlayRandom(random);
        ACPreference.setAudioPlayerRandom(this, random);
    }

    public void clickRepeat(View v) {
        boolean repeat = !v.isSelected();
        v.setSelected(repeat);
        this.mPlmanager.setPlayRepeat(repeat);
        ACPreference.setAudioPlayerRepeat(this, repeat);
    }

    public void clickNext(View v) {
        this.next();
    }


    public void clickPrevious(View v) {
        this.previous();
    }

    private void onCurrentMusicChanged(MusicFile currentMusic) {
        if (currentMusic != null) {
            fillViews();
            try {
                setStarted();
                mMusicPLayer.start(currentMusic);
            } catch (IOException e) {
                Toast.makeText(this, "error while starting:" + mPlmanager.getCurrentSong().getTitle(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        } else {
            setStopped();
        }
    }

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //Do nothing

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //Do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mMusicPLayer.isPlaying()) {
                try {
                    mMusicPLayer.seekTo(seekBar.getProgress());
                } catch (IOException e) {
                    Toast.makeText(MusicPlayerActivity.this, "error while seeking:" + mPlmanager.getCurrentSong().getTitle(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    public void onMusicEnded() {
        clickNext(null);
    }

    @Override
    public void onMusicPlaying(final int msec) {
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MusicFile currentSong = mPlmanager.getCurrentSong();
                mDuration_tv.setText(Utils.getDisplayableTime(msec) + "/" + Utils.getDisplayableTime(currentSong.getDuration()));
                mDuration_sb.setProgress(msec);
            }
        });

    }

    @Override
    public void onError() {
        //TODO
        Toast.makeText(this, "Error callback", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        mMusicPLayer.destroy();
        if (ACService.getInstance() != null) {
            ACService.getInstance().audioPlayerDestroyed();
        }
        super.onDestroy();
    }


    @Override
    public void start() {
        if (mMusicPLayer.isPlaying()) {
            mMusicPLayer.play();
        } else {
            try {
                mMusicPLayer.start(mPlmanager.getCurrentSong());
            } catch (IOException e) {
                Toast.makeText(this, "error while starting:" + mPlmanager.getCurrentSong().getTitle(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void pause() {
        mMusicPLayer.pause();
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
    public void stop() {
        mMusicPLayer.stop();
        this.setStopped();
    }
}
