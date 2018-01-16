package com.flairmusicplayer.flair.loaders;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.utils.PreferenceUtils;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class RecentlyAddedSongsLoader {

    public static ArrayList<Song> getRecentlyAddedSongs(final Context context) {
        return SongLoader.getSongsFromCursor(makeLastAddedCursor(context));
    }

    private static Cursor makeLastAddedCursor(final Context context) {
        long fourWeeksAgo = (System.currentTimeMillis() / 1000) - (4 * 3600 * 24 * 7);
        // possible saved timestamp caused by user "clearing" the last added playlist
        long cutoff = PreferenceUtils.getInstance(context).getRecentlyAddedCutoff() / 1000;
        if (cutoff < fourWeeksAgo) {
            cutoff = fourWeeksAgo;
        }

        String selection = (MediaStore.Audio.AudioColumns.IS_MUSIC + "=1") +
                " AND " + MediaStore.Audio.Media.DATE_ADDED + ">?" +
                cutoff;

        return SongLoader.createSongCursor(
                context,
                selection,
                null,
                MediaStore.Audio.Media.DATE_ADDED + " DESC");
    }

}
