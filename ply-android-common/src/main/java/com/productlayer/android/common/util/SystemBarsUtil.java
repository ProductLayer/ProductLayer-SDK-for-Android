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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * Helper methods to get information about and to modify the System UI.
 */
public class SystemBarsUtil {

    /**
     * @param context
     *         the application context
     * @return the height of the status bar in px
     */
    public static int getStatusBarHeight(Context context) {
        // 25 dp is the default as per Google specs
        int defaultDp = 25;
        if (context == null) {
            return MetricsUtil.inPx(defaultDp);
        }
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        } else {
            return MetricsUtil.inPx(defaultDp);
        }
    }

    /**
     * @return whether this device has support for a natively enabled translucent status bar (Android 4.4+)
     */
    public static boolean supportsTranslucentStatusBarNative() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * @param context
     *         the application context
     * @return true if this device has native support for a translucent status bar or enabled through a
     * proprietary launcher flag, false if neither
     */
    public static boolean supportsTranslucentStatusBar(Context context) {
        return supportsTranslucentStatusBarNative() || getProprietaryTranslucentStatusBarFlag(context) != 0;
    }

    /**
     * Attempts to make the status bar translucent on devices with proprietary launchers supporting this
     * feature.
     *
     * @param activity
     *         the activity to set the flag for
     * @return true on success, false else
     */
    public static boolean setProprietaryTranslucentStatusBar(Activity activity) {
        if (activity == null) {
            return false;
        }
        Window window = activity.getWindow();
        if (window == null) {
            return false;
        }
        View decor = window.getDecorView();
        if (decor == null) {
            return false;
        }
        int flag = getProprietaryTranslucentStatusBarFlag(activity);
        if (flag == 0) {
            return false;
        }
        decor.setSystemUiVisibility(flag);
        return true;
    }

    /**
     * Retrieves the status bar transparency flags of proprietary launchers on Samsung and Sony devices. Adds
     * support for a translucent status bar where it is not supported directly by Android - as in Android
     * versions less than 4.4.
     *
     * @param context
     *         the application context
     * @return the flag for a translucent status bar to set via {@link View#setSystemUiVisibility}.
     */
    public static int getProprietaryTranslucentStatusBarFlag(Context context) {
        if (context == null) {
            return 0;
        }
        int flag = 0;
        String[] libs = context.getPackageManager().getSystemSharedLibraryNames();
        if (libs == null) {
            return flag;
        }
        Collection<String> libsSet = new HashSet<String>(Arrays.asList(libs));
        String flagName;
        if (libsSet.contains("touchwiz")) {
            flagName = "SYSTEM_UI_FLAG_TRANSPARENT_BACKGROUND";
        } else if (libsSet.contains("com.sonyericsson.navigationbar")) {
            flagName = "SYSTEM_UI_FLAG_TRANSPARENT";
        } else {
            return flag;
        }
        try {
            Field field = View.class.getField(flagName);
            if (field.getType() == Integer.TYPE) {
                flag = field.getInt(null);
            }
        } catch (Exception ignored) {
        }
        return flag;
    }

    /**
     * Gets the height of the navigation bar in pixels if one is present at the bottom of the screen.
     *
     * @param context
     *         the application context
     * @return the height of the navigation bar in px or 0 if none is present at the bottom of the screen
     */
    public static int getNavigationBarHeight(Context context) {
        if (context == null) {
            return 0;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Point fullSize = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(fullSize);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                fullSize.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                fullSize.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception ignored) {
            }
        }
        if (size.y < fullSize.y) {
            // navigation bar at the bottom
            return fullSize.y - size.y;
        } else {
            // no navigation bar at the bottom (none at all or one on the right side)
            return 0;
        }
    }

    /**
     * Checks whether the current theme declares the navigation bar to be translucent. This does not check
     * whether a navigation bar is even displayed on the screen.
     *
     * @param context
     *         the application context
     * @return true if the navigation bar is translucent in the current theme, false else
     * @see #getNavigationBarHeight
     */
    public static boolean hasTranslucentNavigationBar(Context context) {
        @SuppressLint("InlinedApi") @SuppressWarnings("UnnecessaryFullyQualifiedName") Boolean
                translucentNav = ThemeUtil.getBooleanValue(context, android.R.attr
                .windowTranslucentNavigation);
        return translucentNav != null && translucentNav;
    }

}
