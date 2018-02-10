package com.flairmusicplayer.flair.customviews;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author: PulakDebasish
 */

public abstract class AudioButton extends AppCompatImageButton implements View.OnClickListener, View.OnLongClickListener {

    public static float ACTIVE_ALPHA = 1.0f;
    public static float INACTIVE_ALPHA = 0.4f;

    @SuppressWarnings("deprecation")
    public AudioButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(final View view) {
        return false;
    }
}
