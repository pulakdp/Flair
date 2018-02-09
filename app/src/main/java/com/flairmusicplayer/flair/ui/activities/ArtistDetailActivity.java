package com.flairmusicplayer.flair.ui.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.adapters.ArtistAlbumAdapter;
import com.flairmusicplayer.flair.adapters.ArtistSongAdapter;
import com.flairmusicplayer.flair.models.Album;
import com.flairmusicplayer.flair.models.Artist;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.flairmusicplayer.flair.utils.MusicUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: PulakDebasish
 */

public class ArtistDetailActivity extends SlidingPanelActivity {

    public static final String EXTRA_ARTIST = "extra_artist";

    @BindView(R.id.artist_detail_toolbar)
    Toolbar toolbar;

    @BindView(R.id.artist_image)
    ImageView artistImageView;

    @BindView(R.id.artist_name)
    TextView artistNameView;

    @BindView(R.id.artist_album_recycler_view)
    RecyclerView albumRecyclerView;

    @BindView(R.id.artist_song_recycler_view)
    RecyclerView songRecyclerView;

    @BindView(R.id.shuffle_fab)
    FloatingActionButton shuffleFab;

    private Artist artist;
    private ArtistSongAdapter artistSongAdapter;
    private ArtistAlbumAdapter artistAlbumAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            artist = getIntent().getExtras().getParcelable(EXTRA_ARTIST);
            if (artist != null)
                setUpArtist();
        }

        setUpToolbar();
        setUpAdapters();
        setUpRecyclerViews();
        setUpFab();
    }

    private void setUpArtist() {
        final Drawable textDrawable = FlairUtils.getRectTextDrawable(this, artist.getArtistName());
        Glide.with(this)
                .load(MusicUtils.getAlbumArtUri(artist.albumsOfArtist.get(0).getAlbumId()))
                .apply(new RequestOptions().error(textDrawable))
                .into(artistImageView);
        artistNameView.setText(artist.getArtistName());
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpAdapters() {
        artistSongAdapter = new ArtistSongAdapter(this,
                artist != null ? artist.getSongsForArtist() : new ArrayList<Song>());
        artistAlbumAdapter = new ArtistAlbumAdapter(this,
                artist != null ? artist.albumsOfArtist : new ArrayList<Album>());
    }

    private void setUpRecyclerViews() {
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songRecyclerView.setAdapter(artistSongAdapter);

        albumRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        albumRecyclerView.setAdapter(artistAlbumAdapter);
    }

    private void setUpFab() {
        shuffleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlairMusicController
                        .openAndShuffleQueue(artist.getSongsForArtist(), true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        shuffleFab.setVisibility(View.GONE);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected View createContentView() {
        return wrapSlidingPanel(R.layout.activity_artist_detail);
    }
}
