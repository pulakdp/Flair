package com.flairmusicplayer.flair.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
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

    private ArrayList<Song> songList = new ArrayList<>();
    private Context context;

    public SongAdapter(ArrayList<Song> songList) {
        this.songList = songList;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(songList.get(position).getTitle().charAt(0)).toUpperCase();
    }

    @Override
    public SongItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_single, null);
        return new SongItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SongItemViewHolder holder, int position) {
        Uri albumArtUri = Song.getAlbumArtUri(songList.get(position).getAlbumId());
        final String songTitle = songList.get(position).getTitle();
        if (holder.itemImage != null)
            Glide.with(context)
                    .load(albumArtUri)
                    .apply(RequestOptions.circleCropTransform())
                    .apply(new RequestOptions().placeholder(FlairUtils.getRoundTextDrawable(context, songTitle)))
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
            playAll(getLayoutPosition());
        }
    }
}
