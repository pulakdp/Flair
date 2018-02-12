package com.flairmusicplayer.flair.adapters;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Album;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class ArtistAlbumAdapter extends AlbumAdapter {

    private AppCompatActivity activity;

    public ArtistAlbumAdapter(AppCompatActivity activity, ArrayList<Album> albumList) {
        super(activity, albumList);
        this.activity = activity;
    }

    @Override
    public AlbumItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(activity).inflate(R.layout.grid_item_album_horizontal, parent, false);
        return new AlbumItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumItemViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        int songCount = albumList.get(position).getSongCount();
        String songCountString;
        if (songCount > 1)
            songCountString = songCount + ArtistAdapter.SONGS;
        else
            songCountString = songCount + ArtistAdapter.SONG;

        holder.albumDetail.setText(songCountString);
    }
}
