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

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Utility class to conveniently handle files in combination with different storage sections in Android.
 */
public class StorageUtil {

    /**
     * Copies data from an input stream to an output file.
     *
     * @param is
     *         the source input stream
     * @param dst
     *         the destination file
     * @return true on success, false on any error
     */
    public static boolean copyFile(InputStream is, File dst) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(dst);
            byte[] buffer = new byte[102400];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            Log.w(StorageUtil.class.getSimpleName(), e);
            return false;
        } catch (IOException e) {
            Log.w(StorageUtil.class.getSimpleName(), e);
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ignored) {
            }
        }
        return true;
    }

    /**
     * Copies a file from one place to another. May be used to copy files between external and internal
     * storage.
     *
     * @param src
     *         the file to copy
     * @param dst
     *         the target file
     * @param move
     *         true to delete the source file after successfully copying it
     * @return true on success, false on any error
     */
    public static boolean copyFile(File src, File dst, boolean move) {
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dst).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (Exception e) {
            Log.e(StorageUtil.class.getSimpleName(), "Error copying file " + src.getPath() + " to " + dst
                    .getPath());
            return false;
        } finally {
            try {
                if (inChannel != null) {
                    inChannel.close();
                }
                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (Exception ignored) {
            }
        }
        if (move) {
            deleteFile(src);
        }
        return true;
    }

    /**
     * Deletes a file and logs the result.
     *
     * @param file
     *         the file to delete
     */
    public static void deleteFile(File file) {
        if (file.delete()) {
            Log.d(StorageUtil.class.getSimpleName(), "Deleted file at " + file.getPath());
        } else {
            Log.w(StorageUtil.class.getSimpleName(), "Unable to delete file at " + file.getPath());
        }
    }

    /**
     * @return the shared external directory for the application
     */
    public static File getExternalDir() {
        File ext = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File plyExt = new File(ext, "PLY" + File.separator);
        //noinspection ResultOfMethodCallIgnored
        plyExt.mkdirs();
        return plyExt;
    }
}
