package com.flairmusicplayer.flair.adapters;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.loaders.AlbumLoader;
import com.flairmusicplayer.flair.loaders.ArtistLoader;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.flairmusicplayer.flair.utils.MusicUtils;
import com.flairmusicplayer.flair.utils.NavUtils;
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
        Uri albumArtUri = MusicUtils.getAlbumArtUri(songList.get(position).getAlbumId());
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

    public class SongItemViewHolder extends SingleItemViewHolder implements PopupMenu.OnMenuItemClickListener {

        SongItemViewHolder(View itemView) {
            super(itemView);

            if (menu == null)
                return;

            menu.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() != R.id.item_menu)
                playAll(getAdapterPosition());
            else {
                PopupMenu popupMenu = new PopupMenu(activity, view);
                popupMenu.inflate(R.menu.popup_menu_song);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return handleMenuClick(activity, getSong(), item.getItemId());
        }

        private Song getSong() {
            return songList.get(getAdapterPosition());
        }

        private boolean handleMenuClick(@NonNull FragmentActivity activity, @NonNull Song song, int menuItemId) {
            switch (menuItemId) {
                case R.id.action_play_next:
                    FlairMusicController.playNext(song);
                    return true;
                case R.id.action_add_to_queue:
                    FlairMusicController.enqueue(song);
                    return true;
                case R.id.action_add_to_favorites:
                    MusicUtils.addToFavorite(activity, getSong());
                    return true;
                case R.id.action_go_to_album:
                    NavUtils.goToAlbum(activity, AlbumLoader.getAlbum(activity, song.getAlbumId()), itemImage);
                    return true;
                case R.id.action_go_to_artist:
                    NavUtils.goToArtist(activity, ArtistLoader.getArtist(activity, song.getArtistId()), itemImage);
                    return true;
                case R.id.action_share:
                    MusicUtils.shareSong(activity, song.getId());
                    return true;
            }
            return false;
        }
    }
}
