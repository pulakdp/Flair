package com.flairmusicplayer.flair.loaders;


import android.content.Context;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.flairmusicplayer.flair.models.Album;
import com.flairmusicplayer.flair.models.Artist;
import com.flairmusicplayer.flair.models.Song;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class ArtistLoader extends WrappedAsyncTaskLoader<ArrayList<Artist>> {

    public ArtistLoader(Context context) {
        super(context);
    }

    public static ArrayList<Artist> getAllArtists(final Context context) {
        ArrayList<Song> songs = SongLoader.getSongsFromCursor(
                SongLoader.createSongCursor(context,
                        null,
                        null,
                        MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
                ));
        return splitIntoArtists(AlbumLoader.splitIntoAlbums(songs));
    }

    public static ArrayList<Artist> getArtists(final Context context, String query) {
        ArrayList<Song> songs = SongLoader.getSongsFromCursor(SongLoader.createSongCursor(
                context,
                MediaStore.Audio.AudioColumns.ARTIST + " LIKE ?",
                new String[]{"%" + query + "%"},
                MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
        ));
        return splitIntoArtists(AlbumLoader.splitIntoAlbums(songs));
    }

    public static ArrayList<Artist> splitIntoArtists(@Nullable final ArrayList<Album> albums) {
        ArrayList<Artist> artists = new ArrayList<>();
        if (albums != null) {
            for (Album album : albums) {
                getOrCreateArtist(artists, album.getArtistId()).albumsOfArtist.add(album);
            }
        }
        return artists;
    }

    private static Artist getOrCreateArtist(ArrayList<Artist> artists, int artistId) {
        for (Artist artist : artists) {
            if (!artist.albumsOfArtist.isEmpty() &&
                    !artist.albumsOfArtist.get(0).songsInAlbum.isEmpty() &&
                    artist.albumsOfArtist.get(0).songsInAlbum.get(0).getArtistId() == artistId) {
                return artist;
            }
        }
        Artist artist = new Artist();
        artists.add(artist);
        return artist;
    }

    @Override
    public ArrayList<Artist> loadInBackground() {
        return getAllArtists(getContext());
    }
}
