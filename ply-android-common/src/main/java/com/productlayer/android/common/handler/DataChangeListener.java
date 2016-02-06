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

package com.productlayer.android.common.handler;

import android.util.Log;

import com.productlayer.core.beans.Opine;
import com.productlayer.core.beans.Product;
import com.productlayer.core.beans.ProductImage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Global listeners for product/opinion creations and updates to keep screens up to date if the user changes
 * any data.
 */
public class DataChangeListener {

    private static final int GC_EACH_CNT_CALL = 100;

    private static final Collection<WeakReference<OnProductCreateListener>> onProductCreateListeners = new
            ArrayList<WeakReference<OnProductCreateListener>>();
    private static final Collection<WeakReference<OnProductUpdateListener>> onProductUpdateListeners = new
            ArrayList<WeakReference<OnProductUpdateListener>>();
    private static final Collection<WeakReference<OnOpinionCreateListener>> onOpinionCreateListeners = new
            ArrayList<WeakReference<OnOpinionCreateListener>>();
    private static final Collection<WeakReference<OnOpinionUpdateListener>> onOpinionUpdateListeners = new
            ArrayList<WeakReference<OnOpinionUpdateListener>>();
    private static final Collection<WeakReference<OnImageCreateListener>> onImageCreateListeners = new
            ArrayList<WeakReference<OnImageCreateListener>>();
    private static final Collection<WeakReference<OnImageUpdateListener>> onImageUpdateListeners = new
            ArrayList<WeakReference<OnImageUpdateListener>>();

    private static int cntCallForGc = 0;

    /**
     * Adds a listener to be notified of product creations.
     *
     * Only a weak reference to the listener is kept - maintain a strong reference to avoid garbage collection
     * of the listener.
     *
     * @param onProductCreateListener
     *         the listener to add
     */
    public static void addOnProductCreateListener(OnProductCreateListener onProductCreateListener) {
        checkGc();
        synchronized (onProductCreateListeners) {
            onProductCreateListeners.add(new WeakReference<OnProductCreateListener>(onProductCreateListener));
        }
    }

    /**
     * Removes the specified listener.
     *
     * @param onProductCreateListener
     *         the listener to remove
     */
    public static void removeOnProductCreateListener(OnProductCreateListener onProductCreateListener) {
        synchronized (onProductCreateListeners) {
            removeFromWeakReferences(onProductCreateListeners, onProductCreateListener);
        }
    }

    /**
     * Adds a listener to be notified of product updates.
     *
     * Only a weak reference to the listener is kept - maintain a strong reference to avoid garbage collection
     * of the listener.
     *
     * @param onProductUpdateListener
     *         the listener to add
     */
    public static void addOnProductUpdateListener(OnProductUpdateListener onProductUpdateListener) {
        checkGc();
        synchronized (onProductUpdateListeners) {
            onProductUpdateListeners.add(new WeakReference<OnProductUpdateListener>(onProductUpdateListener));
        }
    }

    /**
     * Removes the specified listener.
     *
     * @param onProductUpdateListener
     *         the listener to remove
     */
    public static void removeOnProductUpdateListener(OnProductUpdateListener onProductUpdateListener) {
        synchronized (onProductUpdateListeners) {
            removeFromWeakReferences(onProductUpdateListeners, onProductUpdateListener);
        }
    }

    /**
     * Adds a listener to be notified of opinion creations.
     *
     * Only a weak reference to the listener is kept - maintain a strong reference to avoid garbage collection
     * of the listener.
     *
     * @param onOpinionCreateListener
     *         the listener to add
     */
    public static void addOnOpinionCreateListener(OnOpinionCreateListener onOpinionCreateListener) {
        checkGc();
        synchronized (onOpinionCreateListeners) {
            onOpinionCreateListeners.add(new WeakReference<OnOpinionCreateListener>(onOpinionCreateListener));
        }
    }

    /**
     * Removes the specified listener.
     *
     * @param onOpinionCreateListener
     *         the listener to remove
     */
    public static void removeOnOpinionCreateListener(OnOpinionCreateListener onOpinionCreateListener) {
        synchronized (onOpinionCreateListeners) {
            removeFromWeakReferences(onOpinionCreateListeners, onOpinionCreateListener);
        }
    }

    /**
     * Adds a listener to be notified of opinion updates.
     *
     * Only a weak reference to the listener is kept - maintain a strong reference to avoid garbage collection
     * of the listener.
     *
     * @param onOpinionUpdateListener
     *         the listener to add
     */
    public static void addOnOpinionUpdateListener(OnOpinionUpdateListener onOpinionUpdateListener) {
        checkGc();
        synchronized (onOpinionUpdateListeners) {
            onOpinionUpdateListeners.add(new WeakReference<OnOpinionUpdateListener>(onOpinionUpdateListener));
        }
    }

    /**
     * Removes the specified listener.
     *
     * @param onOpinionUpdateListener
     *         the listener to remove
     */
    public static void removeOnOpinionUpdateListener(OnOpinionUpdateListener onOpinionUpdateListener) {
        synchronized (onOpinionUpdateListeners) {
            removeFromWeakReferences(onOpinionUpdateListeners, onOpinionUpdateListener);
        }
    }

    /**
     * Adds a listener to be notified of image creations.
     *
     * Only a weak reference to the listener is kept - maintain a strong reference to avoid garbage collection
     * of the listener.
     *
     * @param onImageCreateListener
     *         the listener to add
     */
    public static void addOnImageCreateListener(OnImageCreateListener onImageCreateListener) {
        checkGc();
        synchronized (onImageCreateListeners) {
            onImageCreateListeners.add(new WeakReference<OnImageCreateListener>(onImageCreateListener));
        }
    }

    /**
     * Removes the specified listener.
     *
     * @param onImageCreateListener
     *         the listener to remove
     */
    public static void removeOnImageCreateListener(OnImageCreateListener onImageCreateListener) {
        synchronized (onImageCreateListeners) {
            removeFromWeakReferences(onImageCreateListeners, onImageCreateListener);
        }
    }

    /**
     * Adds a listener to be notified of image updates.
     *
     * Only a weak reference to the listener is kept - maintain a strong reference to avoid garbage collection
     * of the listener.
     *
     * @param onImageUpdateListener
     *         the listener to add
     */
    public static void addOnImageUpdateListener(OnImageUpdateListener onImageUpdateListener) {
        checkGc();
        synchronized (onImageUpdateListeners) {
            onImageUpdateListeners.add(new WeakReference<OnImageUpdateListener>(onImageUpdateListener));
        }
    }

    /**
     * Removes the specified listener.
     *
     * @param onImageUpdateListener
     *         the listener to remove
     */
    public static void removeOnImageUpdateListener(OnImageUpdateListener onImageUpdateListener) {
        synchronized (onImageUpdateListeners) {
            removeFromWeakReferences(onImageUpdateListeners, onImageUpdateListener);
        }
    }

    /**
     * Notifies all registered {@code OnProductCreateListener}s of a new product.
     *
     * @param product
     *         the product that has been created
     */
    public static void productCreate(Product product) {
        synchronized (onProductCreateListeners) {
            for (WeakReference<OnProductCreateListener> listenerRef : onProductCreateListeners) {
                OnProductCreateListener listener = listenerRef.get();
                if (listener != null && !listenerRef.isEnqueued()) {
                    listener.onProductCreate(product);
                }
            }
        }
    }

    /**
     * Notifies all registered {@code OnProductUpdateListener}s of an updated product.
     *
     * @param product
     *         the product that has been updated (in its updated state)
     */
    public static void productUpdate(Product product) {
        synchronized (onProductUpdateListeners) {
            for (WeakReference<OnProductUpdateListener> listenerRef : onProductUpdateListeners) {
                OnProductUpdateListener listener = listenerRef.get();
                if (listener != null && !listenerRef.isEnqueued()) {
                    listener.onProductUpdate(product);
                }
            }
        }
    }

    /**
     * Notifies all registered {@code OnOpinionCreateListener}s of a new opinion.
     *
     * @param opinion
     *         the opinion that has been created
     */
    public static void opinionCreate(Opine opinion) {
        synchronized (onOpinionCreateListeners) {
            for (WeakReference<OnOpinionCreateListener> listenerRef : onOpinionCreateListeners) {
                OnOpinionCreateListener listener = listenerRef.get();
                if (listener != null && !listenerRef.isEnqueued()) {
                    listener.onOpinionCreate(opinion);
                }
            }
        }
    }

    /**
     * Notifies all registered {@code OnOpinionUpdateListener}s of an updated opinion.
     *
     * @param opinion
     *         the opinion that has been updated (in its updated state)
     */
    public static void opinionUpdate(Opine opinion) {
        synchronized (onOpinionUpdateListeners) {
            for (WeakReference<OnOpinionUpdateListener> listenerRef : onOpinionUpdateListeners) {
                OnOpinionUpdateListener listener = listenerRef.get();
                if (listener != null && !listenerRef.isEnqueued()) {
                    listener.onOpinionUpdate(opinion);
                }
            }
        }
    }

    /**
     * Notifies all registered {@code OnImageCreateListener}s of a new image.
     *
     * @param image
     *         the image that has been created
     */
    public static void imageCreate(ProductImage image) {
        synchronized (onImageCreateListeners) {
            for (WeakReference<OnImageCreateListener> listenerRef : onImageCreateListeners) {
                OnImageCreateListener listener = listenerRef.get();
                if (listener != null && !listenerRef.isEnqueued()) {
                    listener.onImageCreate(image);
                }
            }
        }
    }

    /**
     * Notifies all registered {@code OnImageUpdateListener}s of an updated image.
     *
     * @param image
     *         the image that has been updated (in its updated state)
     */
    public static void imageUpdate(ProductImage image) {
        synchronized (onImageUpdateListeners) {
            for (WeakReference<OnImageUpdateListener> listenerRef : onImageUpdateListeners) {
                OnImageUpdateListener listener = listenerRef.get();
                if (listener != null && !listenerRef.isEnqueued()) {
                    listener.onImageUpdate(image);
                }
            }
        }
    }

    /**
     * Removes an object from a list of objects contained in weak references.
     *
     * @param references
     *         the list to remove the object encapsulated in a weak reference from
     * @param object
     *         the object to search the weak references for
     * @param <T>
     *         the type of object
     */
    public static <T> void removeFromWeakReferences(Iterable<WeakReference<T>> references, T object) {
        for (Iterator<WeakReference<T>> iterator = references.iterator(); iterator.hasNext(); ) {
            WeakReference<T> reference = iterator.next();
            if (reference.get() == object) {
                iterator.remove();
            }
        }
    }

    /**
     * Increases the count of calls and, once it reaches {@link #GC_EACH_CNT_CALL}, removes from all lists of
     * listeners those that have been garbage collected or are enqueued.
     */
    private static void checkGc() {
        cntCallForGc++;
        if (cntCallForGc >= GC_EACH_CNT_CALL) {
            cntCallForGc = 0;
            Log.v(DataChangeListener.class.getSimpleName(), "Starting garbage collection of dead listeners");
            synchronized (onProductCreateListeners) {
                gcDeadReferences(onProductCreateListeners);
            }
            synchronized (onProductUpdateListeners) {
                gcDeadReferences(onProductUpdateListeners);
            }
            synchronized (onOpinionCreateListeners) {
                gcDeadReferences(onOpinionCreateListeners);
            }
            synchronized (onOpinionUpdateListeners) {
                gcDeadReferences(onOpinionUpdateListeners);
            }
            synchronized (onImageCreateListeners) {
                gcDeadReferences(onImageCreateListeners);
            }
            synchronized (onImageUpdateListeners) {
                gcDeadReferences(onImageUpdateListeners);
            }
        }
    }

    /**
     * Removes from a list of weak references those that have been garbage collected or are enqueued.
     *
     * @param references
     *         the list of weak references to go through
     */
    private static <T> void gcDeadReferences(Iterable<WeakReference<T>> references) {
        for (Iterator<WeakReference<T>> iterator = references.iterator(); iterator.hasNext(); ) {
            WeakReference reference = iterator.next();
            if (reference.get() == null || reference.isEnqueued()) {
                iterator.remove();
            }
        }
    }

    /**
     * Listens for new products.
     */
    public interface OnProductCreateListener {
        void onProductCreate(Product product);
    }

    /**
     * Listens for updated products.
     */
    public interface OnProductUpdateListener {
        void onProductUpdate(Product product);
    }

    /**
     * Listens for new opinions.
     */
    public interface OnOpinionCreateListener {
        void onOpinionCreate(Opine opinion);
    }

    /**
     * Listens for updated opinions.
     */
    public interface OnOpinionUpdateListener {
        void onOpinionUpdate(Opine opinion);
    }

    /**
     * Listens for new images.
     */
    public interface OnImageCreateListener {
        void onImageCreate(ProductImage image);
    }

    /**
     * Listens for updated images.
     */
    public interface OnImageUpdateListener {
        void onImageUpdate(ProductImage image);
    }
}
