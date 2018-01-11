//package com.flairmusicplayer.flair.loaders;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.provider.BaseColumns;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//
//import com.flairmusicplayer.flair.models.Song;
//
//import java.util.ArrayList;
//
///**
// * Author: PulakDebasish
// */
//
//public class MostAndRecentlyPlayedSongsLoader {
//
//    public static final int NUMBER_OF_TOP_TRACKS = 99;
//
//    @NonNull
//    public static ArrayList<Song> getRecentlyPlayedTracks(@NonNull Context context) {
//        return SongLoader.getSongs(makeRecentTracksCursorAndClearUpDatabase(context));
//    }
//
//    @NonNull
//    public static ArrayList<Song> getTopTracks(@NonNull Context context) {
//        return SongLoader.getSongs(makeTopTracksCursorAndClearUpDatabase(context));
//    }
//
//    @Nullable
//    public static Cursor makeRecentTracksCursorAndClearUpDatabase(@NonNull final Context context) {
//        SortedLongCursor retCursor = makeRecentTracksCursorImpl(context);
//
//        // clean up the databases with any ids not found
//        if (retCursor != null) {
//            ArrayList<Long> missingIds = retCursor.getMissingIds();
//            if (missingIds != null && missingIds.size() > 0) {
//                for (long id : missingIds) {
//                    HistoryStore.getInstance(context).removeSongId(id);
//                }
//            }
//        }
//        return retCursor;
//    }
//
//    @Nullable
//    public static Cursor makeTopTracksCursorAndClearUpDatabase(@NonNull final Context context) {
//        SortedLongCursor retCursor = makeTopTracksCursorImpl(context);
//
//        // clean up the databases with any ids not found
//        if (retCursor != null) {
//            ArrayList<Long> missingIds = retCursor.getMissingIds();
//            if (missingIds != null && missingIds.size() > 0) {
//                for (long id : missingIds) {
//                    SongPlayCountStore.getInstance(context).removeItem(id);
//                }
//            }
//        }
//        return retCursor;
//    }
//
//    @Nullable
//    private static SortedLongCursor makeRecentTracksCursorImpl(@NonNull final Context context) {
//        // first get the top results ids from the internal database
//        Cursor songs = HistoryStore.getInstance(context).queryRecentIds();
//
//        try {
//            return makeSortedCursor(context, songs,
//                    songs.getColumnIndex(HistoryStore.RecentStoreColumns.ID));
//        } finally {
//            if (songs != null) {
//                songs.close();
//            }
//        }
//    }
//
//    @Nullable
//    private static SortedLongCursor makeTopTracksCursorImpl(@NonNull final Context context) {
//        // first get the top results ids from the internal database
//        Cursor songs = SongPlayCountStore.getInstance(context).getTopPlayedResults(NUMBER_OF_TOP_TRACKS);
//
//        try {
//            return makeSortedCursor(context, songs,
//                    songs.getColumnIndex(SongPlayCountStore.SongPlayCountColumns.ID));
//        } finally {
//            if (songs != null) {
//                songs.close();
//            }
//        }
//    }
//
//    @Nullable
//    private static SortedLongCursor makeSortedCursor(@NonNull final Context context, @Nullable final Cursor cursor, final int idColumn) {
//        if (cursor != null && cursor.moveToFirst()) {
//            // create the list of ids to select against
//            StringBuilder selection = new StringBuilder();
//            selection.append(BaseColumns._ID);
//            selection.append(" IN (");
//
//            // this tracks the order of the ids
//            long[] order = new long[cursor.getCount()];
//
//            long id = cursor.getLong(idColumn);
//            selection.append(id);
//            order[cursor.getPosition()] = id;
//
//            while (cursor.moveToNext()) {
//                selection.append(",");
//
//                id = cursor.getLong(idColumn);
//                order[cursor.getPosition()] = id;
//                selection.append(String.valueOf(id));
//            }
//
//            selection.append(")");
//
//            // get a list of songs with the data given the selection statement
//            Cursor songCursor = SongLoader.makeSongCursor(context, selection.toString(), null);
//            if (songCursor != null) {
//                // now return the wrapped TopTracksCursor to handle sorting given order
//                return new SortedLongCursor(songCursor, order, BaseColumns._ID);
//            }
//        }
//
//        return null;
//    }
//}
