package com.flairmusicplayer.flair.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.adapters.PlaylistAdapter;
import com.flairmusicplayer.flair.loaders.AllPlaylistLoader;
import com.flairmusicplayer.flair.models.Playlist;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: PulakDebasish
 */

public class PlaylistFragment extends MusicServiceFragment
        implements LoaderManager.LoaderCallbacks<ArrayList<Playlist>> {

    @BindView(R.id.recycler_view)
    FastScrollRecyclerView recyclerView;

    private static final int LOADER_ID = 4;
    private ArrayList<Playlist> playlists;
    private PlaylistAdapter playlistAdapter;

    public PlaylistFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playlists = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter((AppCompatActivity) getActivity(), playlists);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_recycler_view, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(playlistAdapter);
    }

    @Override
    public Loader<ArrayList<Playlist>> onCreateLoader(int id, Bundle args) {
        return new AllPlaylistLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Playlist>> loader, ArrayList<Playlist> data) {
        playlists = data;
        playlistAdapter.setData(playlists);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Playlist>> loader) {
        playlistAdapter.setData(new ArrayList<Playlist>());
    }
}
