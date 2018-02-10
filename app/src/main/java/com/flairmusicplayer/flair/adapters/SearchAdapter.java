package com.flairmusicplayer.flair.adapters;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Album;
import com.flairmusicplayer.flair.models.Artist;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.ui.activities.SearchActivity;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.flairmusicplayer.flair.utils.MusicUtils;
import com.flairmusicplayer.flair.utils.NavUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                String detailText = albumCount + albumOrAlbums + bulletChar + songCount + songOrSongs;
                holder.itemDetailText.setText(detailText);
                final TextDrawable textDrawable = FlairUtils.getRoundTextDrawable(activity, artistName);
                Glide.with(activity)
                        .load(MusicUtils.getAlbumArtUri(artist.albumsOfArtist.get(0).getAlbumId()))
                        .apply(new RequestOptions().circleCrop())
                        .apply(new RequestOptions().error(textDrawable))
                        .into(holder.itemImage);
                holder.menu.setVisibility(View.INVISIBLE);
                break;
            case ALBUM:
                Album album = (Album) data.get(position);
                String albumName = album.getAlbumName();
                holder.itemTitle.setText(albumName);
                holder.itemDetailText.setText(album.getArtistName());
                TextDrawable textDrawable1 = FlairUtils.getRoundTextDrawable(activity, albumName);
                Glide.with(activity)
                        .load(MusicUtils.getAlbumArtUri(album.getAlbumId()))
                        .apply(new RequestOptions().circleCrop())
                        .apply(new RequestOptions().error(textDrawable1))
                        .into(holder.itemImage);
                holder.menu.setVisibility(View.INVISIBLE);
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


    private ArrayList<Song> getAllSongsInDataSet(List<Object> data) {
        ArrayList<Song> songs = new ArrayList<>();
        for (Object item : data) {
            if (item instanceof Song)
                songs.add((Song) item);
        }
        return songs;
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
            Object item = data.get(getAdapterPosition());
            switch (getItemViewType()) {
                case ALBUM:
                    if (itemImage != null) {
                        NavUtils.goToAlbum(activity,
                                ((Album) item),
                                itemImage);
                    }
                    break;
                case ARTIST:
                    if (itemImage != null) {
                        NavUtils.goToArtist(activity,
                                ((Artist) item),
                                itemImage);
                    }
                    break;
                case SONG:
                    ArrayList<Song> songsToPlay = getAllSongsInDataSet(data);
                    FlairMusicController.openQueue(songsToPlay, songsToPlay.indexOf(item), true);
                    break;
            }
        }
    }
}