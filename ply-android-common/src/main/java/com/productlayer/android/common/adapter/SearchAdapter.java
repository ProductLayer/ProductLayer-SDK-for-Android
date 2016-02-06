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

package com.productlayer.android.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.productlayer.android.common.global.LoadingIndicator;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.view.SearchResult;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.ImageService;
import com.productlayer.android.sdk.services.ProductService;
import com.productlayer.core.beans.Product;
import com.productlayer.core.beans.ProductImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides the results of a search query.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchResultHolder> {

    private static final String ORDER_BY = "pl-vote-score_desc";

    private Context context;
    private NavigationHandler navigationHandler;
    private PLYAndroid client;

    private int loadItems;

    private int imageWidthPx;
    private int imageMaxHeightPx;

    private List<Product> products = new ArrayList<>();
    private String productName;
    private int page;
    private boolean loadedAllResults;

    private AtomicBoolean loading = new AtomicBoolean(false);

    /**
     * Creates the the search results adapter.
     *
     * Use {@link #setItemDimensions} before attaching the adapter to a view.
     *
     * @param context
     *         the app context
     * @param navigationHandler
     *         the activity's navigation handler to open new screens
     * @param client
     *         the PLYAndroid REST client to use for querying for data
     * @param loadItems
     *         the amount of items to load in one batch
     */
    public SearchAdapter(Context context, NavigationHandler navigationHandler, PLYAndroid client, int
            loadItems) {
        this.context = context;
        this.navigationHandler = navigationHandler;
        this.client = client;
        this.loadItems = loadItems;
        setHasStableIds(true);
    }

    /**
     * Sets the fixed width and the maximum height of images. Images are retrieved in {@link
     * #onBindViewHolder(SearchResultHolder, int)} in the dimensions set here.
     *
     * @param imageWidthPx
     *         the width of an image
     * @param imageMaxHeightPx
     *         the maximum height of an image
     */
    public void setItemDimensions(int imageWidthPx, int imageMaxHeightPx) {
        this.imageWidthPx = imageWidthPx;
        this.imageMaxHeightPx = imageMaxHeightPx;
    }

    /**
     * Retrieves search results for the provided parameter.
     *
     * @param productName
     *         the name of the product to search for
     * @param postSearchRunnable
     *         executed when search results are retrieved
     */
    public void search(String productName, final PostSearchRunnable postSearchRunnable) {
        loading.set(true);
        loadedAllResults = false;
        Log.d(getClass().getSimpleName(), "Searching for " + productName + " ...");
        LoadingIndicator.show();
        this.productName = productName;
        page = 0;
        ProductService.searchProducts(client, null, page, loadItems, null, null, null, null, null, null,
                productName, null, ORDER_BY, new PLYCompletion<Product[]>() {
                    @Override
                    public void onSuccess(Product[] result) {
                    }

                    @Override
                    public void onPostSuccess(Product[] result) {
                        page = 0;
                        int cntItems;
                        if (result == null || result.length == 0) {
                            products = new ArrayList<>();
                            cntItems = 0;
                        } else {
                            products = new ArrayList<>(Arrays.asList(result));
                            cntItems = result.length;
                        }
                        notifyDataSetChanged();
                        loadedAllResults = cntItems < loadItems;
                        postSearchRunnable.runPostSearch(cntItems);
                        loading.set(false);
                        LoadingIndicator.hide();
                    }

                    @Override
                    public void onError(PLYAndroid.QueryError error) {
                        // TODO check if server or client error and display message / retry if init
                        Log.e("SearchCallback", error.getMessage(), error.getException());
                        loading.set(false);
                        LoadingIndicator.hide();
                    }
                });
    }

    /**
     * Continues a previous search retrieving the next batch of results.
     *
     * @param postSearchRunnable
     *         executed when search results are retrieved
     */
    public void searchMore(final PostSearchRunnable postSearchRunnable) {
        if (loadedAllResults) {
            return;
        }
        if (!loading.compareAndSet(false, true)) {
            // TODO reset loading if true for a long time (might happen due to orientation changes)
            // already retrieving items
            return;
        }
        page++;
        Log.d(getClass().getSimpleName(), "Searching for " + productName + " (page " + page + ") ...");
        LoadingIndicator.show();
        ProductService.searchProducts(client, null, page, loadItems, null, null, null, null, null, null,
                productName, null, ORDER_BY, new PLYCompletion<Product[]>() {
                    @Override
                    public void onSuccess(Product[] result) {
                    }

                    @Override
                    public void onPostSuccess(Product[] result) {
                        int cntItemsNew;
                        if (result == null || result.length == 0) {
                            cntItemsNew = 0;
                        } else {
                            cntItemsNew = result.length;
                            int cntItemsCur = products.size();
                            products.addAll(Arrays.asList(result));
                            notifyItemRangeInserted(cntItemsCur, cntItemsNew);
                        }
                        loadedAllResults = cntItemsNew < loadItems;
                        postSearchRunnable.runPostSearch(cntItemsNew);
                        loading.set(false);
                        LoadingIndicator.hide();
                    }

                    @Override
                    public void onError(PLYAndroid.QueryError error) {
                        // TODO check if server or client error and display message / retry if init
                        Log.e("SearchCallback", error.getMessage(), error.getException());
                        loading.set(false);
                        LoadingIndicator.hide();
                    }
                });
    }

    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchResultHolder(new SearchResult(context, navigationHandler, imageWidthPx,
                imageMaxHeightPx));
    }

    @Override
    public void onBindViewHolder(SearchResultHolder holder, int position) {
        Product product = products.get(position);
        ProductImage productImage = product.getDefaultImage();
        String productImageUrl = null;
        int[] productDominantColor = null;
        if (productImage != null) {
            float widthToHeightRatio = productImage.getWidth() / (float) productImage.getHeight();
            int imageHeightPx = Math.min(Math.round(imageWidthPx / widthToHeightRatio), imageMaxHeightPx);
            productImageUrl = ImageService.getImageForSizeURL(client, productImage.getImageFileId(),
                    imageWidthPx, imageHeightPx, true, null);
            productDominantColor = productImage.getDominantColor();
        }
        holder.searchResult.setSearchResult(product, productImageUrl, productDominantColor);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).hashCode();
    }

    /**
     * Contains one function to be called after successfully retrieving search results.
     */
    public interface PostSearchRunnable {

        /**
         * Runs when search results are retrieved.
         *
         * @param cntItems
         *         the amount of items newly fetched
         */
        void runPostSearch(int cntItems);

    }

    /**
     * ViewHolder cached by the RecyclerView. Holds a search result cardview.
     */
    public static class SearchResultHolder extends RecyclerView.ViewHolder {

        public SearchResult searchResult;

        /**
         * Creates the ViewHolder caching a search result.
         *
         * @param searchResult
         *         the search result to cache
         */
        public SearchResultHolder(SearchResult searchResult) {
            super(searchResult);
            this.searchResult = searchResult;
        }

    }

}
