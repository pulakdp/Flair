package com.flairmusicplayer.flair.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.adapters.AlbumArtPagerAdapter;
import com.flairmusicplayer.flair.customviews.RepeatButton;
import com.flairmusicplayer.flair.customviews.ShuffleButton;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.flairmusicplayer.flair.utils.NavUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: PulakDebasish
 */

public class NowPlayingFragment extends MusicServiceFragment
        implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        ViewPager.OnPageChangeListener,
        Toolbar.OnMenuItemClickListener {

    @BindView(R.id.player_toolbar)
    Toolbar toolbar;

    @BindView(R.id.player_progress_seekbar)
    SeekBar progressSeekBar;

    @BindView(R.id.current_time)
    TextView currentPlayTime;

    @BindView(R.id.total_time)
    TextView totalPlayTime;

    @BindView(R.id.song_title)
    TextView songTitle;

    @BindView(R.id.song_artist)
    TextView songArtist;

    @BindView(R.id.play_pause_fab)
    FloatingActionButton playPauseFab;

    @BindView(R.id.player_repeat_button)
    RepeatButton repeatButton;

    @BindView(R.id.player_shuffle_button)
    ShuffleButton shuffleButton;

    @BindView(R.id.player_next_button)
    ImageButton nextButton;

    @BindView(R.id.player_prev_button)
    ImageButton previousButton;

    @BindView(R.id.player_album_art_viewpager)
    ViewPager pager;

    private ArrayList<Song> currentPlayingQueue = new ArrayList<>();

    public NowPlayingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);
        ButterKnife.bind(this, rootView);
        previousButton.setOnClickListener(this);
        playPauseFab.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        progressSeekBar.setOnSeekBarChangeListener(this);
        pager.addOnPageChangeListener(this);
        currentPlayTime.setText("0:00");
        totalPlayTime.setText("0:00");
        setUpToolbar();
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        previousButton.setOnClickListener(null);
        playPauseFab.setOnClickListener(null);
        nextButton.setOnClickListener(null);
        progressSeekBar.setOnSeekBarChangeListener(null);
        pager.removeOnPageChangeListener(null);
    }

    private void setViews() {
        Song currentSong = FlairMusicController.getCurrentSong();
        if (!new Song().equals(currentSong) && currentSong != null) {
            songTitle.setText(currentSong.getTitle());
            songArtist.setText(currentSong.getArtistName());
            totalPlayTime.setText(FlairUtils.formatTimeToString((int) currentSong.getDuration()));
            if (FlairMusicController.getSongProgress() != 0) {
                setProgress();
                updateCurrentPlayTime(FlairMusicController.getSongProgress());
            } else {
                progressSeekBar.setProgress(0);
            }
        }
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            toolbar.inflateMenu(R.menu.now_playing_menu);
            toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_down_black_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
            toolbar.setOnMenuItemClickListener(this);
        }
    }

    private void updateAlbumArtPager() {
        pager.setAdapter(new AlbumArtPagerAdapter(getFragmentManager(), FlairMusicController.getPlayingQueue()));
        pager.setCurrentItem(FlairMusicController.getPosition(), true);
        onPageSelected(FlairMusicController.getPosition());
    }

    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
        setViews();
        if (pager.getAdapter() == null) {
            updateAlbumArtPager();
            repeatButton.updateRepeatState();
            shuffleButton.updateShuffleState();
        } else
            pager.setCurrentItem(FlairMusicController.getPosition(), true);
    }

    @Override
    public void onPlayStateChanged() {
        super.onPlayStateChanged();
        togglePlayPauseAndStartProgress(FlairMusicController.isPlaying());
    }


    @Override
    public void onRepeatModeChanged() {
        super.onRepeatModeChanged();
        repeatButton.updateRepeatState();
    }

    @Override
    public void onShuffleModeChanged() {
        super.onShuffleModeChanged();
        shuffleButton.updateShuffleState();
    }

    @Override
    public void onQueueChanged() {
        super.onQueueChanged();
        setViews();
        updateAlbumArtPager();
    }

    private void togglePlayPauseAndStartProgress(boolean isPlaying) {
        if (isPlaying) {
            playPauseFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
            progressSeekBar.postDelayed(progressUpdater, 10);
        } else {
            playPauseFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.player_prev_button:
                FlairMusicController.playPreviousSong();
                break;
            case R.id.play_pause_fab:
                FlairMusicController.togglePlayPause();
                break;
            case R.id.player_next_button:
                FlairMusicController.playNextSong();
                break;
        }
    }

    public void setProgress() {
        long pos;
        if (FlairMusicController.getCurrentSong() == null)
            return;
        int position = FlairMusicController.getSongProgress();
        int duration = FlairMusicController.getSongDuration();
        progressSeekBar.setMax(1000);
        if (duration > 0) {
            pos = 1000L * position / duration;
            progressSeekBar.setProgress((int) pos);
        }
    }

    public void updateCurrentPlayTime(int position) {
        currentPlayTime.setText(FlairUtils.formatTimeToString(position));
    }

    public Runnable progressUpdater = new Runnable() {
        @Override
        public void run() {
            if (FlairMusicController.isPlaying()) {
                setProgress();
                progressSeekBar.postDelayed(progressUpdater, 1000);
                updateCurrentPlayTime(FlairMusicController.getSongProgress());
            } else
                progressSeekBar.removeCallbacks(this);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser)
            return;
        long duration = FlairMusicController.getSongDuration();
        long newPos = (duration * progress) / 1000L;
        FlairMusicController.seek(newPos);
        progressSeekBar.setProgress(progress);
        updateCurrentPlayTime((int) newPos);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position != FlairMusicController.getPosition()) {
            FlairMusicController.playSongAt(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_clear_playing_queue:
                FlairMusicController.clearQueue();
                break;
            case R.id.action_equalizer:
                NavUtils.openEqualizer(getActivity());
                break;
        }
        return false;
    }
}