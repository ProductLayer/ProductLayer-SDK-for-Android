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

package com.productlayer.android.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.util.ThemeUtil;
import com.productlayer.core.beans.Product;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * A compound view displaying a product search result.
 */
public class SearchResult extends CardView {

    private Context context;

    private ImageView productImage;
    private Target productImageTarget = new ProductImageTarget();
    private int imagePlaceholderColor;
    private TextView productName;
    private TextView productBrand;

    private Product product;

    /**
     * Creates the cardview setting the dimensions of the product image placeholder.
     *
     * @param context
     *         the application context
     * @param navigationHandler
     *         the activity's navigation handler to open new screens
     * @param imageWidthPx
     *         the width of the product image
     * @param maxImageHeightPx
     *         the maximum height of the product image
     */
    public SearchResult(Context context, final NavigationHandler navigationHandler, int imageWidthPx, int
            maxImageHeightPx) {
        this(context, null);
        ViewGroup.LayoutParams productImageLayoutParams = productImage.getLayoutParams();
        productImageLayoutParams.width = imageWidthPx;
        productImageLayoutParams.height = maxImageHeightPx;
        OnClickListener productClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product != null) {
                    navigationHandler.openProductPage(product);
                }
            }
        };
        setOnClickListener(productClickListener);
    }

    /**
     * Creates the cardview. Is called by {@link #SearchResult(Context, NavigationHandler, int, int)} and if
     * inflated from XML.
     *
     * @param context
     *         the application context
     */
    public SearchResult(Context context) {
        this(context, null);
    }

    /**
     * Creates the cardview. Is called by {@link #SearchResult(Context, NavigationHandler, int, int)} and if
     * inflated from XML.
     *
     * @param context
     *         the application context
     * @param attrs
     *         any attributes set via XML
     */
    public SearchResult(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates the cardview. Is called by {@link #SearchResult(Context, NavigationHandler, int, int)} and if
     * inflated from XML.
     *
     * @param context
     *         the application context
     * @param attrs
     *         any attributes set via XML
     * @param defStyleAttr
     *         any style set via XML
     */
    public SearchResult(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.compound_search_result, this, true);
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int feedItemMarginPx = Math.round(getResources().getDimension(R.dimen.feed_item_margin));
        marginLayoutParams.setMargins(feedItemMarginPx, feedItemMarginPx, feedItemMarginPx, feedItemMarginPx);
        setLayoutParams(marginLayoutParams);
        setRadius(getResources().getDimension(R.dimen.feed_item_radius));
        Integer cardBackgroundColor = ThemeUtil.getIntegerValue(context, R.attr.cardBackground);
        setCardBackgroundColor(cardBackgroundColor == null ? Color.WHITE : cardBackgroundColor);
        productImage = (ImageView) findViewById(R.id.product_image);
        productName = (TextView) findViewById(R.id.product_name);
        productBrand = (TextView) findViewById(R.id.product_brand);
        Integer imagePlaceholderColor = ThemeUtil.getIntegerValue(context, R.attr.imagePlaceholder);
        this.imagePlaceholderColor = imagePlaceholderColor == null ? Color.TRANSPARENT :
                imagePlaceholderColor;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setPreventCornerOverlap(false);
            productImage.setBackgroundColor(this.imagePlaceholderColor);
        }
    }

    /**
     * Sets a new search result to be displayed in the cardview.
     *
     * @param product
     *         the product to show
     * @param productImageUrl
     *         the URL of the product image to load
     * @param productDominantColor
     *         the dominant color (rgb) of the product image to use as background while the image is being
     *         loaded
     */
    public void setSearchResult(Product product, String productImageUrl, int[] productDominantColor) {
        if (this.product != null && product.equals(this.product)) {
            return;
        }
        // display new product image
        this.product = product;
        Picasso.with(context).cancelRequest(productImageTarget);
        productImage.setImageBitmap(null);
        // display the image's dominant color as background while it is being loaded
        if (productDominantColor != null) {
            productImage.setBackgroundColor(Color.rgb(productDominantColor[0], productDominantColor[1],
                    productDominantColor[2]));
        } else {
            productImage.setBackgroundColor(imagePlaceholderColor);
        }
        // load product image
        if (productImageUrl != null) {
            productImage.setVisibility(VISIBLE);
            Picasso.with(context).load(productImageUrl).into(productImageTarget);
        } else {
            productImage.setVisibility(GONE);
            // TODO product placeholder image?
        }
        // display product data
        productName.setText(product.getName());
        String brand = product.getBrand();
        productBrand.setText(brand != null ? brand : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SearchResult that = (SearchResult) o;

        return !(product != null ? !product.equals(that.product) : that.product != null);

    }

    @Override
    public int hashCode() {
        return product != null ? product.hashCode() : 0;
    }

    private class ProductImageTarget implements Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ViewGroup.LayoutParams productImageLayoutParams = productImage.getLayoutParams();
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            if (productImageLayoutParams.height != bitmapHeight) {
                productImageLayoutParams.height = bitmapHeight;
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                productImage.setBackgroundColor(Color.TRANSPARENT);
            }
            productImage.setImageBitmap(bitmap);
            Log.d(getClass().getSimpleName(), bitmapWidth + "x" + bitmapHeight + " image from " +
                    from.name() + " loaded into " + productImageLayoutParams.width + "x" +
                    productImageLayoutParams.height + " view");
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.e(getClass().getSimpleName(), "Error loading image for product " + product.getName());
            productImage.setVisibility(GONE);
            // TODO product placeholder image?
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }

}
