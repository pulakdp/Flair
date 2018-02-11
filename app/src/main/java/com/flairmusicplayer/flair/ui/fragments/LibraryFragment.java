package com.flairmusicplayer.flair.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.adapters.TabViewPagerAdapter;
import com.flairmusicplayer.flair.loaders.SongLoader;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.ui.activities.MainActivity;
import com.flairmusicplayer.flair.utils.PreferenceUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author: PulakDebasish
 */

public class LibraryFragment extends MusicServiceFragment
        implements ViewPager.OnPageChangeListener {

    @BindView(R.id.library_toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager tabPager;

    @BindView(R.id.adView)
    AdView adView;

    @BindView(R.id.shuffle_fab)
    FloatingActionButton shuffleFab;

    private PreferenceUtils preferences;

    private Unbinder unbinder;

    public LibraryFragment() {
    }

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceUtils.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_library, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.addDrawerToggle(toolbar);
        }

        if (tabPager != null) {
            tabLayout.setupWithViewPager(tabPager);

            setupViewPager(tabPager);
            tabPager.setOffscreenPageLimit(2);
            tabPager.addOnPageChangeListener(this);

            tabPager.setCurrentItem(preferences.getStartPageIndex());
        }

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(this.getString(R.string.test_device_id))
                .build();

        adView.loadAd(adRequest);

        shuffleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffleAll(getActivity());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (preferences.getLastOpenedIsStartPage() && tabPager != null) {
            preferences.setStartPageIndex(tabPager.getCurrentItem());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tabPager.removeOnPageChangeListener(this);
        unbinder.unbind();
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
        TabViewPagerAdapter tabViewPagerAdapter = new TabViewPagerAdapter(getChildFragmentManager());
        tabViewPagerAdapter.addFragment(new SongsFragment(), getString(R.string.tab_songs));
        tabViewPagerAdapter.addFragment(new AlbumsFragment(), getString(R.string.tab_albums));
        tabViewPagerAdapter.addFragment(new ArtistsFragment(), getString(R.string.tab_artists));
        tabViewPagerAdapter.addFragment(new PlaylistFragment(), getString(R.string.tab_playlists));
        viewPager.setAdapter(tabViewPagerAdapter);
    }

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
}
