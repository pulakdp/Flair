package com.flairmusicplayer.flair.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
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
import butterknife.Unbinder;

/**
 * Author: PulakDebasish
 */

public class ArtistsFragment extends MusicServiceFragment
        implements LoaderManager.LoaderCallbacks<ArrayList<Artist>> {

    public static final int LOADER_ID = 3;

    @BindView(R.id.recycler_view)
    FastScrollRecyclerView artistListView;

    private ArtistAdapter artistAdapter;
    private Unbinder unbinder;

    public ArtistsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_recycler_view, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        artistAdapter = new ArtistAdapter((AppCompatActivity) getActivity(), new ArrayList<Artist>());
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        artistListView.setLayoutManager(layoutManager);
        artistListView.setAdapter(artistAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
        if (artistAdapter != null)
            artistAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Artist>> loader) {
        if (artistAdapter != null)
            artistAdapter.setData(new ArrayList<Artist>());
    }
}
