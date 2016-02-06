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

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.productlayer.android.common.R;
import com.productlayer.android.common.adapter.TimelineAdapter;
import com.productlayer.android.common.handler.AppBarHandler;
import com.productlayer.android.common.handler.EdgeScrollListener;
import com.productlayer.android.common.handler.HasAppBarHandler;
import com.productlayer.android.common.handler.HasNavigationHandler;
import com.productlayer.android.common.handler.HasPLYAndroidHolder;
import com.productlayer.android.common.handler.HasTimelineSettingsHandler;
import com.productlayer.android.common.handler.HasUserHandler;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.handler.PLYAndroidHolder;
import com.productlayer.android.common.handler.TimelineSettingsHandler;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.common.util.MetricsUtil;
import com.productlayer.android.sdk.PLYAndroid;

/**
 * Abstract class to display a timeline feed in a RecyclerView with a staggered grid layout. The type of items
 * to be retrieved can be set by extending classes.
 *
 * Requires the activity to implement {@link HasNavigationHandler}, {@link HasAppBarHandler}, {@link
 * HasUserHandler}, {@link HasTimelineSettingsHandler}, {@link HasPLYAndroidHolder} - and {@link MetricsUtil}
 * to be set up. Calling {@link #setUnusedHeightPx} is advised to be done before or in {@link #onCreateView}.
 */
public abstract class TimelineFragment extends NamedFragment {

    // TODO make configurable by extending classes and set loaded entries based on phone cpu/memory
    private static final int LOAD_ITEMS = 20;
    private static final int MIN_WIDTH_DP_PER_COL = 250;

    private static final int AVATAR_SIZE_DP = 48;

    protected TimelineSettingsHandler timelineSettingsHandler;
    protected UserHandler userHandler;
    private AppBarHandler appBarHandler;

    private RecyclerView feedView;

    private TimelineAdapter feedAdapter;

    private int unusedHeightPx;

    /**
     * Implement to configure the type of timeline feed items to retrieve.
     *
     * @return the call for the initial retrieval of feed items
     */
    protected abstract TimelineAdapter.Retrieval setupRetrieval();

    /**
     * Implement to do any app bar setup.
     *
     * @param appBarHandler
     *         the app bar handler passed to this fragment
     * @param layout
     *         the layout used by this fragment
     */
    protected abstract void setupAppBar(AppBarHandler appBarHandler, View layout);

    /**
     * @return the identifier for this type of timeline
     */
    protected abstract String getSettingsTag();

    /**
     * @return the default display settings for this type of timeline
     */
    protected abstract int getDefaultSettings();

    // FRAGMENT LIFECYCLE - START //

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        try {
            appBarHandler = ((HasAppBarHandler) activity).getAppBarHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasAppBarHandler");
        }
        NavigationHandler navigationHandler;
        try {
            navigationHandler = ((HasNavigationHandler) activity).getNavigationHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasNavigationHandler");
        }
        try {
            userHandler = ((HasUserHandler) activity).getUserHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasUserHandler");
        }
        try {
            timelineSettingsHandler = ((HasTimelineSettingsHandler) activity).getTimelineSettingsHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasTimelineSettingsHandler");
        }
        PLYAndroidHolder plyAndroidHolder;
        try {
            plyAndroidHolder = ((HasPLYAndroidHolder) activity).getPLYAndroidHolder();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasPLYAndroidHolder");
        }
        PLYAndroid client = plyAndroidHolder.getPLYAndroid();
        if (client == null) {
            throw new RuntimeException("PLYAndroid must bet set before creating fragment " + this);
        }
        // create the adapter to receive feed data as soon as possible
        feedAdapter = new TimelineAdapter(activity, navigationHandler, userHandler, client, setupRetrieval
                (), LOAD_ITEMS, timelineSettingsHandler.getTimelineSettings(getSettingsTag(),
                getDefaultSettings()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // listen for feed item updates by the user
        feedAdapter.setupListeners();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.fragment_timeline, container, false);
        setupAppBar(appBarHandler, layout);
        // initialize the recycler view
        feedView = (RecyclerView) layout.findViewById(R.id.feed_recycler_view);
        // calculate available space (must be done after setupAppBar to get real unusedHeightPx)
        int feedWidthPx = (int) (MetricsUtil.getWidthPx() - getResources().getDimension(R.dimen
                .activity_horizontal_margin) * 2);
        int feedHeightPx = MetricsUtil.getHeightPx() - unusedHeightPx;
        // calculate the amount of columns and an item's dimensions
        int gridColumns = Math.max(1, (int) Math.floor(feedWidthPx / MetricsUtil.inPx(MIN_WIDTH_DP_PER_COL)));
        int itemWidthPx = feedWidthPx / gridColumns;
        int imageMaxHeightPx = MetricsUtil.getOrientation() == Configuration.ORIENTATION_PORTRAIT &&
                gridColumns == 1 ? (int) Math.floor(feedHeightPx / 3) : (int) Math.floor(feedHeightPx / 2);
        feedAdapter.setItemDimensions(itemWidthPx, imageMaxHeightPx, MetricsUtil.inPx(AVATAR_SIZE_DP));
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(gridColumns,
                StaggeredGridLayoutManager.VERTICAL);
        feedView.setItemViewCacheSize(gridColumns * 3);
        feedView.setAdapter(feedAdapter);
        feedView.setHasFixedSize(true);
        feedView.setLayoutManager(gridLayoutManager);
        // listen to scroll events to load more items
        feedView.addOnScrollListener(new EdgeScrollListener(gridLayoutManager, gridColumns) {
            @Override
            public void onHitTop() {
                feedAdapter.retrieveFeedItems(TimelineAdapter.TimeRel.EARLIER);
            }

            @Override
            public void onHitBottom() {
                feedAdapter.retrieveFeedItems(TimelineAdapter.TimeRel.LATER);
            }

            @Override
            public void onScrollUp() {
                timelineSettingsHandler.showTimelineSettings();
            }

            @Override
            public void onScrollDown() {
                timelineSettingsHandler.hideTimelineSettings();
            }
        });
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // despite the recyclerview being destroyed it needs to be reset first to avoid memory leaks
        feedView.clearOnScrollListeners();
        feedView.setAdapter(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stop listening for feed item updates by the user
        feedAdapter.destroyListeners();
    }

    // FRAGMENT LIFECYCLE - END //

    public void updateSettings(int settings) {
        feedAdapter.updateSettings(settings);
    }

    /**
     * Sets the value of the height of the device in px the timeline does not use. Used to calculate ideal
     * dimensions for feed items.
     *
     * @param unusedHeightPx
     *         the amount of space of the screen not used vertically
     */
    public void setUnusedHeightPx(int unusedHeightPx) {
        this.unusedHeightPx = unusedHeightPx;
    }

}
