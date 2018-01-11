package com.flairmusicplayer.flair.ui.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.services.FlairMusicService;
import com.flairmusicplayer.flair.services.MusicStateChangeListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public abstract class MusicServiceActivity extends AppCompatActivity implements MusicStateChangeListener {

    private FlairMusicController.ServiceToken serviceToken;
    private ArrayList<MusicStateChangeListener> musicStateChangeListeners = new ArrayList<>();
    private boolean receiverRegistered = false;
    private MusicStateReceiver musicStateReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serviceToken = FlairMusicController.bindToService(this, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MusicServiceActivity.this.onServiceConnected(componentName, iBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                MusicServiceActivity.this.onServiceDisconnected(componentName);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FlairMusicController.unbindFromService(serviceToken);
        if (receiverRegistered) {
            unregisterReceiver(musicStateReceiver);
            receiverRegistered = false;
        }
    }

    public void addMusicStateChangeListener(MusicStateChangeListener listener) {
        if (listener != null)
            musicStateChangeListeners.add(listener);
    }

    public void removeMusicStateChangeListener(MusicStateChangeListener listener) {
        if (listener != null)
            musicStateChangeListeners.remove(listener);
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (!receiverRegistered) {
            musicStateReceiver = new MusicStateReceiver(this);

            final IntentFilter filter = new IntentFilter();
            filter.addAction(FlairMusicService.PLAY_STATE_CHANGED);
            filter.addAction(FlairMusicService.SHUFFLE_MODE_CHANGED);
            filter.addAction(FlairMusicService.REPEAT_MODE_CHANGED);
            filter.addAction(FlairMusicService.META_CHANGED);
            filter.addAction(FlairMusicService.QUEUE_CHANGED);

            registerReceiver(musicStateReceiver, filter);

            receiverRegistered = true;
        }
        onMetaChanged();
    }

    public void onServiceDisconnected(ComponentName componentName) {
        if (receiverRegistered) {
            unregisterReceiver(musicStateReceiver);
            receiverRegistered = false;
        }
    }

    @Override
    public void onMetaChanged() {
        for (MusicStateChangeListener listener: musicStateChangeListeners) {
            if (listener != null)
                listener.onMetaChanged();
        }
    }

    @Override
    public void onQueueChanged() {
        for (MusicStateChangeListener listener: musicStateChangeListeners) {
            if (listener != null)
                listener.onQueueChanged();
        }
    }

    @Override
    public void onPlayStateChanged() {
        for (MusicStateChangeListener listener: musicStateChangeListeners) {
            if (listener != null)
                listener.onPlayStateChanged();
        }
    }

    @Override
    public void onRepeatModeChanged() {
        for (MusicStateChangeListener listener: musicStateChangeListeners) {
            if (listener != null)
                listener.onRepeatModeChanged();
        }
    }

    @Override
    public void onShuffleModeChanged() {
        for (MusicStateChangeListener listener: musicStateChangeListeners) {
            if (listener != null)
                listener.onShuffleModeChanged();
        }
    }

    public static final class MusicStateReceiver extends BroadcastReceiver {

        private WeakReference<MusicServiceActivity> activity;

        public MusicStateReceiver(final MusicServiceActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MusicServiceActivity serviceActivity = activity.get();
            if (action != null) {
                switch (action) {
                    case FlairMusicService.META_CHANGED:
                        serviceActivity.onMetaChanged();
                        break;
                    case FlairMusicService.QUEUE_CHANGED:
                        serviceActivity.onQueueChanged();
                        break;
                    case FlairMusicService.PLAY_STATE_CHANGED:
                        serviceActivity.onPlayStateChanged();
                        break;
                    case FlairMusicService.REPEAT_MODE_CHANGED:
                        serviceActivity.onRepeatModeChanged();
                        break;
                    case FlairMusicService.SHUFFLE_MODE_CHANGED:
                        serviceActivity.onShuffleModeChanged();
                        break;
                }
            }
        }
    }
}
