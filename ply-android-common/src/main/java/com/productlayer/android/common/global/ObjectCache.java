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

package com.productlayer.android.common.global;

import android.content.Context;
import android.util.Log;

import com.productlayer.android.common.util.CacheUtil;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.CategoryService;
import com.productlayer.android.sdk.services.ProductService;
import com.productlayer.core.beans.Category;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * Methods to retrieve and cache seldomly changing and globally used data retrieved from the ProductLayer
 * API.
 */
public class ObjectCache {

    private static final String TAG_CATEGORIES = "categories";
    private static final int MAX_AGE_CATEGORIES = 7 * 86400; // 7 days

    private static final String TAG_BRANDS = "brands";
    private static final int MAX_AGE_BRANDS = 86400; // 1 day

    private static final String TAG_BRAND_OWNERS = "brand_owners";
    private static final int MAX_AGE_BRAND_OWNERS = 86400; // 1 day

    private static Map<String, Object> memCache = new ConcurrentHashMap<String, Object>();

    /**
     * Gets all categories either from the (memory or disk) cache or remotely using the ProductLayer API. The
     * latter saves the retrieved categories in the local cache.
     *
     * Both disk and network access are blocking - do not run on the UI thread unless you know what you are
     * doing (i.e. variable needs to be available and is sure to have been cached in memory)!
     *
     * @param context
     *         the application context
     * @param client
     *         the ProductLayer Android SDK client
     * @param fromCacheOnly
     *         true to check the cache only and to not run any network query
     * @param forceRefresh
     *         true to skip checking the local cache
     * @return an array of all categories or null on any error
     */
    public static Category[] getCategories(final Context context, final PLYAndroid client, boolean
            fromCacheOnly, boolean forceRefresh) {
        Callable<Future<Category[]>> serviceCall = null;
        if (!fromCacheOnly) {
            serviceCall = new Callable<Future<Category[]>>() {
                @Override
                public Future<Category[]> call() throws Exception {
                    return CategoryService.getMainCategories(client, null, new PLYCompletion<Category[]>() {
                        @Override
                        public void onSuccess(Category[] result) {
                            Log.d("GetCategories", "Retrieved " + result.length + " categories");
                        }

                        @Override
                        public void onError(PLYAndroid.QueryError error) {
                            Log.d("GetCategories", error.getMessage());
                        }
                    });
                }
            };
        }
        return get(context, fromCacheOnly, forceRefresh, TAG_CATEGORIES, MAX_AGE_CATEGORIES, serviceCall);
    }

    /**
     * Gets all brand names either from the (memory or disk) cache or remotely using the ProductLayer API. The
     * latter saves the retrieved brand names in the local cache.
     *
     * Both disk and network access are blocking - do not run on the UI thread unless you know what you are
     * doing (i.e. variable needs to be available and is sure to have been cached in memory)!
     *
     * @param context
     *         the application context
     * @param client
     *         the ProductLayer Android SDK client
     * @param fromCacheOnly
     *         true to check the cache only and to not run any network query
     * @param forceRefresh
     *         true to skip checking the local cache
     * @return an array of all brand names or null on any error
     */
    public static String[] getBrands(final Context context, final PLYAndroid client, boolean fromCacheOnly,
            boolean forceRefresh) {
        Callable<Future<String[]>> serviceCall = null;
        if (!fromCacheOnly) {
            serviceCall = new Callable<Future<String[]>>() {
                @Override
                public Future<String[]> call() throws Exception {
                    return ProductService.getBrands(client, new PLYCompletion<String[]>() {
                        @Override
                        public void onSuccess(String[] result) {
                            Log.d("GetBrands", "Retrieved " + result.length + " brand names");
                        }

                        @Override
                        public void onError(PLYAndroid.QueryError error) {
                            Log.d("GetBrands", error.getMessage());
                        }
                    });
                }
            };
        }
        return get(context, fromCacheOnly, forceRefresh, TAG_BRANDS, MAX_AGE_BRANDS, serviceCall);
    }

    /**
     * Gets all brand owner names either from the (memory or disk) cache or remotely using the ProductLayer
     * API. The latter saves the retrieved brand owner names in the local cache.
     *
     * Both disk and network access are blocking - do not run on the UI thread unless you know what you are
     * doing (i.e. variable needs to be available and is sure to have been cached in memory)!
     *
     * @param context
     *         the application context
     * @param client
     *         the ProductLayer Android SDK client
     * @param fromCacheOnly
     *         true to check the cache only and to not run any network query
     * @param forceRefresh
     *         true to skip checking the local cache
     * @return an array of all brand owner names or null on any error
     */
    public static String[] getBrandOwners(final Context context, final PLYAndroid client, boolean
            fromCacheOnly, boolean forceRefresh) {
        Callable<Future<String[]>> serviceCall = null;
        if (!fromCacheOnly) {
            serviceCall = new Callable<Future<String[]>>() {
                @Override
                public Future<String[]> call() throws Exception {
                    return ProductService.getBrandOwners(client, new PLYCompletion<String[]>() {
                        @Override
                        public void onSuccess(String[] result) {
                            Log.d("GetBrandOwners", "Retrieved " + result.length + " brand owner names");
                        }

                        @Override
                        public void onError(PLYAndroid.QueryError error) {
                            Log.d("GetBrandOwners", error.getMessage());
                        }
                    });
                }
            };
        }
        return get(context, fromCacheOnly, forceRefresh, TAG_BRAND_OWNERS, MAX_AGE_BRAND_OWNERS, serviceCall);
    }

    /**
     * Gets an object either from the (memory or disk) cache or remotely using the ProductLayer API. The
     * latter saves the retrieved object in the local cache.
     *
     * Both disk and network access are blocking - do not run on the UI thread unless you know what you are
     * doing (i.e. variable needs to be available and is sure to have been cached in memory)!
     *
     * @param context
     *         the application context
     * @param fromCacheOnly
     *         true to check the cache only and to not run any network query
     * @param forceRefresh
     *         true to skip checking the local cache
     * @param tag
     *         the unique tag associated with the result
     * @param maxAge
     *         the amount of seconds after which the object expires
     * @param serviceCall
     *         the call to the SDK service returning a Future
     * @param <T>
     *         the type of the result
     * @return the cached or remotely retrieved object or null on any error
     */
    private static <T> T get(final Context context, boolean fromCacheOnly, boolean forceRefresh, final
    String tag, long maxAge, Callable<Future<T>> serviceCall) {
        T object = null;
        if (!forceRefresh) {
            // check if already looked up
            if (memCache.containsKey(tag)) {
                Log.d(ObjectCache.class.getSimpleName(), "Requested " + tag + " from memory");
                //noinspection unchecked
                return (T) memCache.get(tag);
            }
            // look up in disk cache
            try {
                //noinspection unchecked
                object = (T) CacheUtil.getFromDiskCache(context, tag, maxAge);
            } catch (Exception e) {
                Log.w(ObjectCache.class.getSimpleName(), "Error getting " + tag + " from cache", e);
            }
            if (object != null) {
                Log.d(ObjectCache.class.getSimpleName(), "Requested " + tag + " from disk cache");
                memCache.put(tag, object);
                return object;
            }
            if (!fromCacheOnly) {
                Log.d(ObjectCache.class.getSimpleName(), tag + " not found in cache, doing remote retrieval");
            }
        }
        if (fromCacheOnly) {
            Log.d(ObjectCache.class.getSimpleName(), tag + " not found in cache, returning null");
            return null;
        }
        // remote retrieval
        try {
            Future<T> objectFuture = serviceCall.call();
            object = objectFuture.get();
        } catch (Exception e) {
            return null;
        }
        if (object == null) {
            return null;
        }
        // save in local cache in the background
        final T finalObject = object;
        new Thread(new Runnable() {
            @Override
            public void run() {
                CacheUtil.saveToDiskCache(context, tag, finalObject);
            }
        }).start();
        memCache.put(tag, object);
        return object;
    }
}
