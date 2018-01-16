package com.flairmusicplayer.flair.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Author: PulakDebasish
 */

public class PreferenceUtils {

    public static final String RECENTLY_ADDED_CUTOFF = "recently_added_cutoff";
    private static PreferenceUtils sInstance;

    private final SharedPreferences mPreferences;

    public PreferenceUtils(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceUtils getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtils(context.getApplicationContext());
        }
        return sInstance;
    }

    public void setRecentlyAddedCutoff(long recentlyAddedMillis) {
        mPreferences.edit().putLong(RECENTLY_ADDED_CUTOFF, recentlyAddedMillis).apply();
    }

    public long getRecentlyAddedCutoff() {
        return mPreferences.getLong(RECENTLY_ADDED_CUTOFF, 0L);
    }

}
