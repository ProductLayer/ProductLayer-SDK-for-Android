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
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.adapter.SearchAdapter;
import com.productlayer.android.common.handler.AppBarHandler;
import com.productlayer.android.common.handler.EdgeScrollListener;
import com.productlayer.android.common.handler.HasAppBarHandler;
import com.productlayer.android.common.handler.HasNavigationHandler;
import com.productlayer.android.common.handler.HasPLYAndroidHolder;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.handler.PLYAndroidHolder;
import com.productlayer.android.common.handler.TimelineSettingsHandler;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.common.util.MetricsUtil;
import com.productlayer.android.common.util.SystemBarsUtil;
import com.productlayer.android.sdk.PLYAndroid;

/**
 * Displays search results in a RecyclerView with a staggered grid layout.
 *
 * Requires the activity to implement {@link HasNavigationHandler}, {@link HasAppBarHandler}, {@link
 * HasPLYAndroidHolder} - and {@link MetricsUtil} to be set up.
 */
public class SearchFragment extends NamedFragment {

    public static final String NAME = "Search";

    private static final String KEY_PRODUCT_NAME = "productName";

    // TODO make configurable by extending classes and set loaded entries based on phone cpu/memory
    private static final int LOAD_ITEMS = 25;
    private static final int MIN_WIDTH_DP_PER_COL = 250;

    protected TimelineSettingsHandler timelineSettingsHandler;
    protected UserHandler userHandler;
    private AppBarHandler appBarHandler;

    private RecyclerView searchResultsView;
    private TextView amountSearchResultsView;

    private SearchAdapter searchAdapter;
    private SearchAdapter.PostSearchRunnable postSearchRunnable;

    private String productName;

    /**
     * @param productName
     *         the name of the product to search for
     * @return this fragment's name and initialization parameters
     */
    public static String makeInstanceName(String productName) {
        String productParam = productName == null ? "" : productName;
        return NAME + "(" + productParam + ")";
    }

    /**
     * Constructs a new instance with the specified parameters. The parameters passed this way survive
     * recreation of the fragment due to orientation changes etc.
     *
     * @param productName
     *         the name of the product to search for
     * @return the fragment with the given parameters
     */
    public static SearchFragment newInstance(String productName) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(KEY_PRODUCT_NAME, productName);
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public String getInstanceName() {
        return makeInstanceName(productName);
    }

    @Override
    public FragmentGrouping getGrouping() {
        return FragmentGrouping.SEARCH;
    }

    // FRAGMENT LIFECYCLE - START //

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle args = getArguments();
        productName = args.getString(KEY_PRODUCT_NAME);
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
        // create the adapter
        searchAdapter = new SearchAdapter(activity, navigationHandler, client, LOAD_ITEMS);
        postSearchRunnable = new SearchAdapter.PostSearchRunnable() {
            @Override
            public void runPostSearch(int cntItems) {
                if (amountSearchResultsView != null) {
                    String amountText = cntItems == LOAD_ITEMS ? (searchAdapter.getItemCount() + "+") :
                            (searchAdapter.getItemCount() + "");
                    String resultsText = getResources().getString(R.string.search_results_for_name,
                            productName);
                    amountSearchResultsView.setText(String.format("%s (%s)", resultsText, amountText));
                }
            }
        };
        searchAdapter.search(productName, postSearchRunnable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Context context = getContext();
        View layout = inflater.inflate(R.layout.fragment_search, container, false);
        appBarHandler.setTimelineAppBar(layout);
        amountSearchResultsView = (TextView) layout.findViewById(R.id.amount_search_results);
        amountSearchResultsView.setText(getResources().getString(R.string.search_results_for_name,
                productName));
        // initialize the recycler view
        searchResultsView = (RecyclerView) layout.findViewById(R.id.search_results);
        // calculate available space (must be done after setting up the app bar to get real unusedHeightPx)
        int feedWidthPx = (int) (MetricsUtil.getWidthPx() - getResources().getDimension(R.dimen
                .activity_horizontal_margin) * 2);
        int unusedHeightPx = appBarHandler.hasTranslucentStatusBar() ? 0 : SystemBarsUtil
                .getStatusBarHeight(context);
        unusedHeightPx += SystemBarsUtil.hasTranslucentNavigationBar(context) ? 0 : SystemBarsUtil
                .getNavigationBarHeight(context);
        int feedHeightPx = MetricsUtil.getHeightPx() - unusedHeightPx;
        // calculate the amount of columns and an image's dimensions
        int gridColumns = Math.max(1, (int) Math.floor(feedWidthPx / MetricsUtil.inPx(MIN_WIDTH_DP_PER_COL)));
        int imageWidthPx = (int) (feedWidthPx / gridColumns / 2.5);
        int imageMaxHeightPx = MetricsUtil.getOrientation() == Configuration.ORIENTATION_PORTRAIT &&
                gridColumns == 1 ? (int) Math.floor(feedHeightPx / 6) : (int) Math.floor(feedHeightPx / 4);
        searchAdapter.setItemDimensions(imageWidthPx, imageMaxHeightPx);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(gridColumns,
                StaggeredGridLayoutManager.VERTICAL);
        searchResultsView.setItemViewCacheSize(gridColumns * 3);
        searchResultsView.setAdapter(searchAdapter);
        searchResultsView.setHasFixedSize(true);
        searchResultsView.setLayoutManager(gridLayoutManager);
        // listen to scroll events to load more items
        searchResultsView.addOnScrollListener(new EdgeScrollListener(gridLayoutManager, gridColumns) {
            @Override
            public void onHitTop() {
            }

            @Override
            public void onHitBottom() {
                searchAdapter.searchMore(postSearchRunnable);
            }

            @Override
            public void onScrollUp() {
            }

            @Override
            public void onScrollDown() {
            }
        });
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // despite the recyclerview being destroyed it needs to be reset first to avoid memory leaks
        searchResultsView.clearOnScrollListeners();
        searchResultsView.setAdapter(null);
    }

    // FRAGMENT LIFECYCLE - END //

}
