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

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.StatFs;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Cache;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

/**
 * Utility class to handle and configure global caches.
 */
public class CacheUtil {

    public static final float PICASSO_CACHE_MEMORY_PERCENTAGE = 0.25f;
    public static final int PICASSO_CACHE_DISK_MB = 100;

    private static final int OBJECT_CACHE_DISK_MB = 10 * 1048576; // 10 MiB
    private static final String OBJECT_CACHE_DIR = "objectCache";
    private static final int INDEX_TIME_MODIFIED = 0;
    private static final int INDEX_OBJECT_DATA = 1;

    private static WeakReference<Cache> picassoMemoryCacheRef;
    private static WeakReference<com.squareup.okhttp.Cache> picassoDiskCacheRef;
    private static volatile DiskLruCache objectCache;

    private static volatile boolean picassoInitialized;

    /**
     * Gets an object from the disk cache. Disk access is blocking - do not run on the UI thread!
     *
     * If the object cache is not available or closed, attempts to set it up.
     *
     * @param context
     *         the application context
     * @param key
     *         the ID associated with the object
     * @param maxAge
     *         the amount of seconds after which the object expires
     * @return the object if found in the cache, null else
     */
    public static Object getFromDiskCache(Context context, String key, long maxAge) {
        if (objectCache == null || objectCache.isClosed()) {
            // set up cache
            try {
                setupDiskLruCache(new File(context.getFilesDir().getAbsolutePath() + File.separator +
                        OBJECT_CACHE_DIR), OBJECT_CACHE_DISK_MB);
            } catch (IOException e) {
                Log.w(CacheUtil.class.getSimpleName(), "Error setting up object disk LRU cache", e);
                return null;
            }
        }
        try {
            // look up object
            DiskLruCache.Snapshot snapshot = objectCache.get(key);
            if (snapshot == null) {
                // object not in cache
                return null;
            }
            // check if cached object is older than allowed
            String timeModifiedString = snapshot.getString(INDEX_TIME_MODIFIED);
            long timeModified = Long.valueOf(timeModifiedString);
            long timeCurrent = System.currentTimeMillis();
            if (timeCurrent - timeModified > maxAge * 1000) {
                return null;
            }
            // deserialize and return object
            InputStream inputStream = snapshot.getInputStream(INDEX_OBJECT_DATA);
            ObjectInput objectInput = new ObjectInputStream(inputStream);
            return objectInput.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Writes an object to the disk cache. Disk access is blocking - do not run on the UI thread!
     *
     * If the object cache is not available or closed, attempts to set it up.
     *
     * @param context
     *         the application context
     * @param key
     *         the ID to be associated with the object
     * @param object
     *         the object to save
     */
    public static void saveToDiskCache(Context context, String key, Object object) {
        if (objectCache == null || objectCache.isClosed()) {
            // set up cache
            try {
                setupDiskLruCache(new File(context.getFilesDir().getAbsolutePath() + File.separator +
                        OBJECT_CACHE_DIR), OBJECT_CACHE_DISK_MB);
            } catch (IOException e) {
                Log.w(CacheUtil.class.getSimpleName(), "Error setting up object disk LRU cache", e);
                return;
            }
        }
        try {
            DiskLruCache.Editor editor = objectCache.edit(key);
            if (editor == null) {
                // entry is currently being edited
                return;
            }
            long timeCurrent = System.currentTimeMillis();
            editor.set(INDEX_TIME_MODIFIED, String.valueOf(timeCurrent));
            OutputStream outputStream = editor.newOutputStream(INDEX_OBJECT_DATA);
            ObjectOutput objectOutput = new ObjectOutputStream(outputStream);
            objectOutput.writeObject(object);
            editor.commit();
        } catch (Exception e) {
            Log.w(CacheUtil.class.getSimpleName(), "Error writing object to cache", e);
        }
    }

    /**
     * Sets up the disk LRU cache for objects.
     *
     * @param cacheDir
     *         the directory to store the cache in
     * @param maxBytes
     *         the maximum amount of bytes the cache may grow to
     * @throws IOException
     *         on failure to open or create the cache
     */
    public static synchronized void setupDiskLruCache(File cacheDir, long maxBytes) throws IOException {
        if (objectCache == null || objectCache.isClosed()) {
            //noinspection ResultOfMethodCallIgnored
            cacheDir.mkdirs();
            objectCache = DiskLruCache.open(cacheDir, 0, 2, maxBytes);
        }
    }

    /**
     * Closes the disk LRU cache for objects.
     */
    public static synchronized void closeDiskLruCache() {
        if (objectCache != null && !objectCache.isClosed()) {
            try {
                objectCache.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Call in {@link android.app.Activity#onStart} to have the object cache set up in the background.
     *
     * @param context
     *         the application context
     * @see #setupDiskLruCache
     */
    public static void onStart(final Context context) {
        final String path = context.getFilesDir().getAbsolutePath() + File.separator + OBJECT_CACHE_DIR;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setupDiskLruCache(new File(path), OBJECT_CACHE_DISK_MB);
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    /**
     * Call in {@link android.app.Activity#onStop} to have the object cache closed.
     *
     * @see #closeDiskLruCache
     */
    public static void onStop() {
        closeDiskLruCache();
    }

    /**
     * Sets up the Picasso singleton instance. Must be called before any calls to Picasso and can only be
     * called once. If this method is not called, Picasso will be using default configuration parameters:
     * using 15% of the memory available to the application as memory cache, using 2% of free disk space (min.
     * 50MB), and no debugging output.
     *
     * This call may access the disk and thus may be expensive.
     *
     * @param context
     *         the application context
     * @param memoryCache
     *         the percentage of the memory available to the application to use as cache (null to keep default
     *         or previously set percentage)
     * @param diskCache
     *         the amount of disk space in MB to use as cache (null to keep default or previously set amount)
     * @param debug
     *         true to turn on debug indicators on images and debug output, false to disable both (null to
     *         keep default or previously set value)
     */
    public static void setupPicassoInstance(Context context, Float memoryCache, Integer diskCache, Boolean
            debug) {
        if (picassoInitialized) {
            return;
        }
        picassoInitialized = true;
        Picasso.Builder builder = new Picasso.Builder(context);
        if (memoryCache != null) {
            int maxSize = Math.round(getAvailableMemory(context) * memoryCache);
            Cache lruCache = new LruCache(maxSize);
            picassoMemoryCacheRef = new WeakReference<Cache>(lruCache);
            builder.memoryCache(lruCache);
            Log.d(CacheUtil.class.getSimpleName(), "Picasso Memory Cache set to " + maxSize + " " +
                    "bytes (" + (memoryCache * 100) + "% of total)");
        }
        OkHttpClient okHttpClient = null;
        if (diskCache != null) {
            long maxSize = diskCache * 1048576;
            try {
                createPicassoDiskCache(context, maxSize);
                com.squareup.okhttp.Cache c = picassoDiskCacheRef.get();
                if (c != null) {
                    okHttpClient = new OkHttpClient();
                    okHttpClient.setCache(c);
                    Log.d(CacheUtil.class.getSimpleName(), "Picasso Disk Cache set to " + maxSize);
                }
            } catch (IOException e) {
                Log.w(CacheUtil.class.getSimpleName(), "Failed to create Picasso disk cache", e);
            }
        }
        if (okHttpClient != null) {
            builder.downloader(new OkHttpDownloader(okHttpClient));
        }
        if (debug != null) {
            builder.indicatorsEnabled(debug);
        }
        //builder.indicatorsEnabled(true);
        //builder.loggingEnabled(true);
        Picasso picasso = builder.build();
        Picasso.setSingletonInstance(picasso);
    }

    /**
     * Clears Picasso's memory cache. Works only if Picasso has been initialized using {@link
     * #setupPicassoInstance}.
     */
    public static void clearPicassoMemoryCache() {
        Cache cache = picassoMemoryCacheRef.get();
        if (cache == null) {
            Log.w(CacheUtil.class.getSimpleName(), "Picasso memory cache unavailable - clearing failed");
            return;
        }
        cache.clear();
    }

    /**
     * Clears the disk cache used by Picasso's OkHttpDownloader. Works only if Picasso has been initialized
     * using {@link #setupPicassoInstance}.
     *
     * This call accesses the disk and is expensive.
     */
    public static void clearPicassoDiskCache() {
        com.squareup.okhttp.Cache diskCache = picassoDiskCacheRef.get();
        if (diskCache == null) {
            Log.w(CacheUtil.class.getSimpleName(), "Picasso disk cache unavailable - clearing failed");
            return;
        }
        try {
            diskCache.evictAll();
        } catch (IOException e) {
            Log.w(CacheUtil.class.getSimpleName(), e);
        }
    }

    /**
     * Creates and initializes the disk cache used by Picasso's OkHttpDownloader with the specified size
     * limited by half of the system's free space.
     *
     * This call accesses the disk and is expensive.
     *
     * @param context
     *         the app context
     * @param maxSize
     *         the maximum size in bytes the cache may grow to
     * @throws IOException
     *         on any access error
     */
    private static void createPicassoDiskCache(Context context, long maxSize) throws IOException {
        File httpCacheDir = new File(context.getCacheDir(), "picasso-cache");
        //noinspection ResultOfMethodCallIgnored
        httpCacheDir.mkdirs();
        long freeDiskSpace = getFreeDiskSpace(httpCacheDir);
        maxSize = Math.min(maxSize, freeDiskSpace / 2);
        com.squareup.okhttp.Cache diskCache = new com.squareup.okhttp.Cache(httpCacheDir, maxSize);
        diskCache.initialize();
        picassoDiskCacheRef = new WeakReference<>(diskCache);
    }

    /**
     * Gets the amount of memory available to the application.
     *
     * Queries whether the largeHeap manifest flag is set.
     *
     * @param context
     *         the application context
     * @return the amount of memory in bytes available to the application
     */
    private static int getAvailableMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context
                .ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & 1048576) != 0;
        int memoryClass = largeHeap ? activityManager.getLargeMemoryClass() : activityManager
                .getMemoryClass();
        return memoryClass * 1048576;
    }

    /**
     * Gets the free disk space at the specified location.
     *
     * @param dir
     *         the directory linking to the filesystem to query
     * @return the free disk space at {@code dir} in bytes
     */
    private static long getFreeDiskSpace(File dir) {
        StatFs statFs = new StatFs(dir.getAbsolutePath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return statFs.getBlockCountLong() * statFs.getBlockSizeLong();
        } else {
            //noinspection deprecation
            return statFs.getBlockCount() * statFs.getBlockSize();
        }
    }

}
