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

package com.productlayer.android.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.productlayer.core.beans.errors.ErrorMessage;
import com.productlayer.core.error.PLYHttpException;
import com.productlayer.core.error.PLYStatusCodes;
import com.productlayer.rest.client.PLYRestClient;
import com.productlayer.rest.client.config.PLYRestClientConfig;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttpClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ProductLayer Android SDK
 */
public class PLYAndroid {

    private static final String PREFS_NAME = "PLYAndroidPrefs";
    private static final String STATE_API_SCHEMA = "stateApiSchema";
    private static final String STATE_API_HOST = "stateApiHost";
    private static final String STATE_API_PORT = "stateApiPort";
    private static final String STATE_API_VERSION = "stateApiVersion";
    private static final String STATE_API_KEY = "stateApiKey";
    private static final String STATE_PROXY_ENABLED = "stateProxyEnabled";
    private static final String STATE_PROXY_HOST = "stateProxyHost";
    private static final String STATE_PROXY_PORT = "stateProxyPort";
    private static final String STATE_CONFIG = "stateConfig";
    private static final String STATE_USERNAME = "stateUsername";
    private static final String STATE_SESSION = "stateSession";
    private static final String STATE_TOKEN = "stateToken";
    private static final String STATE_USER_AGENT = "stateUserAgent";
    private static final String STATE_PREFERRED_LANGUAGE = "statePreferredLanguage";
    private static final String STATE_ADDITIONAL_LANGUAGES = "stateAdditionalLanguages";
    private static final String STATE_ADDITIONAL_HEADERS = "stateAdditionalHeaders";

    private final ExecutorService threadPool;

    private PLYRestClient client;
    private PLYQueryListener queryListener;
    private PLYUserProgressListener userProgressListener;
    private String language;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private volatile Thread stateLoader;
    private volatile boolean stateLoaded;

    /**
     * Initializes a thread pool and the PLYRestClient to handle communication.
     *
     * {@code UserService.login} must be used to log in before using services requiring authentication.
     *
     * @param config
     *         the server connection, API version, any proxy information
     */
    public PLYAndroid(PLYRestClientConfig config) {
        stateLoaded = true;
        threadPool = Executors.newCachedThreadPool();
        initRestClient(config);
    }

    /**
     * Initializes a thread pool and uses the specified PLYRestClient to handle communication.
     *
     * @param client
     *         the REST client
     * @param orderedExecution
     *         true to activate just a single thread to handle service calls in order, false to initialize a
     *         thread pool handling several calls at the same time in an unspecified order
     */
    private PLYAndroid(PLYRestClient client, boolean orderedExecution) {
        stateLoaded = true;
        threadPool = orderedExecution ? Executors.newSingleThreadExecutor() : Executors.newCachedThreadPool();
        this.client = client;
    }

    /**
     * Initializes the REST client using an OkHttp RestTemplate and the provided configuration.
     *
     * @param config
     *         the server connection, API version, any proxy information
     */
    private void initRestClient(PLYRestClientConfig config) {
        client = new PLYRestClient(config);
        client.setRestTemplate(getOkHttpRestTemplate(config));
    }

    /**
     * Builds an HTTP client for Spring using OkHttp.
     *
     * @param config
     *         any proxy configuration
     * @return an OkHttp HTTP client for Spring
     */
    private RestTemplate getOkHttpRestTemplate(PLYRestClientConfig config) {
        OkHttpClient okHttpClient = new OkHttpClient();
        if (config.proxyEnabled) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.proxyHost, config
                    .proxyPort));
            okHttpClient.setProxy(proxy);
        }
        ClientHttpRequestFactory requestFactory = new OkHttpClientHttpRequestFactory(okHttpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        PLYRestClient.enableConverters(restTemplate);
        return restTemplate;
    }

    /**
     * Creates a PLYAndroid instance executing any service calls strictly in the order they are called with
     * only one being active at a time. Shares the REST client and any headers with the original PLYAndroid
     * client - this is not a deep copy.
     *
     * @return a copied PLYAndroid instance with a new thread pool that executes service calls in order
     */
    public PLYAndroid copyForOrderedThreadExecution() {
        waitForStateLoaded();
        PLYAndroid sequentialClient = new PLYAndroid(client, true);
        sequentialClient.setQueryListener(queryListener);
        sequentialClient.setUserProgressListener(userProgressListener);
        sequentialClient.setLanguage(language);
        return sequentialClient;
    }

    /**
     * Creates a new {@link PLYTask} consisting of a REST client query and a completion object and submits it
     * to the thread pool.
     *
     * @param query
     *         the REST client query to execute
     * @param completion
     *         tasks to do on success and on error
     * @param <T>
     *         the type returned by the query
     * @return a Future object to optionally wait for the results or to cancel the query
     */
    public <T> Future<T> submit(Query<T> query, PLYCompletion<T> completion) {
        return threadPool.submit(new PLYTask<>(query, completion));
    }

    /**
     * @return the PLYRestClient handling synchronous communication with the server
     */
    public PLYRestClient getRestClient() {
        waitForStateLoaded();
        return client;
    }

    /**
     * Sets a callback for queries that complete with an error due to failed authentication.
     *
     * @param queryListener
     *         the query listener to install
     */
    public void setQueryListener(PLYQueryListener queryListener) {
        this.queryListener = queryListener;
    }

    /**
     * Sets a callback for queries that result in user progress (i.e. earned points, achievements).
     *
     * @param userProgressListener
     *         the user progress listener to install
     */
    public void setUserProgressListener(PLYUserProgressListener userProgressListener) {
        this.userProgressListener = userProgressListener;
    }

    /**
     * @return the preferred language in ISO 639-1 format
     */
    public String getLanguage() {
        waitForStateLoaded();
        return language;
    }

    /**
     * Sets the preferred language (sent in Accept-Language header).
     *
     * @param language
     *         the preferred language in ISO 639-1 format
     */
    public void setLanguage(String language) {
        this.language = language;
        client.setPreferredLanguage(language);
    }

    /**
     * Clears any headers the REST client is sending with requests for authorization purposes (client-side
     * logout).
     *
     * @see com.productlayer.android.sdk.services.UserService#logout
     * @see com.productlayer.android.sdk.services.UserService#login
     */
    public void resetAuth() {
        waitForStateLoaded();
        client.setUsername(null);
        client.setPassword(null);
        client.setSession(null);
        client.setToken(null);
    }

    /**
     * @return the currently stored authorization token without any "key=" prefix (or null if none is set), to
     * be used with {@link com.productlayer.android.sdk.services.UserService#login(PLYAndroid, String,
     * Boolean, PLYCompletion)}
     */
    public String getAuthToken() {
        waitForStateLoaded();
        String tokenHeader = client.getToken();
        if (tokenHeader == null) {
            return null;
        }
        return tokenHeader.replace(PLYRestClient.COOKIE_AUTH_TOKEN + "=", "");
    }

    /**
     * @return whether an authorization token is sent with each request (does not guarantee a successful
     * login)
     * @see com.productlayer.android.sdk.services.UserService#isSignedIn(PLYAndroid, PLYCompletion)
     * @see com.productlayer.android.sdk.services.UserService#login(PLYAndroid, String, Boolean,
     * PLYCompletion)
     */
    public boolean isAuthTokenSet() {
        String authToken = getAuthToken();
        return authToken != null && !authToken.isEmpty();
    }

    /**
     * Waits for the state loading thread to finish if it is currently running.
     */
    private void waitForStateLoaded() {
        if (stateLoaded) {
            return;
        }
        if (stateLoader != null) {
            try {
                stateLoader.join(1000);
            } catch (Exception ignored) {
            } finally {
                stateLoaded = true;
                stateLoader = null;
            }
        }
    }

    /**
     * Call this in your activity's {@link Activity#onStop} to save the currently used PLYAndroid instance
     * (i.e. config, auth) to persistent SharedPreferences storage.
     *
     * @param context
     *         the context to retrieve shared preferences with
     * @param saveConfig
     *         true to save static configuration such as API and any proxy information
     * @param saveDynamic
     *         true to save dynamic state such as the authentication token
     */
    public void onStop(Context context, boolean saveConfig, boolean saveDynamic) {
        if (!saveConfig && !saveDynamic) {
            return;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if (saveConfig) {
            // save client config in shared preferences
            editor.putString(STATE_API_SCHEMA, client.getConfigApiSchema());
            editor.putString(STATE_API_HOST, client.getConfigApiHost());
            editor.putInt(STATE_API_PORT, client.getConfigApiPort());
            editor.putString(STATE_API_VERSION, client.getConfigApiVersion());
            editor.putString(STATE_API_KEY, client.getConfigApiKey());
            editor.putBoolean(STATE_PROXY_ENABLED, client.isConfigProxyEnabled());
            editor.putString(STATE_PROXY_HOST, client.getConfigProxyHost());
            editor.putInt(STATE_PROXY_PORT, client.getConfigProxyPort());
        }
        if (saveDynamic) {
            // save auth session data in shared preferences
            editor.putString(STATE_USERNAME, client.getUsername());
            editor.putString(STATE_SESSION, client.getSession());
            editor.putString(STATE_TOKEN, client.getToken());
        }
        editor.apply();
    }

    /**
     * Call this in your activity's {@link Activity#onSaveInstanceState} to save the currently used PLYAndroid
     * instance (i.e. config, auth, locale, other headers) in non-persistent {@code Bundle} storage - to be
     * retrieved when/if the activity instance is restored.
     *
     * @param outState
     *         Bundle in which to store the information
     * @param saveConfig
     *         true to save static configuration such as API and any proxy information
     * @param saveDynamic
     *         true to save dynamic state such as the authentication token, language and other headers
     */
    public void onSaveInstanceState(Bundle outState, boolean saveConfig, boolean saveDynamic) {
        if (!saveConfig && !saveDynamic) {
            return;
        }
        if (saveConfig) {
            // save client config in bundle
            Serializable config = new PLYRestClientConfig(client.getConfigApiSchema(), client
                    .getConfigApiHost(), client.getConfigApiPort(), client.getConfigApiVersion(), client
                    .getConfigApiKey(), client.isConfigProxyEnabled(), client.getConfigProxyHost(), client
                    .getConfigProxyPort());
            outState.putSerializable(STATE_CONFIG, config);
        }
        if (saveDynamic) {
            // save auth session data, locale and other headers in bundle
            outState.putString(STATE_USERNAME, client.getUsername());
            outState.putString(STATE_SESSION, client.getSession());
            outState.putString(STATE_TOKEN, client.getToken());
            outState.putString(STATE_USER_AGENT, client.getUserAgent());
            outState.putString(STATE_PREFERRED_LANGUAGE, language);
            outState.putStringArray(STATE_ADDITIONAL_LANGUAGES, client.getAdditionalLanguages());
            outState.putSerializable(STATE_ADDITIONAL_HEADERS, (Serializable) client.getAdditionalHeaders());
        }
    }

    /**
     * Call this in your activity's {@link Activity#onCreate} to restore the previously used PLYAndroid
     * instance (i.e. config, auth) from persistent SharedPreferences storage. This method will not do
     * anything if the configuration is also saved in the supplied {@code Bundle}, in favor of {@link
     * #onRestoreInstanceState} restoring the data from non-persistent storage.
     *
     * In addition make sure to re-set any handlers.
     *
     * @param context
     *         the context to retrieve shared preferences with
     * @param savedInstanceState
     *         Bundle to retrieve previously stored information from
     * @param restoreConfig
     *         true to restore static configuration such as API and any proxy information
     * @param restoreDynamic
     *         true to restore dynamic state such as the authentication token
     */
    public void onCreate(final Context context, Bundle savedInstanceState, final boolean restoreConfig,
            final boolean restoreDynamic) {
        stateLoaded = false;
        if ((!restoreConfig && !restoreDynamic) || (savedInstanceState != null && savedInstanceState
                .getSerializable(STATE_CONFIG) != null)) {
            stateLoaded = true;
            // state will be restored in onRestoreInstanceState
            return;
        }
        // retrieve from shared preferences (in the bg due to disk access, i.e. performance reasons)
        Runnable stateLoaderRunnable = new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                if (restoreConfig) {
                    // restore client config from shared preferences
                    String apiSchema = prefs.getString(STATE_API_SCHEMA, null);
                    if (apiSchema != null) {
                        String apiHost = prefs.getString(STATE_API_HOST, null);
                        int apiPort = prefs.getInt(STATE_API_PORT, 0);
                        String apiVersion = prefs.getString(STATE_API_VERSION, null);
                        String apiKey = prefs.getString(STATE_API_KEY, null);
                        boolean proxyEnabled = prefs.getBoolean(STATE_PROXY_ENABLED, false);
                        String proxyHost = prefs.getString(STATE_PROXY_HOST, null);
                        int proxyPort = prefs.getInt(STATE_PROXY_PORT, 0);
                        initRestClient(new PLYRestClientConfig(apiSchema, apiHost, apiPort, apiVersion,
                                apiKey, proxyEnabled, proxyHost, proxyPort));
                        setLanguage(language);
                    }
                }
                if (restoreDynamic) {
                    // restore auth session data from shared preferences
                    String token = prefs.getString(STATE_TOKEN, null);
                    if (token != null) {
                        client.setToken(token);
                        client.setUsername(prefs.getString(STATE_USERNAME, null));
                        client.setSession(prefs.getString(STATE_SESSION, null));
                    }
                }
                stateLoaded = true;
                stateLoader = null;
            }
        };
        stateLoader = new Thread(stateLoaderRunnable);
        stateLoader.start();
    }

    /**
     * Call this in your activity's {@link Activity#onRestoreInstanceState} to restore the previously used
     * PLYAndroid instance (i.e. config, auth, locale, other headers) from non-persistent {@code Bundle}
     * storage.
     *
     * In addition make sure to re-set any handlers.
     *
     * @param savedInstanceState
     *         Bundle to retrieve previously stored information from
     * @param restoreConfig
     *         true to restore static configuration such as API and any proxy information
     * @param restoreDynamic
     *         true to restore dynamic state such as the authentication token, language and other headers
     */
    public void onRestoreInstanceState(Bundle savedInstanceState, boolean restoreConfig, boolean
            restoreDynamic) {
        stateLoaded = false;
        if ((!restoreConfig && !restoreDynamic) || savedInstanceState == null) {
            stateLoaded = true;
            return;
        }
        // retrieve stored information from bundle
        if (restoreConfig) {
            // restore client config from bundle
            PLYRestClientConfig config = (PLYRestClientConfig) savedInstanceState.getSerializable
                    (STATE_CONFIG);
            if (config != null) {
                initRestClient(config);
            }
        }
        if (restoreDynamic) {
            // restore auth session data, locale and other headers from bundle
            String token = savedInstanceState.getString(STATE_TOKEN);
            if (token != null) {
                client.setToken(token);
                client.setUsername(savedInstanceState.getString(STATE_USERNAME));
                client.setSession(savedInstanceState.getString(STATE_SESSION));
                client.setUserAgent(savedInstanceState.getString(STATE_USER_AGENT));
                client.setAdditionalLanguages(savedInstanceState.getStringArray(STATE_ADDITIONAL_LANGUAGES));
                //noinspection unchecked
                client.setAdditionalHeaders((Map<String, String>) savedInstanceState.getSerializable
                        (STATE_ADDITIONAL_HEADERS));
            }
            String language = savedInstanceState.getString(STATE_PREFERRED_LANGUAGE);
            if (language != null) {
                this.language = language;
            }
        }
        setLanguage(language);
        stateLoaded = true;
    }

    /**
     * Contains a single method returning an object of generic type.
     *
     * Used by the Android SDK service classes to encapsulate calls to the synchronous Java SDK.
     *
     * @param <T>
     *         the type used for the return value of its single execute method
     */
    public interface Query<T> {

        /**
         * @return the result of the implementing class' query
         * @throws PLYHttpException
         *         on any HTTP status code indicating failure
         * @throws RestClientException
         *         on any client-side HTTP error
         */
        T execute() throws PLYHttpException, RestClientException;

    }

    /**
     * Wraps an exception that may be thrown by the REST client, offering convenience methods to determine the
     * type of failure and to query a HTTP status code if present.
     */
    public class QueryError {

        private final Exception exception;

        /**
         * Creates a new query error caused by the given exception.
         *
         * @param exception
         *         the exception wrapped by this query error
         */
        public QueryError(Exception exception) {
            this.exception = exception;
        }

        /**
         * @return true if a HTTP status code that indicated an error (4xx, 5xx) was received, false if a
         * client-side error occurred
         */
        public boolean isHttpStatusError() {
            return exception instanceof PLYHttpException;
        }

        /**
         * @return the HTTP status code if this error was due to an erronous HTTP status code (4xx, 5xx), -1
         * else
         */
        public int getHttpStatusCode() {
            if (!isHttpStatusError()) {
                return -1;
            }
            return ((PLYHttpException) exception).getHttpStatus();
        }

        /**
         * @return any messages and/or HTTP status code in the wrapped exception
         */
        public String getMessage() {
            StringBuilder sb = new StringBuilder();
            if (isHttpStatusError()) {
                PLYHttpException e = (PLYHttpException) exception;
                int httpStatus = e.getHttpStatus();
                if (httpStatus > 0) {
                    sb.append("HTTP Status Code ").append(e.getHttpStatus()).append("\n");
                }
                List<ErrorMessage> errorMessages = e.getErrors();
                if (errorMessages != null) {
                    for (ErrorMessage errorMessage : errorMessages) {
                        sb.append(errorMessage.getMessage()).append(" (").append(errorMessage.getCode())
                                .append(")\n");
                    }
                }
            } else {
                String message = exception.getMessage();
                if (message != null) {
                    sb.append(message);
                }
            }
            return sb.toString();
        }

        /**
         * Looks through the attached internal {@link PLYStatusCodes} error codes to find the specified error
         * code.
         *
         * @param errorCode
         *         the error code to look for
         * @return true if {@code errorCode} was found, false else
         */
        public boolean hasInternalErrorCode(int errorCode) {
            if (!isHttpStatusError()) {
                return false;
            }
            PLYHttpException e = (PLYHttpException) exception;
            List<ErrorMessage> errorMessages = e.getErrors();
            if (errorMessages != null) {
                for (ErrorMessage errorMessage : errorMessages) {
                    if (errorMessage.getCode() == errorCode) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * @return the exception that caused this error (of type PLYHttpException on HTTP status codes
         * indicating failure, RestClientException on client-side HTTP errors)
         */
        public Exception getException() {
            return exception;
        }

    }

    /**
     * Executes a {@link Query} to forward its result to the {@link PLYCompletion} methods.
     *
     * Implements {@link Callable} - designed to be used by another thread.
     *
     * @param <T>
     *         the type that is returned by the {@link Query}
     */
    private class PLYTask<T> implements Callable<T> {

        private final Query<T> query;

        private final PLYCompletion<T> completion;

        /**
         * Stores the parameters to be used during {@link #call()}.
         *
         * @param query
         *         the query to execute
         * @param completion
         *         actions to set on success or on error once the query has finished
         */
        public PLYTask(Query<T> query, PLYCompletion<T> completion) {
            this.query = query;
            this.completion = completion;
        }

        /**
         * Executes the query and forwards its result to {@link PLYCompletion#onSuccess (Object)} or to {@link
         * PLYCompletion#onError(QueryError)} on any exception.
         *
         * @return the result of the query
         */
        @Override
        public T call() {
            final T result;
            HttpHeaders headers;
            try {
                result = query.execute();
                headers = client.getLastHttpHeaders();
            } catch (RuntimeException e) {
                final QueryError queryError = new QueryError(e);
                if (!queryError.isHttpStatusError() || queryError.getHttpStatusCode() != PLYStatusCodes
                        .HTTP_STATUS_FORBIDDEN_CODE || queryListener == null || !queryListener.onFailedAuth
                        (query, completion, queryError)) {
                    completion.onError(queryError);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            completion.onPostError(queryError);
                        }
                    });
                }
                throw e;
            }
            completion.onSuccess(result);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    completion.onPostSuccess(result);
                }
            });
            checkResponseHeaders(headers);
            return result;
        }

        /**
         * Checks the specified response headers for those indicating user progress (i.e. advancement in
         * points or achievements) and, if found, calls the installed {@link PLYUserProgressListener}.
         *
         * @param headers
         *         the HTTP headers to check
         */
        private void checkResponseHeaders(@SuppressWarnings("TypeMayBeWeakened") HttpHeaders headers) {
            if (headers == null) {
                return;
            }
            if (userProgressListener == null) {
                return;
            }
            String pointsStr = headers.getFirst(PLYRestClient.HEADER_USER_POINTS);
            String pointsChangeStr = headers.getFirst(PLYRestClient.HEADER_USER_POINTS_CHANGE);
            if (pointsStr != null && pointsChangeStr != null) {
                try {
                    final long points = Long.valueOf(pointsStr);
                    final int pointsChange = Integer.valueOf(pointsChangeStr);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            userProgressListener.earnedPoints(points, pointsChange);
                        }
                    });
                } catch (NumberFormatException ignored) {
                }
            }
            String achievements = headers.getFirst(PLYRestClient.HEADER_USER_NEW_ACHIEVEMENTS);
            if (achievements != null) {
                try {
                    final List<String> keys = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(achievements);
                    int jsonArraySize = jsonArray.length();
                    for (int i = 0; i < jsonArraySize; i++) {
                        keys.add(jsonArray.getString(i));
                    }
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            userProgressListener.earnedAchievements(keys);
                        }
                    });
                } catch (JSONException ignored) {
                }
            }
        }

    }

}
