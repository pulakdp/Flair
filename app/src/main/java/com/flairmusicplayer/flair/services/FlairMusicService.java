package com.flairmusicplayer.flair.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.widget.Toast;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.providers.FlairPlaybackState;
import com.flairmusicplayer.flair.providers.RecentStore;
import com.flairmusicplayer.flair.providers.SongPlayCount;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.flairmusicplayer.flair.utils.MusicUtils;
import com.flairmusicplayer.flair.utils.NavUtils;
import com.flairmusicplayer.flair.utils.Stopwatch;
import com.flairmusicplayer.flair.widgets.BigWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * Author: PulakDebasish
 */

public class FlairMusicService extends Service {

    public static final String FLAIR_PACKAGE_NAME = "com.flairmusicplayer.flair";
    public static final String MUSIC_PACKAGE_NAME = "com.android.music";

    public static final String PLAY_STATE_CHANGED = FLAIR_PACKAGE_NAME + ".playstatechanged";
    public static final String META_CHANGED = FLAIR_PACKAGE_NAME + ".metachanged";
    public static final String QUEUE_CHANGED = FLAIR_PACKAGE_NAME + ".queuechanged";

    public static final String REPEAT_MODE_CHANGED = FLAIR_PACKAGE_NAME + ".repeatmodechanged";
    public static final String SHUFFLE_MODE_CHANGED = FLAIR_PACKAGE_NAME + ".shufflemodechanged";
    public static final String POSITION_CHANGED = FLAIR_PACKAGE_NAME + ".positionchanged";

    public static final String ACTION_TOGGLE_PAUSE = FLAIR_PACKAGE_NAME + ".togglepause";
    public static final String ACTION_PLAY = FLAIR_PACKAGE_NAME + ".play";
    public static final String ACTION_PLAY_PLAYLIST = FLAIR_PACKAGE_NAME + ".play.playlist";
    public static final String ACTION_PAUSE = FLAIR_PACKAGE_NAME + ".pause";
    public static final String ACTION_STOP = FLAIR_PACKAGE_NAME + ".stop";
    public static final String ACTION_NEXT = FLAIR_PACKAGE_NAME + ".next";
    public static final String ACTION_PREVIOUS = FLAIR_PACKAGE_NAME + ".previous";

    public static final int NOTIFICATION_ID = 1;
    public static final String NOTIFICATION_CHANNEL_ID = "flair_playing_notification";

    public static final String WIDGET_UPDATE = FLAIR_PACKAGE_NAME + ".widgetupdate";
    public static final String EXTRA_WIDGET_NAME = FLAIR_PACKAGE_NAME + "widget_name";

    public static final int SHUFFLE_MODE_NONE = 0;
    public static final int SHUFFLE_MODE_SHUFFLE = 1;

    public static final int REPEAT_MODE_NONE = 0;
    public static final int REPEAT_MODE_ALL = 1;
    public static final int REPEAT_MODE_THIS = 2;
    public static final int PLAY_SONG = 3;
    public static final int PREPARE_NEXT = 4;
    public static final int SET_POSITION = 5;
    public static final int SAVE_QUEUES = 6;
    public static final int RESTORE_QUEUES = 7;
    private static final int FOCUS_CHANGE = 8;

    public static final int TRACK_ENDED = 1;
    public static final int TRACK_WENT_TO_NEXT = 2;

    public static final String SAVED_POSITION = "POSITION";
    public static final String SAVED_SONG_PROGRESS = "POSITION_IN_TRACK";
    public static final String SAVED_SHUFFLE_MODE = "SHUFFLE_MODE";
    public static final String SAVED_REPEAT_MODE = "REPEAT_MODE";

    private static final int NOTIFY_MODE_FOREGROUND = 1;
    private static final int NOTIFY_MODE_BACKGROUND = 2;

    private static final int IDLE_DELAY = 5 * 60 * 1000;
    private static final long REWIND_INSTEAD_PREVIOUS_THRESHOLD = 2000;

    private BigWidget bigWidget = BigWidget.getInstance();

    private final IBinder musicBinder = new FlairMusicBinder();

    private final BroadcastReceiver widgetIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleIntent(intent);
        }
    };
    private ArrayList<Song> playingQueue = new ArrayList<>();
    private ArrayList<Song> originalPlayingQueue = new ArrayList<>();
    private AudioManager audioManager;
    private MediaSessionCompat mediaSession;
    private PowerManager.WakeLock wakeLock;
    private HandlerThread musicPlayerHandlerThread;
    private MusicHandler musicHandler;
    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onAudioFocusChange(final int focusChange) {
            musicHandler.obtainMessage(FOCUS_CHANGE, focusChange, 0).sendToTarget();
        }
    };
    private MultiPlayer player;
    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                pause();
            }
        }
    };
    private NotificationManager notificationManager;
    private IntentFilter becomingNoisyReceiverIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private SongPlayCountHelper songPlayCountHelper = new SongPlayCountHelper();

    private int currentPos = -1;
    private int nextPos = -1;
    private boolean isServiceBound;
    private int shuffleMode;
    private int repeatMode;
    private boolean queuesRestored;
    private int notifyMode = NOTIFY_MODE_BACKGROUND;
    private boolean pausedByTransientLossOfFocus;
    private boolean notHandledMetaChangedForCurrentTrack;
    private boolean becomingNoisyReceiverRegistered = false;

    @Override
    public IBinder onBind(Intent intent) {
        Timber.d("Service Bound. Intent = %s", intent);
        isServiceBound = true;
        return musicBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        isServiceBound = true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isServiceBound = false;
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        musicPlayerHandlerThread = new HandlerThread(MusicHandler.class.getName()
                , Process.THREAD_PRIORITY_BACKGROUND);
        musicPlayerHandlerThread.start();

        musicHandler = new MusicHandler(this, musicPlayerHandlerThread.getLooper());

        setupMediaSession();

        registerReceiver(widgetIntentReceiver, new IntentFilter(WIDGET_UPDATE));

        initNotification();

        player = new MultiPlayer(this, this);
        player.setHandler(musicHandler);

        restoreState();

        mediaSession.setActive(true);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(widgetIntentReceiver);
        if (becomingNoisyReceiverRegistered) {
            unregisterReceiver(becomingNoisyReceiver);
            becomingNoisyReceiverRegistered = false;
        }
        mediaSession.setActive(false);
        cancelNotification();
        player.release();
        mediaSession.release();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction() != null) {
                restoreQueuesAndPositionIfNecessary();
                String action = intent.getAction();
                switch (action) {
                    case ACTION_TOGGLE_PAUSE:
                        if (isPlaying()) {
                            pause();
                        } else {
                            play();
                        }
                        break;
                    case ACTION_PAUSE:
                        pause();
                        break;
                    case ACTION_PLAY:
                        play();
                        break;
                    case ACTION_PLAY_PLAYLIST:
                        break;
                    case ACTION_PREVIOUS:
                        playPreviousSong(true);
                        break;
                    case ACTION_NEXT:
                        playNextSong(true);
                        break;
                    case ACTION_STOP:
                        stop();
                }
            }
        }

        return START_STICKY;
    }

    public void setupMediaSession() {
        mediaSession = new MediaSessionCompat(this, "Flair");
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                play();
            }

            @Override
            public void onPause() {
                pause();
            }

            @Override
            public void onSkipToNext() {
                playNextSong(true);
            }

            @Override
            public void onSkipToPrevious() {
                playPreviousSong(true);
            }

            @Override
            public void onStop() {
                stop();
            }

            @Override
            public void onSeekTo(long pos) {
                seek(pos);
            }

            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                return super.onMediaButtonEvent(mediaButtonEvent);
            }
        });

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0,
                new Intent(this, MediaButtonIntentReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mediaSession.setMediaButtonReceiver(pendingIntent);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
    }

    private void saveQueuesImpl() {
        FlairPlaybackState.getInstance(this).saveQueues(playingQueue, originalPlayingQueue);
    }

    private void savePosition() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(SAVED_POSITION, getPosition()).apply();
    }

    private void saveSongProgress() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(SAVED_SONG_PROGRESS, (int) getSongProgress()).apply();
    }

    public void saveState() {
        saveQueues();
        savePosition();
        saveSongProgress();
    }

    private void saveQueues() {
        musicHandler.removeMessages(SAVE_QUEUES);
        musicHandler.sendEmptyMessage(SAVE_QUEUES);
    }

    private void restoreState() {
        shuffleMode = PreferenceManager.getDefaultSharedPreferences(this).getInt(SAVED_SHUFFLE_MODE, 0);
        repeatMode = PreferenceManager.getDefaultSharedPreferences(this).getInt(SAVED_REPEAT_MODE, 0);
        Timber.d("Restored shuffle and repeat mode");
        handleAndSendChangeInternal(SHUFFLE_MODE_CHANGED);
        handleAndSendChangeInternal(REPEAT_MODE_CHANGED);

        musicHandler.removeMessages(RESTORE_QUEUES);
        musicHandler.sendEmptyMessage(RESTORE_QUEUES);
    }

    private synchronized void restoreQueuesAndPositionIfNecessary() {
        if (!queuesRestored && playingQueue.isEmpty()) {
            ArrayList<Song> restoredQueue = FlairPlaybackState.getInstance(this).getSavedPlayingQueue();
            ArrayList<Song> restoredOriginalQueue = FlairPlaybackState.getInstance(this).getSavedOriginalPlayingQueue();
            int restoredPosition = PreferenceManager.getDefaultSharedPreferences(this).getInt(SAVED_POSITION, -1);
            int restoredSongProgress = PreferenceManager.getDefaultSharedPreferences(this).getInt(SAVED_SONG_PROGRESS, -1);

            if (restoredQueue.size() > 0 && restoredQueue.size() == restoredOriginalQueue.size() && restoredPosition != -1) {
                this.originalPlayingQueue = restoredOriginalQueue;
                this.playingQueue = restoredQueue;

                currentPos = restoredPosition;
                openCurrent();
                prepareNext();

                if (restoredSongProgress > 0) seek(restoredSongProgress);

                notHandledMetaChangedForCurrentTrack = true;
                Timber.d("Queue Restored. Broadcasting change");
                sendChangeInternal(META_CHANGED);
                sendChangeInternal(QUEUE_CHANGED);
            }
        }
        queuesRestored = true;
    }

    public void notifyChange(final String what) {
        handleAndSendChangeInternal(what);
        sendPublicIntent(what);
    }

    /**
     * Handle changes needed to be done inside app and broadcast the same
     */
    public void handleAndSendChangeInternal(final String what) {
        handleChangeInternal(what);
        sendChangeInternal(what);
    }

    public void handleChangeInternal(final String what) {
        switch (what) {
            case META_CHANGED:
                updateNotification();
                savePosition();
                saveSongProgress();
                RecentStore.getInstance(this).addSongId(getCurrentSong().getId());
                if (songPlayCountHelper.shouldBumpPlayCount()) {
                    SongPlayCount.getInstance(this).bumpSongCount(songPlayCountHelper.getSong().getId());
                }
                songPlayCountHelper.notifySongChanged(getCurrentSong());
                break;
            case PLAY_STATE_CHANGED:
                updateNotification();
                final boolean isPlaying = isPlaying();
                if (!isPlaying && getSongProgress() > 0) {
                    saveSongProgress();
                }
                songPlayCountHelper.notifyPlayStateChanged(isPlaying);
                break;
            case QUEUE_CHANGED:
                saveState();
                if (playingQueue.size() > 0) {
                    prepareNext();
                } else {
                    cancelNotification();
                }
                break;
        }
    }

    public void sendChangeInternal(final String what) {
        sendBroadcast(new Intent(what));
        bigWidget.notifyChange(this, what);
    }

    public void sendPublicIntent(final String what) {
        final Intent intent = new Intent(what.replace(FLAIR_PACKAGE_NAME, MUSIC_PACKAGE_NAME));
        final Song song = getCurrentSong();

        intent.putExtra("id", song.getId());
        intent.putExtra("track", song.getTitle());
        intent.putExtra("album", song.getAlbumName());
        intent.putExtra("artist", song.getArtistName());
        intent.putExtra("duration", song.getDuration());
        intent.putExtra("position", getSongProgress());
        intent.putExtra("playing", isPlaying());

        sendStickyBroadcast(intent);
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
        prepareNext();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putInt(SAVED_REPEAT_MODE, repeatMode)
                .apply();
        prepareNext();
        handleAndSendChangeInternal(REPEAT_MODE_CHANGED);
    }

    public int getShuffleMode() {
        return shuffleMode;
    }

    public void setShuffleMode(int shuffleMode) {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putInt(SAVED_SHUFFLE_MODE, shuffleMode)
                .apply();
        switch (shuffleMode) {
            case SHUFFLE_MODE_SHUFFLE:
                this.shuffleMode = shuffleMode;
                MusicUtils.makeShuffleList(this.getPlayingQueue(), getPosition());
                currentPos = 0;
                break;
            case SHUFFLE_MODE_NONE:
                this.shuffleMode = shuffleMode;
                int currentSongId = getCurrentSong().getId();
                playingQueue = new ArrayList<>(originalPlayingQueue);
                int newPos = 0;
                for (Song song : getPlayingQueue()) {
                    if (song.getId() == currentSongId) {
                        newPos = getPlayingQueue().indexOf(song);
                    }
                }
                currentPos = newPos;
                break;
        }
        handleAndSendChangeInternal(SHUFFLE_MODE_CHANGED);
        notifyChange(QUEUE_CHANGED);
    }

    private boolean openCurrent() {
        synchronized (this) {
            try {
                return player.setDataSource(getTrackUri(getCurrentSong()));
            } catch (Exception e) {
                return false;
            }
        }
    }

    private boolean openTrackAndPrepareNextAt(int pos) {
        synchronized (this) {
            this.currentPos = pos;
            boolean prepared = openCurrent();
            if (prepared)
                prepareNextImpl();
            notifyChange(META_CHANGED);
            return prepared;
        }
    }

    private AudioManager getAudioManager() {
        return audioManager != null ? audioManager : (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private boolean requestFocus() {
        return getAudioManager().requestAudioFocus(audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void play() {
        synchronized (this) {
            if (requestFocus() && !player.isPlaying()) {
                if (!player.isInitialized()) {
                    playSongAt(getPosition());
                } else {
                    player.start();
                    if (!becomingNoisyReceiverRegistered) {
                        registerReceiver(becomingNoisyReceiver, becomingNoisyReceiverIntentFilter);
                        becomingNoisyReceiverRegistered = true;
                    }
                    if (notHandledMetaChangedForCurrentTrack) {
                        handleChangeInternal(META_CHANGED);
                        notHandledMetaChangedForCurrentTrack = false;
                    }
                    notifyChange(PLAY_STATE_CHANGED);
                }
            } else
                Toast.makeText(getApplicationContext(), "Failed to acquire audio focus", Toast.LENGTH_SHORT).show();
        }
    }

    public void pause() {
        if (player.isPlaying()) {
            player.pause();
            notifyChange(PLAY_STATE_CHANGED);
        }
    }

    public void togglePlayPause() {
        if (isPlaying()) {
            pause();
            pausedByTransientLossOfFocus = false;
        } else {
            play();
        }
    }

    public long seek(long position) {
        synchronized (this) {
            if (player.isInitialized()) {
                if (position < 0) {
                    position = 0;
                } else if (position > player.duration()) {
                    position = player.duration();
                }
                long result = player.seek(position);
                notifyChange(POSITION_CHANGED);
                return result;
            }
            return -1;
        }
    }

    private void stop() {
        pause();
        cancelNotification();
        pausedByTransientLossOfFocus = false;
        seek(0);
        releaseServiceUiAndStop();
    }

    private void releaseServiceUiAndStop() {
        if (isPlaying() || pausedByTransientLossOfFocus || musicHandler.hasMessages(TRACK_ENDED))
            return;
        Timber.d("Nothing is playing. Stop everything");
        mediaSession.setActive(false);
        getAudioManager().abandonAudioFocus(audioFocusChangeListener);
        stopSelf();
    }

    public void playSongAt(int position) {
        musicHandler.removeMessages(PLAY_SONG);
        musicHandler.obtainMessage(PLAY_SONG, position, 0).sendToTarget();
    }

    private void playSongAtImpl(int position) {
        if (openTrackAndPrepareNextAt(position)) {
            play();
        } else {
            Toast.makeText(this, "Couldn't play file", Toast.LENGTH_SHORT).show();
        }
    }

    private void prepareNext() {
        musicHandler.removeMessages(PREPARE_NEXT);
        musicHandler.obtainMessage(PREPARE_NEXT).sendToTarget();
    }

    public void playNextSong(boolean force) {
        playSongAt(getNextPosition(force));
    }

    public void playPreviousSong(boolean force) {
        if (getSongProgress() < REWIND_INSTEAD_PREVIOUS_THRESHOLD)
            playSongAt(getPreviousPosition(force));
        else
            seek(0);
    }

    private boolean prepareNextImpl() {
        synchronized (this) {
            try {
                int nextPos = getNextPosition(false);
                player.setNextDataSource(getTrackUri(getSongAt(nextPos)));
                this.nextPos = nextPos;
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    private String getTrackUri(Song song) {
        return MusicUtils.getTrackUri(song.getId()).toString();
    }

    private int getPreviousPosition(boolean force) {
        int newPos = getPosition() - 1;
        switch (repeatMode) {
            case REPEAT_MODE_ALL:
                if (newPos < 0) {
                    newPos = getPlayingQueue().size() - 1;
                }
                break;
            case REPEAT_MODE_THIS:
                if (force && newPos < 0) {
                    newPos = getPlayingQueue().size() - 1;
                } else {
                    newPos = getPosition();
                }
                break;
            case REPEAT_MODE_NONE:
                if (newPos < 0) {
                    newPos = 0;
                }
                break;
        }
        return newPos;
    }

    private int getNextPosition(boolean force) {
        int nextPos = getPosition() + 1;
        switch (getRepeatMode()) {
            case REPEAT_MODE_NONE:
                if (isLastTrack())
                    nextPos = getPosition();
                break;
            case REPEAT_MODE_ALL:
                if (isLastTrack())
                    nextPos = 0;
                break;
            case REPEAT_MODE_THIS:
                if (force) {
                    if (isLastTrack())
                        nextPos = 0;
                } else
                    nextPos -= 1;
        }
        return nextPos;
    }

    private boolean isLastTrack() {
        return currentPos == getPlayingQueue().size() - 1;
    }

    public void cycleRepeatMode() {
        switch (getRepeatMode()) {
            case REPEAT_MODE_NONE:
                setRepeatMode(REPEAT_MODE_ALL);
                break;
            case REPEAT_MODE_ALL:
                setRepeatMode(REPEAT_MODE_THIS);
                break;
            default:
                setRepeatMode(REPEAT_MODE_NONE);
                break;
        }
    }

    public void toggleShuffleMode() {
        switch (getShuffleMode()) {
            case SHUFFLE_MODE_NONE:
                setShuffleMode(SHUFFLE_MODE_SHUFFLE);
                break;
            case SHUFFLE_MODE_SHUFFLE:
                setShuffleMode(SHUFFLE_MODE_NONE);
                break;
        }
    }

    public int getPosition() {
        return currentPos;
    }

    public void setPosition(final int position) {
        musicHandler.removeMessages(SET_POSITION);
        musicHandler.obtainMessage(SET_POSITION, position, 0).sendToTarget();
    }

    public ArrayList<Song> getPlayingQueue() {
        return playingQueue;
    }

    private Song getSongAt(final int position) {
        if (position >= 0 && position < getPlayingQueue().size())
            return getPlayingQueue().get(position);
        else
            return new Song();
    }

    public long getSongProgress() {
        return player.position();
    }

    public long getSongDuration() {
        return player.duration();
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public Song getCurrentSong() {
        return getSongAt(getPosition());
    }

    public void openQueue(@Nullable final ArrayList<Song> playingQueue, final int startPosition, final boolean startPlaying) {
        if (playingQueue != null && !playingQueue.isEmpty() && startPosition >= 0 && startPosition < playingQueue.size()) {
            // it is important to copy the playing queue here first as we might add/remove songs later
            originalPlayingQueue = new ArrayList<>(playingQueue);
            this.playingQueue = new ArrayList<>(originalPlayingQueue);

            int position = startPosition;
            if (shuffleMode == SHUFFLE_MODE_SHUFFLE) {
                MusicUtils.makeShuffleList(this.playingQueue, startPosition);
                position = 0;
            }
            if (startPlaying) {
                playSongAt(position);
            } else {
                setPosition(position);
            }
            notifyChange(QUEUE_CHANGED);
        }
    }

    public void clearQueue() {
        playingQueue.clear();
        originalPlayingQueue.clear();

        setPosition(-1);
        notifyChange(QUEUE_CHANGED);
    }

    public int getAudioSessionId() {
        return player.getAudioSessionId();
    }

    private Notification buildNotification() {
        final Song currentSong = getCurrentSong();
        final String albumName = currentSong.getAlbumName();
        final String artistName = currentSong.getArtistName();
        final boolean isPlaying = isPlaying();
        String text = TextUtils.isEmpty(albumName)
                ? artistName : artistName + " \u2022 " + albumName;

        int playButtonResId = isPlaying
                ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_24dp;

        Intent nowPlayingIntent = NavUtils.getNowPlayingIntent(this);
        PendingIntent clickIntent = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap artwork = null;
        try {
            artwork = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                    MusicUtils.getAlbumArtUri(currentSong.getAlbumId()));
        } catch (IOException e) {
            //do nothing
        }

        if (artwork == null)
            artwork = FlairUtils.getBitmapFromDrawable(getResources()
                    .getDrawable(R.drawable.album_art_placeholder));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(artwork)
                .setContentIntent(clickIntent)
                .setContentTitle(currentSong.getTitle())
                .setContentText(text)
                .addAction(R.drawable.ic_skip_previous_black_24dp,
                        "",
                        retrievePlaybackAction(ACTION_PREVIOUS))
                .addAction(playButtonResId, "",
                        retrievePlaybackAction(ACTION_TOGGLE_PAUSE))
                .addAction(R.drawable.ic_skip_next_black_24dp,
                        "",
                        retrievePlaybackAction(ACTION_NEXT));

        if (FlairUtils.isLollipopOrAbove()) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            MediaStyle style = new MediaStyle()
                    .setMediaSession(mediaSession.getSessionToken())
                    .setShowActionsInCompactView(0, 1, 2, 3);
            builder.setStyle(style);
        }
        if (artwork != null && FlairUtils.isLollipopOrAbove())
            builder.setColor(Palette.from(artwork)
                    .generate()
                    .getVibrantColor(Color.parseColor("#403f4d")));

        if (FlairUtils.isOreo())
            builder.setColorized(true);

        return builder.build();
    }

    private void initNotification() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (FlairUtils.isOreo())
            createNotificationChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
        if (notificationChannel == null) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    getString(R.string.playing_notification),
                    NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void updateNotification() {
        int newNotifyMode;
        if (isPlaying()) {
            newNotifyMode = NOTIFY_MODE_FOREGROUND;
        } else {
            newNotifyMode = NOTIFY_MODE_BACKGROUND;
        }

        if (notifyMode != newNotifyMode && newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            stopForeground(false);
        }

        if (newNotifyMode == NOTIFY_MODE_FOREGROUND) {
            startForeground(NOTIFICATION_ID, buildNotification());
        } else if (newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            notificationManager.notify(NOTIFICATION_ID, buildNotification());
        }

        notifyMode = newNotifyMode;
    }

    private void cancelNotification() {
        stopForeground(true);
        notificationManager.cancel(NOTIFICATION_ID);
        notifyMode = NOTIFY_MODE_BACKGROUND;
    }

    private PendingIntent retrievePlaybackAction(final String action) {
        final ComponentName serviceName = new ComponentName(this, FlairMusicService.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);

        return PendingIntent.getService(this, 0, intent, 0);
    }

    /**
     * Handle broadcast intents from widgets
     */
    public void handleIntent(Intent intent) {
        if (intent == null)
            return;
        final String command = intent.getStringExtra(EXTRA_WIDGET_NAME);
        if (BigWidget.WIDGET_NAME.equals(command)) {
            final int[] ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            bigWidget.performUpdate(FlairMusicService.this, ids);
        }
    }

    public static final class MultiPlayer
            implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

        private final WeakReference<FlairMusicService> service;
        private MediaPlayer currentMediaPlayer = new MediaPlayer();
        private MediaPlayer nextMediaPlayer;
        private Context context;
        private Handler handler;
        private boolean isInitialized;

        public MultiPlayer(final Context context, final FlairMusicService service) {
            this.context = context;
            this.service = new WeakReference<>(service);
            currentMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        }

        private boolean setDataSource(final String path) {
            isInitialized = false;
            isInitialized = setDataSourceImpl(currentMediaPlayer, path);
            if (isInitialized)
                setNextDataSource(null);
            return isInitialized;
        }

        private boolean setDataSourceImpl(final MediaPlayer player, final String path) {
            if (context == null)
                return false;
            try {
                player.reset();
                player.setOnPreparedListener(null);
                if (path.startsWith("content://")) {
                    player.setDataSource(context, Uri.parse(path));
                } else {
                    player.setDataSource(path);
                }
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);

                player.prepare();
            } catch (Exception e) {
                Timber.e("Failed to set data source. Error: %s", e.getMessage());
                return false;
            }
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
            return true;
        }

        private void setNextDataSource(String path) {
            if (context == null) {
                return;
            }
            try {
                currentMediaPlayer.setNextMediaPlayer(null);
            } catch (IllegalArgumentException e) {
                Timber.i("Next media player is current one, continuing");
            } catch (IllegalStateException e) {
                Timber.i("Media player not initialized! %s", e.getMessage());
                return;
            }
            if (nextMediaPlayer != null) {
                nextMediaPlayer.release();
                nextMediaPlayer = null;
            }
            if (path == null) {
                return;
            }
            nextMediaPlayer = new MediaPlayer();
            nextMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            nextMediaPlayer.setAudioSessionId(getAudioSessionId());
            if (setDataSourceImpl(nextMediaPlayer, path)) {
                try {
                    currentMediaPlayer.setNextMediaPlayer(nextMediaPlayer);
                } catch (Exception e) {
                    Timber.e("Failed to set next data source! Message: %s", e.getMessage());
                    if (nextMediaPlayer != null) {
                        nextMediaPlayer.release();
                        nextMediaPlayer = null;
                    }
                }
            } else {
                if (nextMediaPlayer != null) {
                    nextMediaPlayer.release();
                    nextMediaPlayer = null;
                }
            }
        }

        private void setHandler(final Handler handler) {
            this.handler = handler;
        }

        private boolean isInitialized() {
            return isInitialized;
        }

        private boolean isPlaying() {
            return isInitialized() && currentMediaPlayer.isPlaying();
        }

        private void start() {
            try {
                currentMediaPlayer.start();
            } catch (IllegalStateException e) {
                Timber.e("Couldn't start playback. Message: %s", e.getMessage());
            }
        }

        private void stop() {
            currentMediaPlayer.reset();
            isInitialized = false;
        }

        private void release() {
            stop();
            currentMediaPlayer.release();
        }

        private void pause() {
            try {
                currentMediaPlayer.pause();
            } catch (IllegalStateException e) {
                Timber.e("Couldn't pause playback. Message: %s", e.getMessage());
            }

        }

        private long duration() {
            if (!isInitialized) {
                return -1;
            }
            try {
                return currentMediaPlayer.getDuration();
            } catch (IllegalStateException e) {
                return -1;
            }
        }

        private long position() {
            if (!isInitialized) {
                return -1;
            }
            try {
                return currentMediaPlayer.getCurrentPosition();
            } catch (IllegalStateException e) {
                return -1;
            }
        }

        private long seek(final long whereto) {
            currentMediaPlayer.seekTo((int) whereto);
            return whereto;
        }

        public void setVolume(final float vol) {
            currentMediaPlayer.setVolume(vol, vol);
        }

        public int getAudioSessionId() {
            return currentMediaPlayer.getAudioSessionId();
        }

        public void setAudioSessionId(final int sessionId) {
            try {
                currentMediaPlayer.setAudioSessionId(sessionId);
            } catch (Exception e) {
                Timber.e("Couldn't set audio session id. Message: %s", e.getMessage());
            }
        }

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if (mediaPlayer == currentMediaPlayer && nextMediaPlayer != null) {
                isInitialized = false;
                release();
                currentMediaPlayer = nextMediaPlayer;
                isInitialized = true;
                nextMediaPlayer = null;
                if (handler != null)
                    handler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
            } else {
                if (handler != null)
                    handler.sendEmptyMessage(TRACK_ENDED);
            }
        }

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
            Timber.e("Error while playing music. what = %d, extra = %d", what, extra);
            isInitialized = false;
            currentMediaPlayer.release();
            currentMediaPlayer = new MediaPlayer();
            currentMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            if (context != null)
                Toast.makeText(context, "Couldn't play song", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private static final class MusicHandler extends Handler {
        WeakReference<FlairMusicService> service;

        public MusicHandler(final FlairMusicService flairMusicService, Looper looper) {
            super(looper);
            service = new WeakReference<>(flairMusicService);
        }

        @Override
        public void handleMessage(Message msg) {
            final FlairMusicService musicService = service.get();
            if (musicService == null)
                return;
            switch (msg.what) {
                case TRACK_WENT_TO_NEXT:
                    if (musicService.getRepeatMode() == REPEAT_MODE_NONE && musicService.isLastTrack()) {
                        musicService.pause();
                        musicService.seek(0);
                    } else {
                        musicService.currentPos = musicService.nextPos;
                        musicService.prepareNextImpl();
                        musicService.notifyChange(META_CHANGED);
                    }
                    break;
                case TRACK_ENDED:
                    if (musicService.getRepeatMode() == REPEAT_MODE_NONE && musicService.isLastTrack()) {
                        musicService.notifyChange(PLAY_STATE_CHANGED);
                        musicService.seek(0);
                    } else {
                        musicService.playNextSong(false);
                    }
                    break;
                case PLAY_SONG:
                    musicService.playSongAtImpl(msg.arg1);
                    break;
                case SET_POSITION:
                    musicService.openTrackAndPrepareNextAt(msg.arg1);
                    musicService.notifyChange(PLAY_STATE_CHANGED);
                    break;
                case PREPARE_NEXT:
                    musicService.prepareNextImpl();
                    break;
                case SAVE_QUEUES:
                    musicService.saveQueuesImpl();
                    break;
                case RESTORE_QUEUES:
                    musicService.restoreQueuesAndPositionIfNecessary();
                    break;
                case FOCUS_CHANGE:
                    switch (msg.arg1) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            if (!musicService.isPlaying() && musicService.pausedByTransientLossOfFocus) {
                                musicService.play();
                                musicService.pausedByTransientLossOfFocus = false;
                            }
                            break;

                        case AudioManager.AUDIOFOCUS_LOSS:
                            // Lost focus for an unbounded amount of time: stop playback and release media playback
                            musicService.pause();
                            break;

                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            // Lost focus for a short time, but we have to stop
                            // playback. We don't release the media playback because playback
                            // is likely to resume
                            boolean wasPlaying = musicService.isPlaying();
                            musicService.pause();
                            musicService.pausedByTransientLossOfFocus = wasPlaying;
                            break;

                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            // Lost focus for a short time, but it's ok to keep playing
                            // at an attenuated level
                            break;
                    }
                    break;
            }
        }
    }

    /**
     * Helper code borrowed from Phonograph with little modification
     */

    private static class SongPlayCountHelper {

        private Stopwatch stopwatch = new Stopwatch();
        private Song song = new Song();

        public Song getSong() {
            return song;
        }

        boolean shouldBumpPlayCount() {
            return song.getDuration() * 0.4d < stopwatch.getElapsedTime();
        }

        void notifySongChanged(Song song) {
            synchronized (this) {
                stopwatch.reset();
                this.song = song;
            }
        }

        void notifyPlayStateChanged(boolean isPlaying) {
            synchronized (this) {
                if (isPlaying) {
                    stopwatch.start();
                } else {
                    stopwatch.pause();
                }
            }
        }
    }

    public class FlairMusicBinder extends Binder {
        public FlairMusicService getService() {
            return FlairMusicService.this;
        }
    }
}