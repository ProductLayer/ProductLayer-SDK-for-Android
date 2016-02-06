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
import com.productlayer.core.beans.Opine;
import com.productlayer.core.beans.Product;
import com.productlayer.core.beans.User;

/**
 * Handles top-level navigation in the app.
 */
public interface NavigationHandler {

    /**
     * Shows the dialog to sign in or up.
     *
     * @param queryOnSuccess
     *         a query to run on successful login (this is used for example if a query fails due to
     *         insufficient credentials and needs to be repeated once logged in) or null
     * @param queryOnSuccessCompletion
     *         a completion callback to go hand in hand with {@code queryOnSuccess} or null
     * @param queryError
     *         the error resulting from {@code queryOnSuccess} if it was run before
     */
    void showSignInDialog(PLYAndroid.Query queryOnSuccess, PLYCompletion queryOnSuccessCompletion,
            PLYAndroid.QueryError queryError);

    /**
     * Shows the dialog to sign in using username and password.
     *
     * @param userName
     *         an optional value to prefill the user name field with
     * @param queryOnSuccess
     *         a query to run on successful login (this is used for example if a query fails due to
     *         insufficient credentials and needs to be repeated once logged in) or null
     * @param queryOnSuccessCompletion
     *         a completion callback to go hand in hand with {@code queryOnSuccess} or null
     * @param queryError
     *         the error resulting from {@code queryOnSuccess} if it was run before
     */
    void showSignInExistingUserDialog(CharSequence userName, PLYAndroid.Query queryOnSuccess, PLYCompletion
            queryOnSuccessCompletion, PLYAndroid.QueryError queryError);

    /**
     * Shows the dialog to sign up as a new user.
     *
     * @param emailAddress
     *         the email address to preset (null for none)
     * @param userName
     *         the user name to preset (null for none)
     * @param queryOnSuccess
     *         a query to run on successful signup (this is used for example if a query fails due to
     *         insufficient credentials and needs to be repeated once logged in) or null
     * @param queryOnSuccessCompletion
     *         a completion callback to go hand in hand with {@code queryOnSuccess} or null
     * @param queryError
     *         the error resulting from {@code queryOnSuccess} if it was run before
     */
    void showSignUpDialog(CharSequence emailAddress, CharSequence userName, PLYAndroid.Query
            queryOnSuccess, PLYCompletion queryOnSuccessCompletion, PLYAndroid.QueryError queryError);

    /**
     * Opens the global timeline.
     */
    void openTimeline();

    /**
     * Looks up the product with the specified GTIN in the ProductLayer database in an async fashion, opening
     * the product page on success or prompting the user to enter details of the new product.
     *
     * @param gtin
     *         the GTIN to look up
     */
    void lookUpProduct(String gtin);

    /**
     * Opens a page enabling the user to enter details and take photos of a new product.
     *
     * @param gtin
     *         the GTIN of the new product
     */
    void openProductCreatePage(String gtin);

    /**
     * Opens a page enabling the user to change details of an existing product.
     *
     * @param product
     *         the product to edit
     */
    void openProductEditPage(Product product);

    /**
     * Opens the product page.
     *
     * @param product
     *         the product to show
     */
    void openProductPage(Product product);

    /**
     * Opens the opinion page.
     *
     * @param product
     *         the product to write an opinion about
     * @param parentOpinion
     *         the opinion to reply to or null if not a reply
     */
    void openOpinionPage(Product product, Opine parentOpinion);

    /**
     * Opens the user profile page.
     *
     * @param user
     *         the user to show
     * @param selectedTab
     *         the index of the tab on the profile page to switch to
     */
    void openProfilePage(User user, int selectedTab);

    /**
     * Opens the user settings page.
     *
     * @param user
     *         the user to edit (= the currently logged in user)
     */
    void openProfileEditPage(User user);

    /**
     * Sends a search query and displays the results.
     *
     * @param query
     *         the query to send
     */
    void search(String query);

}
