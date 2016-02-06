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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.productlayer.android.common.R;
import com.productlayer.android.common.adapter.ProductDetailsAdapter;
import com.productlayer.android.common.global.ObjectCache;
import com.productlayer.android.common.handler.DataChangeListener;
import com.productlayer.android.common.util.SystemBarsUtil;
import com.productlayer.core.beans.Category;
import com.productlayer.core.beans.Product;

/**
 * Displays detailed information of a product.
 */
public class ProductDetailsFragment extends NamedFragment {

    public static final String NAME = "ProductDetails";

    private static final String KEY_PRODUCT = "product";

    private RecyclerView detailsView;

    private Product product;

    // listener would be garbage-collected without reference
    private DataChangeListener.OnProductUpdateListener onProductUpdateListener;

    /**
     * Constructs a new instance with the specified parameters. The parameters passed this way survive
     * recreation of the fragment due to orientation changes etc.
     *
     * @param product
     *         the product to display details of
     * @return the fragment with the given parameters
     */
    public static ProductDetailsFragment newInstance(@SuppressWarnings("TypeMayBeWeakened") Product product) {
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_PRODUCT, product);
        productDetailsFragment.setArguments(args);
        return productDetailsFragment;
    }

    /**
     * @param product
     *         the product to display details of
     * @return this fragment's name and initialization parameters
     */
    public static String makeInstanceName(Product product) {
        String productParam = product == null ? "" : product.getId();
        return NAME + "(" + productParam + ")";
    }

    @Override
    public String getInstanceName() {
        return makeInstanceName(product);
    }

    @Override
    public FragmentGrouping getGrouping() {
        return FragmentGrouping.NONE;
    }

    // FRAGMENT LIFECYCLE - START //

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        product = (Product) args.getSerializable(KEY_PRODUCT);
        // listen for product updates by the user
        setupListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Context context = getContext();
        // inflate the layout
        View layout = inflater.inflate(R.layout.fragment_product_details, container, false);
        detailsView = (RecyclerView) layout.findViewById(R.id.details_recycler_view);
        // prevent users from being covered by the navigation bar
        if (SystemBarsUtil.hasTranslucentNavigationBar(context)) {
            layout.setPadding(layout.getPaddingLeft(), layout.getPaddingTop(), layout.getPaddingRight(),
                    layout.getPaddingBottom() + SystemBarsUtil.getNavigationBarHeight(context));
            ((ViewGroup) layout).setClipToPadding(false);
            // TODO move up if safe for all screens
        }
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // categories ought to be cached by the main activity
        Category[] categories = ObjectCache.getCategories(getContext(), null, true, false);
        ProductDetailsAdapter detailsAdapter = new ProductDetailsAdapter(getContext(), product, categories);
        detailsView.setAdapter(detailsAdapter);
        // TODO base the amount of columns on the width of the screen
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        detailsView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // despite the recyclerview being destroyed it needs to be reset first to avoid memory leaks
        detailsView.setAdapter(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stop listening for product updates by the user
        destroyListener();
    }

    // FRAGMENT LIFECYCLE - END //

    /**
     * Sets up and registers a listener to be notified of product updates by the user.
     */
    private void setupListener() {
        if (onProductUpdateListener != null) {
            return;
        }
        onProductUpdateListener = new DataChangeListener.OnProductUpdateListener() {
            @Override
            public void onProductUpdate(Product product) {
                Log.v("PDetailsCallback", "Product update received for ID " + product.getId());
                ProductDetailsFragment.this.product = product;
            }
        };
        DataChangeListener.addOnProductUpdateListener(onProductUpdateListener);
    }

    /**
     * Destroys listener to stop being notified of any updates.
     */
    private void destroyListener() {
        if (onProductUpdateListener == null) {
            return;
        }
        DataChangeListener.removeOnProductUpdateListener(onProductUpdateListener);
        onProductUpdateListener = null;
    }

}
