package com.flairmusicplayer.flair.adapters;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.utils.MusicUtils;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class AlbumSongAdapter extends SongAdapter {

    public AlbumSongAdapter(AppCompatActivity activity, ArrayList<Song> songList) {
        super(activity, songList);
    }

    @Override
    public SongItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_single, parent, false);
        return new AlbumSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongItemViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final Song song = songList.get(position);
        if (holder.itemImageText != null) {
            final int trackNumber = song.getTrackNumber();
            final String trackNumberString = trackNumber > 0 ? String.valueOf(trackNumber) : "-";
            holder.itemImageText.setText(trackNumberString);
        }

        if (holder.itemDetailText != null)
            holder.itemDetailText.setText(MusicUtils.formatTimeToString((int) song.getDuration()));
    }

    public class AlbumSongViewHolder extends SongAdapter.SongItemViewHolder {

        AlbumSongViewHolder(View itemView) {
            super(itemView);
            if (itemImage != null)
                itemImage.setVisibility(View.GONE);
            if (itemImageText != null)
                itemImageText.setVisibility(View.VISIBLE);
        }
    }
}
