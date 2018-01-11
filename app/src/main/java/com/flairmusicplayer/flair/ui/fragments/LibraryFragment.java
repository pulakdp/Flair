package com.flairmusicplayer.flair.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.adapters.TabViewPagerAdapter;
import com.flairmusicplayer.flair.loaders.SongLoader;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.ui.activities.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: PulakDebasish
 */

public class LibraryFragment extends MusicServiceFragment {

    @BindView(R.id.library_toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager tabPager;

    @BindView(R.id.shuffle_fab)
    FloatingActionButton shuffleFab;

    public LibraryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_library, container, false);
        ButterKnife.bind(this, rootView);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).addDrawerToggle(toolbar);

        setupViewPager(tabPager);

        tabLayout.setupWithViewPager(tabPager);

        tabPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0)
                    hideFab();
                else
                    showFab();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        shuffleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffleAll(getActivity());
            }
        });

        return rootView;
    }

    public void hideFab() {
        shuffleFab.hide();
    }

    public void showFab() {
        shuffleFab.show();
    }

    @SuppressLint("StaticFieldLeak")
    public static void shuffleAll(final Context context) {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                FlairMusicController.openAndShuffleQueue(SongLoader.getAllSongs(context), true);
                return null;
            }
        }.execute();
    }

    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getActivity().getSupportFragmentManager());
        tabViewPagerAdapter.addFragment(new SongsFragment(), getString(R.string.tab_songs));
        tabViewPagerAdapter.addFragment(new AlbumsFragment(), getString(R.string.tab_albums));
        tabViewPagerAdapter.addFragment(new ArtistsFragment(), getString(R.string.tab_artists));
        tabViewPagerAdapter.addFragment(new PlaylistsFragment(), getString(R.string.tab_playlists));
        viewPager.setAdapter(tabViewPagerAdapter);
    }
}
