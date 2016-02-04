package com.productlayer.android.sdk.services;

import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.core.beans.BrandOwner;
import com.productlayer.core.beans.Category;
import com.productlayer.core.beans.Count;
import com.productlayer.core.beans.Product;
import com.productlayer.core.beans.ValuesForKey;
import com.productlayer.core.beans.ranking.RankingResults;
import com.productlayer.core.beans.reports.ProblemReport;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Future;

public class ProductService {

    /**
     * Creates a new product. If the user earns points for this operation 'X-ProductLayer-User-Points' and
     * 'X-ProductLayer-User-Points-Changed' will be present in the response header.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param product
     *         The product
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The newly
     *         created product
     * @return a Future object to optionally wait for the {@code Product} result or to cancel the query
     */
    public static Future<Product> createProduct(final PLYAndroid client, final Product product,
            PLYCompletion<Product> completion) {
        return client.submit(new PLYAndroid.Query<Product>() {
            @Override
            public Product execute() {
                return com.productlayer.rest.client.services.ProductService.createProduct(client
                        .getRestClient(), product);
            }
        }, completion);
    }

    /**
     * Downvotes a specific product. By using the product ID instead of the GTIN it's possible to vote for a
     * specific localized product.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param productID
     *         The identifier of the product (identifies a specific localized product, e.g.: Apple iPhone 5S
     *         (en))
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         product with the new vote score
     * @return a Future object to optionally wait for the {@code Product} result or to cancel the query
     */
    public static Future<Product> downVoteProduct(final PLYAndroid client, final String productID,
            PLYCompletion<Product> completion) {
        return client.submit(new PLYAndroid.Query<Product>() {
            @Override
            public Product execute() {
                return com.productlayer.rest.client.services.ProductService.downVoteProduct(client
                        .getRestClient(), productID);
            }
        }, completion);
    }

    /**
     * Gets suggestions of brand owners of a GTIN.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any
     *         suggested brand owners for a GTIN
     * @return a Future object to optionally wait for the {@code BrandOwner[]} result or to cancel the query
     */
    public static Future<BrandOwner[]> getBrandOwnerSuggestions(final PLYAndroid client, final String gtin,
            PLYCompletion<BrandOwner[]> completion) {
        return client.submit(new PLYAndroid.Query<BrandOwner[]>() {
            @Override
            public BrandOwner[] execute() {
                return com.productlayer.rest.client.services.ProductService.getBrandOwnerSuggestions(client
                        .getRestClient(), gtin);
            }
        }, completion);
    }

    /**
     * Gets known brand owner names.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: All brand
     *         owner names
     * @return a Future object to optionally wait for the {@code String[]} result or to cancel the query
     */
    public static Future<String[]> getBrandOwners(final PLYAndroid client, PLYCompletion<String[]>
            completion) {
        return client.submit(new PLYAndroid.Query<String[]>() {
            @Override
            public String[] execute() {
                return com.productlayer.rest.client.services.ProductService.getBrandOwners(client
                        .getRestClient());
            }
        }, completion);
    }

    /**
     * Gets known brand names.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: All brand
     *         names
     * @return a Future object to optionally wait for the {@code String[]} result or to cancel the query
     */
    public static Future<String[]> getBrands(final PLYAndroid client, PLYCompletion<String[]> completion) {
        return client.submit(new PLYAndroid.Query<String[]>() {
            @Override
            public String[] execute() {
                return com.productlayer.rest.client.services.ProductService.getBrands(client.getRestClient());
            }
        }, completion);
    }

    /**
     * Gets suggestions of categories of a GTIN.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any
     *         suggested categories for a GTIN
     * @return a Future object to optionally wait for the {@code Category[]} result or to cancel the query
     */
    public static Future<Category[]> getCategorySuggestions(final PLYAndroid client, final String gtin,
            PLYCompletion<Category[]> completion) {
        return client.submit(new PLYAndroid.Query<Category[]>() {
            @Override
            public Category[] execute() {
                return com.productlayer.rest.client.services.ProductService.getCategorySuggestions(client
                        .getRestClient(), gtin);
            }
        }, completion);
    }

    /**
     * Gets the scoring history within a range from a product.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param from_date
     *         [Optional] Start date, format: yyyy-MM-dd HH:mm:ss
     * @param to_date
     *         [Optional] End date, format: yyyy-MM-dd HH:mm:ss
     * @param count
     *         [Optional] The amount of results to be returned, default: '200'
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de'), default: 'en'
     * @param showOpines
     *         [Optional] Display opines, default: 'true'
     * @param showReviews
     *         [Optional] Display reviews, default: 'true'
     * @param showPictures
     *         [Optional] Display uploaded images, default: 'true'
     * @param showProducts
     *         [Optional] Display created/updated products, default: 'true'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         hottest products contained within a ranking results object
     * @return a Future object to optionally wait for the {@code RankingResults<Product>} result or to cancel
     * the query
     */
    public static Future<RankingResults<Product>> getHottestProducts(final PLYAndroid client, final Date
            from_date, final Date to_date, final Integer count, final String language, final Boolean
            showOpines, final Boolean showReviews, final Boolean showPictures, final Boolean showProducts,
            PLYCompletion<RankingResults<Product>> completion) {
        return client.submit(new PLYAndroid.Query<RankingResults<Product>>() {
            @Override
            public RankingResults<Product> execute() {
                return com.productlayer.rest.client.services.ProductService.getHottestProducts(client
                        .getRestClient(), from_date, to_date, count, language, showOpines, showReviews,
                        showPictures, showProducts);
            }
        }, completion);
    }

    /**
     * Gets localized category keys.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de'), default: 'en'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: All
     *         category keys and as their value the translation to the
     * @return a Future object to optionally wait for the {@code Map<String, String>} result or to cancel the
     * query preferred language
     */
    public static Future<Map<String, String>> getLocalizedCategories(final PLYAndroid client, final String
            language, PLYCompletion<Map<String, String>> completion) {
        return client.submit(new PLYAndroid.Query<Map<String, String>>() {
            @Override
            public Map<String, String> execute() {
                return com.productlayer.rest.client.services.ProductService.getLocalizedCategories(client
                        .getRestClient(), language);
            }
        }, completion);
    }

    /**
     * Gets localized characteristics keys.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de'), default: 'en'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: All
     *         characteristics keys and as their value the translation to
     * @return a Future object to optionally wait for the {@code Map<String, String>} result or to cancel the
     * query the preferred language
     */
    public static Future<Map<String, String>> getLocalizedCharacteristics(final PLYAndroid client, final
    String language, PLYCompletion<Map<String, String>> completion) {
        return client.submit(new PLYAndroid.Query<Map<String, String>>() {
            @Override
            public Map<String, String> execute() {
                return com.productlayer.rest.client.services.ProductService.getLocalizedCharacteristics
                        (client.getRestClient(), language);
            }
        }, completion);
    }

    /**
     * Gets localized nutrition keys.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de'), default: 'en'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: All
     *         nutrition keys and as their value the translation to the
     * @return a Future object to optionally wait for the {@code Map<String, String>} result or to cancel the
     * query preferred language
     */
    public static Future<Map<String, String>> getLocalizedNutrition(final PLYAndroid client, final String
            language, PLYCompletion<Map<String, String>> completion) {
        return client.submit(new PLYAndroid.Query<Map<String, String>>() {
            @Override
            public Map<String, String> execute() {
                return com.productlayer.rest.client.services.ProductService.getLocalizedNutrition(client
                        .getRestClient(), language);
            }
        }, completion);
    }

    /**
     * Gets the overall product count or the count for a specific timeframe.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param from_date
     *         [Optional] Start date, format: yyyy-MM-dd HH:mm:ss
     * @param to_date
     *         [Optional] End date, format: yyyy-MM-dd HH:mm:ss
     * @param categoryKey
     *         [Optional] The category key starting with 'pl-prod-cat-', e.g.: pl-prod-cat-books
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         product count
     * @return a Future object to optionally wait for the {@code Count} result or to cancel the query
     */
    public static Future<Count> getProductCount(final PLYAndroid client, final Date from_date, final Date
            to_date, final String categoryKey, PLYCompletion<Count> completion) {
        return client.submit(new PLYAndroid.Query<Count>() {
            @Override
            public Count execute() {
                return com.productlayer.rest.client.services.ProductService.getProductCount(client
                        .getRestClient(), from_date, to_date, categoryKey);
            }
        }, completion);
    }

    /**
     * Gets a product by GTIN and language. If language=auto the best language match will be returned. Best
     * language match using preferred language header and predefined secondary languages.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de')
     * @param suggestions
     *         [Optional] Make product suggestions if search returns no results. Product suggestions are all
     *         returned products without a pl-id parameter. Default: false
     * @param fetchOnly
     *         [Optional] Fetch only specific keys
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         identified product
     * @return a Future object to optionally wait for the {@code Product} result or to cancel the query
     */
    public static Future<Product> getProductForGtin(final PLYAndroid client, final String gtin, final
    String language, final Boolean suggestions, final String fetchOnly, PLYCompletion<Product> completion) {
        return client.submit(new PLYAndroid.Query<Product>() {
            @Override
            public Product execute() {
                return com.productlayer.rest.client.services.ProductService.getProductForGtin(client
                        .getRestClient(), gtin, language, suggestions, fetchOnly);
            }
        }, completion);
    }

    /**
     * Gets a product by GTIN and language.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param language
     *         The preferred language (e.g.: 'en' or 'de')
     * @param searchAlsoOtherLocales
     *         Whether to also try different locales if the one specified is not found
     * @param fetchOnly
     *         [Optional] Fetch only specific keys
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         identified product or null if not found
     * @return a Future object to optionally wait for the {@code Product} result or to cancel the query
     */
    public static Future<Product> getProductForGtinAndLocale(final PLYAndroid client, final String gtin,
            final String language, final boolean searchAlsoOtherLocales, final boolean suggestions, final
    String fetchOnly, PLYCompletion<Product> completion) {
        return client.submit(new PLYAndroid.Query<Product>() {
            @Override
            public Product execute() {
                return com.productlayer.rest.client.services.ProductService.getProductForGtinAndLocale
                        (client.getRestClient(), gtin, language, searchAlsoOtherLocales, suggestions,
                                fetchOnly);
            }
        }, completion);
    }

    /**
     * Gets the values of a specific key.<br> <br> e.g.: ?key=pl-brand-name&amp;language=en returns all brands
     * which have been entered for products with the locale en.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param key
     *         The key to query the values of.
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de'), default: 'en'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: All values
     *         of the specified key
     * @return a Future object to optionally wait for the {@code ValuesForKey} result or to cancel the query
     */
    public static Future<ValuesForKey> getValuesForKey(final PLYAndroid client, final String key, final
    String language, PLYCompletion<ValuesForKey> completion) {
        return client.submit(new PLYAndroid.Query<ValuesForKey>() {
            @Override
            public ValuesForKey execute() {
                return com.productlayer.rest.client.services.ProductService.getValuesForKey(client
                        .getRestClient(), key, language);
            }
        }, completion);
    }

    /**
     * Sends a report about copyright infringements or any other problems with the product.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param productID
     *         The identifier of the product (identifies a specific localized product, e.g.: Apple iPhone 5S
     *         (en))
     * @param report
     *         The report
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         problem report object
     * @return a Future object to optionally wait for the {@code ProblemReport} result or to cancel the query
     */
    public static Future<ProblemReport> reportProduct(final PLYAndroid client, final String productID,
            final ProblemReport report, PLYCompletion<ProblemReport> completion) {
        return client.submit(new PLYAndroid.Query<ProblemReport>() {
            @Override
            public ProblemReport execute() {
                return com.productlayer.rest.client.services.ProductService.reportProduct(client
                        .getRestClient(), productID, report);
            }
        }, completion);
    }

    /**
     * Searches for products. If no search parameters are present the first 50 products will be presented.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param query
     *         [Optional] The query may contain the name, GTIN or brand of the product. <b>ATTENTION: If the
     *         query is set all other url parameters will be ignored!</b>
     * @param page
     *         [Optional] The page to be displayed starting with 0 - if no page has been provided, the first
     *         page will be shown
     * @param recordsPerPage
     *         [Optional] The amount of items to be displayed per page, default: '200'
     * @param gtin
     *         [Optional] The GTIN (barcode) of the product
     * @param brand
     *         [Optional] The brand of the product
     * @param brandOwner
     *         [Optional] The brand owner of the product
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de')
     * @param suggestions
     *         [Optional] Make product suggestions if search returns no results. Product suggestions are all
     *         returned products without a pl-id parameter. Default: false
     * @param fetchOnly
     *         [Optional] Fetch only specific keys
     * @param name
     *         [Optional] The name of the product or a substring of it.
     * @param categoryKey
     *         [Optional] The category key starting with 'pl-prod-cat-', e.g.: pl-prod-cat-books
     * @param order_by
     *         [Optional] Used to sort the result-set by one or more columns. The order by parameters are
     *         <strong>seperated by a semicolon</strong>. Also you need to provide a prefix <strong>asc for
     *         ascending</strong> or <strong>desc for descending order</strong><br> <br>
     *         <strong>Default:</strong> pl-prod-name_asc (Product names ascending)
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any
     *         products matching the specified criteria
     * @return a Future object to optionally wait for the {@code Product[]} result or to cancel the query
     */
    public static Future<Product[]> searchProducts(final PLYAndroid client, final String query, final
    Integer page, final Integer recordsPerPage, final String gtin, final String brand, final String
            brandOwner, final String language, final Boolean suggestions, final String fetchOnly, final
    String name, final String categoryKey, final String order_by, PLYCompletion<Product[]> completion) {
        return client.submit(new PLYAndroid.Query<Product[]>() {
            @Override
            public Product[] execute() {
                return com.productlayer.rest.client.services.ProductService.searchProducts(client
                        .getRestClient(), query, page, recordsPerPage, gtin, brand, brandOwner, language,
                        suggestions, fetchOnly, name, categoryKey, order_by);
            }
        }, completion);
    }

    /**
     * Searches for products by brand.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param brand
     *         The brand of the product
     * @param page
     *         [Optional] The page to be displayed starting with 0 - if no page has been provided, the first
     *         page will be shown
     * @param recordsPerPage
     *         [Optional] The amount of items to be displayed per page, default: '200'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any
     *         products matching the brand
     * @return a Future object to optionally wait for the {@code Product[]} result or to cancel the query
     */
    public static Future<Product[]> searchProductsByBrand(final PLYAndroid client, final String brand,
            final Integer page, final Integer recordsPerPage, PLYCompletion<Product[]> completion) {
        return client.submit(new PLYAndroid.Query<Product[]>() {
            @Override
            public Product[] execute() {
                return com.productlayer.rest.client.services.ProductService.searchProductsByBrand(client
                        .getRestClient(), brand, page, recordsPerPage);
            }
        }, completion);
    }

    /**
     * Searches for products by brand owner.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param brandOwner
     *         The brand owner of the product
     * @param page
     *         [Optional] The page to be displayed starting with 0 - if no page has been provided, the first
     *         page will be shown
     * @param recordsPerPage
     *         [Optional] The amount of items to be displayed per page, default: '200'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any
     *         products matching the brand owner
     * @return a Future object to optionally wait for the {@code Product[]} result or to cancel the query
     */
    public static Future<Product[]> searchProductsByBrandOwner(final PLYAndroid client, final String
            brandOwner, final Integer page, final Integer recordsPerPage, PLYCompletion<Product[]>
            completion) {
        return client.submit(new PLYAndroid.Query<Product[]>() {
            @Override
            public Product[] execute() {
                return com.productlayer.rest.client.services.ProductService.searchProductsByBrandOwner
                        (client.getRestClient(), brandOwner, page, recordsPerPage);
            }
        }, completion);
    }

    /**
     * Searches for a product by GTIN (more than one result may be returned). The GTIN is unique for a product
     * but a Product object will be returned per locale.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param gtin
     *         [Optional] The GTIN (barcode) of the product
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any
     *         products matching the GTIN
     * @return a Future object to optionally wait for the {@code Product[]} result or to cancel the query
     */
    public static Future<Product[]> searchProductsByGtin(final PLYAndroid client, final String gtin, final
    boolean suggestions, PLYCompletion<Product[]> completion) {
        return client.submit(new PLYAndroid.Query<Product[]>() {
            @Override
            public Product[] execute() {
                return com.productlayer.rest.client.services.ProductService.searchProductsByGtin(client
                        .getRestClient(), gtin, suggestions);
            }
        }, completion);
    }

    /**
     * Searches for products by using the query string.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param query
     *         The query may contain the name, GTIN or brand of the product
     * @param categoryKey
     *         [Optional] The category key starting with 'pl-prod-cat-', e.g.: pl-prod-cat-books
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any
     *         products matching the specified criteria
     * @return a Future object to optionally wait for the {@code Product[]} result or to cancel the query
     */
    public static Future<Product[]> searchProductsByQuery(final PLYAndroid client, final String query,
            final String categoryKey, final boolean suggestions, PLYCompletion<Product[]> completion) {
        return client.submit(new PLYAndroid.Query<Product[]>() {
            @Override
            public Product[] execute() {
                return com.productlayer.rest.client.services.ProductService.searchProductsByQuery(client
                        .getRestClient(), query, categoryKey, suggestions);
            }
        }, completion);
    }

    /**
     * Upvotes a specific product. By using the product ID instead of the GTIN it's possible to vote for a
     * specific localized product.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param productID
     *         The identifier of the product (identifies a specific localized product, e.g.: Apple iPhone 5S
     *         (en))
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         product with the new vote score
     * @return a Future object to optionally wait for the {@code Product} result or to cancel the query
     */
    public static Future<Product> upVoteProduct(final PLYAndroid client, final String productID,
            PLYCompletion<Product> completion) {
        return client.submit(new PLYAndroid.Query<Product>() {
            @Override
            public Product execute() {
                return com.productlayer.rest.client.services.ProductService.upVoteProduct(client
                        .getRestClient(), productID);
            }
        }, completion);
    }

    /**
     * Updates a specific product. If the user earns points for this operation 'X-ProductLayer-User-Points'
     * and 'X-ProductLayer-User-Points-Changed' will be present in the response header.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param product
     *         The product
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated product
     * @return a Future object to optionally wait for the {@code Product} result or to cancel the query
     */
    public static Future<Product> updateProduct(final PLYAndroid client, final Product product,
            PLYCompletion<Product> completion) {
        return client.submit(new PLYAndroid.Query<Product>() {
            @Override
            public Product execute() {
                return com.productlayer.rest.client.services.ProductService.updateProduct(client
                        .getRestClient(), product);
            }
        }, completion);
    }

    /**
     * Updates a specific product. If the user earns points for this operation 'X-ProductLayer-User-Points'
     * and 'X-ProductLayer-User-Points-Changed' will be present in the response header.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param product
     *         The product
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated product
     * @return a Future object to optionally wait for the {@code Product} result or to cancel the query
     */
    public static Future<Product> updateProduct(final PLYAndroid client, final String gtin, final Product
            product, PLYCompletion<Product> completion) {
        return client.submit(new PLYAndroid.Query<Product>() {
            @Override
            public Product execute() {
                return com.productlayer.rest.client.services.ProductService.updateProduct(client
                        .getRestClient(), gtin, product);
            }
        }, completion);
    }
}
