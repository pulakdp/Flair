package com.flairmusicplayer.flair.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.flairmusicplayer.flair.R;

/**
 * Author: PulakDebasish
 */

public class FlairUtils {

    private static TypedArray colors;
    private static int defaultColor;

    public static boolean isOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean isLollipopOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isMarshmallowOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static Bitmap getBitmapFromDrawable(Drawable resource) {
        if (resource == null)
            return null;
        return ((BitmapDrawable) resource).getBitmap();
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable == null)
            return null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static int getBlackWhiteColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness >= 0.5) {
            return Color.WHITE;
        } else return Color.BLACK;
    }

    private static String getInitials(@NonNull String name) {
        if (!TextUtils.isEmpty(name)) {
            StringBuilder initials = new StringBuilder("");
            initials.append(Character.toUpperCase(name.charAt(0)));
            if (name.length() > 1 && isEnglishLetter(name.charAt(1))) {
                initials.append(Character.toLowerCase(name.charAt(1)));
            }
            return initials.toString();
        }
        return "";
    }

    public static void hideSoftKeyboard(@Nullable Activity activity) {
        if (activity != null) {
            View currentFocus = activity.getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
            }
        }
    }

    private static boolean isEnglishLetter(final char c) {
        return ('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || ('0' <= c && c <= '9');
    }

    public static String getTrimmedName(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

        name = name.trim().toLowerCase();
        if (name.startsWith("the ")) {
            name = name.substring(4);
        }
        if (name.startsWith("an ")) {
            name = name.substring(3);
        }
        if (name.startsWith("a ")) {
            name = name.substring(2);
        }
        if (name.endsWith(", the") || name.endsWith(",the") ||
                name.endsWith(", an") || name.endsWith(",an") ||
                name.endsWith(", a") || name.endsWith(",a")) {
            name = name.substring(0, name.lastIndexOf(','));
        }
        name = name.replaceAll("[\\[\\]()\"'<>.,?!]", "").trim();

        return name;
    }

    public static TextDrawable getRoundTextDrawable(Context context, String name) {
        String trimmedName = FlairUtils.getTrimmedName(name);
        return TextDrawable
                .builder()
                .buildRound(FlairUtils.getInitials(trimmedName)
                        , FlairUtils.getColor(context, trimmedName));
    }

    public static TextDrawable getRectTextDrawable(Context context, String name) {
        String trimmedName = FlairUtils.getTrimmedName(name);
        return TextDrawable
                .builder()
                .buildRect(FlairUtils.getInitials(trimmedName)
                        , FlairUtils.getColor(context, trimmedName));
    }

    public static int getColor(Context context, String identifier) {
        colors = context.getResources().obtainTypedArray(R.array.letter_tile_colors);
        defaultColor = context.getResources().getColor(R.color.letter_tile_default_color);
        final int idx = getColorIndex(identifier);
        if (idx == -1) {
            return defaultColor;
        }

        return colors.getColor(idx, defaultColor);
    }

    private static int getColorIndex(final String identifier) {
        if (TextUtils.isEmpty(identifier)) {
            return -1;
        }

        return Math.abs(identifier.hashCode()) % colors.length();
    }
}
