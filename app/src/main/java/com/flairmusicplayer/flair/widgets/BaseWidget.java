package com.flairmusicplayer.flair.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.flairmusicplayer.flair.services.FlairMusicService;

/**
 * Author: PulakDebasish
 */

public abstract class BaseWidget extends AppWidgetProvider {

    public static final String WIDGET_NAME = "base_widget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        defaultAppWidget(context, appWidgetIds);
        final Intent updateIntent = new Intent(FlairMusicService.WIDGET_UPDATE);
        updateIntent.putExtra(FlairMusicService.EXTRA_WIDGET_NAME, WIDGET_NAME);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        updateIntent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        context.sendBroadcast(updateIntent);
    }

    public void notifyChange(final FlairMusicService service, final String what) {
        if (hasInstances(service)) {
            if (FlairMusicService.META_CHANGED.equals(what) || FlairMusicService.PLAY_STATE_CHANGED.equals(what)) {
                performUpdate(service, null);
            }
        }
    }

    protected boolean hasInstances(final Context context) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final int[] mAppWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                getClass()));
        return mAppWidgetIds.length > 0;
    }

    protected void pushUpdate(final Context context, final int[] appWidgetIds, final RemoteViews views) {
        final AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        if (appWidgetIds != null) {
            widgetManager.updateAppWidget(appWidgetIds, views);
        } else {
            widgetManager.updateAppWidget(new ComponentName(context, getClass()), views);
        }
    }


    abstract protected void defaultAppWidget(final Context context, final int[] appWidgetIds);

    abstract public void performUpdate(final FlairMusicService service, final int[] appWidgetIds);
}
