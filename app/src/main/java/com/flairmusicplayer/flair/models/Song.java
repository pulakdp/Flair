package com.flairmusicplayer.flair.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: PulakDebasish
 */

public class Song implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Song>() {

        @Override
        public Song createFromParcel(Parcel parcel) {
            return new Song(parcel);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
    private final int id;
    private final String title;
    private final long duration;
    private final int trackNumber;
    private final int albumId;
    private final String albumName;
    private final int artistId;
    private final String artistName;

    public Song() {
        this.id = -1;
        this.title = "";
        this.duration = -1;
        this.trackNumber = -1;
        this.albumId = -1;
        this.albumName = "";
        this.artistId = -1;
        this.artistName = "";
    }

    public Song(int id, String title, long duration, int trackNumber, int albumId, String albumName, int artistId, String artistName) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.albumId = albumId;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artistName = artistName;
    }

    public Song(Parcel parcel) {
        id = parcel.readInt();
        title = parcel.readString();
        duration = parcel.readLong();
        trackNumber = parcel.readInt();
        albumId = parcel.readInt();
        albumName = parcel.readString();
        artistId = parcel.readInt();
        artistName = parcel.readString();
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getDuration() {
        return duration;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public int getAlbumId() {
        return albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public int getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeLong(duration);
        dest.writeInt(trackNumber);
        dest.writeInt(albumId);
        dest.writeString(albumName);
        dest.writeInt(artistId);
        dest.writeString(artistName);
    }
}
