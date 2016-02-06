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

package com.productlayer.android.common.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.productlayer.android.common.R;
import com.productlayer.android.common.global.LoadingIndicator;
import com.productlayer.android.common.handler.DataChangeListener;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.common.view.ImagePreview;
import com.productlayer.android.common.view.OpinionView;
import com.productlayer.android.common.view.ProductPreview;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.ImageService;
import com.productlayer.android.sdk.services.ProductService;
import com.productlayer.android.sdk.services.TimelineService;
import com.productlayer.core.beans.BaseObject;
import com.productlayer.core.beans.Opine;
import com.productlayer.core.beans.Product;
import com.productlayer.core.beans.ProductImage;
import com.productlayer.core.beans.ResultSetWithCursor;
import com.productlayer.core.beans.SimpleUserInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides the data feed of product information created by the ProductLayer community as an adapter to be
 * attached to a RecyclerView.
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.FeedItemHolder> {

    private final Activity activity;
    private final NavigationHandler navigationHandler;
    private final UserHandler userHandler;
    private final PLYAndroid client;

    private Retrieval retrieval;

    private int loadItems;
    private int settings;

    private int itemWidthPx;
    private int imageMaxHeightPx;
    private int avatarSizePx;

    private List<BaseObject> feedItems = new ArrayList<BaseObject>();
    private String[] feedUrls = new String[TimeRel.values().length];

    // listeners would be garbage-collected without reference
    private DataChangeListener.OnProductCreateListener onProductCreateListener;
    private DataChangeListener.OnProductUpdateListener onProductUpdateListener;
    private DataChangeListener.OnOpinionCreateListener onOpinionCreateListener;
    private DataChangeListener.OnOpinionUpdateListener onOpinionUpdateListener;
    private DataChangeListener.OnImageCreateListener onImageCreateListener;
    private DataChangeListener.OnImageUpdateListener onImageUpdateListener;

    private AtomicBoolean loading = new AtomicBoolean(false);

    /**
     * Creates the social feed adapter and starts to retrieve the first batch of data.
     *
     * Use {@link #setItemDimensions} before attaching the adapter to a view. Use {@link #setupListeners} and
     * {@link #destroyListeners} to listen for data changes by the user.
     *
     * @param activity
     *         the activity the adapter's view is attached to
     * @param navigationHandler
     *         the activity's navigation handler to open new screens
     * @param userHandler
     *         the activity's user handler to get current user information
     * @param client
     *         the PLYAndroid REST client to use for querying for data
     * @param retrieval
     *         configured call to retrieve the initial set of items
     * @param loadItems
     *         the amount of items to load in one batch
     * @param settings
     *         the display settings for this timeline
     */
    public TimelineAdapter(Activity activity, NavigationHandler navigationHandler, UserHandler userHandler,
            PLYAndroid client, Retrieval retrieval, int loadItems, int settings) {
        this.activity = activity;
        this.navigationHandler = navigationHandler;
        this.userHandler = userHandler;
        this.client = client;
        this.retrieval = retrieval;
        this.loadItems = loadItems;
        this.settings = settings;
        setHasStableIds(true);
        retrieveFeedItems(TimeRel.INITIAL);
    }

    /**
     * Compares an updated or new image to the current default image of a product, checking if it would make
     * the new default image.
     *
     * @param currentDefault
     *         the product's current default image
     * @param image
     *         the new or updated image
     * @return 1 if the specified image is the new default image of the product; -1 if the specified image is
     * the current default and was down-voted (to trigger updates even though it might still remain the
     * default image); 0 else
     */
    public static int triggersDefaultImageChange(ProductImage currentDefault, ProductImage image) {
        if (currentDefault == null) {
            // no default image yet - new image is the new default
            return 1;
        }
        List<SimpleUserInfo> currentUpVoters = currentDefault.getUpVoters();
        List<SimpleUserInfo> currentDownVoters = currentDefault.getDownVoters();
        int cntCurrentUpVoters = currentUpVoters == null ? 0 : currentUpVoters.size();
        int cntCurrentDownVoters = currentDownVoters == null ? 0 : currentDownVoters.size();
        List<SimpleUserInfo> newUpVoters = image.getUpVoters();
        List<SimpleUserInfo> newDownVoters = image.getDownVoters();
        int cntNewUpVoters = newUpVoters == null ? 0 : newUpVoters.size();
        int cntNewDownVoters = newDownVoters == null ? 0 : newDownVoters.size();
        int scoreDifference = cntNewUpVoters - cntNewDownVoters - (cntCurrentUpVoters - cntCurrentDownVoters);
        if (currentDefault.getId().equals(image.getId())) {
            // current default image was downvoted - another image may become default (else no change)
            return scoreDifference < 0 ? -1 : 0;
        }
        if (scoreDifference > 0) {
            // new image has more upvoters and is the new default
            return 1;
        }
        if (scoreDifference == 0) {
            if (image.getCreated() > currentDefault.getCreated()) {
                // new image has same amount of upvoters and is newer making it the new default
                return 1;
            }
        }
        return 0;
    }

    /**
     * Removes an item with the specified ID of a list of feed items if found.
     *
     * @param items
     *         the list of feed items
     * @param id
     *         the ID of the object to remove
     */
    private static void filterItem(Iterable<BaseObject> items, String id) {
        Iterator<BaseObject> it = items.iterator();
        while (it.hasNext()) {
            BaseObject obj = it.next();
            if (id.equals(obj.getId())) {
                it.remove();
                break;
            }
        }
    }

    /**
     * Sets the fixed width of items and the maximum height of images. Images are retrieved in {@link
     * #onBindViewHolder(FeedItemHolder, int)} in the dimensions set here.
     *
     * @param itemWidthPx
     *         the width of an item
     * @param imageMaxHeightPx
     *         the maximum height of an image
     * @param avatarSizePx
     *         the width/height of the avatar of an item's author
     */
    public void setItemDimensions(int itemWidthPx, int imageMaxHeightPx, int avatarSizePx) {
        this.itemWidthPx = itemWidthPx;
        this.imageMaxHeightPx = imageMaxHeightPx;
        this.avatarSizePx = avatarSizePx;
    }

    /**
     * Retrieves items from the timeline.
     *
     * @param timeRel
     *         whether to retrieve the initial batch of items, or earlier or later items
     * @return false if a previous retrieval is still ongoing, true else
     */
    public boolean retrieveFeedItems(TimeRel timeRel) {
        if (!loading.compareAndSet(false, true)) {
            // TODO reset loading if true for a long time (might happen due to orientation changes)
            // already retrieving items
            return false;
        }
        final TimeRel finalTimeRel = feedItems.isEmpty() ? TimeRel.INITIAL : timeRel;
        Log.d(getClass().getSimpleName(), "Retrieving " + finalTimeRel.name() + " timeline ...");
        LoadingIndicator.show();
        PLYCompletion<ResultSetWithCursor> completion = new PLYCompletion<ResultSetWithCursor>() {
            @Override
            public void onSuccess(ResultSetWithCursor result) {
            }

            @Override
            public void onPostSuccess(ResultSetWithCursor result) {
                Log.d("FeedCallback", "Received ResultSetWithCursor for " + finalTimeRel.name() + " " +
                        retrieval.type().name() + " timeline");
                List<BaseObject> newItems = result.getResults();
                // if (retrieval.type() == TimelineType.PRODUCT) {
                // filter the default product image since it's displayed and can be opened in the app bar
                // TODO filter default image and make it clickable in the app bar instead (prob: updates)
                // }
                int newItemCnt = newItems.size();
                if (newItemCnt != 0) {
                    int curItemCnt = feedItems.size();
                    feedUrls[TimeRel.INITIAL.value] = result.getThisResultsUrl();
                    if (finalTimeRel == TimeRel.INITIAL) {
                        feedItems = newItems;
                        feedUrls[TimeRel.EARLIER.value] = result.getSinceThisResultsUrl();
                        feedUrls[TimeRel.LATER.value] = result.getUntilThisResultsUrl();
                        notifyDataSetChanged();
                    } else if (finalTimeRel == TimeRel.EARLIER) {
                        newItems.addAll(feedItems);
                        feedItems = newItems;
                        feedUrls[TimeRel.EARLIER.value] = result.getSinceThisResultsUrl();
                        notifyItemRangeInserted(0, newItemCnt);
                    } else {
                        feedItems.addAll(newItems);
                        feedUrls[TimeRel.LATER.value] = result.getUntilThisResultsUrl();
                        notifyItemRangeInserted(curItemCnt, newItemCnt);
                    }
                } else {
                    if (finalTimeRel == TimeRel.INITIAL) {
                        feedItems.clear();
                        feedUrls[TimeRel.EARLIER.value] = null;
                        feedUrls[TimeRel.LATER.value] = null;
                        notifyDataSetChanged();
                    }
                }
                loading.set(false);
                LoadingIndicator.hide();
            }

            @Override
            public void onError(PLYAndroid.QueryError error) {
                // TODO check if server or client error and display message / retry if init
                Log.e("FeedCallback", error.getMessage(), error.getException());
                loading.set(false);
                LoadingIndicator.hide();
            }
        };
        if (finalTimeRel == TimeRel.EARLIER) {
            TimelineService.getTimelineFromURL(client, feedUrls[TimeRel.EARLIER.value], completion);
        } else if (finalTimeRel == TimeRel.LATER) {
            TimelineService.getTimelineFromURL(client, feedUrls[TimeRel.LATER.value], completion);
        } else {
            retrieval.initiate(client, loadItems, settings, completion);
        }
        return true;
    }

    @Override
    public FeedItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == R.id.feed_item_product) {
            ProductPreview productPreview = new ProductPreview(activity, navigationHandler, itemWidthPx,
                    imageMaxHeightPx, avatarSizePx);
            return new FeedItemHolder(productPreview);
        } else if (viewType == R.id.feed_item_opinion) {
            OpinionView opinionView = new OpinionView(activity, navigationHandler, itemWidthPx, avatarSizePx);
            return new FeedItemHolder(opinionView);
        } else if (viewType == R.id.feed_item_image) {
            ImagePreview imagePreview = new ImagePreview(activity, navigationHandler, itemWidthPx,
                    imageMaxHeightPx, avatarSizePx);
            return new FeedItemHolder(imagePreview);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(FeedItemHolder holder, int position) {
        BaseObject item = feedItems.get(position);
        if (item instanceof Product) {
            // PRODUCT
            Product product = (Product) item;
            ProductImage productImage = product.getDefaultImage();
            String productImageUrl = null;
            int[] productDominantColor = null;
            if (productImage != null) {
                float widthToHeightRatio = productImage.getWidth() / (float) productImage.getHeight();
                int imageHeightPx = Math.min(Math.round(itemWidthPx / widthToHeightRatio), imageMaxHeightPx);
                productImageUrl = ImageService.getImageForSizeURL(client, productImage.getImageFileId(),
                        itemWidthPx, imageHeightPx, true, null);
                productDominantColor = productImage.getDominantColor();
            }
            SimpleUserInfo author = product.getCreatedBy();
            String authorImageUrl = ImageService.getUserAvatarURL(client, author.getId(), avatarSizePx);
            holder.productPreview.setProduct(product, productImageUrl, productDominantColor, author,
                    authorImageUrl, userHandler, client);
        } else if (item instanceof Opine) {
            // OPINION
            Opine opinion = (Opine) item;
            SimpleUserInfo author = opinion.getCreatedBy();
            String authorImageUrl = ImageService.getUserAvatarURL(client, author.getId(), avatarSizePx);
            holder.opinionView.setOpinion(opinion, author, authorImageUrl, userHandler, client);
        } else if (item instanceof ProductImage) {
            // IMAGE
            ProductImage productImage = (ProductImage) item;
            float widthToHeightRatio = productImage.getWidth() / (float) productImage.getHeight();
            int imageHeightPx = Math.min(Math.round(itemWidthPx / widthToHeightRatio), imageMaxHeightPx);
            String productImageUrl = ImageService.getImageForSizeURL(client, productImage.getImageFileId(),
                    itemWidthPx, imageHeightPx, true, null);
            SimpleUserInfo author = productImage.getCreatedBy();
            String authorImageUrl = ImageService.getUserAvatarURL(client, author.getId(), avatarSizePx);
            holder.imagePreview.setImage(productImage, productImageUrl, productImage.getDominantColor(),
                    author, authorImageUrl, userHandler);
        } else {
            // UNSUPPORTED
            throw new RuntimeException("Unsupported item in timeline: " + item.getBeautifiedClass());
        }
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    @Override
    public long getItemId(int position) {
        return feedItems.get(position).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        BaseObject item = feedItems.get(position);
        if (item instanceof Product) {
            return R.id.feed_item_product;
        } else if (item instanceof Opine) {
            return R.id.feed_item_opinion;
        } else if (item instanceof ProductImage) {
            return R.id.feed_item_image;
        } else {
            return 0;
        }
    }

    /**
     * Refreshes the timeline using new settings.
     *
     * @param settings
     *         the new settings to apply
     */
    public void updateSettings(int settings) {
        this.settings = settings;
        retrieveFeedItems(TimeRel.INITIAL);
    }

    /**
     * Sets up and registers listeners to be notified of product/opinion/image creations and updates by the
     * user.
     *
     * Call {@link #destroyListeners} to remove the listeners without having to wait for garbage collection.
     */
    public void setupListeners() {
        if (onProductCreateListener != null) {
            return;
        }
        final TimelineType timelineType = retrieval.type();
        final String identifier = retrieval.identifier();
        onProductCreateListener = new DataChangeListener.OnProductCreateListener() {
            @Override
            public void onProductCreate(Product product) {
                if (timelineType == TimelineType.PRODUCT) {
                    // don't care about new products in a product timeline
                    return;
                }
                if (timelineType == TimelineType.USER && (userHandler.getUser() == null || !userHandler
                        .getUser().getId().equals(identifier))) {
                    // don't care about new products (created by oneself) in another user's timeline
                    return;
                }
                // load the initial set of timeline items if the user added a new product
                Log.v("TimelinePCallback", "Product creation event received");
                retrieveFeedItems(TimeRel.INITIAL);
            }
        };
        DataChangeListener.addOnProductCreateListener(onProductCreateListener);
        onProductUpdateListener = new DataChangeListener.OnProductUpdateListener() {
            @Override
            public void onProductUpdate(final Product product) {
                if (timelineType == TimelineType.PRODUCT && !product.getGtin().equals(identifier)) {
                    // don't care about product updates in another product's timeline
                    return;
                }
                // refresh the product if it has already been loaded
                Log.v("TimelinePCallback", "Product update received for ID " + product.getId());
                if (!loading.compareAndSet(false, true)) {
                    // ignore the update if the feed is currently working
                    return;
                }
                int cntFeedItems = feedItems.size();
                for (int i = 0; i < cntFeedItems; i++) {
                    BaseObject baseObject = feedItems.get(i);
                    if (baseObject instanceof Product) {
                        Product p = (Product) baseObject;
                        if (p.getId().equals(product.getId())) {
                            final int position = i;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    feedItems.set(position, product);
                                    notifyItemChanged(position);
                                }
                            });
                        }
                    } else if (baseObject instanceof Opine) {
                        // update the product contained in opinions
                        final Opine o = (Opine) baseObject;
                        if (o.getProduct().getId().equals(product.getId())) {
                            final int position = i;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    o.setProduct(product);
                                    notifyItemChanged(position);
                                }
                            });
                        }
                    } else if (baseObject instanceof ProductImage) {
                        // update the product contained in product images
                        final ProductImage pi = (ProductImage) baseObject;
                        if (pi.getProduct().getId().equals(product.getId())) {
                            final int position = i;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pi.setProduct(product);
                                    notifyItemChanged(position);
                                }
                            });
                        }
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.set(false);
                    }
                });
            }
        };
        DataChangeListener.addOnProductUpdateListener(onProductUpdateListener);
        onOpinionCreateListener = new DataChangeListener.OnOpinionCreateListener() {
            @Override
            public void onOpinionCreate(Opine opinion) {
                if (timelineType == TimelineType.PRODUCT && !opinion.getGtin().equals(identifier)) {
                    // don't care about new opinions in a different product's timeline
                    return;
                }
                if (timelineType == TimelineType.USER && userHandler.getUser() != null && !userHandler
                        .getUser().getId().equals(identifier)) {
                    // don't care about new opinions (created by oneself) in another user's timeline
                    return;
                }
                // load the initial set of timeline items if the user added a new opinion
                Log.v("TimelineOCallback", "Opinion creation event received");
                retrieveFeedItems(TimeRel.INITIAL);
            }
        };
        DataChangeListener.addOnOpinionCreateListener(onOpinionCreateListener);
        onOpinionUpdateListener = new DataChangeListener.OnOpinionUpdateListener() {
            @Override
            public void onOpinionUpdate(final Opine opinion) {
                if (timelineType == TimelineType.PRODUCT && !opinion.getGtin().equals(identifier)) {
                    // don't care about opinion updates in a different product's timeline
                    return;
                }
                if (timelineType == TimelineType.USER && userHandler.getUser() != null && !userHandler
                        .getUser().getId().equals(identifier)) {
                    // don't care about opinion updates in another user's timeline
                    return;
                }
                // refresh the opinion if it has already been loaded
                Log.v("TimelineOCallback", "Opinion update received for ID " + opinion.getId());
                if (!loading.compareAndSet(false, true)) {
                    // ignore the update if the feed is currently working
                    return;
                }
                int cntFeedItems = feedItems.size();
                for (int i = 0; i < cntFeedItems; i++) {
                    BaseObject baseObject = feedItems.get(i);
                    if (baseObject instanceof Opine) {
                        Opine o = (Opine) baseObject;
                        if (o.getId().equals(opinion.getId())) {
                            final int position = i;
                            if (opinion.getProduct() == null) {
                                // an updated opinion may not return the product it belongs to - re-set
                                opinion.setProduct(o.getProduct());
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    feedItems.set(position, opinion);
                                    notifyItemChanged(position);
                                }
                            });
                            break;
                        }
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.set(false);
                    }
                });
            }
        };
        DataChangeListener.addOnOpinionUpdateListener(onOpinionUpdateListener);
        onImageCreateListener = new DataChangeListener.OnImageCreateListener() {
            @Override
            public void onImageCreate(ProductImage image) {
                if (timelineType == TimelineType.PRODUCT) {
                    // if in the product timeline simply reload the initial data set
                    if (image.getGtin().equals(identifier)) {
                        Log.v("TimelineICallback", "Image creation event received");
                        // only care if this product timeline identifies the same product as the image
                        retrieveFeedItems(TimeRel.INITIAL);
                    }
                } else {
                    Log.v("TimelineICallback", "Image creation event received");
                    // default image changes for products if no images existed or none were upvoted
                    if (!loading.compareAndSet(false, true)) {
                        // ignore the update if the feed is currently working
                        return;
                    }
                    int cntFeedItems = feedItems.size();
                    for (int i = 0; i < cntFeedItems; i++) {
                        BaseObject baseObject = feedItems.get(i);
                        if (baseObject instanceof Product) {
                            Product p = (Product) baseObject;
                            if (p.getGtin().equals(image.getGtin())) {
                                if (triggersDefaultImageChange(p.getDefaultImage(), image) == 1) {
                                    // new image is the new default
                                    p.setDefaultImage(image);
                                    final int position = i;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyItemChanged(position);
                                        }
                                    });
                                }
                            }
                        }
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading.set(false);
                        }
                    });
                }
            }
        };
        DataChangeListener.addOnImageCreateListener(onImageCreateListener);
        onImageUpdateListener = new DataChangeListener.OnImageUpdateListener() {
            @Override
            public void onImageUpdate(final ProductImage image) {
                if (timelineType == TimelineType.PRODUCT && !image.getGtin().equals(identifier)) {
                    // don't care about image updates in a different product's timeline
                    return;
                }
                Log.v("TimelineICallback", "Image update received for ID " + image.getId());
                // up-/downvoting may result in default product image changes
                if (!loading.compareAndSet(false, true)) {
                    // ignore the update if the feed is currently working
                    return;
                }
                int cntFeedItems = feedItems.size();
                for (int i = 0; i < cntFeedItems; i++) {
                    BaseObject baseObject = feedItems.get(i);
                    if (baseObject instanceof ProductImage) {
                        ProductImage img = (ProductImage) baseObject;
                        if (img.getId().equals(image.getId())) {
                            final int position = i;
                            if (image.getProduct() == null) {
                                // an updated image may not return the product it belongs to - re-set
                                image.setProduct(img.getProduct());
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    feedItems.set(position, image);
                                    notifyItemChanged(position);
                                }
                            });
                        }
                    } else if (baseObject instanceof Product) {
                        final Product p = (Product) baseObject;
                        if (p.getGtin().equals(image.getGtin())) {
                            final int position = i;
                            int defaultImageChange = triggersDefaultImageChange(p.getDefaultImage(), image);
                            Log.v("TimelineICallback", "Triggers default image change: " +
                                    defaultImageChange);
                            if (defaultImageChange == 1) {
                                // updated image is the new default
                                p.setDefaultImage(image);
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyItemChanged(position);
                                    }
                                });
                            } else if (defaultImageChange == -1 && p.getImageCount() > 1) {
                                // updated image was the default before but may not be anymore
                                ProductService.getProductForGtin(client, p.getGtin(), null, false, null,
                                        new PLYCompletion<Product>() {
                                            @Override
                                            public void onSuccess(Product result) {
                                            }

                                            @Override
                                            public void onPostSuccess(Product result) {
                                                ProductImage newDefaultImage = result.getDefaultImage();
                                                p.setDefaultImage(newDefaultImage);
                                                notifyItemChanged(position);
                                            }

                                            @Override
                                            public void onError(PLYAndroid.QueryError error) {
                                                Log.w(TimelineAdapter.class.getSimpleName(), error
                                                        .getMessage());
                                            }
                                        });
                            }
                        }
                    }
                    // TODO update images in opinions once implemented
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.set(false);
                    }
                });
            }
        };
        DataChangeListener.addOnImageUpdateListener(onImageUpdateListener);
    }

    /**
     * Destroys listeners to stop being notified of any updates.
     */
    public void destroyListeners() {
        if (onProductCreateListener == null) {
            return;
        }
        DataChangeListener.removeOnProductCreateListener(onProductCreateListener);
        DataChangeListener.removeOnProductUpdateListener(onProductUpdateListener);
        DataChangeListener.removeOnOpinionCreateListener(onOpinionCreateListener);
        DataChangeListener.removeOnOpinionUpdateListener(onOpinionUpdateListener);
        DataChangeListener.removeOnImageCreateListener(onImageCreateListener);
        DataChangeListener.removeOnImageUpdateListener(onImageUpdateListener);
        onProductCreateListener = null;
        onProductUpdateListener = null;
        onOpinionCreateListener = null;
        onOpinionUpdateListener = null;
        onImageCreateListener = null;
        onImageUpdateListener = null;
    }

    /**
     * Enum for a time relation.
     */
    public enum TimeRel {
        INITIAL(0),
        EARLIER(1),
        LATER(2);

        public final int value;

        TimeRel(int value) {
            this.value = value;
        }
    }

    /**
     * Enum for the type of timeline.
     */
    public enum TimelineType {
        GLOBAL, PRODUCT, USER
    }

    /**
     * Interface passed to a {@link TimelineAdapter} to retrieve the first set of feed items. Subsequent
     * retrievals are done using the URLs returned from the initial set of results.
     */
    public interface Retrieval {

        /**
         * Starts the initial retrieval of the feed.
         *
         * @param client
         *         the PLYAndroid client to use for retrievals
         * @param loadItems
         *         the amount of items to load in one retrieval
         * @param settings
         *         the display settings for this timeline
         * @param completion
         *         the completion callback to run once results are returned
         */
        void initiate(PLYAndroid client, int loadItems, int settings, PLYCompletion<ResultSetWithCursor>
                completion);

        /**
         * @return the type this timeline retrieval represents
         */
        TimelineType type();

        /**
         * @return the gtin if a product timeline, the user ID if a user timeline, null if a global timeline
         */
        String identifier();

    }

    /**
     * ViewHolder cached by the RecyclerView. Holds a feed item cardview of one type.
     */
    public static class FeedItemHolder extends RecyclerView.ViewHolder {

        public ProductPreview productPreview;
        public OpinionView opinionView;
        public ImagePreview imagePreview;

        /**
         * Creates the ViewHolder caching a Product feed item.
         *
         * @param productPreview
         *         the ProductPreview to cache
         */
        public FeedItemHolder(ProductPreview productPreview) {
            super(productPreview);
            this.productPreview = productPreview;
        }

        /**
         * Creates the ViewHolder caching an Opinion feed item.
         *
         * @param opinionView
         *         the OpinionView to cache
         */
        public FeedItemHolder(OpinionView opinionView) {
            super(opinionView);
            this.opinionView = opinionView;
        }

        /**
         * Creates the ViewHolder caching an Image feed item.
         *
         * @param imagePreview
         *         the ImagePreview to cache
         */
        public FeedItemHolder(ImagePreview imagePreview) {
            super(imagePreview);
            this.imagePreview = imagePreview;
        }
    }
}
