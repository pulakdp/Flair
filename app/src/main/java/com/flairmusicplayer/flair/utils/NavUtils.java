package com.flairmusicplayer.flair.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.ImageView;
import android.widget.Toast;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Album;
import com.flairmusicplayer.flair.models.Artist;
import com.flairmusicplayer.flair.models.Playlist;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.services.FlairMusicService;
import com.flairmusicplayer.flair.ui.activities.AlbumDetailActivity;
import com.flairmusicplayer.flair.ui.activities.ArtistDetailActivity;
import com.flairmusicplayer.flair.ui.activities.MainActivity;
import com.flairmusicplayer.flair.ui.activities.PlaylistDetailActivity;

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

    public static void goToPlaylist(@NonNull final Activity activity, final Playlist playlist) {
        final Intent intent = new Intent(activity, PlaylistDetailActivity.class);
        intent.putExtra(PlaylistDetailActivity.EXTRA_PLAYLIST, playlist);
        activity.startActivity(intent);
    }

    public static void goToAlbum(@NonNull final Activity activity, final Album album, @Nullable ImageView sharedImage) {
        final Intent intent = new Intent(activity, AlbumDetailActivity.class);
        intent.putExtra(AlbumDetailActivity.EXTRA_ALBUM, album);

        if (sharedImage == null) {
            activity.startActivity(intent);
            return;
        }
        activity.startActivity(intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        sharedImage,
                        activity.getResources().getString(R.string.transition_album_art))
                        .toBundle());
    }

    public static void goToArtist(@NonNull final Activity activity, final Artist artist, @Nullable ImageView sharedImage) {
        final Intent intent = new Intent(activity, ArtistDetailActivity.class);
        intent.putExtra(ArtistDetailActivity.EXTRA_ARTIST, artist);

        if (sharedImage == null) {
            activity.startActivity(intent);
            return;
        }
        activity.startActivity(intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        sharedImage,
                        activity.getResources().getString(R.string.transition_artist_image))
                        .toBundle());
    }

    public static Intent getNowPlayingIntent(FlairMusicService flairMusicService) {
        Intent action = new Intent(flairMusicService, MainActivity.class);
        action.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return action;
    }
}
