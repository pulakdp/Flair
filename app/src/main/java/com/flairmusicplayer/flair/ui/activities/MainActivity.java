package com.flairmusicplayer.flair.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.ui.fragments.FoldersFragment;
import com.flairmusicplayer.flair.ui.fragments.LibraryFragment;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.flairmusicplayer.flair.utils.MusicUtils;
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

    private static final int REQUEST_PERMISSION_FLAIR = 14;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    private HeaderViewHolder headerViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        headerViewHolder = new HeaderViewHolder(navigationView.getHeaderView(0));

        navigationView.setNavigationItemSelectedListener(this);

        //Not the best way to ask and handle permissions. Will need improvement in future.

        if (FlairUtils.isMarshmallowOrAbove() && !hasPermissions()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_FLAIR);
        } else if (hasPermissions()) {
            loadNormally();
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_FLAIR) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        //Permission Denied
                        Toast.makeText(getApplicationContext(), R.string.need_permission, Toast.LENGTH_LONG).show();
                    } else {
                        //Permission Denied + Don't Ask Again = Take to app info screen
                        Toast.makeText(getApplicationContext(), R.string.need_permission, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    loadNormally();
                }
            }
        }
    }

    private boolean hasPermissions() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    private void loadNormally() {
        setActiveFragment(PreferenceUtils.getInstance(this).getLastActiveFragment());
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
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.share_app) {
            startShareIntent();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startShareIntent() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String messageString = getString(R.string.share_message) + "\n";
            messageString = messageString + getString(R.string.play_store_link);
            i.putExtra(Intent.EXTRA_TEXT, messageString);
            startActivity(Intent.createChooser(i, "Share via"));
        } catch (Exception e) {
            //e.toString();
        }
    }


    public void onPanelExpanded() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void onPanelCollapsed() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
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
        if (currentSong != null && !currentSong.equals(new Song())) {
            headerViewHolder.songTitleHeader.setText(currentSong.getTitle());
            headerViewHolder.songArtistHeader.setText(currentSong.getArtistName());
            Glide.with(this)
                    .load(MusicUtils.getAlbumArtUri(currentSong.getAlbumId()))
                    .apply(new RequestOptions().placeholder(R.drawable.album_art_placeholder))
                    .apply(new RequestOptions().error(R.drawable.album_art_placeholder))
                    .into(headerViewHolder.albumArtHeader);
        } else {
            headerViewHolder.songTitleHeader.setText("");
            headerViewHolder.songArtistHeader.setText("");
            headerViewHolder.albumArtHeader.setImageResource(R.drawable.album_art_placeholder);
        }
    }

    @Override
    public void onQueueChanged() {
        super.onQueueChanged();
        hideBottomBar(FlairMusicController.getPlayingQueue().isEmpty());
        setSongDetailsInNavHeader();
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
