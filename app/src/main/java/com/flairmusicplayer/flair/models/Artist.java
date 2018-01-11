package com.flairmusicplayer.flair.models;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class Artist {

    public final ArrayList<Album> albumsOfArtist;

    public Artist() {
        albumsOfArtist = new ArrayList<>();
    }

    public Artist(ArrayList<Album> albumsOfArtist) {
        this.albumsOfArtist = albumsOfArtist;
    }

    public int getArtistId() {
        return getFirstAlbum().getArtistId();
    }

    public String getArtistName() {
        return getFirstAlbum().getArtistName();
    }

    public int getSongCount() {
        int songCount = 0;
        for (Album album: albumsOfArtist) {
            songCount += album.getSongCount();
        }
        return songCount;
    }

    public int getAlbumCount() {
        return albumsOfArtist.size();
    }

    public Album getFirstAlbum() {
        return albumsOfArtist.isEmpty() ? new Album() : albumsOfArtist.get(0);
    }
}
