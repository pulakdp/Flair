package com.flairmusicplayer.flair.models;

import android.content.Context;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.loaders.MostAndRecentlyPlayedSongsLoader;
import com.flairmusicplayer.flair.providers.SongPlayCount;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class MostPlayedPlaylist extends AbsSmartPlaylist {

    public MostPlayedPlaylist(Context context) {
        super(context.getString(R.string.most_played), R.drawable.ic_trending_up_black_24dp);
    }

    @Override
    public ArrayList<Song> getSongs(Context context) {
        return MostAndRecentlyPlayedSongsLoader.getSongs(context, MostAndRecentlyPlayedSongsLoader.QueryType.MostPlayed);
    }

    @Override
    public void clearPlaylist(Context context) {
        SongPlayCount.getInstance(context).deleteAll();
    }
}
