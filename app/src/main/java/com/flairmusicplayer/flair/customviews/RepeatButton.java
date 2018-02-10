package com.flairmusicplayer.flair.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.services.FlairMusicService;

/**
 * Author: PulakDebasish
 */

public class RepeatButton extends AudioButton {

    public RepeatButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(final View v) {
        FlairMusicController.cycleRepeatMode();
        updateRepeatState();
    }

    public void updateRepeatState() {
        switch (FlairMusicController.getRepeatMode()) {
            case FlairMusicService.REPEAT_MODE_ALL:
                setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_black_24dp));
                setAlpha(ACTIVE_ALPHA);
                break;
            case FlairMusicService.REPEAT_MODE_THIS:
                setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one_black_24dp));
                setAlpha(ACTIVE_ALPHA);
                break;
            case FlairMusicService.REPEAT_MODE_NONE:
                setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_black_24dp));
                setAlpha(INACTIVE_ALPHA);
                break;
            default:
                break;
        }
    }
}
