package com.flairmusicplayer.flair.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.loaders.MostAndRecentlyPlayedSongsLoader;
import com.flairmusicplayer.flair.providers.RecentStoreContract;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class RecentlyPlayedPlaylist extends AbsSmartPlaylist {

    public static final Creator<RecentlyPlayedPlaylist> CREATOR = new Parcelable.Creator<RecentlyPlayedPlaylist>() {

        @Override
        public RecentlyPlayedPlaylist createFromParcel(Parcel source) {
            return new RecentlyPlayedPlaylist(source);
        }

        @Override
        public RecentlyPlayedPlaylist[] newArray(int size) {
            return new RecentlyPlayedPlaylist[size];
        }
    };

    public RecentlyPlayedPlaylist(Context context) {
        super(context.getString(R.string.recently_played), R.drawable.ic_history_black_24dp);
    }

    public RecentlyPlayedPlaylist(Parcel in) {
        super(in);
    }

    @Override
    public ArrayList<Song> getSongs(Context context) {
        return MostAndRecentlyPlayedSongsLoader.getSongs(context, MostAndRecentlyPlayedSongsLoader.QueryType.RecentlyPlayed);
    }

    @Override
    public void clearPlaylist(Context context) {
        //Replacement
        context.getContentResolver().delete(RecentStoreContract.RecentStoreColumns.CONTENT_URI, null, null);
//        RecentStore.getInstance(context).deleteAll();
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
