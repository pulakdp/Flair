package com.flairmusicplayer.flair.utils;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.flairmusicplayer.flair.models.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Author: PulakDebasish
 */

public class MusicUtils {

    public static Uri getTrackUri(int songId) {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
    }

    public static String formatTimeToString(int timeInMillis) {
        int timeInSeconds = timeInMillis / 1000;
        int seconds = timeInSeconds % 60;
        int minutes = (timeInSeconds / 60) % 60;
        int hours = timeInSeconds / 3600; // Such a big file. Wow.
        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    public static void makeShuffleList(@NonNull ArrayList<Song> listToShuffle, final int current) {
        if (listToShuffle.isEmpty())
            return;
        if (current >= 0) {
            Song currentSong = listToShuffle.remove(current);
            Collections.shuffle(listToShuffle);
            listToShuffle.add(0, currentSong);
        }else {
            Collections.shuffle(listToShuffle);
        }
    }
}
