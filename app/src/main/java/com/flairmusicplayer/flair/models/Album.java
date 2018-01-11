package com.flairmusicplayer.flair.models;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class Album {
    public final ArrayList<Song> songsInAlbum;

    public Album() {
        songsInAlbum = new ArrayList<>();
    }

    public Album(ArrayList<Song> songsInAlbum) {
        this.songsInAlbum = songsInAlbum;
    }

    public int getAlbumId() {
        return getFirstSong().getAlbumId();
    }

    public String getAlbumName() {
        return getFirstSong().getAlbumName();
    }

    public int getArtistId() {
        return getFirstSong().getArtistId();
    }

    public String getArtistName() {
        return getFirstSong().getArtistName();
    }

    public int getSongCount() {
        return songsInAlbum.size();
    }

    public Song getFirstSong() {
        return songsInAlbum.isEmpty() ? new Song() : songsInAlbum.get(0);
    }
}
