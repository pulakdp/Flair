package com.flairmusicplayer.flair.loaders;

import android.annotation.SuppressLint;
import android.database.AbstractCursor;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author: PulakDebasish
 */

public class SortedCursor extends AbstractCursor {
    // cursor to wrap
    private final Cursor cursor;
    // the map of external indices to internal indices
    private ArrayList<Integer> orderedPositions;
    // this contains the ids that weren't found in the underlying cursor
    private ArrayList<Long> missingIds;
    // this contains the mapped cursor positions and afterwards the extra ids that weren't found
    private HashMap<Long, Integer> mapCursorPositions;

    /**
     * @param cursor     to wrap
     * @param order      the list of unique ids in sorted order to display
     * @param columnName the column name of the id to look up in the internal cursor
     */
    public SortedCursor(final Cursor cursor, final long[] order, final String columnName) {
        if (cursor == null) {
            throw new IllegalArgumentException("Non-null cursor is needed");
        }

        this.cursor = cursor;
        missingIds = buildCursorPositionMapping(order, columnName);
    }

    /**
     * This function populates orderedPositions with the cursor positions in the order based
     * on the order passed in
     *
     * @param order     the target order of the internal cursor
     * @return returns the ids that aren't found in the underlying cursor
     */
    @SuppressLint("UseSparseArrays")
    private ArrayList<Long> buildCursorPositionMapping(final long[] order,
                                                       final String columnName) {
        ArrayList<Long> missingIds = new ArrayList<>();

        orderedPositions = new ArrayList<>(cursor.getCount());

        mapCursorPositions = new HashMap<>(cursor.getCount());
        final int idPosition = cursor.getColumnIndex(columnName);

        if (cursor.moveToFirst()) {
            // first figure out where each of the ids are in the cursor
            do {
                mapCursorPositions.put(cursor.getLong(idPosition), cursor.getPosition());
            } while (cursor.moveToNext());

            // now create the ordered positions to map to the internal cursor given the
            // external sort order
            for (int i = 0; order != null && i < order.length; i++) {
                final long id = order[i];
                if (mapCursorPositions.containsKey(id)) {
                    orderedPositions.add(mapCursorPositions.get(id));
                    mapCursorPositions.remove(id);
                } else {
                    missingIds.add(id);
                }
            }

            cursor.moveToFirst();
        }

        return missingIds;
    }

    /**
     * @return the list of ids that weren't found in the underlying cursor
     */
    public ArrayList<Long> getMissingIds() {
        return missingIds;
    }

    @Override
    public void close() {
        cursor.close();
        super.close();
    }

    @Override
    public int getCount() {
        return orderedPositions.size();
    }

    @Override
    public String[] getColumnNames() {
        return cursor.getColumnNames();
    }

    @Override
    public String getString(int column) {
        return cursor.getString(column);
    }

    @Override
    public short getShort(int column) {
        return cursor.getShort(column);
    }

    @Override
    public int getInt(int column) {
        return cursor.getInt(column);
    }

    @Override
    public long getLong(int column) {
        return cursor.getLong(column);
    }

    @Override
    public float getFloat(int column) {
        return cursor.getFloat(column);
    }

    @Override
    public double getDouble(int column) {
        return cursor.getDouble(column);
    }

    @Override
    public boolean isNull(int column) {
        return cursor.isNull(column);
    }

    @Override
    public boolean onMove(int oldPosition, int newPosition) {
        if (newPosition >= 0 && newPosition < getCount()) {
            cursor.moveToPosition(orderedPositions.get(newPosition));
            return true;
        }
        return false;
    }
}