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

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;

/**
 * Handles the Facebook connection options.
 */
public interface FacebookHandler {

    /**
     * Creates a view enabling a user to sign in using Facebook.
     *
     * @param targetActivity
     *         the activity to receiving the result
     * @param loginHandler
     *         the login handler
     * @param client
     *         the PLYAndroid REST client
     * @param dialogFragment
     *         the dialog the sign in view will be attached to
     * @param queryOnSuccess
     *         a query to run on successful login (this is used for example if a query fails due to
     *         insufficient credentials and needs to be repeated once logged in) or null
     * @param queryOnSuccessCompletion
     *         a completion callback to go hand in hand with {@code queryOnSuccess} or null
     * @param queryError
     *         the error resulting from {@code queryOnSuccess} if it was run before
     * @return the view enabling a user to sign in using Facebook
     */
    View getFacebookSignInView(Activity targetActivity, LoginHandler loginHandler, PLYAndroid client,
            DialogFragment dialogFragment, PLYAndroid.Query queryOnSuccess, PLYCompletion
            queryOnSuccessCompletion, PLYAndroid.QueryError queryError);

    /**
     * Initiates the Facebook SDK login.
     *
     * @param targetActivity
     *         the activity to receiving the result
     * @param loginHandler
     *         the login handler
     * @param success
     *         code to run on success (on the UI thread)
     * @param failure
     *         code to run on failure (on the UI thread)
     */
    void connectFacebook(Activity targetActivity, LoginHandler loginHandler, Runnable success, Runnable
            failure);

    /**
     * Disconnects the currently logged in user account from Facebook, deauthorizing the app and removing the
     * ProductLayer API link.
     *
     * @param targetActivity
     *         the activity to receiving the result
     * @param userHandler
     *         the user handler
     * @param client
     *         the PLYAndroid REST client
     * @param success
     *         code to run on success (on the UI thread)
     * @param failure
     *         code to run on failure (on the UI thread)
     */
    void disconnectFacebook(Activity targetActivity, UserHandler userHandler, PLYAndroid client, Runnable
            success, Runnable failure);

    /**
     * @return true if the current user has Facebook publish rights, false else
     */
    boolean hasFacebookPublishPermission();

    /**
     * Attempts to get Facebook publish permissions from the user if not yet granted or declined. If the
     * permission has to be granted by the user the Facebook access token is updated as well.
     *
     * @param targetActivity
     *         the activity to receiving the result
     * @param loginHandler
     *         the login handler
     * @param success
     *         code to run on success (on the UI thread)
     * @param failure
     *         code to run on failure (on the UI thread)
     */
    void requestFacebookPublishPermission(Activity targetActivity, LoginHandler loginHandler, Runnable
            success, Runnable failure);

    /**
     * @param context
     *         the app context
     * @return whether to share posts on Facebook by default
     */
    boolean isShareOnFacebook(Context context);

    /**
     * Turns on or off default sharing on Facebook.
     *
     * @param context
     *         the app context
     * @param enabled
     *         whether to share posts on Facebook by default
     */
    void setShareOnFacebook(Context context, boolean enabled);

    /**
     * Extends the Facebook token with all permissions if it has expired or is close to doing so.
     *
     * @param targetActivity
     *         the activity to receiving the result
     * @param loginHandler
     *         the login handler
     * @return true if a renewal of the token was initiated, false else
     */
    boolean renewFacebookToken(Activity targetActivity, LoginHandler loginHandler);

    /**
     * Clears the locally stored Facebook access token.
     */
    void clearLocalFacebookToken();

}
