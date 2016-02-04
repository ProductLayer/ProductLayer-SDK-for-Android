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

package com.productlayer.android.common.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.productlayer.android.common.adapter.TimelineAdapter;
import com.productlayer.android.common.handler.AppBarHandler;
import com.productlayer.android.common.util.SystemBarsUtil;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.TimelineService;
import com.productlayer.core.beans.ResultSetWithCursor;

import static com.productlayer.android.common.handler.TimelineSettingsHandler.TimelineSetting;

/**
 * Displays the global timeline feed including any new products and opinions (and optionally images).
 *
 * @see TimelineFragment
 */
public class GlobalTimelineFragment extends TimelineFragment {

    public static final String NAME = "GlobalTimeline";
    public static final String SETTINGS_TAG = "global";
    public static final int DEFAULT_SETTINGS = 0;

    /**
     * @return this fragment's name and initialization parameters
     */
    public static String makeInstanceName() {
        return NAME + "()";
    }

    @Override
    public String getInstanceName() {
        return makeInstanceName();
    }

    @Override
    public FragmentGrouping getGrouping() {
        return FragmentGrouping.HOME;
    }

    @Override
    protected TimelineAdapter.Retrieval setupRetrieval() {
        return new TimelineAdapter.Retrieval() {
            @Override
            public void initiate(PLYAndroid client, int loadItems, int settings,
                    PLYCompletion<ResultSetWithCursor> completion) {
                boolean friendsOnly = (settings & TimelineSetting.FRIENDS_ONLY.value) > 0;
                boolean includeImages = (settings & TimelineSetting.INCLUDE_IMAGES.value) > 0;
                TimelineService.getTimeline(client, null, null, null, loadItems, null, null, true, false,
                        includeImages, true, friendsOnly, completion);
            }

            @Override
            public TimelineAdapter.TimelineType type() {
                return TimelineAdapter.TimelineType.GLOBAL;
            }

            @Override
            public String identifier() {
                return null;
            }
        };
    }

    @Override
    protected void setupAppBar(AppBarHandler appBarHandler, View layout) {
        appBarHandler.setTimelineAppBar(layout);
        Context context = getContext();
        int unusedHeightPx = appBarHandler.hasTranslucentStatusBar() ? 0 : SystemBarsUtil
                .getStatusBarHeight(context);
        unusedHeightPx += SystemBarsUtil.hasTranslucentNavigationBar(context) ? 0 : SystemBarsUtil
                .getNavigationBarHeight(context);
        setUnusedHeightPx(unusedHeightPx);
    }

    @Override
    protected String getSettingsTag() {
        return SETTINGS_TAG;
    }

    @Override
    protected int getDefaultSettings() {
        return DEFAULT_SETTINGS;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = super.onCreateView(inflater, container, savedInstanceState);
        timelineSettingsHandler.configureTimelineSettings(getSettingsTag(), new
                TimelineSetting[]{TimelineSetting.INCLUDE_IMAGES, TimelineSetting.FRIENDS_ONLY},
                getDefaultSettings(), this);
        return layout;
    }
}
