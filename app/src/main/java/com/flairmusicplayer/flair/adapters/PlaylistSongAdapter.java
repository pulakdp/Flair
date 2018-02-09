package com.flairmusicplayer.flair.adapters;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.flairmusicplayer.flair.utils.MusicUtils;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.PlaylistSongViewHolder> {

    private ArrayList<Song> playlistSongs;
    private AppCompatActivity activity;

    public PlaylistSongAdapter(AppCompatActivity activity) {
        this.activity = activity;
        playlistSongs = new ArrayList<>();
    }

    public void setData(ArrayList<Song> playlistSongs) {
        this.playlistSongs = playlistSongs;
        notifyDataSetChanged();
    }

    @Override
    public PlaylistSongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_single, parent, false);
        return new PlaylistSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistSongViewHolder holder, int position) {
        String songTitle = playlistSongs.get(position).getTitle();
        Uri albumArtUri = MusicUtils.getAlbumArtUri(playlistSongs.get(position).getAlbumId());
        if (holder.itemTitle != null)
            holder.itemTitle.setText(songTitle);
        if (holder.itemDetailText != null)
            holder.itemDetailText.setText(playlistSongs.get(position).getArtistName());
        if (holder.itemImage != null)
            Glide.with(activity)
                    .load(albumArtUri)
                    .apply(RequestOptions.circleCropTransform())
                    .apply(new RequestOptions().placeholder(FlairUtils.getRoundTextDrawable(activity, songTitle)))
                    .into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return playlistSongs.size();
    }

    public class PlaylistSongViewHolder extends SingleItemViewHolder {

        public PlaylistSongViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View view) {
            super.onClick(view);
        }
    }
}
