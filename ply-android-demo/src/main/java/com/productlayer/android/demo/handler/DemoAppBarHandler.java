/*
 * Copyright (c) 2016, ProductLayer GmbH All rights reserved.
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

package com.productlayer.android.demo.handler;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.productlayer.android.common.handler.AppBarHandler;
import com.productlayer.android.demo.R;

/**
 * Dummy app bar handler.
 */
public class DemoAppBarHandler implements AppBarHandler {

    private final AppCompatActivity activity;

    /**
     * Constructs a new handler to manage changes to the app bar on loading fragments.
     *
     * @param activity
     *         the activity hosting the app's action bar
     */
    public DemoAppBarHandler(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setTimelineAppBar(View view) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void setProductAppBar(View view, String title, View customView, int addCollapsedHeight, int
            expandedHeight, View backdrop) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setEditProductAppBar(View view, String title) {
    }

    @Override
    public void setProfileAppBar(View view, String title, View customView, int addCollapsedHeight, int
            expandedHeight, View backdrop) {
    }

    @Override
    public void setProfileEditAppBar(View view, String title) {
    }

    @Override
    public void setOpinionAppBar(View view, String title) {
    }

    @Override
    public boolean hasTranslucentStatusBar() {
        return false;
    }

    @Override
    public int getCurrentCollapsedToolbarHeight() {
        return 0;
    }

    @Override
    public int getCurrentExpandedToolbarHeight() {
        return 0;
    }

    @Override
    public int getCurrentContentPadding() {
        return 0;
    }
}
