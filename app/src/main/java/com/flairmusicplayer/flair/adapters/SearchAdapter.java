package com.flairmusicplayer.flair.adapters;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Album;
import com.flairmusicplayer.flair.models.Artist;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.ui.activities.SearchActivity;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.flairmusicplayer.flair.utils.MusicUtils;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: PulakDebasish
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemViewHolder> {

    private static final int HEADER = 0;
    private static final int SONG = 1;
    private static final int ARTIST = 2;
    private static final int ALBUM = 3;

    private SearchActivity activity;
    private ArrayList<Object> data;

    public SearchAdapter(SearchActivity activity, ArrayList<Object> data) {
        this.activity = activity;
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof Song) return SONG;
        if (data.get(position) instanceof Artist) return ARTIST;
        if (data.get(position) instanceof Album) return ALBUM;
        return HEADER;
    }

    public void setData(ArrayList<Object> newData) {
        data = newData;
        notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case SONG:
            case ARTIST:
            case ALBUM:
                return new ItemViewHolder(LayoutInflater.from(activity).inflate(R.layout.list_item_single, null));
            case HEADER:
            default:
                return new ItemViewHolder(LayoutInflater.from(activity).inflate(R.layout.search_header, null));
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case SONG:
                Song song = (Song) data.get(position);
                Uri albumArtUri = MusicUtils.getAlbumArtUri(song.getAlbumId());
                final String songTitle = song.getTitle();
                Glide.with(activity)
                        .load(albumArtUri)
                        .apply(RequestOptions.circleCropTransform())
                        .apply(new RequestOptions().placeholder(FlairUtils.getRoundTextDrawable(activity, songTitle)))
                        .into(holder.itemImage);
                holder.itemTitle.setText(songTitle);
                holder.itemDetailText.setText(song.getArtistName());
                break;
            case ARTIST:
                Artist artist = (Artist) data.get(position);
                String artistName = artist.getArtistName();
                String bulletChar = " \u2022 ";
                int albumCount = artist.getAlbumCount();
                int songCount = artist.getSongCount();
                String albumOrAlbums = albumCount > 1 ? " Albums" : " Album";
                String songOrSongs = songCount > 1 ? " Songs" : " Song";
                holder.itemTitle.setText(artistName);
                holder.itemDetailText.setText(albumCount + albumOrAlbums + bulletChar + songCount + songOrSongs);
                holder.itemImage.setImageDrawable(FlairUtils.getRoundTextDrawable(activity, artistName));
                break;
            case ALBUM:
                Album album = (Album) data.get(position);
                String albumName = album.getAlbumName();
                holder.itemTitle.setText(albumName);
                holder.itemDetailText.setText(album.getArtistName());
                holder.itemImage.setImageDrawable(FlairUtils.getRoundTextDrawable(activity, albumName));
                break;
            case HEADER:
                holder.headerTitle.setText(data.get(position).toString());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data != Collections.emptyList() ? data.size() : 0;
    }

    public class ItemViewHolder extends SingleItemViewHolder {

        @Nullable
        @BindView(R.id.header_title)
        TextView headerTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {

        }
    }
}