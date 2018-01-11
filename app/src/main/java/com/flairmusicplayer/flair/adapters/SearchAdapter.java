package com.flairmusicplayer.flair.adapters;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Album;
import com.flairmusicplayer.flair.models.Artist;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.ui.activities.SearchActivity;
import com.flairmusicplayer.flair.utils.FlairUtils;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

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

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case SONG:
                return new ItemViewHolder(LayoutInflater.from(activity).inflate(R.layout.list_item_song, null));
            case ARTIST:
                return new ItemViewHolder(LayoutInflater.from(activity).inflate(R.layout.list_item_artist, null));
            case ALBUM:
                return new ItemViewHolder(LayoutInflater.from(activity).inflate(R.layout.list_item_album, null));
            case HEADER:
            default:
                return new ItemViewHolder(LayoutInflater.from(activity).inflate(R.layout.search_header, null));
        }
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Timber.d("onBind called");
        switch (getItemViewType(position)) {
            case SONG:
                Song song = (Song) data.get(position);
                Uri albumArtUri = Song.getAlbumArtUri(song.getAlbumId());
                final String songTitle = song.getTitle();
                Glide.with(activity)
                        .load(albumArtUri)
                        .apply(RequestOptions.circleCropTransform())
                        .apply(new RequestOptions().placeholder(FlairUtils.getRoundTextDrawable(activity, songTitle)))
                        .into(holder.songAlbumArt);
                holder.songTitle.setText(songTitle);
                holder.songArtist.setText(song.getArtistName());
                break;
            case ARTIST:
                Artist artist = (Artist) data.get(position);
                String artistName = artist.getArtistName();
                String bulletChar = " \u2022 ";
                int albumCount = artist.getAlbumCount();
                int songCount = artist.getSongCount();
                String albumOrAlbums = albumCount > 1 ? " Albums" : " Album";
                String songOrSongs = songCount > 1 ? " Songs" : " Song";
                holder.artistName.setText(artistName);
                holder.artistAlbumSongInfo.setText(albumCount + albumOrAlbums + bulletChar + songCount + songOrSongs);
                holder.artistInitials.setImageDrawable(FlairUtils.getRoundTextDrawable(activity, artistName));
                break;
            case ALBUM:
                Album album = (Album) data.get(position);
                String albumName = album.getAlbumName();
                holder.albumTitle.setText(albumName);
                holder.albumArtist.setText(album.getArtistName());
                holder.albumInitials.setImageDrawable(FlairUtils.getRoundTextDrawable(activity, albumName));
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

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        @BindView(R.id.header_title)
        TextView headerTitle;

        @Nullable
        @BindView(R.id.item_song_title)
        TextView songTitle;

        @Nullable
        @BindView(R.id.item_song_artist)
        TextView songArtist;

        @Nullable
        @BindView(R.id.item_song_image)
        ImageView songAlbumArt;

        @Nullable
        @BindView(R.id.artist_name)
        TextView artistName;

        @Nullable
        @BindView(R.id.artist_album_song_info)
        TextView artistAlbumSongInfo;

        @Nullable
        @BindView(R.id.artist_initials)
        ImageView artistInitials;

        @Nullable
        @BindView(R.id.album_title)
        TextView albumTitle;

        @Nullable
        @BindView(R.id.album_artist)
        TextView albumArtist;

        @Nullable
        @BindView(R.id.album_initials)
        ImageView albumInitials;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

}