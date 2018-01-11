package com.flairmusicplayer.flair.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: PulakDebasish
 */

public class SongAdapter
        extends FastScrollRecyclerView.Adapter<SongAdapter.SongItemViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter{

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
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_song, null);
        return new SongItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SongItemViewHolder holder, int position) {
        Uri albumArtUri = Song.getAlbumArtUri(songList.get(position).getAlbumId());
        final String songTitle = songList.get(position).getTitle();
        Glide.with(context)
                .load(albumArtUri)
                .apply(RequestOptions.circleCropTransform())
                .apply(new RequestOptions().placeholder(FlairUtils.getRoundTextDrawable(context, songTitle)))
                .into(holder.albumArt);
        holder.titleText.setText(songTitle);
        holder.artistText.setText(songList.get(position).getArtistName());
    }

    private void playAll(int position) {
        FlairMusicController.openQueue(songList, position, true);
    }

    @Override
    public int getItemCount() {
        return (songList != null ? songList.size() : 0);
    }

    public void setData(ArrayList<Song> songs){
        songList = songs;
        notifyDataSetChanged();
    }

    public class SongItemViewHolder extends FastScrollRecyclerView.ViewHolder {
        @BindView(R.id.item_song_image)
        ImageView albumArt;
        @BindView(R.id.item_song_title)
        TextView titleText;
        @BindView(R.id.item_song_artist)
        TextView artistText;

        Switch aSwitch;
        SongItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playAll(getLayoutPosition());
                }
            });
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.d("Position: ", String.valueOf(getLayoutPosition()));
                }
            });
        }
    }
}
