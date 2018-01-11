package com.flairmusicplayer.flair.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.ui.fragments.LibraryFragment;
import com.flairmusicplayer.flair.ui.fragments.MiniPlayerFragment;
import com.flairmusicplayer.flair.ui.fragments.NowPlayingFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends MusicServiceActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SlidingUpPanelLayout.PanelSlideListener {

    public static final String LIBRARY_FRAGMENT = LibraryFragment.class.getName();

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.slide_panel_layout)
    SlidingUpPanelLayout panel;

    private MiniPlayerFragment miniPlayerFragment;
    private NowPlayingFragment nowPlayingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new LibraryFragment(), LIBRARY_FRAGMENT)
                .commit();

        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        miniPlayerFragment = (MiniPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.mini_player_fragment);
//        nowPlayingFragment = (NowPlayingFragment) getSupportFragmentManager().findFragmentById(R.id.now_playing_fragment);
        //noinspection ConstantConditions
        miniPlayerFragment.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandPanel();
            }
        });
        panel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                panel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    onPanelExpanded();
                } else if (getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    onPanelCollapsed();
                }
            }
        });
        panel.addPanelSlideListener(this);
    }

    public SlidingUpPanelLayout.PanelState getPanelState() {
        return panel != null ? panel.getPanelState() : null;
    }

    public void setMiniPlayerAlpha(float progress) {
        if (miniPlayerFragment.getView() == null) return;
        float alpha = 1 - progress;
        miniPlayerFragment.getView().setAlpha(alpha);
        miniPlayerFragment.getView().setVisibility(alpha == 0 ? View.GONE : View.VISIBLE);

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

        } else if (id == R.id.nav_folder) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_about) {

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void collapsePanel() {
        panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void expandPanel() {
        panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        setMiniPlayerAlpha(slideOffset);
    }

    public void onPanelExpanded() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void onPanelCollapsed() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
        if (!FlairMusicController.getPlayingQueue().isEmpty()) {
            hideBottomBar(false);
        }
    }

    @Override
    public void onQueueChanged() {
        super.onQueueChanged();
        hideBottomBar(FlairMusicController.getPlayingQueue().isEmpty());
    }

    public void hideBottomBar(final boolean hide) {
        if (hide) {
            panel.setPanelHeight(0);
            collapsePanel();
        } else {
            panel.setPanelHeight(getResources().getDimensionPixelSize(R.dimen.mini_player_height));
        }
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        switch (newState) {
            case COLLAPSED:
                onPanelCollapsed();
                break;
            case EXPANDED:
                onPanelExpanded();
                break;
            case ANCHORED:
                break;
        }
    }
}
