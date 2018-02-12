package com.flairmusicplayer.flair.providers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Author: PulakDebasish
 */

public class RecentStoreContract {

    public static final String CONTENT_AUTHORITY = "com.flairmusicplayer.flair";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SONG = "recent_songs";

    public static class RecentStoreColumns implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SONG).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SONG;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SONG;

        /* Table name */
        public static final String TABLE_NAME = "recent_songs";

        /* Album IDs column */
        public static final String SONG_ID = "song_id";

        /* Time played column */
        public static final String TIMES_PLAYED = "times_played";

        public static Uri buildSongUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static int getSongIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }

}
