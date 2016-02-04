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

import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.lang.ref.WeakReference;

/**
 * Blurs an image loaded by Picasso. Use in {@link RequestCreator#transform}.
 */
public class PicassoBlur implements Transformation {

    private WeakReference<Context> contextRef;

    /**
     * Creates the transformation to blur an image.
     *
     * @param context
     *         the application context
     */
    public PicassoBlur(Context context) {
        contextRef = new WeakReference<Context>(context);
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap bitmapOut = BitmapUtil.blur(contextRef.get(), source);
        if (bitmapOut == null) {
            bitmapOut = BitmapUtil.stackBlur(source, 10);
        }
        source.recycle();
        return bitmapOut;
    }

    @Override
    public String key() {
        return "blur0.5/6";
    }
}
