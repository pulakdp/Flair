package com.flairmusicplayer.flair.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.loaders.PlaylistLoader;
import com.flairmusicplayer.flair.models.Playlist;
import com.flairmusicplayer.flair.models.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Author: PulakDebasish
 */

public class MusicUtils {

    public static Uri getAlbumArtUri(int albumId) {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(sArtworkUri, albumId);
    }

    public static Uri getTrackUri(int songId) {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
    }

    public static void shareSong(final Context context, int songId) {

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("audio/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, getSongFileUri(context, songId));
            context.startActivity(Intent.createChooser(shareIntent, "Share"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri getSongFileUri(Context context, int songId) {

        final Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                BaseColumns._ID + "=?",
                new String[]{String.valueOf(songId)},
                null);

        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();
        try {
            Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            cursor.close();
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    public static Playlist getFavoritesPlaylist(@NonNull final Context context) {
        return PlaylistLoader.getPlaylist(context, context.getString(R.string.favorite_playlist));
    }

    private static Playlist getOrCreateFavoritesPlaylist(@NonNull final Context context) {
        return PlaylistLoader.getPlaylist(context,
                PlaylistUtils.createPlaylist(context, context.getString(R.string.favorite_playlist)));
    }

    public static boolean isFavorite(@NonNull final Context context, @NonNull final Song song) {
        return PlaylistUtils.doPlaylistContains(context, getFavoritesPlaylist(context).getId(), song.getId());
    }

    public static void toggleFavorite(@NonNull final Context context, @NonNull final Song song) {
        if (isFavorite(context, song)) {
            PlaylistUtils.removeFromPlaylist(context, song, getFavoritesPlaylist(context).getId());
        } else {
            PlaylistUtils.addToPlaylist(context, song, getOrCreateFavoritesPlaylist(context).getId(), false);
        }
    }

    public static void addToFavorite(@NonNull final Context context, @NonNull final Song song) {
        if (!isFavorite(context, song))
            PlaylistUtils.addToPlaylist(context, song, getOrCreateFavoritesPlaylist(context).getId(), false);
    }
}
