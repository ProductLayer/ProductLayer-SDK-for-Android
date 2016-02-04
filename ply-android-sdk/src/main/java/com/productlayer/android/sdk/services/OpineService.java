package com.productlayer.android.sdk.services;

import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.core.beans.Opine;
import com.productlayer.core.beans.reports.ProblemReport;

import java.util.concurrent.Future;

public class OpineService {

    /**
     * Posts an opine. If the user earns points for this operation 'X-ProductLayer-User-Points' and
     * 'X-ProductLayer-User-Points-Changed' will be present in the response header.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param opine
     *         The opine
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The newly
     *         created opine
     * @return a Future object to optionally wait for the {@code Opine} result or to cancel the query
     */
    public static Future<Opine> createOpine(final PLYAndroid client, final Opine opine,
            PLYCompletion<Opine> completion) {
        return client.submit(new PLYAndroid.Query<Opine>() {
            @Override
            public Opine execute() {
                return com.productlayer.rest.client.services.OpineService.createOpine(client.getRestClient
                        (), opine);
            }
        }, completion);
    }

    /**
     * Deletes an opine. Only the owner of the opine can delete it. If the user earns points for this
     * operation 'X-ProductLayer-User-Points' and 'X-ProductLayer-User-Points-Changed' will be present in the
     * response header.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param opineID
     *         The identifier of the opine
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         deleted opine
     * @return a Future object to optionally wait for the {@code Opine} result or to cancel the query
     */
    public static Future<Opine> deleteOpine(final PLYAndroid client, final String opineID,
            PLYCompletion<Opine> completion) {
        return client.submit(new PLYAndroid.Query<Opine>() {
            @Override
            public Opine execute() {
                return com.productlayer.rest.client.services.OpineService.deleteOpine(client.getRestClient
                        (), opineID);
            }
        }, completion);
    }

    /**
     * Downvotes a specific opine.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param opineID
     *         The identifier of the opine
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The opine
     *         with the new vote score
     * @return a Future object to optionally wait for the {@code Opine} result or to cancel the query
     */
    public static Future<Opine> downVoteOpine(final PLYAndroid client, final String opineID,
            PLYCompletion<Opine> completion) {
        return client.submit(new PLYAndroid.Query<Opine>() {
            @Override
            public Opine execute() {
                return com.productlayer.rest.client.services.OpineService.downVoteOpine(client
                        .getRestClient(), opineID);
            }
        }, completion);
    }

    /**
     * Gets a specific opine.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param opineID
     *         The identifier of the opine
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         identified opine
     * @return a Future object to optionally wait for the {@code Opine} result or to cancel the query
     */
    public static Future<Opine> getOpine(final PLYAndroid client, final String opineID,
            PLYCompletion<Opine> completion) {
        return client.submit(new PLYAndroid.Query<Opine>() {
            @Override
            public Opine execute() {
                return com.productlayer.rest.client.services.OpineService.getOpine(client.getRestClient(),
                        opineID);
            }
        }, completion);
    }

    /**
     * Sends a report about copyright infringements or any other problems with the opine.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param opineID
     *         The identifier of the opine
     * @param report
     *         The report
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         problem report object
     * @return a Future object to optionally wait for the {@code ProblemReport} result or to cancel the query
     */
    public static Future<ProblemReport> reportOpine(final PLYAndroid client, final String opineID, final
    ProblemReport report, PLYCompletion<ProblemReport> completion) {
        return client.submit(new PLYAndroid.Query<ProblemReport>() {
            @Override
            public ProblemReport execute() {
                return com.productlayer.rest.client.services.OpineService.reportOpine(client.getRestClient
                        (), opineID, report);
            }
        }, completion);
    }

    /**
     * Searches for an opine.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param page
     *         [Optional] The page to be displayed starting with 0 - if no page has been provided, the first
     *         page will be shown
     * @param recordsPerPage
     *         [Optional] The amount of items to be displayed per page, default: '200'
     * @param gtin
     *         [Optional] The GTIN (barcode) of the product
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de')
     * @param showFriendsOnly
     *         [Optional] Show only content created by friends (followed users), default: 'false'
     * @param nickname
     *         [Optional] The nickname of the user
     * @param userID
     *         [Optional] The identifier of the user
     * @param order_by
     *         [Optional] Used to sort the result-set by one or more columns. The order by parameters are
     *         <strong>seperated by a semicolon</strong>. Also you need to provide a prefix <strong>asc for
     *         ascending</strong> or <strong>desc for descending order</strong><br> <br>
     *         <strong>Default:</strong> pl-created-time_asc (The date the opine was created ascending)
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any opines
     *         matching the specified criteria
     * @return a Future object to optionally wait for the {@code Opine[]} result or to cancel the query
     */
    public static Future<Opine[]> searchOpines(final PLYAndroid client, final Integer page, final Integer
            recordsPerPage, final String gtin, final String language, final Boolean showFriendsOnly, final
    String nickname, final String userID, final String order_by, PLYCompletion<Opine[]> completion) {
        return client.submit(new PLYAndroid.Query<Opine[]>() {
            @Override
            public Opine[] execute() {
                return com.productlayer.rest.client.services.OpineService.searchOpines(client.getRestClient
                        (), page, recordsPerPage, gtin, language, showFriendsOnly, nickname, userID,
                        order_by);
            }
        }, completion);
    }

    /**
     * Upvotes a specific opine.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param opineID
     *         The identifier of the opine
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The opine
     *         with the new vote score
     * @return a Future object to optionally wait for the {@code Opine} result or to cancel the query
     */
    public static Future<Opine> upVoteOpine(final PLYAndroid client, final String opineID,
            PLYCompletion<Opine> completion) {
        return client.submit(new PLYAndroid.Query<Opine>() {
            @Override
            public Opine execute() {
                return com.productlayer.rest.client.services.OpineService.upVoteOpine(client.getRestClient
                        (), opineID);
            }
        }, completion);
    }
}
