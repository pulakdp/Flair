package com.flairmusicplayer.flair.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.adapters.AlbumAdapter;
import com.flairmusicplayer.flair.loaders.AlbumLoader;
import com.flairmusicplayer.flair.models.Album;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author: PulakDebasish
 */

public class AlbumsFragment extends MusicServiceFragment
        implements LoaderManager.LoaderCallbacks<ArrayList<Album>> {

    private static final int LOADER_ID = 2;

    @BindView(R.id.recycler_view)
    FastScrollRecyclerView albumGridView;

    private AlbumAdapter albumAdapter;
    private Unbinder unbinder;

    public AlbumsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumAdapter = new AlbumAdapter(new ArrayList<Album>());
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
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        albumGridView.setLayoutManager(layoutManager);
        albumGridView.setAdapter(albumAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public Loader<ArrayList<Album>> onCreateLoader(int id, Bundle args) {
        return new AlbumLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Album>> loader, ArrayList<Album> data) {
        if (albumAdapter != null)
            albumAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Album>> loader) {
        if (albumAdapter != null)
            albumAdapter.setData(new ArrayList<Album>());
    }
}
