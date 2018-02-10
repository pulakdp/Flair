package com.flairmusicplayer.flair.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.adapters.PlaylistSongAdapter;
import com.flairmusicplayer.flair.loaders.PlaylistSongLoader;
import com.flairmusicplayer.flair.models.Playlist;
import com.flairmusicplayer.flair.models.Song;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: PulakDebasish
 */

public class PlaylistDetailActivity extends SlidingPanelActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Song>> {

    private static final int LOADER_ID = 5;
    public static String EXTRA_PLAYLIST = "extra_playlist";

    @BindView(R.id.playlist_toolbar)
    Toolbar toolbar;

    @BindView(R.id.playlist_songs_rv)
    RecyclerView recyclerView;

    @BindView(R.id.empty_text)
    TextView emptyText;

    private Playlist playlist;
    private PlaylistSongAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            playlist = getIntent().getExtras().getParcelable(EXTRA_PLAYLIST);
        }

        setUpToolbar();
        setUpRecyclerView();

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void setUpRecyclerView() {
        adapter = new PlaylistSongAdapter(this);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                emptyText.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(playlist.getName());
    }

    @Override
    protected View createContentView() {
        return wrapSlidingPanel(R.layout.activity_playlist_detail);
    }

    @Override
    public void onBackPressed() {
        if (getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<Song>> onCreateLoader(int id, Bundle args) {
        return new PlaylistSongLoader(this, playlist);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Song>> loader, ArrayList<Song> data) {
        adapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Song>> loader) {
        adapter.setData(new ArrayList<Song>());
    }
}
