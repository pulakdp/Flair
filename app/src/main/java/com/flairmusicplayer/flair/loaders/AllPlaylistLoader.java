package com.flairmusicplayer.flair.loaders;

import android.content.Context;

import com.flairmusicplayer.flair.models.MostPlayedPlaylist;
import com.flairmusicplayer.flair.models.Playlist;
import com.flairmusicplayer.flair.models.RecentlyAddedPlaylist;
import com.flairmusicplayer.flair.models.RecentlyPlayedPlaylist;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class AllPlaylistLoader extends WrappedAsyncTaskLoader<ArrayList<Playlist>> {

    public AllPlaylistLoader(Context context) {
        super(context);
    }

    private ArrayList<Playlist> getAllPlaylists(Context context) {
        ArrayList<Playlist> allPlaylists = new ArrayList<>();
        allPlaylists.add(new MostPlayedPlaylist(context));
        allPlaylists.add(new RecentlyPlayedPlaylist(context));
        allPlaylists.add(new RecentlyAddedPlaylist(context));
        allPlaylists.addAll(PlaylistLoader.getAllPlaylists(context));
        return allPlaylists;
    }

    @Override
    public ArrayList<Playlist> loadInBackground() {
        return getAllPlaylists(getContext());
    }
}
