/*
 * Copyright (c) 2016, ProductLayer GmbH All rights reserved.
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

package com.productlayer.android.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.productlayer.android.common.activity.ScannerActivity;
import com.productlayer.android.common.global.ObjectCache;
import com.productlayer.android.common.handler.AppBarHandler;
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
import com.productlayer.android.common.util.CacheUtil;
import com.productlayer.android.common.util.CameraUtil;
import com.productlayer.android.common.util.LocaleUtil;
import com.productlayer.android.common.util.MetricsUtil;
import com.productlayer.android.common.util.SnackbarUtil;
import com.productlayer.android.demo.handler.DemoAppBarHandler;
import com.productlayer.android.demo.handler.DemoFloatingActionButtonHandler;
import com.productlayer.android.demo.handler.DemoNavigationHandler;
import com.productlayer.android.demo.handler.DemoQueryListener;
import com.productlayer.android.demo.handler.DemoTimelineSettingsHandler;
import com.productlayer.android.demo.handler.DemoUserHandler;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.rest.client.config.PLYRestClientConfig;

/**
 * Demonstrates usage of the ProductLayer SDK and its common components.
 *
 * The app presents a feed of the latest products and opinions added to ProductLayer using the {@link
 * com.productlayer.android.common.fragment.GlobalTimelineFragment} component. Product details are implemented
 * using {@link com.productlayer.android.common.fragment.ProductFragment}. Barcode lookup is provided by
 * {@link ScannerActivity}. Editing and interaction functionality can be achieved simply by calling {@link
 * com.productlayer.android.sdk.services.UserService#login} before any request requiring authentication. To
 * maintain a user session through app restarts {@link PLYAndroid} contains methods in line with the lifecycle
 * of activities.
 */
public class Demo extends AppCompatActivity implements HasPLYAndroidHolder, PLYAndroidHolder,
        HasAppBarHandler, HasNavigationHandler, HasTimelineSettingsHandler, HasUserHandler,
        HasFloatingActionButtonHandler {

    private static final int REQUEST_CODE_SCAN = 1;

    private PLYAndroid client;

    private DemoAppBarHandler appBarHandler;
    private DemoNavigationHandler navigationHandler;
    private DemoTimelineSettingsHandler timelineSettingsHandler;
    private DemoUserHandler userHandler;
    private DemoFloatingActionButtonHandler floatingActionButtonHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // use default REST client config and set API key
        PLYRestClientConfig config = new PLYRestClientConfig();
        // get your own API key from https://developer.productlayer.com and set it here
        config.apiKey = getString(R.string.api_demo_key);
        // create PLYAndroid client
        client = new PLYAndroid(config);
        client.setLanguage(LocaleUtil.getDefaultLanguage());
        client.setQueryListener(new DemoQueryListener());
        // get screen data
        MetricsUtil.update(this);
        // set up image caching
        CacheUtil.setupPicassoInstance(getApplicationContext(), CacheUtil.PICASSO_CACHE_MEMORY_PERCENTAGE,
                CacheUtil.PICASSO_CACHE_DISK_MB, false);
        // set up handlers
        appBarHandler = new DemoAppBarHandler(this);
        navigationHandler = new DemoNavigationHandler(getSupportFragmentManager(), R.id.content, client);
        timelineSettingsHandler = new DemoTimelineSettingsHandler();
        userHandler = new DemoUserHandler();
        floatingActionButtonHandler = new DemoFloatingActionButtonHandler();
        // any previously attached fragments may be recreated
        super.onCreate(savedInstanceState);
        // attach app layout
        setContentView(R.layout.activity_demo);
        // set up app bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // if no fragment is restored, display a global timeline of products and opinions
        if (savedInstanceState == null) {
            navigationHandler.openTimeline();
        }
        // get product categories to translate keys to titles on the product page
        cacheCategories();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // populate the action bar
        getMenuInflater().inflate(R.menu.menu_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                if (CameraUtil.hasCameraAny(this)) {
                    // start the barcode scanner
                    Intent intent = new Intent(this, ScannerActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                } else {
                    SnackbarUtil.make(this, findViewById(R.id.content), R.string.no_camera_found, Snackbar
                            .LENGTH_LONG).show();
                }
                return true;
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MetricsUtil.update(this);
        if (requestCode == REQUEST_CODE_SCAN) {
            if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                return;
            }
            // get any GTIN extracted by the scanner and look it up on ProductLayer
            String gtin = data.getStringExtra(ScannerActivity.RESULT_GTIN);
            navigationHandler.lookUpProduct(gtin);
        } else {
            // dispatch to fragments
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public PLYAndroidHolder getPLYAndroidHolder() {
        return this;
    }

    @Override
    public PLYAndroid getPLYAndroid() {
        return client;
    }

    @Override
    public AppBarHandler getAppBarHandler() {
        return appBarHandler;
    }

    @Override
    public NavigationHandler getNavigationHandler() {
        return navigationHandler;
    }

    @Override
    public TimelineSettingsHandler getTimelineSettingsHandler() {
        return timelineSettingsHandler;
    }

    @Override
    public UserHandler getUserHandler() {
        return userHandler;
    }

    @Override
    public FloatingActionButtonHandler getFloatingActionButtonHandler() {
        return floatingActionButtonHandler;
    }

    /**
     * Caches categories retrieved via the ProductLayer API in the background.
     */
    private void cacheCategories() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectCache.getCategories(Demo.this, client, false, false);
            }
        }).start();
    }
}
