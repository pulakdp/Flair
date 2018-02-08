package com.flairmusicplayer.flair.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class Artist implements Parcelable {

    public final ArrayList<Album> albumsOfArtist;

    public Artist() {
        albumsOfArtist = new ArrayList<>();
    }

    public Artist(ArrayList<Album> albumsOfArtist) {
        this.albumsOfArtist = albumsOfArtist;
    }

    public Artist(Parcel in) {
        albumsOfArtist = in.readArrayList(getClass().getClassLoader());
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

    public ArrayList<Song> getSongsForArtist() {
        ArrayList<Song> songsOfArtist = new ArrayList<>();
        for (Album album : albumsOfArtist) {
            songsOfArtist.addAll(album.songsInAlbum);
        }
        return songsOfArtist;
    }

    public int getAlbumCount() {
        return albumsOfArtist.size();
    }

    public Album getFirstAlbum() {
        return albumsOfArtist.isEmpty() ? new Album() : albumsOfArtist.get(0);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Artist>() {

        @Override
        public Artist createFromParcel(Parcel parcel) {
            return new Artist(parcel);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(albumsOfArtist);
    }
}
