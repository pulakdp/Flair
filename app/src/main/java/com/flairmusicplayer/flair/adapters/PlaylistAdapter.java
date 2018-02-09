package com.flairmusicplayer.flair.adapters;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.AbsSmartPlaylist;
import com.flairmusicplayer.flair.models.Playlist;
import com.flairmusicplayer.flair.utils.NavUtils;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private static final int SMART_PLAYLIST = 0;
    private static final int DEFAULT_PLAYLIST = 1;

    private AppCompatActivity activity;
    private ArrayList<Playlist> allPlaylist;

    public PlaylistAdapter(AppCompatActivity activity, ArrayList<Playlist> allPlaylist) {
        this.activity = activity;
        this.allPlaylist = allPlaylist;
    }

    public void setData(ArrayList<Playlist> newAllPlaylist) {
        allPlaylist = newAllPlaylist;
        notifyDataSetChanged();
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_single, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        final Playlist playlist = allPlaylist.get(position);

        if (holder.itemTitle != null)
            holder.itemTitle.setText(playlist.getName());

        if (holder.itemImage != null)
            holder.itemImage.setImageResource(getResIcon(playlist));
    }

    private int getResIcon(Playlist playlist) {
        if (playlist instanceof AbsSmartPlaylist)
            return ((AbsSmartPlaylist) playlist).getIconRes();
        else
            return activity.getString(R.string.favorite_playlist).equalsIgnoreCase(playlist.getName()) ?
                    R.drawable.ic_favorite_black_24dp : R.drawable.ic_queue_music_black_24dp;
    }

    @Override
    public long getItemId(int position) {
        return allPlaylist.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return allPlaylist.get(position) instanceof AbsSmartPlaylist ? SMART_PLAYLIST : DEFAULT_PLAYLIST;
    }

    @Override
    public int getItemCount() {
        return allPlaylist.size();
    }

    public class PlaylistViewHolder extends SingleItemViewHolder {

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            if (itemDetailText != null)
                itemDetailText.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            super.onClick(view);
            Playlist playlist = allPlaylist.get(getAdapterPosition());
            NavUtils.goToPlaylist(activity, playlist);
        }
    }

}
