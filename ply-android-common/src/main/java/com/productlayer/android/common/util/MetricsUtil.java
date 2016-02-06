/*
 * Copyright (c) 2015, ProductLayer GmbH All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.productlayer.android.common.util;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Utility class to conveniently get information about the display metrics.
 */
public class MetricsUtil {

    private static int orientation;

    // parsed directly from DisplayMetrics
    private static float density;
    private static int densityDpi;
    private static int widthPx;
    private static int heightPx;

    // calculated from above
    private static int widthDp;
    private static int heightDp;

    /**
     * Stores all data from DisplayMetrics and translates dimensions into density-independent pixels. Must be
     * called once before getting data.
     *
     * @param activity
     *         the current activity
     */
    public static void update(Activity activity) {
        orientation = activity.getResources().getConfiguration().orientation;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.density;
        densityDpi = displayMetrics.densityDpi;
        widthPx = displayMetrics.widthPixels;
        heightPx = displayMetrics.heightPixels;
        updateDp();
        Log.d(MetricsUtil.class.getSimpleName(), "Orientation: " + nameForOrientation(orientation) +
                "\nWidth (px): " + widthPx + ", Height (px): " + heightPx + "\nDPI: " + densityDpi + ", " +
                "Density Factor: " + density + "\nWidth (dp): " + widthDp + ", Height (dp): " + heightDp);
    }

    /**
     * Translates pixels (px) into density-independent pixels (dp) by dividing px by {@link
     * DisplayMetrics#density}.
     *
     * @param px
     *         a value in pixels (px)
     * @return the corresponding value in density-independent pixels (dp)
     */
    public static int inDp(int px) {
        return Math.round(px / density);
    }

    /**
     * Translates density-independent pixels (dp) into pixels (px) by multiplying dp with {@link
     * DisplayMetrics#density}.
     *
     * @param dp
     *         a value in density-independent pixels (dp)
     * @return the corresponsing value in pixels (px)
     */
    public static int inPx(int dp) {
        return Math.round(dp * density);
    }

    private static void updateDp() {
        widthDp = inDp(widthPx);
        heightDp = inDp(heightPx);
    }

    /**
     * @param orientation
     *         the screen orientation retrieved by {@link #getOrientation()}
     * @return the textual representation of the orientation value, either Portrait, Landscape, or Undefined
     */
    public static String nameForOrientation(int orientation) {
        String orientationName = "Undefined";
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            orientationName = "Portrait";
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientationName = "Landscape";
        }
        return orientationName;
    }

    /**
     * @return the screen orientation, one of {@link Configuration#ORIENTATION_PORTRAIT}, {@link
     * Configuration#ORIENTATION_LANDSCAPE}
     */
    public static int getOrientation() {
        return orientation;
    }

    /**
     * @return the screen density
     * @see DisplayMetrics#density
     */
    public static float getDensity() {
        return density;
    }

    /**
     * @return the screen density as dots per inch
     * @see DisplayMetrics#densityDpi
     */
    public static int getDensityDpi() {
        return densityDpi;
    }

    /**
     * @return the width of the screen in pixels
     * @see DisplayMetrics#widthPixels
     */
    public static int getWidthPx() {
        return widthPx;
    }

    /**
     * @return the height of the screen in pixels
     * @see DisplayMetrics#heightPixels
     */
    public static int getHeightPx() {
        return heightPx;
    }

    /**
     * @return the width of the screen in density-independent pixels
     */
    public static int getWidthDp() {
        return widthDp;
    }

    /**
     * @return the height of the screen in density-independent pixels
     */
    public static int getHeightDp() {
        return heightDp;
    }

}
