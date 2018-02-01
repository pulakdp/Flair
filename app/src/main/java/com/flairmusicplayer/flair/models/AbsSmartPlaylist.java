package com.flairmusicplayer.flair.models;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.DrawableRes;

import com.flairmusicplayer.flair.R;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public abstract class AbsSmartPlaylist extends Playlist {
    @DrawableRes
    private final int iconRes;

    public AbsSmartPlaylist(final String name, final int iconRes) {
        super(Math.abs(31 * name.hashCode() + (iconRes * name.hashCode() * 31 * 31)), name);
        this.iconRes = iconRes;
    }

    public AbsSmartPlaylist() {
        super();
        this.iconRes = R.drawable.ic_queue_music_black_24dp;
    }

    public AbsSmartPlaylist(Parcel in) {
        super(in);
        this.iconRes = in.readInt();
    }

    public int getIconRes() {
        return iconRes;
    }

    public abstract ArrayList<Song> getSongs(Context context);

    public abstract void clearPlaylist(Context context);

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(iconRes);
    }
}
