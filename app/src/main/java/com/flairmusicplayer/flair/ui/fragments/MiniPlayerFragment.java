package com.flairmusicplayer.flair.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.flairmusicplayer.flair.utils.MusicUtils;
import com.flairmusicplayer.flair.utils.SwipeController;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: PulakDebasish
 */

public class MiniPlayerFragment extends MusicServiceFragment
        implements View.OnClickListener {
    public MiniPlayerFragment() {
    }

    @BindView(R.id.mini_player_album_art)
    ImageView albumArt;

    @BindView(R.id.mini_player_play_pause_button)
    ImageButton playPauseButton;

    @BindView(R.id.mini_player_song_title)
    TextView songTitle;

    @BindView(R.id.mini_player_progress_bar)
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mini_player, container, false);
        ButterKnife.bind(this, rootView);
        playPauseButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onPlayStateChanged() {
        super.onPlayStateChanged();
        if (FlairMusicController.isPlaying()) {
            playPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
            progressBar.postDelayed(progressUpdater, 10);
        } else {
            playPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(new SwipeController(getActivity()));
    }

    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
        updateMiniPlayer();
    }

    @Override
    public void onQueueChanged() {
        super.onQueueChanged();
    }

    private void updateMiniPlayer() {
        Song currentSong = FlairMusicController.getCurrentSong();
        if (!(new Song().equals(currentSong))) {
            updateAlbumArtAndTitle(currentSong);
            if (FlairMusicController.getSongProgress() != 0)
                setProgress();
            onPlayStateChanged();
        }
    }

    private void updateAlbumArtAndTitle(Song currentSong) {
        Glide.with(this)
                .load(MusicUtils.getAlbumArtUri(currentSong.getAlbumId()))
                .apply(RequestOptions.circleCropTransform())
                .apply(new RequestOptions()
                        .placeholder(FlairUtils.getRoundTextDrawable(getContext(),
                                currentSong.getTitle())))
                .into(albumArt);
        songTitle.setText(currentSong.getTitle());
    }

    @Override
    public void onClick(View view) {
        FlairMusicController.togglePlayPause();
    }

    public void setProgress() {
        long pos;
        if (FlairMusicController.getCurrentSong() == null)
            return;
        int position = FlairMusicController.getSongProgress();
        int duration = FlairMusicController.getSongDuration();
        progressBar.setMax(1000);
        if (duration > 0) {
            pos = 1000L * position / duration;
            progressBar.setProgress((int) pos);
        }
    }

    public Runnable progressUpdater = new Runnable() {
        @Override
        public void run() {
            if (FlairMusicController.isPlaying()) {
                setProgress();
                progressBar.postDelayed(progressUpdater, 1000);
            } else
                progressBar.removeCallbacks(this);
        }
    };
}