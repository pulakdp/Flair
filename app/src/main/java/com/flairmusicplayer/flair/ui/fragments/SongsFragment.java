package com.flairmusicplayer.flair.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.adapters.SongAdapter;
import com.flairmusicplayer.flair.loaders.SongLoader;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.ui.activities.MainActivity;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author: PulakDebasish
 */

public class SongsFragment extends MusicServiceFragment
        implements LoaderManager.LoaderCallbacks<ArrayList<Song>> {

    private static final int LOADER_ID = 1;
    @BindView(R.id.recycler_view)
    public FastScrollRecyclerView songList;
    public SongAdapter songAdapter;
    private Unbinder unbinder;

    public SongsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songAdapter = new SongAdapter(new ArrayList<Song>());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_recycler_view, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        songList.setLayoutManager(layoutManager);
        songList.setAdapter(songAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final FragmentManager fm = getFragmentManager();
        LibraryFragment libFragment = null;
        if (fm != null) {
            libFragment = (LibraryFragment) fm.findFragmentByTag(MainActivity.LIBRARY_FRAGMENT);
        }
        final LibraryFragment fragment = libFragment;
        songList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (fragment != null) {
                    if (dy > 0) {
                        fragment.hideFab();
                    } else if (dy < 0) {
                        fragment.showFab();
                    } else {
                        fragment.showFab();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        songList.addOnScrollListener(null);
        unbinder.unbind();
    }

    @Override
    public Loader<ArrayList<Song>> onCreateLoader(int id, Bundle args) {
        return new SongLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Song>> loader, ArrayList<Song> data) {
        if (songAdapter != null)
            songAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Song>> loader) {
        if (songAdapter != null)
            songAdapter.setData(new ArrayList<Song>());
    }
}
