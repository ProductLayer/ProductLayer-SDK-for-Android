package com.productlayer.android.sdk.services;

import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.core.beans.lists.ProductList;
import com.productlayer.core.beans.lists.ProductListItem;

import java.util.concurrent.Future;

public class ProductListService {

    /**
     * Adds the product to the list or, if it exists, replaces it.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param productlistId
     *         The identifier of the product list
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param language
     *         [Optional] The preferred language of the product (e.g.: 'en' or 'de')
     * @param listItem
     *         The listItem
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated product list
     * @return a Future object to optionally wait for the {@code ProductList} result or to cancel the query
     */
    public static Future<ProductList> addToProductList(final PLYAndroid client, final String productlistId,
            final String gtin, final String language, final ProductListItem listItem,
            PLYCompletion<ProductList> completion) {
        return client.submit(new PLYAndroid.Query<ProductList>() {
            @Override
            public ProductList execute() {
                return com.productlayer.rest.client.services.ProductListService.addToProductList(client
                        .getRestClient(), productlistId, gtin, language, listItem);
            }
        }, completion);
    }

    /**
     * Creates a new product list for the authenticated user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param list
     *         The list
     * @param language
     *         [Optional] The preferred language of the product (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The newly
     *         created product list
     * @return a Future object to optionally wait for the {@code ProductList} result or to cancel the query
     */
    public static Future<ProductList> createNewProductList(final PLYAndroid client, final ProductList list,
            final String language, PLYCompletion<ProductList> completion) {
        return client.submit(new PLYAndroid.Query<ProductList>() {
            @Override
            public ProductList execute() {
                return com.productlayer.rest.client.services.ProductListService.createNewProductList(client
                        .getRestClient(), list, language);
            }
        }, completion);
    }

    /**
     * Deletes a product from the list.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param productlistId
     *         The identifier of the product list
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param language
     *         [Optional] The preferred language for the loaded product objects.
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated product list
     * @return a Future object to optionally wait for the {@code ProductList} result or to cancel the query
     */
    public static Future<ProductList> deleteFromProductList(final PLYAndroid client, final String
            productlistId, final String gtin, final String language, PLYCompletion<ProductList> completion) {
        return client.submit(new PLYAndroid.Query<ProductList>() {
            @Override
            public ProductList execute() {
                return com.productlayer.rest.client.services.ProductListService.deleteFromProductList
                        (client.getRestClient(), productlistId, gtin, language);
            }
        }, completion);
    }

    /**
     * Deletes the product list matching the ID.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param productlistId
     *         The identifier of the product list
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         deleted product list
     * @return a Future object to optionally wait for the {@code ProductList} result or to cancel the query
     */
    public static Future<ProductList> deleteProductList(final PLYAndroid client, final String
            productlistId, PLYCompletion<ProductList> completion) {
        return client.submit(new PLYAndroid.Query<ProductList>() {
            @Override
            public ProductList execute() {
                return com.productlayer.rest.client.services.ProductListService.deleteProductList(client
                        .getRestClient(), productlistId);
            }
        }, completion);
    }

    /**
     * Gets a product list by ID.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param productlistId
     *         The identifier of the product list
     * @param language
     *         [Optional] The preferred language of the product (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         identified product list
     * @return a Future object to optionally wait for the {@code ProductList} result or to cancel the query
     */
    public static Future<ProductList> getProductList(final PLYAndroid client, final String productlistId,
            final String language, PLYCompletion<ProductList> completion) {
        return client.submit(new PLYAndroid.Query<ProductList>() {
            @Override
            public ProductList execute() {
                return com.productlayer.rest.client.services.ProductListService.getProductList(client
                        .getRestClient(), productlistId, language);
            }
        }, completion);
    }

    /**
     * Gets product lists matching certain criteria.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param type
     *         [Optional] The type can be: <ul> <li>owned (Shows only product lists which are owned by the
     *         user.)</li> <li>shared (Shows only product lists which have been shared by the user.)</li>
     *         <li>public (Shows all public product lists.)</li> </ul>
     * @param user_id
     *         [Optional] The identifier of the user
     * @param page
     *         [Optional] The page to be displayed starting with 0 - if no page has been provided, the first
     *         page will be shown
     * @param recordsPerPage
     *         [Optional] The amount of items to be displayed per page, default: '200'
     * @param language
     *         [Optional] The preferred language of the product (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Product
     *         lists matching the specified criteria
     * @return a Future object to optionally wait for the {@code ProductList[]} result or to cancel the query
     */
    public static Future<ProductList[]> searchProductLists(final PLYAndroid client, final String type,
            final String user_id, final Integer page, final Integer recordsPerPage, final String language,
            PLYCompletion<ProductList[]> completion) {
        return client.submit(new PLYAndroid.Query<ProductList[]>() {
            @Override
            public ProductList[] execute() {
                return com.productlayer.rest.client.services.ProductListService.searchProductLists(client
                        .getRestClient(), type, user_id, page, recordsPerPage, language);
            }
        }, completion);
    }

    /**
     * Gets a user's product lists matching certain criteria.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param listType
     *         [Optional] The type of the product list
     * @param userId
     *         The identifier of the user
     * @param page
     *         [Optional] The page to be displayed starting with 0 - if no page has been provided, the first
     *         page will be shown
     * @param recordsPerPage
     *         [Optional] The amount of items to be displayed per page, default: '200'
     * @param language
     *         [Optional] The preferred language of the product (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Product
     *         lists of the user matching the specified criteria
     * @return a Future object to optionally wait for the {@code ProductList[]} result or to cancel the query
     */
    public static Future<ProductList[]> searchUserProductLists(final PLYAndroid client, final String
            listType, final String userId, final Integer page, final Integer recordsPerPage, final String
            language, PLYCompletion<ProductList[]> completion) {
        return client.submit(new PLYAndroid.Query<ProductList[]>() {
            @Override
            public ProductList[] execute() {
                return com.productlayer.rest.client.services.ProductListService.searchUserProductLists
                        (client.getRestClient(), listType, userId, page, recordsPerPage, language);
            }
        }, completion);
    }

    /**
     * Shares a list with a user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param productlistId
     *         The identifier of the product list
     * @param userId
     *         The identifier of the user
     * @param language
     *         [Optional] The preferred language of the product (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated product list
     * @return a Future object to optionally wait for the {@code ProductList} result or to cancel the query
     */
    public static Future<ProductList> shareProductList(final PLYAndroid client, final String productlistId,
            final String userId, final String language, PLYCompletion<ProductList> completion) {
        return client.submit(new PLYAndroid.Query<ProductList>() {
            @Override
            public ProductList execute() {
                return com.productlayer.rest.client.services.ProductListService.shareProductList(client
                        .getRestClient(), productlistId, userId, language);
            }
        }, completion);
    }

    /**
     * Unshares a list with a user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param productlistId
     *         The identifier of the product list
     * @param userId
     *         The identifier of the user
     * @param language
     *         [Optional] The preferred language of the product (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated product list
     * @return a Future object to optionally wait for the {@code ProductList} result or to cancel the query
     */
    public static Future<ProductList> unshareProductList(final PLYAndroid client, final String
            productlistId, final String userId, final String language, PLYCompletion<ProductList>
            completion) {
        return client.submit(new PLYAndroid.Query<ProductList>() {
            @Override
            public ProductList execute() {
                return com.productlayer.rest.client.services.ProductListService.unshareProductList(client
                        .getRestClient(), productlistId, userId, language);
            }
        }, completion);
    }

    /**
     * Updates a product list.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param productlistId
     *         The identifier of the product list
     * @param language
     *         [Optional] The preferred language of the product (e.g.: 'en' or 'de')
     * @param list
     *         The list
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated product list
     * @return a Future object to optionally wait for the {@code ProductList} result or to cancel the query
     */
    public static Future<ProductList> updateProductList(final PLYAndroid client, final String
            productlistId, final String language, final ProductList list, PLYCompletion<ProductList>
            completion) {
        return client.submit(new PLYAndroid.Query<ProductList>() {
            @Override
            public ProductList execute() {
                return com.productlayer.rest.client.services.ProductListService.updateProductList(client
                        .getRestClient(), productlistId, language, list);
            }
        }, completion);
    }
}
