package com.flairmusicplayer.flair.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class Album implements Parcelable {
    public final ArrayList<Song> songsInAlbum;

    public Album() {
        songsInAlbum = new ArrayList<>();
    }

    public Album(ArrayList<Song> songsInAlbum) {
        this.songsInAlbum = songsInAlbum;
    }

    public Album(Parcel in) {
        songsInAlbum = in.readArrayList(getClass().getClassLoader());
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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Album>() {

        @Override
        public Album createFromParcel(Parcel parcel) {
            return new Album(parcel);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(songsInAlbum);
    }
}
