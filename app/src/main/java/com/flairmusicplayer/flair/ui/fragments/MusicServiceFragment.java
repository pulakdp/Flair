package com.flairmusicplayer.flair.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.flairmusicplayer.flair.services.MusicStateChangeListener;
import com.flairmusicplayer.flair.ui.activities.MusicServiceActivity;

/**
 * Author: PulakDebasish
 */

public abstract class MusicServiceFragment extends Fragment implements MusicStateChangeListener {

    public MusicServiceActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            activity = (MusicServiceActivity) context;
        }catch (ClassCastException e) {
            throw new RuntimeException(context.getClass().getName() + " must be an instance of " + MusicServiceActivity.class.getName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.addMusicStateChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.removeMusicStateChangeListener(this);
    }

    @Override
    public void onMetaChanged() {

    }

    @Override
    public void onQueueChanged() {

    }

    @Override
    public void onPlayStateChanged() {

    }

    @Override
    public void onRepeatModeChanged() {

    }

    @Override
    public void onShuffleModeChanged() {

    }
}
