package com.flairmusicplayer.flair.models;

import android.content.Context;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.loaders.RecentlyAddedSongsLoader;
import com.flairmusicplayer.flair.utils.PreferenceUtils;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class RecentlyAddedPlaylist extends AbsSmartPlaylist {

    public RecentlyAddedPlaylist(Context context) {
        super(context.getString(R.string.recently_added), R.drawable.ic_library_add_black_24dp);
    }

    @Override
    public ArrayList<Song> getSongs(Context context) {
        return RecentlyAddedSongsLoader.getRecentlyAddedSongs(context);
    }

    @Override
    public void clearPlaylist(Context context) {
        PreferenceUtils.getInstance(context).setRecentlyAddedCutoff(System.currentTimeMillis());
    }
}
