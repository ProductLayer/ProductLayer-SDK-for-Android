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

package com.productlayer.android.common.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.adapter.NamedFragmentPagerAdapter;
import com.productlayer.android.common.adapter.TimelineAdapter;
import com.productlayer.android.common.global.LoadingIndicator;
import com.productlayer.android.common.handler.AppBarHandler;
import com.productlayer.android.common.handler.DataChangeListener;
import com.productlayer.android.common.handler.FloatingActionButtonHandler;
import com.productlayer.android.common.handler.HasAppBarHandler;
import com.productlayer.android.common.handler.HasFloatingActionButtonHandler;
import com.productlayer.android.common.handler.HasNavigationHandler;
import com.productlayer.android.common.handler.HasPLYAndroidHolder;
import com.productlayer.android.common.handler.HasTimelineSettingsHandler;
import com.productlayer.android.common.handler.HasUserHandler;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.handler.PLYAndroidHolder;
import com.productlayer.android.common.handler.TimelineSettingsHandler;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.common.util.ColorUtil;
import com.productlayer.android.common.util.MetricsUtil;
import com.productlayer.android.common.util.PhotoUtil;
import com.productlayer.android.common.util.SnackbarUtil;
import com.productlayer.android.common.util.SystemBarsUtil;
import com.productlayer.android.common.util.ThemeUtil;
import com.productlayer.android.common.view.LikeView;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.ImageService;
import com.productlayer.android.sdk.services.ProductService;
import com.productlayer.core.beans.Product;
import com.productlayer.core.beans.ProductImage;
import com.productlayer.core.beans.User;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Displays the screen including a product's details, its timeline of opinions, a large preview photo as part
 * of the toolbar, and possibilities for interaction.
 *
 * Requires the activity to have {@link AppBarHandler}, {@link FloatingActionButtonHandler}, {@link
 * TimelineSettingsHandler}, {@link NavigationHandler}, {@link UserHandler}, {@link PLYAndroidHolder} - and
 * {@link MetricsUtil} to be set up.
 */
// TODO register callback to UserHandler to react to user changes, ie to reset the like view and timeline
public class ProductFragment extends NamedFragment {

    public static final String NAME = "Product";

    public static final int TAB_INDEX_DEFAULT = -1;
    public static final int TAB_INDEX_TIMELINE = 0;
    public static final int TAB_INDEX_DETAILS = 1;

    private static final int QUALITY_PRODUCT_IMAGE = 85;

    private static final String KEY_PRODUCT = "product";
    private static final String STATE_PRODUCT = "product";

    private AppBarHandler appBarHandler;
    private FloatingActionButtonHandler fabHandler;
    private TimelineSettingsHandler timelineSettingsHandler;
    private NavigationHandler navigationHandler;
    private UserHandler userHandler;

    private PLYAndroid client;

    private Product product;
    private ProductImage defaultImage;

    private NamedFragmentPagerAdapter pagerAdapter;

    private int appBarHeight;
    private ImageView backdrop;

    // listener would be garbage-collected without reference
    private DataChangeListener.OnProductUpdateListener onProductUpdateListener;
    private DataChangeListener.OnImageCreateListener onImageCreateListener;
    private DataChangeListener.OnImageUpdateListener onImageUpdateListener;

    /**
     * Constructs a new instance with the specified parameters. The parameters passed this way survive
     * recreation of the fragment due to orientation changes etc.
     *
     * @param product
     *         the product to present
     * @return the fragment with the given parameters
     */
    public static ProductFragment newInstance(@SuppressWarnings("TypeMayBeWeakened") Product product) {
        ProductFragment productFragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_PRODUCT, product);
        productFragment.setArguments(args);
        return productFragment;
    }

    /**
     * @param product
     *         the product to present
     * @return this fragment's name and initialization parameters
     */
    public static String makeInstanceName(Product product) {
        String productParam = product == null ? "" : product.getId();
        return NAME + "(" + productParam + ")";
    }

    @Override
    public String getInstanceName() {
        return makeInstanceName(product);
    }

    @Override
    public FragmentGrouping getGrouping() {
        return FragmentGrouping.NONE;
    }

    // FRAGMENT LIFECYCLE - START //

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        try {
            appBarHandler = ((HasAppBarHandler) activity).getAppBarHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasAppBarHandler");
        }
        try {
            fabHandler = ((HasFloatingActionButtonHandler) activity).getFloatingActionButtonHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasFloatingActionButtonHandler");
        }
        try {
            timelineSettingsHandler = ((HasTimelineSettingsHandler) activity).getTimelineSettingsHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasTimelineSettingsHandler");
        }
        try {
            navigationHandler = ((HasNavigationHandler) activity).getNavigationHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasNavigationHandler");
        }
        try {
            userHandler = ((HasUserHandler) activity).getUserHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasUserHandler");
        }
        PLYAndroidHolder plyAndroidHolder;
        try {
            plyAndroidHolder = ((HasPLYAndroidHolder) activity).getPLYAndroidHolder();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasPLYAndroidHolder");
        }
        client = plyAndroidHolder.getPLYAndroid();
        if (client == null) {
            throw new RuntimeException("PLYAndroid must bet set before creating fragment " + this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // get product from stored fragment state
            product = (Product) savedInstanceState.getSerializable(STATE_PRODUCT);
        }
        if (product == null) {
            // get product from initial arguments
            Bundle args = getArguments();
            product = (Product) args.getSerializable(KEY_PRODUCT);
        }
        assert product != null;
        // keeping reference to default image for comparison for image update listeners
        defaultImage = product.getDefaultImage();
        // this fragment adds actions to the app bar
        setHasOptionsMenu(true);
        // create tabs and subfragments
        pagerAdapter = new NamedFragmentPagerAdapter(getChildFragmentManager());
        ProductTimelineFragment productTimeline = null;
        ProductDetailsFragment productDetails = null;
        if (savedInstanceState != null) {
            // on orientation change fragments are managed by the fragment handler and getItem of the pager
            // adapter is not even called anymore (to get cached fragments see workarounds in adapter class)
            productTimeline = (ProductTimelineFragment) NamedFragmentPagerAdapter.findFragmentByPosition
                    (pagerAdapter, getChildFragmentManager(), R.id.pager, TAB_INDEX_TIMELINE);
            productDetails = (ProductDetailsFragment) NamedFragmentPagerAdapter.findFragmentByPosition
                    (pagerAdapter, getChildFragmentManager(), R.id.pager, TAB_INDEX_DETAILS);
        }
        // only create fragments if they could not be recreated from a previous state
        if (productTimeline == null) {
            productTimeline = ProductTimelineFragment.newInstance(product.getGtin());
        }
        if (productDetails == null) {
            productDetails = ProductDetailsFragment.newInstance(product);
        }
        pagerAdapter.addFragment(getString(R.string.product_timeline), productTimeline);
        pagerAdapter.addFragment(getString(R.string.product_details), productDetails);
        // listen for product/image updates by the user
        setupListeners();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Context context = getContext();
        // inflate the layout
        View layout = inflater.inflate(R.layout.fragment_product, container, false);
        // set up the view pager
        ViewPager pager = (ViewPager) layout.findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        // let the activity modify its app bar
        String title = product.getName();
        // the app bar content scrim does not disappear if the height is less than ~174dp
        // title does not disappear if the height is less than ~187dp
        int minHeight = MetricsUtil.inPx(187);
        int halfHeight = MetricsUtil.getHeightPx() / 2;
        if (appBarHandler.hasTranslucentStatusBar()) {
            halfHeight += SystemBarsUtil.getStatusBarHeight(context);
        }
        appBarHeight = Math.max(halfHeight, minHeight);
        backdrop = new ImageView(context);
        updateBackdrop(context, product.getDefaultImage(), true);
        // voting view
        LikeView likeView = new LikeView(context, LikeView.Type.PRODUCT, product.getId());
        likeView.setSizeMini();
        likeView.setClient(client);
        User user = userHandler.getUser();
        String userId = user == null ? null : user.getId();
        int widthPx = MetricsUtil.getWidthPx() - (int) getResources().getDimension(R.dimen
                .exp_toolbar_title_margin_left) - (int) getResources().getDimension(R.dimen
                .exp_toolbar_title_margin_right);
        likeView.setFromVoters(product.getUpVoters(), product.getDownVoters(), userId, userHandler
                .getFriends(), widthPx);
        appBarHandler.setProductAppBar(layout, title, likeView, getResources().getDimensionPixelSize(R
                .dimen.feed_interaction_icon_mini), appBarHeight, backdrop);
        // show tabs
        final TabLayout tabLayout = (TabLayout) layout.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);
        // configure floating action button and timeline settings per tab
        prepareFab(pager.getCurrentItem());
        prepareTsb(pager.getCurrentItem());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab currentTab = tabLayout.getTabAt(position);
                assert currentTab != null;
                Log.v("ProductFragment", "Page " + currentTab.getText() + " selected");
                prepareFab(position);
                prepareTsb(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save changes done to the product object (i.e. votes)
        outState.putSerializable(STATE_PRODUCT, product);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getClass().getSimpleName(), "Received result code " + resultCode + " from request " +
                requestCode);
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (PhotoUtil.onActivityResult(requestCode, resultCode, data, getContext(), PhotoUtil
                .getPhotoPathCache(product.getGtin(), activity))) {
            LoadingIndicator.show();
            // upload new product image
            uploadProductImage(PhotoUtil.getPhotoPathCache(product.getGtin(), activity));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        backdrop = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stop listening for product/image updates by the user
        destroyListeners();
    }

    // FRAGMENT LIFECYCLE - END //

    // MENU - START //

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actions_product, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // tinting submenu items
        Integer tintColor = ThemeUtil.getIntegerValue(getActivity(), android.R.attr.textColorPrimary);
        if (tintColor == null) {
            return;
        }
        int cntMenuItems = menu.size();
        for (int i = 0; i < cntMenuItems; i++) {
            Menu subMenu = menu.getItem(i).getSubMenu();
            if (subMenu != null) {
                ColorUtil.tintMenuItems(subMenu, tintColor, -1);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.popup_take_photo) {
            PhotoUtil.takePhoto(getActivity(), this);
            return true;
        } else if (itemId == R.id.popup_pick_from_gallery) {
            PhotoUtil.pickGalleryImage(getActivity(), this);
            return true;
        }
        return false;
    }

    // MENU - END //

    /**
     * Loads the specified product image for the app bar backdrop.
     *
     * @param context
     *         the app context
     * @param image
     *         the image to load
     * @param useCache
     *         whether to try to load the image from cache
     */
    private void updateBackdrop(Context context, ProductImage image, boolean useCache) {
        if (context == null || image == null || backdrop == null) {
            return;
        }
        // display the image's dominant color as background while it is being loaded
        int[] productDominantColor = image.getDominantColor();
        if (productDominantColor != null) {
            backdrop.setBackgroundColor(Color.rgb(productDominantColor[0], productDominantColor[1],
                    productDominantColor[2]));
        } else {
            //noinspection ConstantConditions
            backdrop.setBackgroundColor(ThemeUtil.getIntegerValue(context, R.attr.imagePlaceholder));
        }
        // load the image
        backdrop.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String imageUrl = ImageService.getImageForSizeURL(client, image.getImageFileId(), MetricsUtil
                .getWidthPx(), appBarHeight, true, QUALITY_PRODUCT_IMAGE);
        Log.v(getClass().getSimpleName(), "Loading backdrop image for product screen from " + imageUrl);
        if (useCache) {
            Picasso.with(context).load(imageUrl).into(backdrop);
        } else {
            Picasso.with(context).invalidate(imageUrl);
            Picasso.with(context).load(imageUrl).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy
                    (NetworkPolicy.NO_CACHE).into(backdrop);
        }
    }

    /**
     * Uploads a new product image.
     */
    private void uploadProductImage(String imagePath) {
        final String gtin = product.getGtin();
        // TODO limit file size
        ImageService.uploadProductImage(client, gtin, imagePath, new PLYCompletion<ProductImage>() {
            @Override
            public void onSuccess(ProductImage result) {
                Log.d("UploadPImageCallback", "New image for product with GTIN " + gtin + " uploaded");
                LoadingIndicator.hide();
                SnackbarUtil.make(getActivity(), getView(), R.string.image_uploaded, Snackbar.LENGTH_LONG)
                        .show();
                DataChangeListener.imageCreate(result);
            }

            @Override
            public void onError(PLYAndroid.QueryError error) {
                Log.d("UploadPImageCallback", error.getMessage());
                LoadingIndicator.hide();
                SnackbarUtil.make(getActivity(), getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Sets up and registers listeners to be notified of product/image updates by the user.
     */
    private void setupListeners() {
        if (onProductUpdateListener != null) {
            return;
        }
        onProductUpdateListener = new DataChangeListener.OnProductUpdateListener() {
            @Override
            public void onProductUpdate(Product product) {
                Log.v("PFragmentPCallback", "Product update received for ID " + product.getId());
                ProductFragment.this.product = product;
                defaultImage = product.getDefaultImage();
                // title and like view should not need updating because they cannot be edited on this screen
                // default image is updated in image listeners
            }
        };
        DataChangeListener.addOnProductUpdateListener(onProductUpdateListener);
        onImageCreateListener = new DataChangeListener.OnImageCreateListener() {
            @Override
            public void onImageCreate(final ProductImage image) {
                if (!image.getGtin().equals(product.getGtin())) {
                    // don't care about images belonging to other products
                    return;
                }
                Log.v("PFragmentICallback", "Image creation event received");
                if (TimelineAdapter.triggersDefaultImageChange(defaultImage, image) == 1) {
                    // new image is the new default
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                product.setDefaultImage(image);
                                defaultImage = image;
                                updateBackdrop(getContext(), image, false);
                            }
                        });
                    }
                }
            }
        };
        DataChangeListener.addOnImageCreateListener(onImageCreateListener);
        onImageUpdateListener = new DataChangeListener.OnImageUpdateListener() {
            @Override
            public void onImageUpdate(final ProductImage image) {
                if (!image.getGtin().equals(product.getGtin())) {
                    // don't care about images belonging to other products
                    return;
                }
                Log.v("PFragmentICallback", "Image update received for ID " + image.getId());
                int defaultImageChange = TimelineAdapter.triggersDefaultImageChange(defaultImage, image);
                Log.v("PFragmentICallback", "Triggers default image change: " + defaultImageChange);
                if (defaultImageChange == 1) {
                    // updated image is the new default
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                product.setDefaultImage(image);
                                defaultImage = image;
                                updateBackdrop(getContext(), image, false);
                            }
                        });
                    }
                } else if (defaultImageChange == -1 && product.getImageCount() > 1) {
                    // updated image was the default before but may not be anymore
                    ProductService.getProductForGtin(client, product.getGtin(), null, false, null, new
                            PLYCompletion<Product>() {
                        @Override
                        public void onSuccess(Product result) {
                        }

                        @Override
                        public void onPostSuccess(Product result) {
                            ProductImage newDefaultImage = result.getDefaultImage();
                            product.setDefaultImage(newDefaultImage);
                            defaultImage = newDefaultImage;
                            updateBackdrop(getContext(), newDefaultImage, false);
                        }

                        @Override
                        public void onError(PLYAndroid.QueryError error) {
                            Log.w(ProductFragment.class.getSimpleName(), error.getMessage());
                        }
                    });
                }
            }
        };
        DataChangeListener.addOnImageUpdateListener(onImageUpdateListener);
    }

    /**
     * Destroys listeners to stop being notified of any updates.
     */
    private void destroyListeners() {
        if (onProductUpdateListener == null) {
            return;
        }
        DataChangeListener.removeOnProductUpdateListener(onProductUpdateListener);
        DataChangeListener.removeOnImageCreateListener(onImageCreateListener);
        DataChangeListener.removeOnImageUpdateListener(onImageUpdateListener);
        onProductUpdateListener = null;
        onImageCreateListener = null;
        onImageUpdateListener = null;
    }

    /**
     * Configures the floating action button with regards to the currently selected tab.
     *
     * @param position
     *         the current tab position
     */
    private void prepareFab(int position) {
        if (position == TAB_INDEX_DETAILS) {
            // configure floating action button to edit the product
            fabHandler.configureFab(R.drawable.edit_24dp, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open edit product screen
                    navigationHandler.openProductEditPage(product);
                }
            });
        } else {
            // configure floating action button to add an opinion
            fabHandler.configureFab(R.drawable.opinion_24dp, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open write opinion dialog/screen
                    navigationHandler.openOpinionPage(product, null);
                }
            });
        }
    }

    /**
     * Configures the timeline settings button with regards to the currently selected tab.
     *
     * @param position
     *         the current tab position
     */
    private void prepareTsb(int position) {
        if (position == TAB_INDEX_DETAILS) {
            // hide the timeline settings button
            timelineSettingsHandler.hideTimelineSettings();
        } else {
            // configures and shows the timeline settings
            ProductTimelineFragment productTimeline = (ProductTimelineFragment) NamedFragmentPagerAdapter
                    .findFragmentByPosition(pagerAdapter, getChildFragmentManager(), R.id.pager,
                            TAB_INDEX_TIMELINE);
            if (productTimeline == null) {
                // if we can't find the fragment in the fragment manager, we'll try with the one in the pager
                productTimeline = (ProductTimelineFragment) pagerAdapter.getItem(TAB_INDEX_TIMELINE);
            }
            productTimeline.prepareTimelineSettings(timelineSettingsHandler);
        }
    }
}
