package com.flairmusicplayer.flair.models;

import android.content.Context;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.loaders.MostAndRecentlyPlayedSongsLoader;
import com.flairmusicplayer.flair.providers.RecentStore;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class RecentlyPlayedPlaylist extends AbsSmartPlaylist {

    public RecentlyPlayedPlaylist(Context context) {
        super(context.getString(R.string.recently_played), R.drawable.ic_history_black_24dp);
    }

    @Override
    public ArrayList<Song> getSongs(Context context) {
        return MostAndRecentlyPlayedSongsLoader.getSongs(context, MostAndRecentlyPlayedSongsLoader.QueryType.RecentlyPlayed);
    }

    @Override
    public void clearPlaylist(Context context) {
        RecentStore.getInstance(context).deleteAll();
    }
}
