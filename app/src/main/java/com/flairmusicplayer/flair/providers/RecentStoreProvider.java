package com.flairmusicplayer.flair.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Author: PulakDebasish
 */

public class RecentStoreProvider extends ContentProvider {

    static final int SONG = 100;
    static final int SONG_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private RecentStoreHelper recentStoreHelper;

    /* Maximum # of items in the db */
    private static final int MAX_ITEMS_IN_DB = 100;

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecentStoreContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, RecentStoreContract.PATH_SONG, SONG);
        matcher.addURI(authority, RecentStoreContract.PATH_SONG + "/#", SONG_WITH_ID);
        return matcher;

    }

    public Cursor queryRecentIds() {
        final SQLiteDatabase database = recentStoreHelper.getReadableDatabase();
        return database.query(RecentStoreContract.RecentStoreColumns.TABLE_NAME,
                new String[]{RecentStoreContract.RecentStoreColumns.SONG_ID},
                null, null, null, null,
                RecentStoreContract.RecentStoreColumns.TIMES_PLAYED + " DESC", null);
    }

    public void addSongId(final long songId) {
        final SQLiteDatabase database = recentStoreHelper.getWritableDatabase();
        database.beginTransaction();

        try {
            // delete previous entry for the same song
            removeItem(songId);

            // add the entry
            final ContentValues values = new ContentValues(2);
            values.put(RecentStoreContract.RecentStoreColumns.SONG_ID, songId);
            values.put(RecentStoreContract.RecentStoreColumns.TIMES_PLAYED, System.currentTimeMillis());
            database.insert(RecentStoreContract.RecentStoreColumns.TABLE_NAME, null, values);

            // if our db is too large, delete the extra items
            Cursor oldest = null;
            try {
                oldest = database.query(RecentStoreContract.RecentStoreColumns.TABLE_NAME,
                        new String[]{RecentStoreContract.RecentStoreColumns.TIMES_PLAYED},
                        null, null, null, null,
                        RecentStoreContract.RecentStoreColumns.TIMES_PLAYED + " ASC");

                if (oldest != null && oldest.getCount() > MAX_ITEMS_IN_DB) {
                    oldest.moveToPosition(oldest.getCount() - MAX_ITEMS_IN_DB);
                    long timeOfRecordToKeep = oldest.getLong(0);

                    database.delete(RecentStoreContract.RecentStoreColumns.TABLE_NAME,
                            RecentStoreContract.RecentStoreColumns.TIMES_PLAYED + " < ?",
                            new String[]{String.valueOf(timeOfRecordToKeep)});

                }
            } finally {
                if (oldest != null) {
                    oldest.close();
                    oldest = null;
                }
            }
        } finally {
            database.setTransactionSuccessful();
            database.endTransaction();
        }
    }

    public void removeItem(final long songId) {
        final SQLiteDatabase database = recentStoreHelper.getWritableDatabase();
        database.delete(RecentStoreContract.RecentStoreColumns.TABLE_NAME,
                RecentStoreContract.RecentStoreColumns.SONG_ID + " = ?", new String[]{
                        String.valueOf(songId)
                });
    }

    public void deleteAll() {
        final SQLiteDatabase database = recentStoreHelper.getWritableDatabase();
        database.delete(RecentStoreContract.RecentStoreColumns.TABLE_NAME, null, null);
    }

    @Override
    public boolean onCreate() {
        recentStoreHelper = new RecentStoreHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor returnedCursor;
        switch (sUriMatcher.match(uri)) {
            case SONG:
                returnedCursor = queryRecentIds();
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnedCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SONG:
                return RecentStoreContract.RecentStoreColumns.CONTENT_TYPE;
            case SONG_WITH_ID:
                return RecentStoreContract.RecentStoreColumns.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case SONG_WITH_ID: {
                addSongId(RecentStoreContract.RecentStoreColumns.getSongIdFromUri(uri));
                returnUri = uri;
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SONG:
                deleteAll();
                break;
            case SONG_WITH_ID:
                removeItem(RecentStoreContract.RecentStoreColumns.getSongIdFromUri(uri));
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
