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
import android.view.View;

import com.productlayer.android.common.adapter.TimelineAdapter;
import com.productlayer.android.common.handler.AppBarHandler;
import com.productlayer.android.common.handler.TimelineSettingsHandler;
import com.productlayer.android.common.util.SystemBarsUtil;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.TimelineService;
import com.productlayer.core.beans.ResultSetWithCursor;

/**
 * Displays the timeline feed of a product including any opinions and images.
 *
 * @see TimelineFragment
 */
// TODO don't show empty tab when there are no entries (ie invite to write the first opinion etc)
public class ProductTimelineFragment extends TimelineFragment {

    public static final String NAME = "ProductTimeline";
    public static final String SETTINGS_TAG = "product";
    public static final int DEFAULT_SETTINGS = 0;

    private static final String KEY_GTIN = "gtin";

    private String gtin;

    /**
     * Constructs a new instance with the specified parameters. The parameters passed this way survive
     * recreation of the fragment due to orientation changes etc.
     *
     * @param gtin
     *         the GTIN of the product to display the timeline of
     * @return the fragment with the given parameters
     */
    public static ProductTimelineFragment newInstance(String gtin) {
        ProductTimelineFragment productTimelineFragment = new ProductTimelineFragment();
        Bundle args = new Bundle();
        args.putString(KEY_GTIN, gtin);
        productTimelineFragment.setArguments(args);
        return productTimelineFragment;
    }

    @Override
    public String getInstanceName() {
        String productParam = gtin == null ? "" : gtin;
        return NAME + "(" + productParam + ")";
    }

    @Override
    public FragmentGrouping getGrouping() {
        return FragmentGrouping.NONE;
    }

    @Override
    public void onAttach(Context context) {
        Bundle args = getArguments();
        gtin = args.getString(KEY_GTIN);
        super.onAttach(context);
    }

    @Override
    protected TimelineAdapter.Retrieval setupRetrieval() {
        return new TimelineAdapter.Retrieval() {
            @Override
            public void initiate(PLYAndroid client, int loadItems, int settings,
                    PLYCompletion<ResultSetWithCursor> completion) {
                boolean friendsOnly = (settings & TimelineSettingsHandler.TimelineSetting.FRIENDS_ONLY
                        .value) > 0;
                TimelineService.getProductTimeline(client, gtin, loadItems, null, null, true, false, true,
                        false, friendsOnly, completion);
            }

            @Override
            public TimelineAdapter.TimelineType type() {
                return TimelineAdapter.TimelineType.PRODUCT;
            }

            @Override
            public String identifier() {
                return gtin;
            }
        };
    }

    @Override
    protected void setupAppBar(AppBarHandler appBarHandler, View layout) {
        Context context = getContext();
        int unusedHeightPx = appBarHandler.hasTranslucentStatusBar() ? 0 : SystemBarsUtil
                .getStatusBarHeight(context);
        unusedHeightPx += SystemBarsUtil.hasTranslucentNavigationBar(context) ? 0 : SystemBarsUtil
                .getNavigationBarHeight(context);
        unusedHeightPx += appBarHandler.getCurrentCollapsedToolbarHeight();
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

    /**
     * Prepares the available timeline settings for this timeline.
     *
     * @param timelineSettingsHandler
     *         the handler for timeline settings
     */
    public void prepareTimelineSettings(TimelineSettingsHandler timelineSettingsHandler) {
        timelineSettingsHandler.configureTimelineSettings(getSettingsTag(), new TimelineSettingsHandler
                .TimelineSetting[]{TimelineSettingsHandler.TimelineSetting.FRIENDS_ONLY},
                getDefaultSettings(), this);
    }

}
