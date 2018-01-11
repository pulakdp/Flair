package com.flairmusicplayer.flair.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.services.FlairMusicService;

/**
 * Author: PulakDebasish
 */

public class ShuffleButton extends AudioButton {

    public ShuffleButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(final View v) {
        FlairMusicController.toggleShuffleMode();
        updateShuffleState();
    }

    /** Sets the correct drawable for the shuffle state. */
    public void updateShuffleState() {
        switch (FlairMusicController.getShuffleMode()) {
            case FlairMusicService.SHUFFLE_MODE_SHUFFLE:
//                setContentDescription(getResources().getString(R.string.accessibility_shuffle_all));
                setAlpha(ACTIVE_ALPHA);
                break;
            case FlairMusicService.SHUFFLE_MODE_NONE:
                setAlpha(INACTIVE_ALPHA);
                break;
            default:
                break;
        }
    }
}