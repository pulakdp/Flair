package com.flairmusicplayer.flair.loaders;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.flairmusicplayer.flair.models.Song;

import java.util.ArrayList;

/**
 * Coded by PulakDebasish on 26-05-2017.
 */

public class SongLoader extends WrappedAsyncTaskLoader<ArrayList<Song>> {

    public static final String BASE_SELECTION = AudioColumns.IS_MUSIC + "= 1" + " AND " + AudioColumns.TITLE + " != ''";

    public SongLoader(Context context) {
        super(context);
    }

    public static Cursor createSongCursor(Context context, @Nullable String selection, @Nullable String[] selectionArgs) {
        return createSongCursor(context, selection, selectionArgs, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

    public static Cursor createSongCursor(Context context, String selection, String[] selectionArgs, String sortOrder) {
        String finalSelection = BASE_SELECTION;
        if (!TextUtils.isEmpty(selection) || selection != null) {
            finalSelection += " AND " + selection;
        }

        return context.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                        BaseColumns._ID, // 0
                        AudioColumns.TITLE, // 1
                        AudioColumns.DURATION, // 2
                        AudioColumns.TRACK, // 3
                        AudioColumns.ALBUM_ID, // 4
                        AudioColumns.ALBUM, // 5
                        AudioColumns.ARTIST_ID, // 6
                        AudioColumns.ARTIST, // 7

                }, finalSelection, selectionArgs, sortOrder);
    }

    public static ArrayList<Song> getSongs(@NonNull final Context context, final String query) {
        Cursor cursor = createSongCursor(context, AudioColumns.TITLE + " LIKE ?", new String[]{"%" + query + "%"});
        return getSongsFromCursor(cursor);
    }

    public static ArrayList<Song> getAllSongs(Context context) {
        Cursor cursor = createSongCursor(context, null, null);
        return getSongsFromCursor(cursor);
    }

    public static ArrayList<Song> getSongsFromCursor(Cursor musicCursor) {
        ArrayList<Song> list = new ArrayList<>();
        if (musicCursor != null && musicCursor.moveToFirst()) {
            do {
                list.add(getSongFromCursor(musicCursor));
            } while (musicCursor.moveToNext());
        }
        if (musicCursor != null)
            musicCursor.close();
        return list;
    }

    @NonNull
    public static Song getSongFromCursor(Cursor musicCursor) {
        final int id = musicCursor.getInt(0);
        final String title = musicCursor.getString(1);
        final long duration = musicCursor.getLong(2);
        final int trackNumber = musicCursor.getInt(3);
        final int albumId = musicCursor.getInt(4);
        final String albumName = musicCursor.getString(5);
        final int artistId = musicCursor.getInt(6);
        final String artistName = musicCursor.getString(7);

        return new Song(id, title, duration, trackNumber, albumId, albumName, artistId, artistName);
    }

    @Override
    public ArrayList<Song> loadInBackground() {
        return getAllSongs(getContext());
    }
}
