package com.flairmusicplayer.flair.services;

/**
 * Author: PulakDebasish, Andrew Neal
 */

public interface MusicStateChangeListener {

    void onMetaChanged();

    void onQueueChanged();

    void onPlayStateChanged();

    void onRepeatModeChanged();

    void onShuffleModeChanged();

}
