package com.flairmusicplayer.flair.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Author: PulakDebasish
 */

public class PreferenceUtils {

    private static final String ACTIVE_FRAGMENT = "active_fragment";

    private static final String RECENTLY_ADDED_CUTOFF = "recently_added_cutoff";

    private static PreferenceUtils instance;

    private final SharedPreferences preferences;

    public PreferenceUtils(final Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceUtils getInstance(final Context context) {
        if (instance == null) {
            instance = new PreferenceUtils(context.getApplicationContext());
        }
        return instance;
    }

    public void setActiveFragment(int key) {
        preferences.edit().putInt(ACTIVE_FRAGMENT, key).apply();
    }

    public int getLastActiveFragment() {
        return preferences.getInt(ACTIVE_FRAGMENT, 0);
    }

    public void setRecentlyAddedCutoff(long recentlyAddedMillis) {
        preferences.edit().putLong(RECENTLY_ADDED_CUTOFF, recentlyAddedMillis).apply();
    }

    public long getRecentlyAddedCutoff() {
        return preferences.getLong(RECENTLY_ADDED_CUTOFF, 0L);
    }

}
