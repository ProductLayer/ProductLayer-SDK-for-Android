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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.productlayer.android.common.handler.AppBarHandler;
import com.productlayer.android.common.handler.HasAppBarHandler;
import com.productlayer.android.common.handler.HasNavigationHandler;
import com.productlayer.android.common.handler.HasPLYAndroidHolder;
import com.productlayer.android.common.handler.HasTimelineSettingsHandler;
import com.productlayer.android.common.handler.HasUserHandler;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.handler.PLYAndroidHolder;
import com.productlayer.android.common.handler.TimelineSettingsHandler;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.common.util.CacheUtil;
import com.productlayer.android.common.util.LocaleUtil;
import com.productlayer.android.common.util.MetricsUtil;
import com.productlayer.android.demo.handler.DemoAppBarHandler;
import com.productlayer.android.demo.handler.DemoNavigationHandler;
import com.productlayer.android.demo.handler.DemoTimelineSettingsHandler;
import com.productlayer.android.demo.handler.DemoUserHandler;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.rest.client.config.PLYRestClientConfig;

/**
 * Demonstrates usage of the ProductLayer SDK and its common components.
 */
public class Demo extends AppCompatActivity implements HasPLYAndroidHolder, PLYAndroidHolder,
        HasAppBarHandler, HasNavigationHandler, HasTimelineSettingsHandler, HasUserHandler {

    private PLYAndroid client;

    private DemoAppBarHandler appBarHandler;
    private DemoNavigationHandler navigationHandler;
    private DemoTimelineSettingsHandler timelineSettingsHandler;
    private DemoUserHandler userHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // use default REST client config and set API key
        PLYRestClientConfig config = new PLYRestClientConfig();
        config.apiKey = getString(R.string.api_demo_key);
        // create PLYAndroid client
        client = new PLYAndroid(config);
        client.setLanguage(LocaleUtil.getDefaultLanguage());
        // get screen data
        MetricsUtil.update(this);
        // set up image caching
        CacheUtil.setupPicassoInstance(getApplicationContext(), CacheUtil.PICASSO_CACHE_MEMORY_PERCENTAGE,
                CacheUtil.PICASSO_CACHE_DISK_MB, false);
        // set up handlers
        appBarHandler = new DemoAppBarHandler();
        navigationHandler = new DemoNavigationHandler();
        timelineSettingsHandler = new DemoTimelineSettingsHandler();
        userHandler = new DemoUserHandler();
        // any previously attached fragments may be recreated
        super.onCreate(savedInstanceState);
        // attach app layout
        setContentView(R.layout.activity_demo);
        // set up app bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_scan) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
