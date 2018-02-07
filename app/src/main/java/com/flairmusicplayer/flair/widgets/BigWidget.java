package com.flairmusicplayer.flair.widgets;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.services.FlairMusicService;
import com.flairmusicplayer.flair.ui.activities.MainActivity;
import com.flairmusicplayer.flair.utils.FlairUtils;

import java.io.IOException;

/**
 * Author: PulakDebasish
 */

public class BigWidget extends BaseWidget {

    public static final String WIDGET_NAME = "big_widget";

    private static BigWidget instance;

    public static synchronized BigWidget getInstance() {
        if (instance == null) {
            instance = new BigWidget();
        }
        return instance;
    }

    @Override
    protected void defaultAppWidget(Context context, int[] appWidgetIds) {
        final RemoteViews bigWidgetView = new RemoteViews(context.getPackageName(), R.layout.widget_big);

        bigWidgetView.setViewVisibility(R.id.song_info, View.INVISIBLE);
        bigWidgetView.setImageViewResource(R.id.image, R.drawable.album_art_placeholder);
        bigWidgetView.setImageViewBitmap(R.id.next_widget,
                FlairUtils.getBitmapFromVectorDrawable(context, R.drawable.ic_skip_next_black_24dp));
        bigWidgetView.setImageViewBitmap(R.id.prev_widget,
                FlairUtils.getBitmapFromVectorDrawable(context, R.drawable.ic_skip_previous_black_24dp));
        bigWidgetView.setImageViewBitmap(R.id.toggle_play_pause_widget,
                FlairUtils.getBitmapFromVectorDrawable(context, R.drawable.ic_play_arrow_black_24dp));

        setPendingIntentsForButtons(context, bigWidgetView);
        pushUpdate(context, appWidgetIds, bigWidgetView);
    }

    @Override
    public void performUpdate(FlairMusicService service, int[] appWidgetIds) {
        final RemoteViews bigWidgetView = new RemoteViews(service.getPackageName(), R.layout.widget_big);

        final boolean isPlaying = service.isPlaying();
        final Song song = service.getCurrentSong();

        if (TextUtils.isEmpty(song.getTitle()) && TextUtils.isEmpty(song.getArtistName())) {
            bigWidgetView.setViewVisibility(R.id.song_info, View.INVISIBLE);
        } else {
            bigWidgetView.setViewVisibility(R.id.song_info, View.VISIBLE);
            bigWidgetView.setTextViewText(R.id.song_title_widget, song.getTitle());
            bigWidgetView.setTextViewText(R.id.song_artist_widget, song.getArtistName());
        }

        int playPauseRes = isPlaying ? R.drawable.ic_pause_black_24dp : R.drawable.ic_play_arrow_black_24dp;
        bigWidgetView.setImageViewBitmap(R.id.toggle_play_pause_widget,
                FlairUtils.getBitmapFromVectorDrawable(service.getApplicationContext(), playPauseRes));

        bigWidgetView.setImageViewBitmap(R.id.next_widget,
                FlairUtils.getBitmapFromVectorDrawable(service.getApplicationContext(),
                        R.drawable.ic_skip_next_black_24dp));
        bigWidgetView.setImageViewBitmap(R.id.prev_widget,
                FlairUtils.getBitmapFromVectorDrawable(service.getApplicationContext(),
                        R.drawable.ic_skip_previous_black_24dp));

        setPendingIntentsForButtons(service, bigWidgetView);

        Bitmap albumArtBitmap = null;

        try {
            albumArtBitmap = MediaStore.Images.Media.getBitmap(service.getContentResolver(),
                    Song.getAlbumArtUri(song.getAlbumId()));
        } catch (IOException e) {
            //do nothing
        }
        if (albumArtBitmap == null)
            albumArtBitmap = FlairUtils.getBitmapFromDrawable(service.getResources()
                    .getDrawable(R.drawable.album_art_placeholder));

        bigWidgetView.setTextColor(R.id.song_artist_widget, Palette.from(albumArtBitmap)
                .generate()
                .getDarkVibrantColor(Color.parseColor("#000000")));

        bigWidgetView.setImageViewBitmap(R.id.album_art_widget, albumArtBitmap);

        pushUpdate(service.getApplicationContext(), appWidgetIds, bigWidgetView);
    }

    private void setPendingIntentsForButtons(final Context context, final RemoteViews remoteViews) {
        Intent action;
        PendingIntent pendingIntent;

        final ComponentName serviceName = new ComponentName(context, FlairMusicService.class);

        action = new Intent(context, MainActivity.class);
        action.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(context, 0, action, 0);
        remoteViews.setOnClickPendingIntent(R.id.clickable_area, pendingIntent);

        pendingIntent = buildPendingIntent(context, FlairMusicService.ACTION_PREVIOUS, serviceName);
        remoteViews.setOnClickPendingIntent(R.id.prev_widget, pendingIntent);

        pendingIntent = buildPendingIntent(context, FlairMusicService.ACTION_TOGGLE_PAUSE, serviceName);
        remoteViews.setOnClickPendingIntent(R.id.toggle_play_pause_widget, pendingIntent);

        pendingIntent = buildPendingIntent(context, FlairMusicService.ACTION_NEXT, serviceName);
        remoteViews.setOnClickPendingIntent(R.id.next_widget, pendingIntent);
    }

    private PendingIntent buildPendingIntent(final Context context, final String action, final ComponentName serviceName) {
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);
        if (FlairUtils.isOreo()) {
            return PendingIntent.getForegroundService(context, 0, intent, 0);
        } else {
            return PendingIntent.getService(context, 0, intent, 0);
        }
    }
}
