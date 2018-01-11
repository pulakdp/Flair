package com.flairmusicplayer.flair.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Artist;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: PulakDebasish
 */

public class ArtistAdapter extends FastScrollRecyclerView.Adapter<ArtistAdapter.ArtistItemViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {

    private ArrayList<Artist> artists;
    private Context context;

    public ArtistAdapter(ArrayList<Artist> artists) {
        this.artists = artists;
    }

    @Override
    public ArtistItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        @SuppressLint("InflateParams")
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_artist, null);

        return new ArtistItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ArtistItemViewHolder holder, int position) {
        String artistName = artists.get(position).getArtistName();
        String bulletChar = " \u2022 ";
        int albumCount = artists.get(position).getAlbumCount();
        int songCount = artists.get(position).getSongCount();
        String albumOrAlbums = albumCount > 1 ? " Albums" : " Album";
        String songOrSongs = songCount > 1 ? " Songs" : " Song";
        holder.artistName.setText(artistName);
        holder.artistInfo.setText(albumCount + albumOrAlbums + bulletChar + songCount + songOrSongs);
        holder.artistInitials.setImageDrawable(FlairUtils.getRoundTextDrawable(context, artistName));
    }

    public void setData(ArrayList<Artist> artists) {
        this.artists = artists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return artists != null ? artists.size() : 0;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(artists.get(position).getArtistName().charAt(0)).toUpperCase();
    }

    public class ArtistItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.artist_initials)
        ImageView artistInitials;

        @BindView(R.id.artist_name)
        TextView artistName;

        @BindView(R.id.artist_album_song_info)
        TextView artistInfo;

        public ArtistItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
