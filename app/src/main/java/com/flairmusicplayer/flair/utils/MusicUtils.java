package com.flairmusicplayer.flair.utils;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.flairmusicplayer.flair.models.Song;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Author: PulakDebasish
 */

public class MusicUtils {

    public static Uri getTrackUri(int songId) {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
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
