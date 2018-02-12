package com.flairmusicplayer.flair.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Author: PulakDebasish
 */

public class RecentStoreHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recent_store.db";
    private static final int VERSION = 2;

    public RecentStoreHelper(final Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + RecentStoreContract.RecentStoreColumns.TABLE_NAME + " ("
                + RecentStoreContract.RecentStoreColumns.SONG_ID + " LONG NOT NULL,"
                + RecentStoreContract.RecentStoreColumns.TIMES_PLAYED
                + " LONG NOT NULL);");
    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecentStoreContract.RecentStoreColumns.TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If we ever have downgrade, drop the table to be safe
        db.execSQL("DROP TABLE IF EXISTS " + RecentStoreContract.RecentStoreColumns.TABLE_NAME);
        onCreate(db);
    }
}
