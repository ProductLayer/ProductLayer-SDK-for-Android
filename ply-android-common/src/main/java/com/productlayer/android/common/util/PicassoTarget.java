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

package com.productlayer.android.common.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Utility class to get Target objects for Picasso, f.e. to transform a bitmap to rounded corners and load it
 * into an ImageView.
 */
public class PicassoTarget {

    /**
     * Creates a Picasso target to transform a bitmap to having rounded corners and load it into the provided
     * ImageView. The bitmap's rounded corner radius is calculated using default values from {@code
     * R.integer.corner_radius_default_min} and {@code R.integer.corner_radius_divisor_default}.
     *
     * Make sure to keep a reference to the returned target, else it may be garbage-collected before
     * completing the call.
     *
     * @param context
     *         the application context
     * @param imageViewTarget
     *         the target view for the transformed bitmap
     * @return the Target object to be used with Picasso
     */
    public static Target roundedCornersImage(final Context context, final ImageView imageViewTarget) {
        return roundedCornersImage(context, imageViewTarget, null);
    }

    /**
     * Creates a Picasso target to transform a bitmap to having rounded corners and load it into the provided
     * ImageView.
     *
     * Make sure to keep a reference to the returned target, else it may be garbage-collected before
     * completing the call.
     *
     * @param context
     *         the application context
     * @param imageViewTarget
     *         the target view for the transformed bitmap
     * @param cornerRadius
     *         the corner radius in px
     * @return the Target object to be used with Picasso
     */
    public static Target roundedCornersImage(final Context context, final ImageView imageViewTarget, final
    Float cornerRadius) {
        return new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Drawable drawable = cornerRadius == null ? BitmapUtil.getRoundedBitmapDrawable(context,
                        bitmap) : BitmapUtil.getRoundedBitmapDrawable(context, bitmap, cornerRadius);
                imageViewTarget.setImageDrawable(drawable);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(getClass().getSimpleName(), "Error loading image with rounded corners");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }

}
