package com.flairmusicplayer.flair.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.flairmusicplayer.flair.loaders.SongLoader;
import com.flairmusicplayer.flair.models.Song;

import java.util.ArrayList;

/**
 * Author: PulakDebasish, Andrew Neal
 */

public class FlairPlaybackState extends SQLiteOpenHelper {

    @Nullable
    private static FlairPlaybackState sInstance = null;
    private static final String DATABASE_NAME = "flair_playback_state.db";
    private static final String PLAYING_QUEUE_TABLE_NAME = "playing_queue";
    private static final String ORIGINAL_PLAYING_QUEUE_TABLE_NAME = "original_playing_queue";
    private static final int VERSION = 1;

    public FlairPlaybackState(final Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(@NonNull final SQLiteDatabase db) {
        createTable(db, PLAYING_QUEUE_TABLE_NAME);
        createTable(db, ORIGINAL_PLAYING_QUEUE_TABLE_NAME);
    }

    private void createTable(@NonNull final SQLiteDatabase db, final String tableName) {
        //noinspection StringBufferReplaceableByString
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(tableName);
        builder.append("(");

        builder.append(BaseColumns._ID);
        builder.append(" INT NOT NULL,");

        builder.append(AudioColumns.TITLE);
        builder.append(" STRING NOT NULL,");

        builder.append(AudioColumns.DURATION);
        builder.append(" LONG NOT NULL,");

        builder.append(AudioColumns.TRACK);
        builder.append(" INT NOT NULL,");

        builder.append(AudioColumns.ALBUM_ID);
        builder.append(" INT NOT NULL,");

        builder.append(AudioColumns.ALBUM);
        builder.append(" STRING NOT NULL,");

        builder.append(AudioColumns.ARTIST_ID);
        builder.append(" INT NOT NULL,");

        builder.append(AudioColumns.ARTIST);
        builder.append(" STRING NOT NULL);");

        db.execSQL(builder.toString());
    }

    @Override
    public void onUpgrade(@NonNull final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PLAYING_QUEUE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ORIGINAL_PLAYING_QUEUE_TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        // If we ever have downgrade, drop the table to be safe
        db.execSQL("DROP TABLE IF EXISTS " + PLAYING_QUEUE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ORIGINAL_PLAYING_QUEUE_TABLE_NAME);
        onCreate(db);
    }

    @NonNull
    public static synchronized FlairPlaybackState getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new FlairPlaybackState(context.getApplicationContext());
        }
        return sInstance;
    }

    public synchronized void saveQueues(@NonNull final ArrayList<Song> playingQueue, @NonNull final ArrayList<Song> originalPlayingQueue) {
        saveQueue(PLAYING_QUEUE_TABLE_NAME, playingQueue);
        saveQueue(ORIGINAL_PLAYING_QUEUE_TABLE_NAME, originalPlayingQueue);
    }

    private synchronized void saveQueue(final String tableName, @NonNull final ArrayList<Song> queue) {
        final SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();

        try {
            database.delete(tableName, null, null);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        final int NUM_PROCESS = 20;
        int position = 0;
        while (position < queue.size()) {
            database.beginTransaction();
            try {
                for (int i = position; i < queue.size() && i < position + NUM_PROCESS; i++) {
                    Song song = queue.get(i);
                    ContentValues values = new ContentValues(4);

                    values.put(BaseColumns._ID, song.getId());
                    values.put(AudioColumns.TITLE, song.getTitle());
                    values.put(AudioColumns.DURATION, song.getDuration());
                    values.put(AudioColumns.TRACK, song.getTrackNumber());
                    values.put(AudioColumns.ALBUM_ID, song.getAlbumId());
                    values.put(AudioColumns.ALBUM, song.getAlbumName());
                    values.put(AudioColumns.ARTIST_ID, song.getArtistId());
                    values.put(AudioColumns.ARTIST, song.getArtistName());

                    database.insert(tableName, null, values);
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
                position += NUM_PROCESS;
            }
        }
    }

    @NonNull
    public ArrayList<Song> getSavedPlayingQueue() {
        return getQueue(PLAYING_QUEUE_TABLE_NAME);
    }

    @NonNull
    public ArrayList<Song> getSavedOriginalPlayingQueue() {
        return getQueue(ORIGINAL_PLAYING_QUEUE_TABLE_NAME);
    }

    @NonNull
    private ArrayList<Song> getQueue(@NonNull final String tableName) {
        Cursor cursor = getReadableDatabase().query(tableName, null,
                null, null, null, null, null);
        return SongLoader.getSongsFromCursor(cursor);
    }
}
