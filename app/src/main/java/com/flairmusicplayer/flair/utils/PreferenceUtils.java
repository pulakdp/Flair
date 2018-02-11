package com.flairmusicplayer.flair.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;

/**
 * Author: PulakDebasish
 */

public class PreferenceUtils {

    private static final String ACTIVE_FRAGMENT = "active_fragment";

    private static final String RECENTLY_ADDED_CUTOFF = "recently_added_cutoff";

    private static final String LAST_FOLDER = "last_folder";

    private static final String START_PAGE_INDEX = "start_page_index";

    private static final String LASTOPENED_IS_START_PAGE = "lastopened_is_start_page";

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

    public int getStartPageIndex() {
        return preferences.getInt(START_PAGE_INDEX, 0);
    }

    public void setStartPageIndex(final int index) {
        preferences.edit().putInt(START_PAGE_INDEX, index).apply();
    }

    public void setLastOpenedAsStartPage(boolean preference) {
        preferences.edit().putBoolean(LASTOPENED_IS_START_PAGE, preference).apply();
    }

    public boolean getLastOpenedIsStartPage() {
        return preferences.getBoolean(LASTOPENED_IS_START_PAGE, true);
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

    public File getLastOpenedDirectory() {
        return new File(preferences.getString(LAST_FOLDER, FileUtils.getDefaultStartDirectoryPath()));
    }

}
