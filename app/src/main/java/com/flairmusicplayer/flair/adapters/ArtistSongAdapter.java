package com.flairmusicplayer.flair.adapters;

import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.flairmusicplayer.flair.models.Song;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class ArtistSongAdapter extends SongAdapter {

    public ArtistSongAdapter(AppCompatActivity activity, ArrayList<Song> songList) {
        super(activity, songList);
    }

    @Override
    public SongItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(SongItemViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder.itemDetailText != null)
            holder.itemDetailText.setText(songList.get(position).getAlbumName());
    }
}
