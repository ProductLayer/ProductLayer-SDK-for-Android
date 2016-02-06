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

package com.productlayer.android.common.global;

import android.os.Handler;
import android.view.View;

import com.productlayer.android.common.util.ThreadUtil;

import java.lang.ref.WeakReference;

/**
 * Holds a weak reference to a widget to serve as the central loading indicator for components of the Common
 * module.
 */
public class LoadingIndicator {

    private static WeakReference<View> loadingIndicatorRef;

    private static Handler mainHandler;

    /**
     * Sets the loading indicator view to show and hide on command. This widget must be attached to the
     * activity's layout and must be at least on the same hierarchy level as overlapping views (it may be
     * hidden under sibling views).
     *
     * @param loadingIndicator
     *         the view to show and hide on command
     * @param mainHandler
     *         a handler associated with the main thread
     */
    public static void set(View loadingIndicator, Handler mainHandler) {
        loadingIndicatorRef = new WeakReference<View>(loadingIndicator);
        LoadingIndicator.mainHandler = mainHandler;
    }

    /**
     * Brings the loading indicator to the front and makes it {@link View#VISIBLE}.
     */
    public static void show() {
        final View loadingIndicator = getLoadingIndicator();
        if (loadingIndicator == null) {
            return;
        }
        if (ThreadUtil.isMainThread()) {
            loadingIndicator.bringToFront();
            loadingIndicator.setVisibility(View.VISIBLE);
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadingIndicator.bringToFront();
                    loadingIndicator.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * Sets the loading indicator's visibility to {@link View#GONE}.
     */
    public static void hide() {
        final View loadingIndicator = getLoadingIndicator();
        if (loadingIndicator == null) {
            return;
        }
        if (ThreadUtil.isMainThread()) {
            loadingIndicator.setVisibility(View.GONE);
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadingIndicator.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * @return the loading indicator view or null if none set
     */
    private static View getLoadingIndicator() {
        return loadingIndicatorRef == null ? null : loadingIndicatorRef.get();
    }
}
