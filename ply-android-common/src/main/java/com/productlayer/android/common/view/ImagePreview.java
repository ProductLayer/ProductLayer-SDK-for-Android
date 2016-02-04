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
import android.content.Intent;
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

import com.productlayer.android.common.R;
import com.productlayer.android.common.activity.ImageActivity;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.common.util.ThemeUtil;
import com.productlayer.core.beans.ProductImage;
import com.productlayer.core.beans.SimpleUserInfo;
import com.productlayer.core.beans.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * A compound view displaying a preview of a product image.
 */
public class ImagePreview extends CardView {

    private final int cardBackgroundColor;
    private final int friendBackgroundColor;

    private Context context;

    private AuthorView authorView;
    private ImageView productImage;
    private Target productImageTarget = new ProductImageTarget();
    private int imagePlaceholderColor;
    private View scrim;

    private ProductImage image;
    private User currentUser;
    private ArrayList<User> friends;

    /**
     * Creates the cardview setting the dimensions of the product and author image placeholder.
     *
     * @param context
     *         the application context
     * @param navigationHandler
     *         the activity's navigation handler to open new screens
     * @param imageWidthPx
     *         the width of the product image
     * @param maxImageHeightPx
     *         the maximum height of the product image
     * @param avatarSizePx
     *         the width/height of the author's avatar
     */
    public ImagePreview(final Context context, final NavigationHandler navigationHandler, int imageWidthPx,
            int maxImageHeightPx, int avatarSizePx) {
        this(context, null);
        ViewGroup.LayoutParams productImageLayoutParams = productImage.getLayoutParams();
        productImageLayoutParams.width = imageWidthPx;
        productImageLayoutParams.height = maxImageHeightPx;
        ViewGroup.LayoutParams lpForScrim = scrim.getLayoutParams();
        lpForScrim.height = ProductPreview.calcScrimSize(maxImageHeightPx);
        authorView.setAvatarSize(avatarSizePx);
        authorView.setOnClickListeners(navigationHandler);
        productImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image != null) {
                    Intent intent = new Intent(getContext(), ImageActivity.class);
                    intent.putExtra(ImageActivity.EXTRA_IMAGE, image);
                    intent.putExtra(ImageActivity.EXTRA_USER, currentUser);
                    intent.putExtra(ImageActivity.EXTRA_FRIENDS, friends);
                    context.startActivity(intent);
                }
            }
        });
    }

    /**
     * Creates the cardview. Is called by {@link #ImagePreview(Context, NavigationHandler, int, int, int)} and
     * if inflated from XML.
     *
     * @param context
     *         the application context
     */
    public ImagePreview(Context context) {
        this(context, null);
    }

    /**
     * Creates the cardview. Is called by {@link #ImagePreview(Context, NavigationHandler, int, int, int)} and
     * if inflated from XML.
     *
     * @param context
     *         the application context
     * @param attrs
     *         any attributes set via XML
     */
    public ImagePreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates the cardview. Is called by {@link #ImagePreview(Context, NavigationHandler, int, int, int)} and
     * if inflated from XML.
     *
     * @param context
     *         the application context
     * @param attrs
     *         any attributes set via XML
     * @param defStyleAttr
     *         any style set via XML
     */
    public ImagePreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.compound_image_preview, this, true);
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int feedItemMarginPx = Math.round(getResources().getDimension(R.dimen.feed_item_margin));
        marginLayoutParams.setMargins(feedItemMarginPx, feedItemMarginPx, feedItemMarginPx, feedItemMarginPx);
        setLayoutParams(marginLayoutParams);
        setRadius(getResources().getDimension(R.dimen.feed_item_radius));
        Integer cardBackgroundColor = ThemeUtil.getIntegerValue(context, R.attr.cardBackground);
        this.cardBackgroundColor = cardBackgroundColor == null ? Color.WHITE : cardBackgroundColor;
        setCardBackgroundColor(this.cardBackgroundColor);
        Integer friendBackgroundColor = ThemeUtil.getIntegerValue(context, R.attr.friendBackground);
        this.friendBackgroundColor = friendBackgroundColor == null ? Color.WHITE : friendBackgroundColor;
        authorView = (AuthorView) findViewById(R.id.author);
        productImage = (ImageView) findViewById(R.id.product_image);
        scrim = findViewById(R.id.scrim);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setPreventCornerOverlap(false);
            Integer imagePlaceholderColor = ThemeUtil.getIntegerValue(context, R.attr.imagePlaceholder);
            this.imagePlaceholderColor = imagePlaceholderColor == null ? Color.TRANSPARENT :
                    imagePlaceholderColor;
            productImage.setBackgroundColor(this.imagePlaceholderColor);
        }
    }

    /**
     * Sets a new image to be displayed in the cardview.
     *
     * @param image
     *         the image to show
     * @param productImageUrl
     *         the URL of the product image to load
     * @param productDominantColor
     *         the dominant color (rgb) of the product image to use as background while the image is being
     *         loaded
     * @param author
     *         the author to show
     * @param authorImageUrl
     *         the URL of the author's avatar image to load
     * @param userHandler
     *         the handler to retrieve user info from for friend and voting info
     */
    public void setImage(ProductImage image, String productImageUrl, int[] productDominantColor,
            SimpleUserInfo author, String authorImageUrl, UserHandler userHandler) {
        currentUser = userHandler.getUser();
        friends = userHandler.getFriends();
        if (this.image != null && image.equals(this.image)) {
            return;
        }
        // display new product image
        this.image = image;
        Picasso.with(context).cancelRequest(productImageTarget);
        productImage.setImageBitmap(null);
        boolean isFriend = userHandler.isFriend(author);
        // colorize cardview if this entry was created by oneself or a friend
        setCardBackgroundColor(isFriend ? friendBackgroundColor : cardBackgroundColor);
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
            // TODO hide view or failed loading picture placeholder
        }
        // display new author
        authorView.setAuthor(author, authorImageUrl, isFriend, image.getProduct(), image.getCreated());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ImagePreview that = (ImagePreview) o;

        if (image != null ? !image.equals(that.image) : that.image != null)
            return false;
        return !(currentUser != null ? !currentUser.equals(that.currentUser) : that.currentUser != null);

    }

    @Override
    public int hashCode() {
        int result = image != null ? image.hashCode() : 0;
        result = 31 * result + (currentUser != null ? currentUser.hashCode() : 0);
        return result;
    }

    private class ProductImageTarget implements Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ViewGroup.LayoutParams productImageLayoutParams = productImage.getLayoutParams();
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            if (productImageLayoutParams.height != bitmapHeight) {
                productImageLayoutParams.height = bitmapHeight;
                ViewGroup.LayoutParams lpForScrim = scrim.getLayoutParams();
                lpForScrim.height = ProductPreview.calcScrimSize(bitmapHeight);
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
            Log.e(getClass().getSimpleName(), "Error loading image for product " + image.getProduct()
                    .getName());
            productImage.setVisibility(GONE);
            // TODO hide view or failed loading picture placeholder
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }
}
