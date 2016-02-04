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

package com.productlayer.android.common.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

/**
 * Activity (compat) featuring verbose lifecycle logging.
 */
public abstract class VerboseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(getClass().getSimpleName(), "Entering onCreate ...");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        Log.v(getClass().getSimpleName(), "Entering onStart ...");
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(getClass().getSimpleName(), "Entering onRestoreInstanceState ...");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        Log.v(getClass().getSimpleName(), "Entering onResume ...");
        super.onResume();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.v(getClass().getSimpleName(), "Entering onPostCreate ...");
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(getClass().getSimpleName(), "Entered onCreateOptionsMenu ...");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.v(getClass().getSimpleName(), "Entered onPrepareOptionsMenu ...");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        Log.v(getClass().getSimpleName(), "Entering onPause ...");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(getClass().getSimpleName(), "Entered onSaveInstanceState ...");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        Log.v(getClass().getSimpleName(), "Entering onStop ...");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.v(getClass().getSimpleName(), "Entering onRestart ...");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.v(getClass().getSimpleName(), "Entering onDestroy ...");
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.v(getClass().getSimpleName(), "Entering onConfigurationChanged ...");
        super.onConfigurationChanged(newConfig);
    }

}
