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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import com.productlayer.android.common.R;

import java.io.ByteArrayOutputStream;

/**
 * Convenience methods working on Bitmaps.
 */
public class BitmapUtil {

    /**
     * Transforms a bitmap into a rounded bitmap drawable using default corner radius from {@code
     * R.integer.corner_radius_default_min} and {@code R.integer.corner_radius_divisor_default}.
     *
     * @param context
     *         the application context
     * @param bitmap
     *         the bitmap to transform
     * @return the rounded bitmap drawable
     */
    public static RoundedBitmapDrawable getRoundedBitmapDrawable(Context context, Bitmap bitmap) {
        Resources res = context.getResources();
        float cornerRadius = Math.max(res.getInteger(R.integer.corner_radius_default_min), Math.min(bitmap
                .getHeight(), bitmap.getWidth()) / (float) res.getInteger(R.integer
                .corner_radius_divisor_default));
        return getRoundedBitmapDrawable(context, bitmap, cornerRadius);
    }

    /**
     * Transforms a bitmap into a rounded bitmap drawable.
     *
     * @param context
     *         the application context
     * @param bitmap
     *         the bitmap to transform
     * @param cornerRadius
     *         the corner radius in px
     * @return the rounded bitmap drawable
     */
    public static RoundedBitmapDrawable getRoundedBitmapDrawable(Context context, Bitmap bitmap, float
            cornerRadius) {
        Resources res = context.getResources();
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
        dr.setCornerRadius(cornerRadius);
        return dr;
    }

    /**
     * Copies a bitmap's pixels to a byte array.
     *
     * @param bitmap
     *         the bitmap to put into the byte array
     * @return the byte array reflecting the bitmap
     */
    public static byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Creates a bitmap from a byte array.
     *
     * @param bytes
     *         the byte array to create the bitmap with
     * @return the newly created bitmap
     */
    public static Bitmap fromByteArray(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Loads a scaled bitmap from a file.
     *
     * @param path
     *         the path of the file to load
     * @param width
     *         the width of the scaled bitmap
     * @param height
     *         the height of the scaled bitmap
     * @param exactDimensions
     *         if false does less processing and only scales it so both dimensions remain equal or somewhat
     *         bigger than the specified dimensions
     * @return the scaled bitmap or null if the specified dimensions are zero
     */
    public static Bitmap fromFileScaled(String path, int width, int height, boolean exactDimensions) {
        if (width == 0 || height == 0) {
            return null;
        }
        BitmapFactory.Options orig = new BitmapFactory.Options();
        orig.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, orig);
        int scale = 1;
        while (orig.outWidth / (scale * 2) >= width && orig.outHeight / (scale * 2) >= height) {
            scale *= 2;
        }
        BitmapFactory.Options scaled = new BitmapFactory.Options();
        scaled.inSampleSize = scale;
        Bitmap scaledBitmap = BitmapFactory.decodeFile(path, scaled);
        if (exactDimensions) {
            return ThumbnailUtils.extractThumbnail(scaledBitmap, width, height, ThumbnailUtils
                    .OPTIONS_RECYCLE_INPUT);
        } else {
            return scaledBitmap;
        }
    }

    /**
     * Applies a Gaussian blur effect to a bitmap.
     *
     * @param context
     *         the application context
     * @param bitmap
     *         the input bitmap
     * @return a new blurred version of the input bitmap or null on any error
     */
    public static Bitmap blur(Context context, Bitmap bitmap) {
        int width = Math.round(bitmap.getWidth() / 2);
        int height = Math.round(bitmap.getHeight() / 2);
        Bitmap bitmapIn = null;
        Bitmap bitmapOut = null;
        try {
            bitmapIn = Bitmap.createScaledBitmap(bitmap, width, height, false);
            bitmapOut = Bitmap.createBitmap(bitmapIn);
            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation allocationIn = Allocation.createFromBitmap(rs, bitmapIn);
            Allocation allocationOut = Allocation.createFromBitmap(rs, bitmapOut);
            intrinsicBlur.setRadius(6);
            intrinsicBlur.setInput(allocationIn);
            intrinsicBlur.forEach(allocationOut);
            allocationOut.copyTo(bitmapOut);
        } catch (Exception e) {
            Log.e(BitmapUtil.class.getSimpleName(), "Error creating blurred image", e);
        } finally {
            if (bitmapIn != null && bitmapIn != bitmap && bitmapIn != bitmapOut) {
                bitmapIn.recycle();
            }
        }
        return bitmapOut;
    }

    /**
     * Applies a blur effect to a bitmap without using any native or support RenderScript library.
     *
     * @param bitmapIn
     *         the input bitmap
     * @param radius
     *         the blur radius (min 1)
     * @return a new blurred version of the input bitmap
     */
    public static Bitmap stackBlur(Bitmap bitmapIn, int radius) {
        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap = bitmapIn.copy(bitmapIn.getConfig(), true);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum;
        int gsum;
        int bsum;
        int x;
        int y;
        int i;
        int p;
        int yp;
        int yi = 0;
        int yw = 0;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = i / divsum;
        }

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum;
        int goutsum;
        int boutsum;
        int rinsum;
        int ginsum;
        int binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = p & 0x0000ff;
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = p & 0x0000ff;

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }

        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }

}
