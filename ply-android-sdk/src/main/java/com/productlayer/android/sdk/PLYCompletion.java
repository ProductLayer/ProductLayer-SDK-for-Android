package com.productlayer.android.sdk;

/**
 * Contains methods to either handle a successful query returning a result or a failed query generating an
 * exception. Methods are split into {@link #onSuccess}, {@link #onError} running on a background thread and
 * {@link #onPostSuccess}, {@link #onPostError} running on the UI thread.
 */
public abstract class PLYCompletion<T> {

    /**
     * Actions to set after a successful query. Do not modify the UI in this method.
     *
     * @param result
     *         the return value of the query
     */
    public abstract void onSuccess(T result);

    /**
     * Actions to set after a failed query. Do not directly modify the UI in this method.
     *
     * @param error
     *         contains the exception that was generated as a result of the failed query, can be used to
     *         determine the type of failure
     */
    public abstract void onError(PLYAndroid.QueryError error);

    /**
     * UI changes to display after {@link #onSuccess}. Runs on the UI thread.
     *
     * @param result
     *         the return value of the query
     */
    @SuppressWarnings("NoopMethodInAbstractClass")
    public void onPostSuccess(T result) {
    }

    /**
     * UI changes to display after {@link #onError}. Runs on the UI thread.
     *
     * @param error
     *         contains the exception that was generated as a result of the failed query, can be used to
     *         determine the type of failure
     */
    @SuppressWarnings("NoopMethodInAbstractClass")
    public void onPostError(PLYAndroid.QueryError error) {
    }

    /**
     * @return true if an authorization failure should trigger a login prompt, false else
     */
    public boolean promptForLogin() {
        return true;
    }

}
