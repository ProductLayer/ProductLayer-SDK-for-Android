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

package com.productlayer.android.sdk.services;

import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.core.beans.ResultSetWithCursor;

import java.util.concurrent.Future;

public class TimelineService {

    /**
     * Gets the most recent social content posted by the signed in user. The timeline is always sorted by
     * date.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param count
     *         [Optional] The amount of results to be returned, default and maximum: '200'
     * @param sinceID
     *         [Optional] Results with an ID greater than (that is, more recent than) the specified ID
     * @param untilID
     *         [Optional] Results with an ID less than (that is, older than) the specified ID
     * @param showOpines
     *         [Optional] Display opines, default: 'true'
     * @param showReviews
     *         [Optional] Display reviews, default: 'true'
     * @param showPictures
     *         [Optional] Display uploaded images, default: 'true'
     * @param showProducts
     *         [Optional] Display created/updated products, default: 'true'
     * @param includeFriends
     *         [Optional] Show also content created by friends, default: 'false'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         timeline of the currently signed in user and URLs to load the
     * @return a Future object to optionally wait for the {@code ResultSetWithCursor} result or to cancel the
     * query timeline since or until the provided timeline
     */
    public static Future<ResultSetWithCursor> getMyTimeline(final PLYAndroid client, final Integer count,
            final String sinceID, final String untilID, final Boolean showOpines, final Boolean
            showReviews, final Boolean showPictures, final Boolean showProducts, final Boolean
            includeFriends, PLYCompletion<ResultSetWithCursor> completion) {
        return client.submit(new PLYAndroid.Query<ResultSetWithCursor>() {
            @Override
            public ResultSetWithCursor execute() {
                return com.productlayer.rest.client.services.TimelineService.getMyTimeline(client
                        .getRestClient(), count, sinceID, untilID, showOpines, showReviews, showPictures,
                        showProducts, includeFriends);
            }
        }, completion);
    }

    /**
     * Gets the most recent social content for a product. The timeline is always sorted by date.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param count
     *         [Optional] The amount of results to be returned, default and maximum: '200'
     * @param sinceID
     *         [Optional] Results with an ID greater than (that is, more recent than) the specified ID
     * @param untilID
     *         [Optional] Results with an ID less than (that is, older than) the specified ID
     * @param showOpines
     *         [Optional] Display opines, default: 'true'
     * @param showReviews
     *         [Optional] Display reviews, default: 'true'
     * @param showPictures
     *         [Optional] Display uploaded images, default: 'true'
     * @param showProducts
     *         [Optional] Display created/updated products, default: 'true'
     * @param showFriendsOnly
     *         [Optional] Show only content created by friends (followed users), default: 'false'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         timeline and URLs to load the timeline since or until the
     * @return a Future object to optionally wait for the {@code ResultSetWithCursor} result or to cancel the
     * query provided timeline.
     */
    public static Future<ResultSetWithCursor> getProductTimeline(final PLYAndroid client, final String
            gtin, final Integer count, final String sinceID, final String untilID, final Boolean
            showOpines, final Boolean showReviews, final Boolean showPictures, final Boolean showProducts,
            final Boolean showFriendsOnly, PLYCompletion<ResultSetWithCursor> completion) {
        return client.submit(new PLYAndroid.Query<ResultSetWithCursor>() {
            @Override
            public ResultSetWithCursor execute() {
                return com.productlayer.rest.client.services.TimelineService.getProductTimeline(client
                        .getRestClient(), gtin, count, sinceID, untilID, showOpines, showReviews,
                        showPictures, showProducts, showFriendsOnly);
            }
        }, completion);
    }

    /**
     * Gets the most recent social content posted. The timeline is always sorted by date.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param categoryKey
     *         [Optional] The category key starting with 'pl-prod-cat-', e.g.: pl-prod-cat-books
     * @param brand
     *         [Optional] The brand of the product
     * @param brandOwner
     *         [Optional] The brand owner of the product
     * @param count
     *         [Optional] The amount of results to be returned, default and maximum: '200'
     * @param sinceID
     *         [Optional] Results with an ID greater than (that is, more recent than) the specified ID
     * @param untilID
     *         [Optional] Results with an ID less than (that is, older than) the specified ID
     * @param showOpines
     *         [Optional] Display opines, default: 'true'
     * @param showReviews
     *         [Optional] Display reviews, default: 'true'
     * @param showPictures
     *         [Optional] Display uploaded images, default: 'true'
     * @param showProducts
     *         [Optional] Display created/updated products, default: 'true'
     * @param showFriendsOnly
     *         [Optional] Show only content created by friends (followed users), default: 'false'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         timeline and URLs to load the timeline since or until the
     * @return a Future object to optionally wait for the {@code ResultSetWithCursor} result or to cancel the
     * query provided timeline.
     */
    public static Future<ResultSetWithCursor> getTimeline(final PLYAndroid client, final String
            categoryKey, final String brand, final String brandOwner, final Integer count, final String
            sinceID, final String untilID, final Boolean showOpines, final Boolean showReviews, final
    Boolean showPictures, final Boolean showProducts, final Boolean showFriendsOnly,
            PLYCompletion<ResultSetWithCursor> completion) {
        return client.submit(new PLYAndroid.Query<ResultSetWithCursor>() {
            @Override
            public ResultSetWithCursor execute() {
                return com.productlayer.rest.client.services.TimelineService.getTimeline(client
                        .getRestClient(), categoryKey, brand, brandOwner, count, sinceID, untilID,
                        showOpines, showReviews, showPictures, showProducts, showFriendsOnly);
            }
        }, completion);
    }

    /**
     * Gets the most recent social content posted using parameters stored in the URL. The timeline is always
     * sorted by date.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param url
     *         The URL containing the path to the endpoint and any filter parameters
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         timeline and URLs to load the timeline since or until the
     * @return a Future object to optionally wait for the {@code ResultSetWithCursor} result or to cancel the
     * query provided timeline
     */
    public static Future<ResultSetWithCursor> getTimelineFromURL(final PLYAndroid client, final String url,
            PLYCompletion<ResultSetWithCursor> completion) {
        return client.submit(new PLYAndroid.Query<ResultSetWithCursor>() {
            @Override
            public ResultSetWithCursor execute() {
                return com.productlayer.rest.client.services.TimelineService.getTimelineFromURL(client
                        .getRestClient(), url);
            }
        }, completion);
    }

    /**
     * Gets the most recent social content posted by a user ID. The timeline is always sorted by date.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userID
     *         The identifier of the user
     * @param count
     *         [Optional] The amount of results to be returned, default and maximum: '200'
     * @param sinceID
     *         [Optional] Results with an ID greater than (that is, more recent than) the specified ID
     * @param untilID
     *         [Optional] Results with an ID less than (that is, older than) the specified ID
     * @param showOpines
     *         [Optional] Display opines, default: 'true'
     * @param showReviews
     *         [Optional] Display reviews, default: 'true'
     * @param showPictures
     *         [Optional] Display uploaded images, default: 'true'
     * @param showProducts
     *         [Optional] Display created/updated products, default: 'true'
     * @param includeFriends
     *         [Optional] Show also content created by friends, default: 'false'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         timeline of a user and URLs to load the timeline since or
     * @return a Future object to optionally wait for the {@code ResultSetWithCursor} result or to cancel the
     * query until the provided timeline
     */
    public static Future<ResultSetWithCursor> getUserTimeline(final PLYAndroid client, final String userID,
            final Integer count, final String sinceID, final String untilID, final Boolean showOpines,
            final Boolean showReviews, final Boolean showPictures, final Boolean showProducts, final
    Boolean includeFriends, PLYCompletion<ResultSetWithCursor> completion) {
        return client.submit(new PLYAndroid.Query<ResultSetWithCursor>() {
            @Override
            public ResultSetWithCursor execute() {
                return com.productlayer.rest.client.services.TimelineService.getUserTimeline(client
                        .getRestClient(), userID, count, sinceID, untilID, showOpines, showReviews,
                        showPictures, showProducts, includeFriends);
            }
        }, completion);
    }
}
