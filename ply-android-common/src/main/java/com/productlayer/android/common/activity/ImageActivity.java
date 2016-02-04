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

package com.productlayer.android.common.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.productlayer.android.common.R;
import com.productlayer.android.common.handler.DataChangeListener;
import com.productlayer.android.common.util.LocaleUtil;
import com.productlayer.android.common.util.MetricsUtil;
import com.productlayer.android.common.view.LikeView;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.services.ImageService;
import com.productlayer.core.beans.ProductImage;
import com.productlayer.core.beans.User;
import com.productlayer.rest.client.config.PLYRestClientConfig;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * Features zoomable images in fullscreen to be voted on and (by admins only) to be edited.
 *
 * Expects metadata {@link ProductImage} for the image to load in the calling intent in {@link #EXTRA_IMAGE}
 * and for voting the currently logged in user in {@link #EXTRA_USER} as well as friends in {@link
 * #EXTRA_FRIENDS}.
 */
public class ImageActivity extends VerboseActivity {

    public static final String EXTRA_IMAGE = "com.productlayer.android.common.IMAGE";
    public static final String EXTRA_USER = "com.productlayer.android.common.USER";
    public static final String EXTRA_FRIENDS = "com.productlayer.android.common.FRIENDS";

    private static final String STATE_IMAGE = "image";
    private static final String STATE_USER = "user";
    private static final String STATE_FRIENDS = "friends";

    private static final int IMAGE_QUALITY = 85;

    private PLYAndroid client;

    private ProductImage image;
    private User currentUser;
    private ArrayList<User> friends;

    private PhotoView photoView;
    private Target imageTarget = new ImageTarget();
    private LikeView likeView;

    private DataChangeListener.OnImageUpdateListener onImageUpdateListener;

    // ACTIVITY LIFECYCLE - START //

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        MetricsUtil.update(this);
        // REST client config
        client = new PLYAndroid(new PLYRestClientConfig());
        client.setLanguage(LocaleUtil.getDefaultLanguage());
        // try to restore the state of the previously saved PLYAndroid client (i.e. auth data)
        // TODO make sure state has been saved at least once before opening this activity
        // TODO only onPause of previous activity is called before going into onCreate-onResume here
        client.onCreate(this, state, true, true);
        client.onRestoreInstanceState(state, true, true);
        // TODO install user progress listener to return any level advance through voting
        // inflate layout
        setContentView(R.layout.activity_image);
        photoView = (PhotoView) findViewById(R.id.photo_view);
        likeView = (LikeView) findViewById(R.id.like_view);
        // get input either from a saved state (after an orientation change) or from the intent
        if (state != null) {
            image = (ProductImage) state.getSerializable(STATE_IMAGE);
            currentUser = (User) state.getSerializable(STATE_USER);
            //noinspection unchecked
            friends = (ArrayList<User>) state.getSerializable(STATE_FRIENDS);
        }
        if (image == null) {
            Intent intent = getIntent();
            image = (ProductImage) intent.getSerializableExtra(EXTRA_IMAGE);
            if (image == null) {
                throw new RuntimeException("ImageActivity expects an instance of ProductImage to be " +
                        "supplied as " + EXTRA_IMAGE + " through the intent");
            }
            currentUser = (User) intent.getSerializableExtra(EXTRA_USER);
            //noinspection unchecked
            friends = (ArrayList<User>) intent.getSerializableExtra(EXTRA_FRIENDS);
        }
        int[] dominantColor = image.getDominantColor();
        if (dominantColor == null) {
            findViewById(R.id.container).setBackgroundColor(Color.BLACK);
        } else {
            // darken dominant color
            int red = (int) (dominantColor[0] * 0.2);
            int green = (int) (dominantColor[1] * 0.2);
            int blue = (int) (dominantColor[2] * 0.2);
            findViewById(R.id.container).setBackgroundColor(Color.rgb(red, green, blue));
        }
        int maxWidthPx = (int) (MetricsUtil.getWidthPx() * 2.5);
        int maxHeightPx = (int) (MetricsUtil.getHeightPx() * 2.5);
        String imageUrl = ImageService.getImageForSizeURL(client, image.getImageFileId(), maxWidthPx,
                maxHeightPx, false, IMAGE_QUALITY);
        Log.d(getClass().getSimpleName(), "Loading image for fullscreen mode from " + imageUrl);
        Picasso.with(this).load(imageUrl).into(imageTarget);
        // listen for image updates by the user
        setupListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save the state in case of user modifications (f.e. voting)
        outState.putSerializable(STATE_IMAGE, image);
        outState.putSerializable(STATE_USER, currentUser);
        outState.putSerializable(STATE_FRIENDS, friends);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // stop listening for image updates by the user
        destroyListener();
    }

    // ACTIVITY LIFECYCLE - END //

    /**
     * Sets up and registers a listener to be notified of image updates by the user.
     */
    private void setupListener() {
        if (onImageUpdateListener != null) {
            return;
        }
        onImageUpdateListener = new DataChangeListener.OnImageUpdateListener() {
            @Override
            public void onImageUpdate(ProductImage image) {
                if (!image.getId().equals(ImageActivity.this.image.getId())) {
                    // don't care about different images
                    return;
                }
                Log.v("IActivityICallback", "Image update received for ID " + image.getId());
                ImageActivity.this.image = image;
            }
        };
        DataChangeListener.addOnImageUpdateListener(onImageUpdateListener);
    }

    /**
     * Destroys listener to stop being notified of any updates.
     */
    private void destroyListener() {
        if (onImageUpdateListener == null) {
            return;
        }
        DataChangeListener.removeOnImageUpdateListener(onImageUpdateListener);
        onImageUpdateListener = null;
    }

    private class ImageTarget implements Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            photoView.setImageBitmap(bitmap);
            // update and show like view if a user is logged in
            if (currentUser == null) {
                return;
            }
            likeView.setObjectId(image.getImageFileId());
            likeView.setClient(client);
            int widthPx = MetricsUtil.getWidthPx() - (int) getResources().getDimension(R.dimen
                    .exp_toolbar_title_margin_left) - (int) getResources().getDimension(R.dimen
                    .exp_toolbar_title_margin_right);
            likeView.setFromVoters(image.getUpVoters(), image.getDownVoters(), currentUser.getId(),
                    friends, widthPx);
            likeView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.e(getClass().getSimpleName(), "Error loading fullscreen image for product " + image
                    .getProduct().getName());
            // TODO failed loading picture placeholder
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            // TODO image placeholder or loading progress
        }
    }

}
