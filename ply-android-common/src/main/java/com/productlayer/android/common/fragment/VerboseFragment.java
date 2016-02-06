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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment (compat) featuring verbose lifecycle logging.
 */
public abstract class VerboseFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        Log.v(getClass().getSimpleName(), "Entering onAttach ...");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(getClass().getSimpleName(), "Entering onCreate ...");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(getClass().getSimpleName(), "Entering onCreateView ...");
        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(getClass().getSimpleName(), "Entering onActivityCreated ...");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.v(getClass().getSimpleName(), "Entering onStart ...");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.v(getClass().getSimpleName(), "Entering onResume ...");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.v(getClass().getSimpleName(), "Entering onPause ...");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.v(getClass().getSimpleName(), "Entering onStop ...");
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(getClass().getSimpleName(), "Entered onSaveInstanceState ...");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        Log.v(getClass().getSimpleName(), "Entering onDestroyView ...");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.v(getClass().getSimpleName(), "Entering onDestroy ...");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.v(getClass().getSimpleName(), "Entering onDetach ...");
        super.onDetach();
    }

}
