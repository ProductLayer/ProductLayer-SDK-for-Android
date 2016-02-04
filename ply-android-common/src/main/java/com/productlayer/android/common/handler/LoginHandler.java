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

import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.core.beans.User;

/**
 * Handles login and logout actions as well as login status queries. Notifies listeners of events.
 */
public interface LoginHandler {

    /**
     * Attempts to either log in the user identified by the specified {@code userName} - {@code password}
     * combination or to resume the session using the token stored in the {@link PLYAndroid} client.
     *
     * @param userName
     *         the user name to log in as
     * @param password
     *         the password set for the user
     * @param resumeSession
     *         whether to attempt resuming the session identified by the client's token instead of logging in
     *         using user name and password
     * @param queryOnSuccess
     *         a query to run on successful login (this is used for example if a query fails due to
     *         insufficient credentials and needs to be repeated once logged in) or null
     * @param queryOnSuccessCompletion
     *         a completion callback to go hand in hand with {@code queryOnSuccess} or null
     * @param queryError
     *         the error resulting from {@code queryOnSuccess} if it was run before
     * @param facebookHandler
     *         the handler to use to clear the facebook token in case resuming a session fails
     * @param twitterHandler
     *         the handler to use to clear the twitter session in case resuming a session fails
     * @param navigationHandler
     *         the handler to use to show the retry dialog if login fails
     */
    void login(String userName, String password, boolean resumeSession, PLYAndroid.Query queryOnSuccess,
            PLYCompletion queryOnSuccessCompletion, PLYAndroid.QueryError queryError, FacebookHandler
            facebookHandler, TwitterHandler twitterHandler, NavigationHandler navigationHandler);

    /**
     * Notifies the ProductLayer servers of a new Facebook token for the currently logged in user. If no user
     * is logged in, either results in a login if a corresponding user is found, or in a signup if this
     * Facebook user ID/email/token is new, or in an error if, for example, the email address is found but
     * that user is not connected to Facebook. If a user is logged in, results in a token update if the
     * Facebook user ID/email/token is not already connected to another user.
     *
     * @param providerKey
     *         the key of the social network (i.e. facebook or twitter)
     * @param providerUserId
     *         the user ID as returned by the social network
     * @param providerToken
     *         the token for authentication returned by the social network
     * @param providerSecret
     *         the secret returned by twitter
     * @param success
     *         code to run on success (on the UI thread)
     * @param failure
     *         code to run on failure (on the UI thread)
     * @param onInvalidToken
     *         code to run if the provided social token is invalid
     */
    void loginWithSocialToken(String providerKey, String providerUserId, String providerToken, String
            providerSecret, Runnable success, Runnable failure, Runnable onInvalidToken);

    /**
     * Logs out the current user.
     *
     * @param userInitiated
     *         whether the action has been initiated by the user
     * @param facebookHandler
     *         the handler to use to clear the facebook token
     * @param twitterHandler
     *         the handler to use to clear the twitter session
     */
    void logout(boolean userInitiated, FacebookHandler facebookHandler, TwitterHandler twitterHandler);

    /**
     * Checks if a token is set locally to be possibly logged in. For a guarantee of being logged in use
     * {@link #isLoggedInRemote(Runnable, Runnable)}.
     *
     * @return true if apparently logged in, false else
     */
    boolean isLoggedIn();

    /**
     * Checks in an async fashion whether the current token is valid.
     *
     * @param onTrue
     *         to be executed if logged in (is not run on the main thread)
     * @param onFalse
     *         to be executed if not logged in (is not run on the main thread)
     */
    void isLoggedInRemote(Runnable onTrue, Runnable onFalse);

    /**
     * Adds a listener to be notified of logins.
     *
     * Only a weak reference to the listener is kept - maintain a strong reference to avoid garbage collection
     * of the listener.
     *
     * @param onLoginListener
     *         the listener to add
     */
    void addOnLoginListener(OnLoginListener onLoginListener);

    /**
     * Removes the specified listener.
     *
     * @param onLoginListener
     *         the listener to remove
     */
    void removeOnLoginListener(OnLoginListener onLoginListener);

    /**
     * Adds a listener to be notified of logouts.
     *
     * Only a weak reference to the listener is kept - maintain a strong reference to avoid garbage collection
     * of the listener.
     *
     * @param onLogoutListener
     *         the listener to add
     */
    void addOnLogoutListener(OnLogoutListener onLogoutListener);

    /**
     * Removes the specified listener.
     *
     * @param onLogoutListener
     *         the listener to remove
     */
    void removeOnLogoutListener(OnLogoutListener onLogoutListener);

    /**
     * Listens for login events.
     */
    interface OnLoginListener {

        /**
         * Called whenever the user logs in.
         *
         * @param user
         *         the logged in user
         */
        void onLogin(User user);

    }

    /**
     * Listens for logout events.
     */
    interface OnLogoutListener {

        /**
         * Called whenever the user logs out.
         */
        void onLogout();

    }

}
