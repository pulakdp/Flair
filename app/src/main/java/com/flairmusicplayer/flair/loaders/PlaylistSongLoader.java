package com.flairmusicplayer.flair.loaders;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.flairmusicplayer.flair.models.AbsSmartPlaylist;
import com.flairmusicplayer.flair.models.Playlist;
import com.flairmusicplayer.flair.models.Song;

import java.util.ArrayList;

public class PlaylistSongLoader extends WrappedAsyncTaskLoader<ArrayList<Song>> {

    private Playlist playlist;

    public PlaylistSongLoader(Context context, Playlist playlist) {
        super(context);
        this.playlist = playlist;
    }

    public static Cursor makePlaylistSongCursor(@NonNull final Context context, final int playlistId) {
        try {
            return context.getContentResolver().query(
                    MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                    new String[]{
                            MediaStore.Audio.Playlists.Members.AUDIO_ID,// 0
                            AudioColumns.TITLE, // 1
                            AudioColumns.DURATION, // 2
                            AudioColumns.TRACK, // 3
                            AudioColumns.ALBUM_ID, // 4
                            AudioColumns.ALBUM, // 5
                            AudioColumns.ARTIST_ID, // 6
                            AudioColumns.ARTIST, // 7
                    }, SongLoader.BASE_SELECTION, null,
                    MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
        } catch (SecurityException e) {
            return null;
        }
    }

    @NonNull
    private ArrayList<Song> getPlaylistSongList(@NonNull final Context context, final int playlistId) {
        ArrayList<Song> songs = new ArrayList<>();
        Cursor cursor = makePlaylistSongCursor(context, playlistId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(SongLoader.getSongFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return songs;
    }

    @Nullable
    @Override
    public ArrayList<Song> loadInBackground() {
        if (playlist instanceof AbsSmartPlaylist) {
            return ((AbsSmartPlaylist) playlist).getSongs(getContext());
        } else {
            return getPlaylistSongList(getContext(), playlist.getId());
        }
    }
}
