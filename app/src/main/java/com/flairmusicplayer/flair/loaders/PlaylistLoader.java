package com.flairmusicplayer.flair.loaders;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.flairmusicplayer.flair.models.Playlist;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class PlaylistLoader extends WrappedAsyncTaskLoader<ArrayList<Playlist>> {

    public PlaylistLoader(Context context) {
        super(context);
    }

    public static ArrayList<Playlist> getAllPlaylists(final Context context) {
        return getAllPlaylists(makePlaylistCursor(context, null, null));
    }

    public static Playlist getPlaylist(final Context context, final int playlistId) {
        return getPlaylist(makePlaylistCursor(
                context,
                BaseColumns._ID + "=?",
                new String[]{
                        String.valueOf(playlistId)
                }
        ));
    }

    public static Playlist getPlaylist(final Context context, final String playlistName) {
        return getPlaylist(makePlaylistCursor(
                context,
                PlaylistsColumns.NAME + "=?",
                new String[]{
                        playlistName
                }
        ));
    }

    public static Playlist getPlaylist(final Cursor cursor) {
        Playlist playlist = new Playlist();

        if (cursor != null && cursor.moveToFirst()) {
            playlist = getPlaylistFromCursorImpl(cursor);
        }
        if (cursor != null)
            cursor.close();
        return playlist;
    }

    public static ArrayList<Playlist> getAllPlaylists(final Cursor cursor) {
        ArrayList<Playlist> playlists = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                playlists.add(getPlaylistFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return playlists;
    }

    @NonNull
    private static Playlist getPlaylistFromCursorImpl(final Cursor cursor) {
        final int id = cursor.getInt(0);
        final String name = cursor.getString(1);
        return new Playlist(id, name);
    }

    @Nullable
    public static Cursor makePlaylistCursor(final Context context, final String selection, final String[] values) {
        try {
            return context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    new String[]{
                        /* 0 */
                            BaseColumns._ID,
                        /* 1 */
                            PlaylistsColumns.NAME
                    }, selection, values, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
        } catch (SecurityException e) {
            return null;
        }
    }

    @Override
    public ArrayList<Playlist> loadInBackground() {
        return getAllPlaylists(getContext());
    }
}
