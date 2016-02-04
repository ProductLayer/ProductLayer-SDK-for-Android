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

package com.productlayer.android.common.handler;

import android.view.View;

/**
 * Handles appearance and functionality changes to the app bar such as scrolling behavior and custom views
 * when the screen changes.
 */
public interface AppBarHandler {

    /**
     * Transforms the app bar to fit in with the timeline screen.
     *
     * @param view
     *         the fragment's root view
     */
    void setTimelineAppBar(View view);

    /**
     * Transforms the app bar to fit in with the product screen. May include a custom view (i.e. product title
     * and available actions) as well as a backdrop view (i.e. product image) to be scrolled off the screen.
     *
     * @param view
     *         the fragment's root view
     * @param title
     *         the name of the product to display as toolbar title
     * @param customView
     *         the custom view possibly containing the product title and actions
     * @param addCollapsedHeight
     *         the height to collapse to in addition to the standard toolbar size (in px)
     * @param expandedHeight
     *         the height to expand to in total (in px)
     * @param backdrop
     *         the backdrop view to scroll off
     */
    void setProductAppBar(View view, String title, View customView, int addCollapsedHeight, int
            expandedHeight, View backdrop);

    /**
     * Transforms the app bar to fit in with the edit product screen.
     *
     * @param view
     *         the fragment's root view
     * @param title
     *         the name of the product to display as toolbar title
     */
    void setEditProductAppBar(View view, String title);

    /**
     * Transforms the app bar to fit in with the product screen. May include a custom view (i.e. product title
     * and available actions) as well as a backdrop view (i.e. product image) to be scrolled off the screen.
     *
     * @param view
     *         the fragment's root view
     * @param title
     *         the user name to display as toolbar title in collapsed state
     * @param customView
     *         the custom view possibly containing the user's avatar and name
     * @param addCollapsedHeight
     *         the height to collapse to in addition to the standard toolbar size (in px)
     * @param expandedHeight
     *         the height to expand to in total (in px)
     * @param backdrop
     *         the backdrop view to scroll off
     */
    void setProfileAppBar(View view, String title, View customView, int addCollapsedHeight, int
            expandedHeight, View backdrop);

    /**
     * Transforms the app bar to fit in with the user settings screen.
     *
     * @param view
     *         the fragment's root view
     * @param title
     *         the user name to display as toolbar title
     */
    void setProfileEditAppBar(View view, String title);

    /**
     * Transforms the app bar to fit in with the opinion screen.
     *
     * @param view
     *         the fragment's root view
     * @param title
     *         the name of the product to display as toolbar title
     */
    void setOpinionAppBar(View view, String title);

    /**
     * @return true if the status bar is translucent, false else
     */
    boolean hasTranslucentStatusBar();

    /**
     * @return the current height value in px of the toolbar in collapsed state
     */
    int getCurrentCollapsedToolbarHeight();

    /**
     * @return the current height value in px of the toolbar in expanded state
     */
    int getCurrentExpandedToolbarHeight();

    /**
     * @return the current height value in px the content is padded from the top but not clipped by the parent
     * view when scrolling up
     */
    int getCurrentContentPadding();

}
