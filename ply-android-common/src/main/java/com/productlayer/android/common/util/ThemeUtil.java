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
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Utility class to conveniently access resources of the active theme.
 */
public class ThemeUtil {

    /**
     * Retrieves the integer value of the specified attribute of the current theme.
     *
     * @param context
     *         the application context
     * @param attributeId
     *         the ID of the attribute to find
     * @return the integer value of the specified attribute if found, null else
     */
    public static Integer getIntegerValue(Context context, int attributeId) {
        TypedValue value = new TypedValue();
        if (context != null && context.getTheme().resolveAttribute(attributeId, value, true)) {
            return value.data;
        } else {
            return null;
        }
    }

    /**
     * Retrieves the boolean value of the specified attribute of the current theme.
     *
     * @param context
     *         the application context
     * @param attributeId
     *         the ID of the attribute to find
     * @return the boolean value of the specified attribute if found, null else
     */
    public static Boolean getBooleanValue(Context context, int attributeId) {
        TypedValue value = new TypedValue();
        if (context != null && context.getTheme().resolveAttribute(attributeId, value, true)) {
            return value.coerceToString().equals("true");
        } else {
            return null;
        }
    }

    /**
     * Retrieves the px value of the specified attribute of the current theme.
     *
     * @param activity
     *         the current activity
     * @param attributeId
     *         the ID of the attribute to find
     * @return the px value of the specified attribute if found, null else
     */
    public static Integer getPxValue(Activity activity, int attributeId) {
        TypedValue value = new TypedValue();
        if (activity != null && activity.getTheme().resolveAttribute(attributeId, value, true)) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return Math.round(value.getDimension(displayMetrics));
        } else {
            return null;
        }
    }

}
