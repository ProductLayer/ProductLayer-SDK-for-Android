package com.productlayer.android.sdk.services;

import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.core.beans.Opine;
import com.productlayer.core.beans.User;
import com.productlayer.core.beans.social.SocialOpine;

import java.net.URI;
import java.util.concurrent.Future;

public class SocialService {

    /**
     * Connects the user to a social network provider like Twitter or Facebook. After establishing a
     * connection to the provider on behalf of the member the user will be redirected to the provided URL.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param provider
     *         The social service provider, currently valid are only facebook and twitter
     * @param redirectUrl
     *         The URL for redirecting after provider connect
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The URL to
     *         redirect to
     * @return a Future object to optionally wait for the {@code URI} result or to cancel the query
     */
    public static Future<URI> connectProvider(final PLYAndroid client, final String provider, final String
            redirectUrl, PLYCompletion<URI> completion) {
        return client.submit(new PLYAndroid.Query<URI>() {
            @Override
            public URI execute() {
                return com.productlayer.rest.client.services.SocialService.connectProvider(client
                        .getRestClient(), provider, redirectUrl);
            }
        }, completion);
    }

    /**
     * Removes the social network connection from the logged in user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param provider
     *         The social service provider, currently valid are only facebook and twitter
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated user object
     * @return a Future object to optionally wait for the {@code User} result or to cancel the query
     */
    public static Future<User> disconnectProvider(final PLYAndroid client, final String provider,
            PLYCompletion<User> completion) {
        return client.submit(new PLYAndroid.Query<User>() {
            @Override
            public User execute() {
                return com.productlayer.rest.client.services.SocialService.disconnectProvider(client
                        .getRestClient(), provider);
            }
        }, completion);
    }

    /**
     * Gets the provider connection URL.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param provider
     *         The social service provider, currently valid are only facebook and twitter
     * @param redirectUrl
     *         The URL for redirecting after provider connect
     * @param token
     *         Any tokens for the connection to the API server
     * @return The provider connection URL
     */
    public static String getProviderConnectionURL(final PLYAndroid client, final String provider, final
    String redirectUrl, final String token) {
        return com.productlayer.rest.client.services.SocialService.getProviderConnectionURL(client
                .getRestClient(), provider, redirectUrl, token);
    }

    /**
     * Gets the provider sign in URL.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param provider
     *         The social service provider, currently valid are only facebook and twitter
     * @param redirectUrl
     *         The URL for redirecting after provider sign in
     * @return The provider sign in URL
     */
    public static String getProviderSigninURL(final PLYAndroid client, final String provider, final String
            redirectUrl) {
        return com.productlayer.rest.client.services.SocialService.getProviderSigninURL(client
                .getRestClient(), provider, redirectUrl);
    }

    /**
     * Get the social response for the specific post.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param opineID
     *         The identifier of the opine
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: A list of
     *         social responses
     * @return a Future object to optionally wait for the {@code SocialOpine[]} result or to cancel the query
     */
    public static Future<SocialOpine[]> getSocialReplies(final PLYAndroid client, final String opineID,
            PLYCompletion<SocialOpine[]> completion) {
        return client.submit(new PLYAndroid.Query<SocialOpine[]>() {
            @Override
            public SocialOpine[] execute() {
                return com.productlayer.rest.client.services.SocialService.getSocialReplies(client
                        .getRestClient(), opineID);
            }
        }, completion);
    }

    /**
     * Check if the token for this provider is expired.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param providerId
     *         The social service provider (currently only facebook and twitter are valid)
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: True if
     *         the token is expired, otherwise false.
     * @return a Future object to optionally wait for the {@code Boolean} result or to cancel the query
     */
    public static Future<Boolean> isTokenValid(final PLYAndroid client, final String providerId,
            PLYCompletion<Boolean> completion) {
        return client.submit(new PLYAndroid.Query<Boolean>() {
            @Override
            public Boolean execute() {
                return com.productlayer.rest.client.services.SocialService.isTokenValid(client
                        .getRestClient(), providerId);
            }
        }, completion);
    }

    /**
     * Get tweets about a product
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param providerId
     *         The social service provider (currently only facebook and twitter are valid)
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated user
     * @return a Future object to optionally wait for the {@code Opine[]} result or to cancel the query
     */
    public static Future<Opine[]> searchForProductPosts(final PLYAndroid client, final String providerId,
            final String gtin, final String language, PLYCompletion<Opine[]> completion) {
        return client.submit(new PLYAndroid.Query<Opine[]>() {
            @Override
            public Opine[] execute() {
                return com.productlayer.rest.client.services.SocialService.searchForProductPosts(client
                        .getRestClient(), providerId, gtin, language);
            }
        }, completion);
    }
}
