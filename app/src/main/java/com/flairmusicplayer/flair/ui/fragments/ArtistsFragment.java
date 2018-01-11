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
import com.flairmusicplayer.flair.adapters.ArtistAdapter;
import com.flairmusicplayer.flair.loaders.ArtistLoader;
import com.flairmusicplayer.flair.models.Artist;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Author: PulakDebasish
 */

public class ArtistsFragment extends MusicServiceFragment
        implements LoaderManager.LoaderCallbacks<ArrayList<Artist>> {

    public static final int LOADER_ID = 3;

    @BindView(R.id.recycler_view)
    FastScrollRecyclerView artistListView;

    ArrayList<Artist> artists;
    ArtistAdapter artistAdapter;

    public ArtistsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artists = new ArrayList<>();
        artistAdapter = new ArtistAdapter(artists);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_recycler_view, container, false);
        ButterKnife.bind(this, rootView);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        artistListView.setLayoutManager(layoutManager);
        artistListView.setAdapter(artistAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<ArrayList<Artist>> onCreateLoader(int id, Bundle args) {
        return new ArtistLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Artist>> loader, ArrayList<Artist> data) {
        artists = data;
        Timber.d("Received data size: " + String.valueOf(data.size()));
        artistAdapter.setData(artists);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Artist>> loader) {
        artistAdapter.setData(new ArrayList<Artist>());
    }
}
