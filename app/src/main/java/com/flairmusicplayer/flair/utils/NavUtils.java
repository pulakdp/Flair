package com.flairmusicplayer.flair.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.flairmusicplayer.flair.services.FlairMusicController;

/**
 * Author: PulakDebasish
 */

public class NavUtils {

    public static void openEqualizer(@NonNull final Activity activity) {
        final int sessionId = FlairMusicController.getAudioSessionId();
        if (sessionId == AudioEffect.ERROR_BAD_VALUE) {
            Toast.makeText(activity, "No audio id", Toast.LENGTH_LONG).show();
        } else {
            try {
                final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId);
                effects.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                activity.startActivityForResult(effects, 0);
            } catch (@NonNull final ActivityNotFoundException notFound) {
                Toast.makeText(activity, "No equalizer found", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
