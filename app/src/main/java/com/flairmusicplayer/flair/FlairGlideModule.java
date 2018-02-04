package com.flairmusicplayer.flair;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * Author: PulakDebasish
 */

@GlideModule
public class FlairGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);
        builder.setLogLevel(Log.ERROR);
    }
}
