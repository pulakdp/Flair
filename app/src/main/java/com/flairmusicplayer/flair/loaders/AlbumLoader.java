package com.flairmusicplayer.flair.loaders;

import android.content.Context;
import android.provider.MediaStore;

import com.flairmusicplayer.flair.models.Album;
import com.flairmusicplayer.flair.models.Song;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class AlbumLoader extends WrappedAsyncTaskLoader<ArrayList<Album>> {

    public AlbumLoader(Context context) {
        super(context);
    }

    public static ArrayList<Album> getAllAlbums(final Context context) {
        ArrayList<Song> songs = SongLoader.getSongsFromCursor(SongLoader.createSongCursor(
                context,
                null,
                null,
                MediaStore.Audio.Albums.DEFAULT_SORT_ORDER)
        );
        return splitIntoAlbums(songs);
    }

    public static ArrayList<Album> getAlbums(final Context context, String query) {
        ArrayList<Song> songs = SongLoader.getSongsFromCursor(SongLoader.createSongCursor(
                context,
                MediaStore.Audio.AudioColumns.ALBUM + " LIKE ?",
                new String[]{"%" + query + "%"},
                MediaStore.Audio.Albums.DEFAULT_SORT_ORDER)
        );
        return splitIntoAlbums(songs);
    }

    public static ArrayList<Album> splitIntoAlbums(final ArrayList<Song> songs) {
        ArrayList<Album> albums = new ArrayList<>();
        if (songs != null) {
            for (Song song : songs) {
                getOrCreateAlbum(albums, song.getAlbumId()).songsInAlbum.add(song);
            }
        }
        return albums;
    }

    private static Album getOrCreateAlbum(ArrayList<Album> albums, int albumId) {
        for (Album album : albums) {
            if (!album.songsInAlbum.isEmpty() && album.songsInAlbum.get(0).getAlbumId() == albumId) {
                return album;
            }
        }
        Album album = new Album();
        albums.add(album);
        return album;
    }

    @Override
    public ArrayList<Album> loadInBackground() {
        return getAllAlbums(getContext());
    }
}
