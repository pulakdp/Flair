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
import com.flairmusicplayer.flair.adapters.AlbumSongAdapter;
import com.flairmusicplayer.flair.models.Album;
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

public class AlbumDetailActivity extends SlidingPanelActivity {

    public static final String EXTRA_ALBUM = "extra_album";
    @BindView(R.id.album_art)
    ImageView albumArt;
    @BindView(R.id.album_details_toolbar)
    Toolbar toolbar;
    @BindView(R.id.album_name)
    TextView albumName;
    @BindView(R.id.album_song_list)
    RecyclerView albumSongList;
    @BindView(R.id.shuffle_fab)
    FloatingActionButton shuffleFab;

    private Album album;
    private AlbumSongAdapter albumSongAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            album = getIntent().getExtras().getParcelable(EXTRA_ALBUM);
            setUpAlbum();
        }

        setUpToolbar();
        setUpAdapter();
        setUpRecyclerView();
        setUpFab();
    }

    private void setUpAlbum() {
        final Drawable textDrawable = FlairUtils.getRectTextDrawable(this, album.getAlbumName());
        Glide.with(this)
                .load(MusicUtils.getAlbumArtUri(album.getAlbumId()))
                .apply(new RequestOptions().error(textDrawable))
                .into(albumArt);
        albumName.setText(album.getAlbumName());
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpAdapter() {
        albumSongAdapter = new AlbumSongAdapter(this,
                album != null ? album.songsInAlbum : new ArrayList<Song>());
    }

    private void setUpRecyclerView() {
        albumSongList.setLayoutManager(new LinearLayoutManager(this));
        albumSongList.setAdapter(albumSongAdapter);
    }

    private void setUpFab() {
        shuffleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlairMusicController
                        .openAndShuffleQueue(album.songsInAlbum, true);
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
        return wrapSlidingPanel(R.layout.activity_album_detail);
    }
}
