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
import com.productlayer.core.beans.localization.LocalizedKey;

import java.util.Map;
import java.util.concurrent.Future;

public class LocalizationService {

    /**
     * Gets localized keys for a domain.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param domain
     *         [Optional] The root domain, e.g.: pl-prod
     * @param fetchChilds
     *         [Optional] If true all localizations containing the domain are returned, otherwise only the
     *         specific key will be returned.
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de'), default: 'en'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any keys
     *         belonging to the specific base domain and as their value
     * @return a Future object to optionally wait for the {@code Map<String, String>} result or to cancel the
     * query the translation to the preferred language
     */
    public static Future<Map<String, String>> getLocalizedKeys(final PLYAndroid client, final String
            domain, final boolean fetchChilds, final String language, PLYCompletion<Map<String, String>>
            completion) {
        return client.submit(new PLYAndroid.Query<Map<String, String>>() {
            @Override
            public Map<String, String> execute() {
                return com.productlayer.rest.client.services.LocalizationService.getLocalizedKeys(client
                        .getRestClient(), domain, fetchChilds, language);
            }
        }, completion);
    }

    /**
     * Gets localizable strings property file for Java applications.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param language
     *         The language (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         content of the localizable property file
     * @return a Future object to optionally wait for the {@code String} result or to cancel the query
     */
    public static Future<String> getLocalizedPropertiesFile(final PLYAndroid client, final String language,
            PLYCompletion<String> completion) {
        return client.submit(new PLYAndroid.Query<String>() {
            @Override
            public String execute() {
                return com.productlayer.rest.client.services.LocalizationService.getLocalizedPropertiesFile
                        (client.getRestClient(), language);
            }
        }, completion);
    }

    /**
     * Gets localizable strings file for iOS applications.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param language
     *         The language (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         content of the localizable strings file
     * @return a Future object to optionally wait for the {@code String} result or to cancel the query
     */
    public static Future<String> getLocalizedStringsFile(final PLYAndroid client, final String language,
            PLYCompletion<String> completion) {
        return client.submit(new PLYAndroid.Query<String>() {
            @Override
            public String execute() {
                return com.productlayer.rest.client.services.LocalizationService.getLocalizedStringsFile
                        (client.getRestClient(), language);
            }
        }, completion);
    }

    /**
     * Creates a new localization.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param item
     *         The item
     */
    public static Future<Void> insertLocalizedKey(final PLYAndroid client, final LocalizedKey item,
            PLYCompletion<Void> completion) {
        return client.submit(new PLYAndroid.Query<Void>() {
            @Override
            public Void execute() {
                com.productlayer.rest.client.services.LocalizationService.insertLocalizedKey(client
                        .getRestClient(), item);
                return null;
            }
        }, completion);
    }

    /**
     * Updates a localization.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param item
     *         The item
     */
    public static Future<Void> updateLocalizedKey(final PLYAndroid client, final LocalizedKey item,
            PLYCompletion<Void> completion) {
        return client.submit(new PLYAndroid.Query<Void>() {
            @Override
            public Void execute() {
                com.productlayer.rest.client.services.LocalizationService.updateLocalizedKey(client
                        .getRestClient(), item);
                return null;
            }
        }, completion);
    }
}
