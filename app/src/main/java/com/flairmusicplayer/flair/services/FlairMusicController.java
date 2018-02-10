package com.flairmusicplayer.flair.services;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.flairmusicplayer.flair.models.Song;

import java.util.ArrayList;
import java.util.Random;
import java.util.WeakHashMap;

/**
 * Author: PulakDebasish
 */

public class FlairMusicController {

    private static FlairMusicService fmService;

    private static final WeakHashMap<Context, ServiceBinder> connectionMap = new WeakHashMap<>();

    public static ServiceToken bindToService(final Context context,
                                             final ServiceConnection callback) {
        Activity realActivity = ((Activity) context).getParent();
        if (realActivity == null) {
            realActivity = (Activity) context;
        }
        final ContextWrapper contextWrapper = new ContextWrapper(realActivity);
        contextWrapper.startService(new Intent(contextWrapper, FlairMusicService.class));
        final ServiceBinder binder = new ServiceBinder(callback);
        if (contextWrapper.bindService(
                new Intent().setClass(contextWrapper, FlairMusicService.class), binder, 0)) {
            connectionMap.put(contextWrapper, binder);
            return new ServiceToken(contextWrapper);
        }
        return null;
    }

    public static void unbindFromService(final ServiceToken token) {
        if (token == null) {
            return;
        }
        final ContextWrapper contextWrapper = token.wrappedContext;
        final ServiceBinder binder = connectionMap.remove(contextWrapper);
        if (binder == null) {
            return;
        }
        contextWrapper.unbindService(binder);
        if (connectionMap.isEmpty()) {
            fmService = null;
        }
    }

    public static final class ServiceBinder implements ServiceConnection {

        private final ServiceConnection callback;

        public ServiceBinder(final ServiceConnection callback) {
            this.callback = callback;
        }

        @Override
        public void onServiceConnected(final ComponentName className, final IBinder service) {
            FlairMusicService.FlairMusicBinder binder = (FlairMusicService.FlairMusicBinder) service;
            fmService = binder.getService();
            if (callback != null) {
                callback.onServiceConnected(className, service);
            }
        }

        @Override
        public void onServiceDisconnected(final ComponentName className) {
            if (callback != null) {
                callback.onServiceDisconnected(className);
            }
            fmService = null;
        }
    }

    public static final class ServiceToken {

        private ContextWrapper wrappedContext;

        private ServiceToken(final ContextWrapper context) {
            wrappedContext = context;
        }
    }

    public static void playSongAt(final int position) {
        if (fmService != null)
            fmService.playSongAt(position);
    }

    public static int getPosition() {
        if (fmService != null)
            return fmService.getPosition();
        return -1;
    }

    private static void setPosition(final int position) {
        if (fmService != null)
            fmService.setPosition(position);
    }

    public static boolean playNext(Song song) {
        if (fmService != null) {
            if (getPlayingQueue().size() > 0) {
                fmService.addSong(getPosition() + 1, song);
            } else {
                ArrayList<Song> queue = new ArrayList<>();
                queue.add(song);
                openQueue(queue, 0, false);
            }
            return true;
        }
        return false;
    }

    public static boolean enqueue(Song song) {
        if (fmService != null) {
            if (getPlayingQueue().size() > 0) {
                fmService.addSong(song);
            } else {
                ArrayList<Song> queue = new ArrayList<>();
                queue.add(song);
                openQueue(queue, 0, false);
            }
            return true;
        }
        return false;
    }

    public static void openQueue(final ArrayList<Song> queue, final int startPosition, final boolean startPlaying) {
        if (!tryToHandleOpenPlayingQueue(queue, startPosition, startPlaying) && fmService != null)
            fmService.openQueue(queue, startPosition, startPlaying);
    }

    public static void openAndShuffleQueue(final ArrayList<Song> queue, boolean startPlaying) {
        int startPosition = 0;
        if (!queue.isEmpty()) {
            startPosition = new Random().nextInt(queue.size());
        }

        if (!tryToHandleOpenPlayingQueue(queue, startPosition, startPlaying) && fmService != null) {
            openQueue(queue, startPosition, startPlaying);
            setShuffleMode(FlairMusicService.SHUFFLE_MODE_SHUFFLE);
        }
    }

    private static boolean tryToHandleOpenPlayingQueue(final ArrayList<Song> queue, final int startPosition, final boolean startPlaying) {
        if (getPlayingQueue() == queue) {
            if (startPlaying) {
                playSongAt(startPosition);
            } else {
                setPosition(startPosition);
            }
            return true;
        }
        return false;
    }

    public static ArrayList<Song> getPlayingQueue() {
        if (fmService != null)
            return fmService.getPlayingQueue();
        return new ArrayList<>();
    }

    public static void clearQueue() {
        if (fmService != null) {
            fmService.clearQueue();
        }
    }

    public static void pauseSong() {
        if (fmService != null)
            fmService.pause();
    }

    public static void playNextSong() {
        if (fmService != null)
            fmService.playNextSong(false);
    }

    public static void playPreviousSong() {
        if (fmService != null) {
            fmService.playPreviousSong(true);
        }
    }

    public static boolean isPlaying() {
        return fmService != null && fmService.isPlaying();
    }

    public static void resumePlaying() {
        if (fmService != null) {
            fmService.play();
        }
    }

    public static void togglePlayPause() {
        if (fmService != null)
            fmService.togglePlayPause();
    }

    public static void cycleRepeatMode() {
        if (fmService != null)
            fmService.cycleRepeatMode();
    }

    public static void toggleShuffleMode() {
        if (fmService != null)
            fmService.toggleShuffleMode();
    }

    public static void seek(final long position) {
        if (fmService != null) {
            try {
                fmService.seek(position);
            } catch (Exception ignored) {

            }
        }
    }

    public static int getRepeatMode() {
        if (fmService != null)
            return fmService.getRepeatMode();
        return -1;
    }

    public static void setRepeatMode(int repeatMode) {
        if (fmService != null)
            fmService.setRepeatMode(repeatMode);
    }

    public static int getShuffleMode() {
        if (fmService != null)
            return fmService.getShuffleMode();
        return -1;
    }

    public static void setShuffleMode(int shuffleMode) {
        if (fmService != null)
            fmService.setShuffleMode(shuffleMode);
    }

    public static Song getCurrentSong() {
        return fmService != null ? fmService.getCurrentSong() : new Song();
    }

    public static int getSongProgress() {
        return fmService != null ? (int) fmService.getSongProgress() : -1;
    }

    public static int getSongDuration() {
        return fmService != null ? (int) fmService.getSongDuration() : -1;
    }

    public static int getAudioSessionId() {
        if (fmService != null)
            return fmService.getAudioSessionId();
        return -1;
    }
}
