package com.flairmusicplayer.flair.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Author: PulakDebasish
 */

public class RecentStore extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recent_store.db";
    private static final int VERSION = 2;
    /* Maximum # of items in the db */
    private static final int MAX_ITEMS_IN_DB = 100;

    private static RecentStore sInstance = null;

    public RecentStore(final Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + RecentStoreColumns.NAME + " ("
                + RecentStoreColumns.ID + " LONG NOT NULL," + RecentStoreColumns.TIME_PLAYED
                + " LONG NOT NULL);");
    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecentStoreColumns.NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If we ever have downgrade, drop the table to be safe
        db.execSQL("DROP TABLE IF EXISTS " + RecentStoreColumns.NAME);
        onCreate(db);
    }

    /**
     * @param context The {@link Context} to use
     * @return A new instance of this class.
     */
    public static synchronized RecentStore getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new RecentStore(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Used to store song IDs in the database.
     *
     * @param songId The song id to store
     */
    public void addSongId(final long songId) {
        final SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();

        try {
            // delete previous entry for the same song
            removeItem(songId);

            // add the entry
            final ContentValues values = new ContentValues(2);
            values.put(RecentStoreColumns.ID, songId);
            values.put(RecentStoreColumns.TIME_PLAYED, System.currentTimeMillis());
            database.insert(RecentStoreColumns.NAME, null, values);

            // if our db is too large, delete the extra items
            Cursor oldest = null;
            try {
                oldest = database.query(RecentStoreColumns.NAME,
                        new String[]{RecentStoreColumns.TIME_PLAYED}, null, null, null, null,
                        RecentStoreColumns.TIME_PLAYED + " ASC");

                if (oldest != null && oldest.getCount() > MAX_ITEMS_IN_DB) {
                    oldest.moveToPosition(oldest.getCount() - MAX_ITEMS_IN_DB);
                    long timeOfRecordToKeep = oldest.getLong(0);

                    database.delete(RecentStoreColumns.NAME,
                            RecentStoreColumns.TIME_PLAYED + " < ?",
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

    /**
     * @param songId to remove.
     */
    public void removeItem(final long songId) {
        final SQLiteDatabase database = getWritableDatabase();
        database.delete(RecentStoreColumns.NAME, RecentStoreColumns.ID + " = ?", new String[]{
                String.valueOf(songId)
        });

    }

    public void deleteAll() {
        final SQLiteDatabase database = getWritableDatabase();
        database.delete(RecentStoreColumns.NAME, null, null);
    }

    /**
     * Gets a cursor to the list of recently played content
     *
     * @param limit # of songs to limit the result to
     * @return cursor
     */
    public Cursor queryRecentIds(final String limit) {
        final SQLiteDatabase database = getReadableDatabase();
        return database.query(RecentStoreColumns.NAME,
                new String[]{RecentStoreColumns.ID}, null, null, null, null,
                RecentStoreColumns.TIME_PLAYED + " DESC", limit);
    }

    public interface RecentStoreColumns {
        /* Table name */
        String NAME = "recent_history";

        /* Album IDs column */
        String ID = "song_id";

        /* Time played column */
        String TIME_PLAYED = "time_played";
    }
}
