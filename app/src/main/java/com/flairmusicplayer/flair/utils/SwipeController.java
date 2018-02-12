package com.flairmusicplayer.flair.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.flairmusicplayer.flair.services.FlairMusicController;

/**
 * Author: PulakDebasish, kabouzeid
 */

public class SwipeController implements View.OnTouchListener {

    GestureDetector gestureDetector;

    public SwipeController(Context context) {

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX < 0) {
                        FlairMusicController.playNextSong();
                        return true;
                    } else if (velocityX > 0) {
                        FlairMusicController.playPreviousSong();
                        return true;
                    }
                }
                return false;
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }
}
