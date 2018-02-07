package com.flairmusicplayer.flair.adapters;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class SongAdapter
        extends FastScrollRecyclerView.Adapter<SongAdapter.SongItemViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {

    public ArrayList<Song> songList = new ArrayList<>();
    private AppCompatActivity activity;

    public SongAdapter(AppCompatActivity activity, ArrayList<Song> songList) {
        this.activity = activity;
        this.songList = songList;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(songList.get(position).getTitle().charAt(0)).toUpperCase();
    }

    @Override
    public SongItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_single, null);
        return new SongItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SongItemViewHolder holder, int position) {
        Uri albumArtUri = Song.getAlbumArtUri(songList.get(position).getAlbumId());
        final String songTitle = songList.get(position).getTitle();
        if (holder.itemImage != null)
            Glide.with(activity)
                    .load(albumArtUri)
                    .apply(RequestOptions.circleCropTransform())
                    .apply(new RequestOptions().placeholder(FlairUtils.getRoundTextDrawable(activity, songTitle)))
                    .into(holder.itemImage);

        if (holder.itemTitle != null)
            holder.itemTitle.setText(songTitle);

        if (holder.itemDetailText != null)
            holder.itemDetailText.setText(songList.get(position).getArtistName());
    }

    private void playAll(int position) {
        FlairMusicController.openQueue(songList, position, true);
    }

    @Override
    public int getItemCount() {
        return (songList != null ? songList.size() : 0);
    }

    public void setData(ArrayList<Song> songs) {
        songList = songs;
        notifyDataSetChanged();
    }

    public class SongItemViewHolder extends SingleItemViewHolder {

        SongItemViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View view) {
            playAll(getAdapterPosition());
        }
    }
}
