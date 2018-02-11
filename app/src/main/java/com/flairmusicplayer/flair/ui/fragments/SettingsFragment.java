package com.flairmusicplayer.flair.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.utils.PreferenceUtils;

/**
 * Author: PulakDebasish
 */

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private static final String KEY_START_PAGE = "start_page_preference";

    public static final String LAST_OPENED = "last_opened";

    public static final String SONGS = "songs";

    public static final String ALBUMS = "albums";

    public static final String ARTISTS = "artists";

    public static final String PLAYLISTS = "playlists";

    private PreferenceUtils preferenceUtils;
    private ListPreference startPagePreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceUtils = PreferenceUtils.getInstance(getActivity());

        startPagePreference = (ListPreference) findPreference(KEY_START_PAGE);

        startPagePreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch ((String) newValue) {
            case LAST_OPENED:
                preferenceUtils.setLastOpenedAsStartPage(true);
                break;
            case SONGS:
                preferenceUtils.setLastOpenedAsStartPage(false);
                preferenceUtils.setStartPageIndex(0);
                break;
            case ALBUMS:
                preferenceUtils.setLastOpenedAsStartPage(false);
                preferenceUtils.setStartPageIndex(1);
                break;
            case ARTISTS:
                preferenceUtils.setLastOpenedAsStartPage(false);
                preferenceUtils.setStartPageIndex(2);
                break;
            case PLAYLISTS:
                preferenceUtils.setLastOpenedAsStartPage(false);
                preferenceUtils.setStartPageIndex(3);
                break;
        }
        return true;
    }
}
