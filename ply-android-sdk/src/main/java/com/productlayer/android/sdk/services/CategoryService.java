package com.productlayer.android.sdk.services;

import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.core.beans.Category;

import java.util.concurrent.Future;

public class CategoryService {

    /**
     * Gets the category identified by the specified key.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param categoryKey
     *         The category key starting with 'pl-prod-cat-', e.g.: pl-prod-cat-books
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         identified category
     * @return a Future object to optionally wait for the {@code Category} result or to cancel the query
     */
    public static Future<Category> getCategoryForKey(final PLYAndroid client, final String categoryKey,
            final String language, PLYCompletion<Category> completion) {
        return client.submit(new PLYAndroid.Query<Category>() {
            @Override
            public Category execute() {
                return com.productlayer.rest.client.services.CategoryService.getCategoryForKey(client
                        .getRestClient(), categoryKey, language);
            }
        }, completion);
    }

    /**
     * Gets all category keys.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: All
     *         category keys
     * @return a Future object to optionally wait for the {@code String[]} result or to cancel the query
     */
    public static Future<String[]> getCategoryKeys(final PLYAndroid client, PLYCompletion<String[]>
            completion) {
        return client.submit(new PLYAndroid.Query<String[]>() {
            @Override
            public String[] execute() {
                return com.productlayer.rest.client.services.CategoryService.getCategoryKeys(client
                        .getRestClient());
            }
        }, completion);
    }

    /**
     * Gets the main categories with product counts and sub categories.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: All main
     *         categories
     * @return a Future object to optionally wait for the {@code Category[]} result or to cancel the query
     */
    public static Future<Category[]> getMainCategories(final PLYAndroid client, final String language,
            PLYCompletion<Category[]> completion) {
        return client.submit(new PLYAndroid.Query<Category[]>() {
            @Override
            public Category[] execute() {
                return com.productlayer.rest.client.services.CategoryService.getMainCategories(client
                        .getRestClient(), language);
            }
        }, completion);
    }
}
