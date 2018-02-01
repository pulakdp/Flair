package com.flairmusicplayer.flair.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.loaders.RecentlyAddedSongsLoader;
import com.flairmusicplayer.flair.utils.PreferenceUtils;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class RecentlyAddedPlaylist extends AbsSmartPlaylist {

    public static final Creator<RecentlyAddedPlaylist> CREATOR = new Parcelable.Creator<RecentlyAddedPlaylist>() {

        @Override
        public RecentlyAddedPlaylist createFromParcel(Parcel source) {
            return new RecentlyAddedPlaylist(source);
        }

        @Override
        public RecentlyAddedPlaylist[] newArray(int size) {
            return new RecentlyAddedPlaylist[size];
        }
    };

    public RecentlyAddedPlaylist(Context context) {
        super(context.getString(R.string.recently_added), R.drawable.ic_library_add_black_24dp);
    }

    public RecentlyAddedPlaylist(Parcel in) {
        super(in);
    }

    @Override
    public ArrayList<Song> getSongs(Context context) {
        return RecentlyAddedSongsLoader.getRecentlyAddedSongs(context);
    }

    @Override
    public void clearPlaylist(Context context) {
        PreferenceUtils.getInstance(context).setRecentlyAddedCutoff(System.currentTimeMillis());
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
