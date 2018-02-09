package com.flairmusicplayer.flair.adapters;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Artist;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.flairmusicplayer.flair.utils.MusicUtils;
import com.flairmusicplayer.flair.utils.NavUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class ArtistAdapter extends FastScrollRecyclerView.Adapter<ArtistAdapter.ArtistItemViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {

    private ArrayList<Artist> artists;
    private AppCompatActivity activity;

    public ArtistAdapter(AppCompatActivity activity, ArrayList<Artist> artists) {
        this.activity = activity;
        this.artists = artists;
    }

    @Override
    public ArtistItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View itemView = LayoutInflater.from(activity).inflate(R.layout.list_item_single, null);

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

        if (holder.itemTitle != null)
            holder.itemTitle.setText(artistName);

        if (holder.itemDetailText != null)
            holder.itemDetailText.setText(albumCount + albumOrAlbums + bulletChar + songCount + songOrSongs);

        if (holder.itemImage != null) {
            final TextDrawable textDrawable = FlairUtils.getRoundTextDrawable(activity, artistName);
            Glide.with(activity)
                    .load(MusicUtils.getAlbumArtUri(artists.get(position).albumsOfArtist.get(0).getAlbumId()))
                    .apply(new RequestOptions().circleCrop())
                    .apply(new RequestOptions().error(textDrawable))
                    .into(holder.itemImage);
        }
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

    public class ArtistItemViewHolder extends SingleItemViewHolder {

        public ArtistItemViewHolder(View itemView) {
            super(itemView);
            if (itemImage != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                itemImage.setTransitionName(activity.getResources().getString(R.string.transition_artist_image));
        }

        @Override
        public void onClick(View view) {
            if (itemImage != null) {
                NavUtils.goToArtist(activity, artists.get(getAdapterPosition()), itemImage);
            }
        }
    }

}
