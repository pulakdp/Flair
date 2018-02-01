package com.flairmusicplayer.flair.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.loaders.MostAndRecentlyPlayedSongsLoader;
import com.flairmusicplayer.flair.providers.SongPlayCount;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class MostPlayedPlaylist extends AbsSmartPlaylist {

    public static final Creator<MostPlayedPlaylist> CREATOR = new Parcelable.Creator<MostPlayedPlaylist>() {

        @Override
        public MostPlayedPlaylist createFromParcel(Parcel source) {
            return new MostPlayedPlaylist(source);
        }

        @Override
        public MostPlayedPlaylist[] newArray(int size) {
            return new MostPlayedPlaylist[size];
        }
    };

    public MostPlayedPlaylist(Context context) {
        super(context.getString(R.string.most_played), R.drawable.ic_trending_up_black_24dp);
    }

    public MostPlayedPlaylist(Parcel in) {
        super(in);
    }

    @Override
    public ArrayList<Song> getSongs(Context context) {
        return MostAndRecentlyPlayedSongsLoader.getSongs(context, MostAndRecentlyPlayedSongsLoader.QueryType.MostPlayed);
    }

    @Override
    public void clearPlaylist(Context context) {
        SongPlayCount.getInstance(context).deleteAll();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
