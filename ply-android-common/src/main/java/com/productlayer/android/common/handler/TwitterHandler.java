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
 * Handles the Twitter connection options.
 */
public interface TwitterHandler {

    /**
     * Creates a view enabling a user to sign in using Twitter.
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
    View getTwitterSignInView(Activity targetActivity, LoginHandler loginHandler, PLYAndroid client,
            DialogFragment dialogFragment, PLYAndroid.Query queryOnSuccess, PLYCompletion
            queryOnSuccessCompletion, PLYAndroid.QueryError queryError);

    /**
     * Initiates the Twitter SDK login.
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
    void connectTwitter(Activity targetActivity, LoginHandler loginHandler, Runnable success, Runnable
            failure);

    /**
     * Disconnects the currently logged in user account from Twitter, deauthorizing the app and removing the
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
    void disconnectTwitter(Activity targetActivity, UserHandler userHandler, PLYAndroid client, Runnable
            success, Runnable failure);

    /**
     * @param context
     *         the app context
     * @return whether to share posts on Twitter by default
     */
    boolean isShareOnTwitter(Context context);

    /**
     * Turns on or off default sharing on Twitter.
     *
     * @param context
     *         the app context
     * @param enabled
     *         whether to share posts on Twitter by default
     */
    void setShareOnTwitter(Context context, boolean enabled);

    /**
     * Clears the currently persisted Twitter session.
     */
    void clearLocalTwitterSession();

}
