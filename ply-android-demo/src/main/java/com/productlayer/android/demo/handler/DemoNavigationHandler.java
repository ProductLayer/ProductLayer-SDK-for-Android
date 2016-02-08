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

package com.productlayer.android.demo.handler;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.productlayer.android.common.fragment.GlobalTimelineFragment;
import com.productlayer.android.common.fragment.ProductFragment;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.ProductService;
import com.productlayer.core.beans.Opine;
import com.productlayer.core.beans.Product;
import com.productlayer.core.beans.User;
import com.productlayer.core.error.PLYStatusCodes;

/**
 * Handles navigation between the fragments of the demo app.
 */
public class DemoNavigationHandler implements NavigationHandler {

    private static final String TAG = DemoNavigationHandler.class.getSimpleName();

    private final FragmentManager fragmentManager;
    private final int contentViewId;
    private final PLYAndroid client;

    /**
     * Creates a new handler replacing fragments in {@code contentViewId} to navigate through the app.
     *
     * @param fragmentManager
     *         the activity's fragment manager
     * @param contentViewId
     *         the view to hold new fragments
     * @param client
     *         the PLYAndroid REST client to connect to ProductLayer
     */
    public DemoNavigationHandler(FragmentManager fragmentManager, int contentViewId, PLYAndroid client) {
        this.fragmentManager = fragmentManager;
        this.contentViewId = contentViewId;
        this.client = client;
    }

    @Override
    public void showSignInDialog(PLYAndroid.Query queryOnSuccess, PLYCompletion queryOnSuccessCompletion,
            PLYAndroid.QueryError queryError) {
    }

    @Override
    public void showSignInExistingUserDialog(CharSequence userName, PLYAndroid.Query queryOnSuccess,
            PLYCompletion queryOnSuccessCompletion, PLYAndroid.QueryError queryError) {
    }

    @Override
    public void showSignUpDialog(CharSequence emailAddress, CharSequence userName, PLYAndroid.Query
            queryOnSuccess, PLYCompletion queryOnSuccessCompletion, PLYAndroid.QueryError queryError) {
    }

    @Override
    public void openTimeline() {
        // create new timeline fragment and attach it
        replaceContent(new GlobalTimelineFragment(), false);
    }

    @Override
    public void lookUpProduct(final String gtin) {
        ProductService.getProductForGtin(client, gtin, null, false, null, new PLYCompletion<Product>() {
            @Override
            public void onSuccess(Product result) {
                openProductPage(result);
            }

            @Override
            public void onError(final PLYAndroid.QueryError error) {
                if (error.isHttpStatusError()) {
                    if (error.getHttpStatusCode() == PLYStatusCodes.HTTP_STATUS_NOT_FOUND_CODE) {
                        Log.i(TAG, "The product with GTIN " + gtin + " is not yet known to ProductLayer.");
                    }
                } else {
                    Log.w(TAG, "Connection failed - please make sure you are connected!");
                }
            }
        });
    }

    @Override
    public void openProductCreatePage(String gtin) {
    }

    @Override
    public void openProductEditPage(Product product) {
    }

    @Override
    public void openProductPage(Product product) {
        // create new product fragment and attach it
        Fragment productFragment = ProductFragment.newInstance(product);
        replaceContent(productFragment, true);
    }

    @Override
    public void openOpinionPage(Product product, Opine parentOpinion) {
    }

    @Override
    public void openProfilePage(User user, int selectedTab) {
    }

    @Override
    public void openProfileEditPage(User user) {
    }

    @Override
    public void search(String query) {
    }

    /**
     * Replaces the fragment in the content view.
     *
     * @param fragment
     *         the fragment replacing the one currently displayed
     * @param addToBackStack
     *         true if the previous state may be restored, false else
     */
    private void replaceContent(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(contentViewId, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }
}
