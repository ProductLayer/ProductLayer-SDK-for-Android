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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;

/**
 * Helps create Snackbars compatible with a translucent navigation bar.
 */
public class SnackbarUtil {

    /**
     * Creates a snackbar that can handle a translucent navigation bar instead of popping up under it.
     *
     * Expects a CoordinatorLayout as the provided view or as a parent at some point in the view hierarchy.
     *
     * @param activity
     *         the currently visible activity
     * @param view
     *         the container view
     * @param text
     *         the message to show
     * @param duration
     *         the duration to show it for (see {@link Snackbar#LENGTH_SHORT} and {@link
     *         Snackbar#LENGTH_LONG}
     * @return the snackbar prepared to be shown
     */
    public static Snackbar make(Activity activity, final View view, CharSequence text, int duration) {
        Snackbar snackbar = Snackbar.make(view, text, duration);
        if (activity == null) {
            return snackbar;
        }
        final int navigationBarHeight = SystemBarsUtil.getNavigationBarHeight(activity);
        if (navigationBarHeight == 0 || !SystemBarsUtil.hasTranslucentNavigationBar(activity)) {
            return snackbar;
        }
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar snackbar) {
                View snackbarView = snackbar.getView();
                CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams
                        (snackbarView.getLayoutParams());
                layoutParams.setMargins(0, 0, 0, navigationBarHeight);
                layoutParams.gravity = Gravity.BOTTOM;
                snackbarView.setLayoutParams(layoutParams);
                super.onShown(snackbar);
            }
        });
        return snackbar;
    }

    /**
     * Creates a snackbar that can handle a translucent navigation bar instead of popping up under it.
     *
     * Expects a CoordinatorLayout as the provided view or as a parent at some point in the view hierarchy.
     *
     * @param activity
     *         the currently visible activity
     * @param view
     *         the container view
     * @param resourceId
     *         the message to show
     * @param duration
     *         the duration to show it for (see {@link Snackbar#LENGTH_SHORT} and {@link
     *         Snackbar#LENGTH_LONG}
     * @return the snackbar prepared to be shown
     */
    public static Snackbar make(Activity activity, View view, int resourceId, int duration) {
        return make(activity, view, activity.getResources().getString(resourceId), duration);
    }
}
