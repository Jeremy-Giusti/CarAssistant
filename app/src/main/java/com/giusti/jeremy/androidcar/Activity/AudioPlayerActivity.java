package com.giusti.jeremy.androidcar.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.giusti.jeremy.androidcar.MusicPlayer.IMusicsPlayerEventListener;
import com.giusti.jeremy.androidcar.MusicPlayer.MusicFile;
import com.giusti.jeremy.androidcar.MusicPlayer.MusicListAdapter;
import com.giusti.jeremy.androidcar.MusicPlayer.MusicsPlayer;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Utils.Utils;

import java.util.ArrayList;

/**
 * Created by jérémy on 05/05/2016.<br>
 * add extra with key {@value STARTPLAYER_EXTRA_KEY} and song name as value to launch with song
 * <br> or with value {@value PLAY_ANYTHING} to launch with anything playing
 */
public class AudioPlayerActivity extends AppCompatActivity implements MusicListAdapter.IItemEventListener, IMusicsPlayerEventListener {
    public static final String STARTPLAYER_EXTRA_KEY = "startPlayer";
    public static final String PLAY_ANYTHING = "play anything";


    private RecyclerView mMusics_rv;
    private ImageButton mPlayButton;
    private TextView mDuration_tv;
    private TextView mMusicTitle_tv;
    private SeekBar mDuration_sb;
    private MusicsPlayer mMusicPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        initViews();

        mMusicPlayer = MusicsPlayer.getInstance(this);
        mMusicPlayer.addListener(this);
        if (mMusicPlayer.isPlaying()) mPlayButton.setSelected(true);

        manageExtra();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cmdlist_activity_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_close:
                mMusicPlayer.destroy();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    }

    private void fillViews(final MusicFile currentSong) {
        AudioPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDuration_tv.setText("00:00/" + Utils.getDisplayableTime(currentSong.getDuration()));
                mMusicTitle_tv.setText(currentSong.getTitle() + " - " + currentSong.getArtist());
                mDuration_sb.setMax((int) currentSong.getDuration());
                mDuration_sb.setProgress(0);

                ((MusicListAdapter) mMusics_rv.getAdapter()).updateList(mMusicPlayer.getDisplayablePlaylist());
            }
        });

    }

    /**
     * manage the extra:<br>
     * the activity may have een started to play a specifique song or to immediatly start
     */
    private void manageExtra() {
        if (!TextUtils.isEmpty(getIntent().getStringExtra(STARTPLAYER_EXTRA_KEY))) {
            String extra = getIntent().getExtras().getString(STARTPLAYER_EXTRA_KEY);
            if (PLAY_ANYTHING.equals(extra)) {
                this.clickPlayPause(mPlayButton);
            } else {
                mMusicPlayer.play(extra);
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
        mMusicPlayer.play(item.data);
        //onCurrentMusicChanged(item.data);

    }

    public void clickPlayPause(View v) {
        if (v.isSelected()) {
            mMusicPlayer.pause();
        } else {
            mMusicPlayer.start();
        }
    }

    public void clickRandom(View v) {
        boolean random = !v.isSelected();
        this.mMusicPlayer.random(random);
    }

    public void clickRepeat(View v) {
        boolean repeat = !v.isSelected();
        this.mMusicPlayer.repeat(repeat);
    }

    public void clickNext(View v) {
        mMusicPlayer.next();
    }


    public void clickPrevious(View v) {
        mMusicPlayer.previous();
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
            mMusicPlayer.seekTo(seekBar.getProgress());
        }
    };


    @Override
    protected void onDestroy() {
        mMusicPlayer.removeListener(this);
        // mMusicPlayer.destroy();//keep it ? destroy musicsplayer if no listener ?

        super.onDestroy();
    }


    @Override
    public void onMusicStart(MusicFile currentFile) {
        setStarted();
        this.fillViews(currentFile);
    }

    @Override
    public void onMusicPlay() {
        setStarted();
    }

    @Override
    public void onMusicPause() {
        this.mPlayButton.setSelected(false);
    }

    @Override
    public void onMusicStop() {
        setStopped();
    }

    @Override
    public void onMusicChange(MusicFile currentFile) {
        this.fillViews(currentFile);
    }

    @Override
    public void onRandomChange(boolean random) {
        findViewById(R.id.am_random_bt).setSelected(random);
    }

    @Override
    public void onRepeatChange(boolean repeat) {
        findViewById(R.id.am_repeat_bt).setSelected(repeat);
    }

    @Override
    public void onMusicplaying(final int position, final int duration) {
        AudioPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDuration_tv.setText(Utils.getDisplayableTime(position) + "/" + Utils.getDisplayableTime(duration));
                mDuration_sb.setProgress(position);
            }
        });
    }

}
