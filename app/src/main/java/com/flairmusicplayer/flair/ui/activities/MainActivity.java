package com.flairmusicplayer.flair.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.ui.fragments.FoldersFragment;
import com.flairmusicplayer.flair.ui.fragments.LibraryFragment;
import com.flairmusicplayer.flair.utils.PreferenceUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends SlidingPanelActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String LIBRARY_FRAGMENT = LibraryFragment.class.getName();
    public static final String FOLDERS_FRAGMENT = FoldersFragment.class.getName();

    public static final int LIBRARY = 0;
    public static final int FOLDERS = 1;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private HeaderViewHolder headerViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        headerViewHolder = new HeaderViewHolder(navigationView.getHeaderView(0));

        if (savedInstanceState == null)
            setActiveFragment(PreferenceUtils.getInstance(this).getLastActiveFragment());

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setActiveFragment(int key) {
        PreferenceUtils.getInstance(this).setActiveFragment(key);
        switch (key) {
            case LIBRARY:
                navigationView.setCheckedItem(R.id.nav_library);
                setCurrentFragment(LibraryFragment.newInstance());
                break;
            case FOLDERS:
                navigationView.setCheckedItem(R.id.nav_folder);
                setCurrentFragment(FoldersFragment.newInstance(this));
        }
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        fragment,
                        fragment instanceof LibraryFragment ? LIBRARY_FRAGMENT : FOLDERS_FRAGMENT)
                .commit();
    }

    public void addDrawerToggle(Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
        } else if (id == R.id.action_shuffle_all) {
            LibraryFragment.shuffleAll(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_library) {
            setActiveFragment(LIBRARY);
        } else if (id == R.id.nav_folder) {
            setActiveFragment(FOLDERS);
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_about) {

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void onPanelExpanded() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void onPanelCollapsed() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void setPanelTouchEnabled(boolean touchEnabled) {
        panel.setTouchEnabled(touchEnabled);
    }

    @Override
    protected View createContentView() {
        View drawerLayout = getLayoutInflater().inflate(R.layout.activity_main_drawer_layout, null);
        ViewGroup drawerContent = drawerLayout.findViewById(R.id.drawer_content_container);
        drawerContent.addView(wrapSlidingPanel(R.layout.activity_main_content));
        return drawerLayout;
    }

    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
        if (!FlairMusicController.getPlayingQueue().isEmpty()) {
            setSongDetailsInNavHeader();
            hideBottomBar(false);
        }
    }

    private void setSongDetailsInNavHeader() {
        Song currentSong = FlairMusicController.getCurrentSong();
        if (currentSong != null) {
            headerViewHolder.songTitleHeader.setText(currentSong.getTitle());
            headerViewHolder.songArtistHeader.setText(currentSong.getArtistName());
            Glide.with(this)
                    .load(Song.getAlbumArtUri(currentSong.getAlbumId()))
                    .apply(new RequestOptions().placeholder(R.drawable.album_art_placeholder))
                    .apply(new RequestOptions().error(R.drawable.album_art_placeholder))
                    .into(headerViewHolder.albumArtHeader);
        }
    }

    @Override
    public void onQueueChanged() {
        super.onQueueChanged();
        hideBottomBar(FlairMusicController.getPlayingQueue().isEmpty());
    }

    public static class HeaderViewHolder {
        @BindView(R.id.album_art_header)
        ImageView albumArtHeader;

        @BindView(R.id.song_title_header)
        TextView songTitleHeader;

        @BindView(R.id.song_artist_header)
        TextView songArtistHeader;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
