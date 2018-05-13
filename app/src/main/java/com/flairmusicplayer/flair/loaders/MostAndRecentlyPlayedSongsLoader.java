package com.flairmusicplayer.flair.loaders;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.providers.RecentStore;
import com.flairmusicplayer.flair.providers.SongPlayCount;

import java.util.ArrayList;

/**
 * Author: PulakDebasish, Andrew Neal
 */

public class MostAndRecentlyPlayedSongsLoader {

    private static final int NUMBER_OF_TOP_TRACKS = 99;

    public enum QueryType {
        MostPlayed,
        RecentlyPlayed
    }

    public static ArrayList<Song> getSongs(Context context, QueryType type) {
        return SongLoader.getSongsFromCursor(makeSongCursorAndCleanDatabase(context, type));
    }

    public static Cursor makeSongCursorAndCleanDatabase(final Context context, QueryType type) {
        SortedCursor returnedCursor = makeSongCursorImpl(context, type);

        if (returnedCursor != null) {
            ArrayList<Long> missingIds = returnedCursor.getMissingIds();
            if (missingIds != null && missingIds.size() > 0) {
                for (long id : missingIds) {
                    switch (type) {
                        case MostPlayed:
                            SongPlayCount.getInstance(context).removeItem(id);
                            break;
                        case RecentlyPlayed:
                            RecentStore.getInstance(context).removeItem(id);
                            break;
                    }
                }
            }
        }
        return returnedCursor;
    }

    public static SortedCursor makeSongCursorImpl(final Context context, QueryType type) {
        Cursor songs;
        switch (type) {
            case MostPlayed:
                songs = SongPlayCount.getInstance(context).getTopPlayedResults(NUMBER_OF_TOP_TRACKS);
                try {
                    return makeSortedCursor(context, songs,
                            songs.getColumnIndex(SongPlayCount.SongPlayCountColumns.ID));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (songs != null) {
                    songs.close();
                }
                break;
            case RecentlyPlayed:
                songs = RecentStore.getInstance(context).queryRecentIds(null);
                try {
                    return makeSortedCursor(context, songs,
                            songs.getColumnIndex(RecentStore.RecentStoreColumns.ID));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (songs != null) {
                    songs.close();
                }
                break;
        }
        return null;
    }

    @Nullable
    private static SortedCursor makeSortedCursor(@NonNull final Context context, @Nullable final Cursor cursor, final int idColumn) {
        if (cursor != null && cursor.moveToFirst()) {
            // create the list of ids to select against
            StringBuilder selection = new StringBuilder();
            selection.append(BaseColumns._ID);
            selection.append(" IN (");

            // this tracks the order of the ids
            long[] order = new long[cursor.getCount()];

            long id = cursor.getLong(idColumn);
            selection.append(id);
            order[cursor.getPosition()] = id;

            while (cursor.moveToNext()) {
                selection.append(",");

                id = cursor.getLong(idColumn);
                order[cursor.getPosition()] = id;
                selection.append(String.valueOf(id));
            }

            selection.append(")");

            // get a list of songs with the data given the selection statement
            Cursor songCursor = SongLoader.createSongCursor(context, selection.toString(), null);
            if (songCursor != null) {
                // now return the wrapped TopTracksCursor to handle sorting given order
                return new SortedCursor(songCursor, order, BaseColumns._ID);
            }
        }
        return null;
    }
}
