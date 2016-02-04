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

package com.productlayer.android.sdk;

/**
 * Implement this interface and set it in {@link PLYAndroid#setQueryListener} to receive callbacks on queries
 * that failed due to insufficient permissions (i.e. not logged in).
 */
public interface PLYQueryListener {

    /**
     * Called whenever a query fails due to login status (i.e. no or invalid user name / password / token).
     *
     * @param query
     *         the query that failed
     * @param completion
     *         the callback that was supposed to be executed ({@link PLYCompletion#promptForLogin} determines
     *         whether to prompt for a login)
     * @param error
     *         the error the query produced
     * @param <T>
     *         the expected return type of the query
     * @return true if the event is handled (resulting in no further error handling), false else (resulting in
     * the completion object's onError being called)
     */
    <T> boolean onFailedAuth(PLYAndroid.Query<T> query, PLYCompletion<T> completion, PLYAndroid.QueryError
            error);

}
