package com.flairmusicplayer.flair.adapters;

import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.flairmusicplayer.flair.models.Song;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class PlayingQueueAdapter extends SongAdapter {

    public PlayingQueueAdapter(AppCompatActivity activity, ArrayList<Song> songList) {
        super(activity, songList);
    }

    @Override
    public SongItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(SongItemViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void setData(ArrayList<Song> songs) {
        super.setData(songs);
    }
}
