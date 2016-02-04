package com.productlayer.android.sdk.services;

import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.core.beans.Review;
import com.productlayer.core.beans.reports.ProblemReport;
import com.productlayer.core.beans.summary.FullReviewStatistics;

import java.util.concurrent.Future;

public class ReviewService {

    /**
     * Creates a new review for a product. If the user earns points for this operation
     * 'X-ProductLayer-User-Points' and 'X-ProductLayer-User-Points-Changed' will be present in the response
     * header.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param review
     *         The review
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The newly
     *         created review
     * @return a Future object to optionally wait for the {@code Review} result or to cancel the query
     */
    public static Future<Review> createReview(final PLYAndroid client, final String gtin, final Review
            review, PLYCompletion<Review> completion) {
        return client.submit(new PLYAndroid.Query<Review>() {
            @Override
            public Review execute() {
                return com.productlayer.rest.client.services.ReviewService.createReview(client
                        .getRestClient(), gtin, review);
            }
        }, completion);
    }

    /**
     * Creates a new review for a product. If the user earns points for this operation
     * 'X-ProductLayer-User-Points' and 'X-ProductLayer-User-Points-Changed' will be present in the response
     * header.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param review
     *         The review
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The newly
     *         created review
     * @return a Future object to optionally wait for the {@code Review} result or to cancel the query
     */
    public static Future<Review> createReview(final PLYAndroid client, final Review review,
            PLYCompletion<Review> completion) {
        return client.submit(new PLYAndroid.Query<Review>() {
            @Override
            public Review execute() {
                return com.productlayer.rest.client.services.ReviewService.createReview(client
                        .getRestClient(), review);
            }
        }, completion);
    }

    /**
     * Downvotes a specific review.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param reviewID
     *         The identifier of the review
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The review
     *         with the new vote score
     * @return a Future object to optionally wait for the {@code Review} result or to cancel the query
     */
    public static Future<Review> downVoteReview(final PLYAndroid client, final String reviewID,
            PLYCompletion<Review> completion) {
        return client.submit(new PLYAndroid.Query<Review>() {
            @Override
            public Review execute() {
                return com.productlayer.rest.client.services.ReviewService.downVoteReview(client
                        .getRestClient(), reviewID);
            }
        }, completion);
    }

    /**
     * Gets a review by ID.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param reviewID
     *         The identifier of the review
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         identified review
     * @return a Future object to optionally wait for the {@code Review} result or to cancel the query
     */
    public static Future<Review> getReview(final PLYAndroid client, final String reviewID,
            PLYCompletion<Review> completion) {
        return client.submit(new PLYAndroid.Query<Review>() {
            @Override
            public Review execute() {
                return com.productlayer.rest.client.services.ReviewService.getReview(client.getRestClient()
                        , reviewID);
            }
        }, completion);
    }

    /**
     * Gets the review statistics for the specified GTIN and language.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param gtin
     *         The GTIN (barcode) of the product
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The review
     *         statistics
     * @return a Future object to optionally wait for the {@code FullReviewStatistics} result or to cancel the
     * query
     */
    public static Future<FullReviewStatistics> getReviewStatistics(final PLYAndroid client, final String
            gtin, final String language, PLYCompletion<FullReviewStatistics> completion) {
        return client.submit(new PLYAndroid.Query<FullReviewStatistics>() {
            @Override
            public FullReviewStatistics execute() {
                return com.productlayer.rest.client.services.ReviewService.getReviewStatistics(client
                        .getRestClient(), gtin, language);
            }
        }, completion);
    }

    /**
     * Sends a report about copyright infringements or any other problems with the review.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param reviewID
     *         The identifier of the review
     * @param report
     *         The report
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         problem report object
     * @return a Future object to optionally wait for the {@code ProblemReport} result or to cancel the query
     */
    public static Future<ProblemReport> reportReview(final PLYAndroid client, final String reviewID, final
    ProblemReport report, PLYCompletion<ProblemReport> completion) {
        return client.submit(new PLYAndroid.Query<ProblemReport>() {
            @Override
            public ProblemReport execute() {
                return com.productlayer.rest.client.services.ReviewService.reportReview(client
                        .getRestClient(), reviewID, report);
            }
        }, completion);
    }

    /**
     * Searches for reviews.
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
     *         [Optional] The preferred language (e.g.: 'en' or 'de'), default: 'en'
     * @param nickname
     *         [Optional] The nickname of the user
     * @param userID
     *         [Optional] The identifier of the user
     * @param rating
     *         [Optional] The rating between 0 and 5 stars
     * @param order_by
     *         [Optional] Used to sort the result-set by one or more columns. The order by parameters are
     *         <strong>seperated by a semicolon</strong>. Also you need to provide a prefix <strong>asc for
     *         ascending</strong> or <strong>desc for descending order</strong><br> <br>
     *         <strong>Default:</strong> pl-upd-time_desc, pl-created-time_desc (Newly inserted and updated
     *         reviews first.)
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any
     *         reviews matching the specified criteria
     * @return a Future object to optionally wait for the {@code Review[]} result or to cancel the query
     */
    public static Future<Review[]> searchReviews(final PLYAndroid client, final Integer page, final Integer
            recordsPerPage, final String gtin, final String language, final String nickname, final String
            userID, final Integer rating, final String order_by, PLYCompletion<Review[]> completion) {
        return client.submit(new PLYAndroid.Query<Review[]>() {
            @Override
            public Review[] execute() {
                return com.productlayer.rest.client.services.ReviewService.searchReviews(client
                        .getRestClient(), page, recordsPerPage, gtin, language, nickname, userID, rating,
                        order_by);
            }
        }, completion);
    }

    /**
     * Upvotes a specific review.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param reviewID
     *         The identifier of the review
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The review
     *         with the new vote score
     * @return a Future object to optionally wait for the {@code Review} result or to cancel the query
     */
    public static Future<Review> upVoteReview(final PLYAndroid client, final String reviewID,
            PLYCompletion<Review> completion) {
        return client.submit(new PLYAndroid.Query<Review>() {
            @Override
            public Review execute() {
                return com.productlayer.rest.client.services.ReviewService.upVoteReview(client
                        .getRestClient(), reviewID);
            }
        }, completion);
    }

    /**
     * Updates an existing review of a product. Only the title, body, rating and language can be changed. If
     * the user earns points for this operation 'X-ProductLayer-User-Points' and
     * 'X-ProductLayer-User-Points-Changed' will be present in the response header.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param review
     *         The review
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated review
     * @return a Future object to optionally wait for the {@code Review} result or to cancel the query
     */
    public static Future<Review> updateReview(final PLYAndroid client, final Review review,
            PLYCompletion<Review> completion) {
        return client.submit(new PLYAndroid.Query<Review>() {
            @Override
            public Review execute() {
                return com.productlayer.rest.client.services.ReviewService.updateReview(client
                        .getRestClient(), review);
            }
        }, completion);
    }

    /**
     * Updates an existing review of a product. Only the title, body, rating and language can be changed. If
     * the user earns points for this operation 'X-ProductLayer-User-Points' and
     * 'X-ProductLayer-User-Points-Changed' will be present in the response header.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param reviewID
     *         The identifier of the review
     * @param review
     *         The review
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated review
     * @return a Future object to optionally wait for the {@code Review} result or to cancel the query
     */
    public static Future<Review> updateReview(final PLYAndroid client, final String reviewID, final Review
            review, PLYCompletion<Review> completion) {
        return client.submit(new PLYAndroid.Query<Review>() {
            @Override
            public Review execute() {
                return com.productlayer.rest.client.services.ReviewService.updateReview(client
                        .getRestClient(), reviewID, review);
            }
        }, completion);
    }
}
