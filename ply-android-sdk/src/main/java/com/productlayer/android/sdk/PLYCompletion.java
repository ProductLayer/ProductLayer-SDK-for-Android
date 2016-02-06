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
