package com.flairmusicplayer.flair;

import android.app.Application;

import java.util.logging.Level;
import java.util.logging.Logger;

import timber.log.Timber;

/**
 * Author: PulakDebasish
 */

public class Flair extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Logger.getLogger("com.bumptech.glide").setLevel(Level.OFF);
    }
}
