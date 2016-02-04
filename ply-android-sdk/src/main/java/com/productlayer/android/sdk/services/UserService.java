package com.productlayer.android.sdk.services;

import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.core.beans.Count;
import com.productlayer.core.beans.Product;
import com.productlayer.core.beans.User;
import com.productlayer.core.beans.UserChangePassword;
import com.productlayer.core.beans.UserEmail;
import com.productlayer.core.beans.ranking.RankingResults;
import com.productlayer.core.beans.reports.ProblemReport;

import java.util.Date;
import java.util.concurrent.Future;

public class UserService {

    /**
     * Changes the password of the current user. An email will be sent to the user, so a compromised account
     * can be detected. The user will be automatically logged out and needs to login with the new password.
     * <br> There are 2 ways of updating the password: <ul> <li>the user must be logged in</li> <li>a reset
     * token must be present</li> </ul> The reset token will be sent via email if the user doesn't know the
     * password.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param resetToken
     *         [Optional] The user must be logged in or the reset token must be set to reset the password.
     * @param oldPassword
     *         The current password
     * @param newPassword
     *         The new password
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The user
     *         if found
     * @return a Future object to optionally wait for the {@code User} result or to cancel the query
     */
    public static Future<User> changePassword(final PLYAndroid client, final String resetToken, final
    String oldPassword, final String newPassword, PLYCompletion<User> completion) {
        return client.submit(new PLYAndroid.Query<User>() {
            @Override
            public User execute() {
                return com.productlayer.rest.client.services.UserService.changePassword(client
                        .getRestClient(), resetToken, oldPassword, newPassword);
            }
        }, completion);
    }

    /**
     * Changes the password of the current user. An email will be sent to the user, so a compromised account
     * can be detected. The user will be automatically logged out and needs to login with the new password.
     * <br> There are 2 ways of updating the password: <ul> <li>the user must be logged in</li> <li>a reset
     * token must be present</li> </ul> The reset token will be sent via email if the user doesn't know the
     * password.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param resetToken
     *         [Optional] The user must be logged in or the reset token must be set to reset the password.
     * @param changePassword
     *         The changePassword
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The user
     *         if found
     * @return a Future object to optionally wait for the {@code User} result or to cancel the query
     */
    public static Future<User> changePassword(final PLYAndroid client, final String resetToken, final
    UserChangePassword changePassword, PLYCompletion<User> completion) {
        return client.submit(new PLYAndroid.Query<User>() {
            @Override
            public User execute() {
                return com.productlayer.rest.client.services.UserService.changePassword(client
                        .getRestClient(), resetToken, changePassword);
            }
        }, completion);
    }

    /**
     * Registers a new user. Minimum information which must be provided is (nickname, email).
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param user
     *         The user
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The newly
     *         created user
     * @return a Future object to optionally wait for the {@code User} result or to cancel the query
     */
    public static Future<User> createUser(final PLYAndroid client, final User user, PLYCompletion<User>
            completion) {
        return client.submit(new PLYAndroid.Query<User>() {
            @Override
            public User execute() {
                return com.productlayer.rest.client.services.UserService.createUser(client.getRestClient(),
                        user);
            }
        }, completion);
    }

    /**
     * Disable email newsletter with the token from a newsletter email.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param token
     *         The one time token from the email can only be used to disable the email newsletter.
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: OK if
     *         success.
     * @return a Future object to optionally wait for the {@code String} result or to cancel the query
     */
    public static Future<String> disableEmailNewsletterViaToken(final PLYAndroid client, final String
            token, PLYCompletion<String> completion) {
        return client.submit(new PLYAndroid.Query<String>() {
            @Override
            public String execute() {
                return com.productlayer.rest.client.services.UserService.disableEmailNewsletterViaToken
                        (client.getRestClient(), token);
            }
        }, completion);
    }

    /**
     * Disable email notifications with the token from a notification email.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param token
     *         The one time token from the email can only be used to disable the email notifications.
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: OK if
     *         success.
     * @return a Future object to optionally wait for the {@code String} result or to cancel the query
     */
    public static Future<String> disableEmailNotificationViaToken(final PLYAndroid client, final String
            token, PLYCompletion<String> completion) {
        return client.submit(new PLYAndroid.Query<String>() {
            @Override
            public String execute() {
                return com.productlayer.rest.client.services.UserService.disableEmailNotificationViaToken
                        (client.getRestClient(), token);
            }
        }, completion);
    }

    /**
     * Enable or disable email newsletter for the user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param enable
     *         Boolean for enabling or disabling the email newsletter
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The user
     *         if found.
     * @return a Future object to optionally wait for the {@code User} result or to cancel the query
     */
    public static Future<User> enableOrDisableEmailNewsletter(final PLYAndroid client, final Boolean
            enable, PLYCompletion<User> completion) {
        return client.submit(new PLYAndroid.Query<User>() {
            @Override
            public User execute() {
                return com.productlayer.rest.client.services.UserService.enableOrDisableEmailNewsletter
                        (client.getRestClient(), enable);
            }
        }, completion);
    }

    /**
     * Enable or disable email notification for the user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param enable
     *         Boolean for enabling or disabling the email notification
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The user
     *         if found.
     * @return a Future object to optionally wait for the {@code User} result or to cancel the query
     */
    public static Future<User> enableOrDisableEmailNotification(final PLYAndroid client, final Boolean
            enable, PLYCompletion<User> completion) {
        return client.submit(new PLYAndroid.Query<User>() {
            @Override
            public User execute() {
                return com.productlayer.rest.client.services.UserService.enableOrDisableEmailNotification
                        (client.getRestClient(), enable);
            }
        }, completion);
    }

    /**
     * Find friends from other connected social providers which have already a productlayer account .
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param facebook
     *         [Optional] Find facebook friends which are already here. Default: true
     * @param twitter
     *         [Optional] Find twitter friends which are already here. Default: true
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The found
     *         friends from other social providers.
     * @return a Future object to optionally wait for the {@code User[]} result or to cancel the query
     */
    public static Future<User[]> findFriendsFromOtherSocialNetworks(final PLYAndroid client, final Boolean
            facebook, final Boolean twitter, PLYCompletion<User[]> completion) {
        return client.submit(new PLYAndroid.Query<User[]>() {
            @Override
            public User[] execute() {
                return com.productlayer.rest.client.services.UserService.findFriendsFromOtherSocialNetworks
                        (client.getRestClient(), facebook, twitter);
            }
        }, completion);
    }

    /**
     * Follows a specific user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param followUser
     *         The nickname of the user
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated user
     * @return a Future object to optionally wait for the {@code User} result or to cancel the query
     */
    public static Future<User> followUser(final PLYAndroid client, final String followUser,
            PLYCompletion<User> completion) {
        return client.submit(new PLYAndroid.Query<User>() {
            @Override
            public User execute() {
                return com.productlayer.rest.client.services.UserService.followUser(client.getRestClient(),
                        followUser);
            }
        }, completion);
    }

    /**
     * Gets GTINs of all products that have been downvoted by a user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userID
     *         The identifier of the user
     * @param categoryKey
     *         [Optional] The category key starting with 'pl-prod-cat-', e.g.: pl-prod-cat-books
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: GTINs of
     *         any found products
     * @return a Future object to optionally wait for the {@code String[]} result or to cancel the query
     */
    public static Future<String[]> getDownVotedGTINsFromUser(final PLYAndroid client, final String userID,
            final String categoryKey, PLYCompletion<String[]> completion) {
        return client.submit(new PLYAndroid.Query<String[]>() {
            @Override
            public String[] execute() {
                return com.productlayer.rest.client.services.UserService.getDownVotedGTINsFromUser(client
                        .getRestClient(), userID, categoryKey);
            }
        }, completion);
    }

    /**
     * Gets the count of all products that have been downvoted by a user. If a category key is present, only
     * the count for the specified category and subcategories is returned.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userID
     *         The identifier of the user
     * @param categoryKey
     *         [Optional] The category key starting with 'pl-prod-cat-', e.g.: pl-prod-cat-books
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         product count
     * @return a Future object to optionally wait for the {@code Count} result or to cancel the query
     */
    public static Future<Count> getDownVotedProductCountFromUser(final PLYAndroid client, final String
            userID, final String categoryKey, PLYCompletion<Count> completion) {
        return client.submit(new PLYAndroid.Query<Count>() {
            @Override
            public Count execute() {
                return com.productlayer.rest.client.services.UserService.getDownVotedProductCountFromUser
                        (client.getRestClient(), userID, categoryKey);
            }
        }, completion);
    }

    /**
     * Get all products which have been down-voted by the user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userID
     *         The identifier of the user
     * @param categoryKey
     *         [Optional] The category key starting with 'pl-prod-cat-', e.g.: pl-prod-cat-books
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de')
     * @param fetchOnly
     *         [Optional] Fetch only specific keys
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Array of
     *         products
     * @return a Future object to optionally wait for the {@code Product[]} result or to cancel the query
     */
    public static Future<Product[]> getDownVotedProductsFromUser(final PLYAndroid client, final String
            userID, final String categoryKey, final String language, final String fetchOnly,
            PLYCompletion<Product[]> completion) {
        return client.submit(new PLYAndroid.Query<Product[]>() {
            @Override
            public Product[] execute() {
                return com.productlayer.rest.client.services.UserService.getDownVotedProductsFromUser
                        (client.getRestClient(), userID, categoryKey, language, fetchOnly);
            }
        }, completion);
    }

    /**
     * Gets the followed users IDs of the specific user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param nicknameOrId
     *         The nickname or ID of the user
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any
     *         followed user IDs
     * @return a Future object to optionally wait for the {@code String[]} result or to cancel the query
     */
    public static Future<String[]> getFollowedUserIDs(final PLYAndroid client, final String nicknameOrId,
            PLYCompletion<String[]> completion) {
        return client.submit(new PLYAndroid.Query<String[]>() {
            @Override
            public String[] execute() {
                return com.productlayer.rest.client.services.UserService.getFollowedUserIDs(client
                        .getRestClient(), nicknameOrId);
            }
        }, completion);
    }

    /**
     * Gets the followed users of a specific user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param page
     *         [Optional] The page to be displayed starting with 0 - if no page has been provided, paging is
     *         disabled
     * @param recordsPerPage
     *         [Optional] The amount of items to be displayed per page
     * @param nicknameOrId
     *         The nickname or ID of the user
     * @param orderBy
     *         [Optional] Used to sort the result-set by one or more columns. The order by parameters are
     *         <strong>seperated by a semicolon</strong>. Also you need to provide a prefix <strong>asc for
     *         ascending</strong> or <strong>desc for descending order</strong><br> <br>
     *         <strong>Default:</strong> pl-usr-points_desc (User Score descending)
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any
     *         followed users
     * @return a Future object to optionally wait for the {@code User[]} result or to cancel the query
     */
    public static Future<User[]> getFollowedUsers(final PLYAndroid client, final Integer page, final
    Integer recordsPerPage, final String nicknameOrId, final String orderBy, PLYCompletion<User[]>
            completion) {
        return client.submit(new PLYAndroid.Query<User[]>() {
            @Override
            public User[] execute() {
                return com.productlayer.rest.client.services.UserService.getFollowedUsers(client
                        .getRestClient(), page, recordsPerPage, nicknameOrId, orderBy);
            }
        }, completion);
    }

    /**
     * Gets the follower IDs of a specific user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param nicknameOrId
     *         The nickname or ID of the user
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any user
     *         IDs following the specified user
     * @return a Future object to optionally wait for the {@code String[]} result or to cancel the query
     */
    public static Future<String[]> getFollowingUserIDs(final PLYAndroid client, final String nicknameOrId,
            PLYCompletion<String[]> completion) {
        return client.submit(new PLYAndroid.Query<String[]>() {
            @Override
            public String[] execute() {
                return com.productlayer.rest.client.services.UserService.getFollowingUserIDs(client
                        .getRestClient(), nicknameOrId);
            }
        }, completion);
    }

    /**
     * Gets the followers of a specific user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param page
     *         [Optional] The page to be displayed starting with 0 - if no page has been provided, paging is
     *         disabled
     * @param recordsPerPage
     *         [Optional] The amount of items to be displayed per page
     * @param nicknameOrId
     *         The nickname or ID of the user
     * @param orderBy
     *         [Optional] Used to sort the result-set by one or more columns. The order by parameters are
     *         <strong>seperated by a semicolon</strong>. Also you need to provide a prefix <strong>asc for
     *         ascending</strong> or <strong>desc for descending order</strong><br> <br>
     *         <strong>Default:</strong> pl-usr-points_desc (User Score descending)
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any users
     *         following the specified user
     * @return a Future object to optionally wait for the {@code User[]} result or to cancel the query
     */
    public static Future<User[]> getFollowingUsers(final PLYAndroid client, final Integer page, final
    Integer recordsPerPage, final String nicknameOrId, final String orderBy, PLYCompletion<User[]>
            completion) {
        return client.submit(new PLYAndroid.Query<User[]>() {
            @Override
            public User[] execute() {
                return com.productlayer.rest.client.services.UserService.getFollowingUsers(client
                        .getRestClient(), page, recordsPerPage, nicknameOrId, orderBy);
            }
        }, completion);
    }

    /**
     * Gets the top scoring users within a time range.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param from_date
     *         [Optional] Start date, format: yyyy-MM-dd HH:mm:ss
     * @param to_date
     *         [Optional] End date, format: yyyy-MM-dd HH:mm:ss
     * @param count
     *         [Optional] The amount of results to be returned, default: '200'
     * @param showOpines
     *         [Optional] Display opines, default: 'true'
     * @param showReviews
     *         [Optional] Display reviews, default: 'true'
     * @param showPictures
     *         [Optional] Display uploaded images, default: 'true'
     * @param showProducts
     *         [Optional] Display created/updated products, default: 'true'
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The top
     *         users contained within a ranking results object
     * @return a Future object to optionally wait for the {@code RankingResults<User>} result or to cancel the
     * query
     */
    public static Future<RankingResults<User>> getTopScorers(final PLYAndroid client, final Date from_date,
            final Date to_date, final int count, final Boolean showOpines, final Boolean showReviews, final
    Boolean showPictures, final Boolean showProducts, PLYCompletion<RankingResults<User>> completion) {
        return client.submit(new PLYAndroid.Query<RankingResults<User>>() {
            @Override
            public RankingResults<User> execute() {
                return com.productlayer.rest.client.services.UserService.getTopScorers(client.getRestClient
                        (), from_date, to_date, count, showOpines, showReviews, showPictures, showProducts);
            }
        }, completion);
    }

    /**
     * Gets GTINs of all products that have been upvoted by a user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userID
     *         The identifier of the user
     * @param categoryKey
     *         [Optional] The category key starting with 'pl-prod-cat-', e.g.: pl-prod-cat-books
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: GTINs of
     *         any found products
     * @return a Future object to optionally wait for the {@code String[]} result or to cancel the query
     */
    public static Future<String[]> getUpVotedGTINsFromUser(final PLYAndroid client, final String userID,
            final String categoryKey, PLYCompletion<String[]> completion) {
        return client.submit(new PLYAndroid.Query<String[]>() {
            @Override
            public String[] execute() {
                return com.productlayer.rest.client.services.UserService.getUpVotedGTINsFromUser(client
                        .getRestClient(), userID, categoryKey);
            }
        }, completion);
    }

    /**
     * Gets the count of all products that have been upvoted by a user. If a category key is present, only the
     * count for the specified category and subcategories is returned.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userID
     *         The identifier of the user
     * @param categoryKey
     *         [Optional] The category key starting with 'pl-prod-cat-', e.g.: pl-prod-cat-books
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         product count
     * @return a Future object to optionally wait for the {@code Count} result or to cancel the query
     */
    public static Future<Count> getUpVotedProductCountFromUser(final PLYAndroid client, final String
            userID, final String categoryKey, PLYCompletion<Count> completion) {
        return client.submit(new PLYAndroid.Query<Count>() {
            @Override
            public Count execute() {
                return com.productlayer.rest.client.services.UserService.getUpVotedProductCountFromUser
                        (client.getRestClient(), userID, categoryKey);
            }
        }, completion);
    }

    /**
     * Get all products which have been up-voted by the user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userID
     *         The identifier of the user
     * @param categoryKey
     *         [Optional] The category key starting with 'pl-prod-cat-', e.g.: pl-prod-cat-books
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de')
     * @param fetchOnly
     *         [Optional] Fetch only specific keys
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Array of
     *         products
     * @return a Future object to optionally wait for the {@code Product[]} result or to cancel the query
     */
    public static Future<Product[]> getUpVotedProductsFromUser(final PLYAndroid client, final String
            userID, final String categoryKey, final String language, final String fetchOnly,
            PLYCompletion<Product[]> completion) {
        return client.submit(new PLYAndroid.Query<Product[]>() {
            @Override
            public Product[] execute() {
                return com.productlayer.rest.client.services.UserService.getUpVotedProductsFromUser(client
                        .getRestClient(), userID, categoryKey, language, fetchOnly);
            }
        }, completion);
    }

    /**
     * Gets a specific user by nickname or ID. The nickname can change so the ID should be used to request
     * user data.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param nicknameOrID
     *         The nickname or ID of the user
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         identified user
     * @return a Future object to optionally wait for the {@code User} result or to cancel the query
     */
    public static Future<User> getUserByNicknameOrID(final PLYAndroid client, final String nicknameOrID,
            PLYCompletion<User> completion) {
        return client.submit(new PLYAndroid.Query<User>() {
            @Override
            public User execute() {
                return com.productlayer.rest.client.services.UserService.getUserByNicknameOrID(client
                        .getRestClient(), nicknameOrID);
            }
        }, completion);
    }

    /**
     * Gets the overall user count or the count for a specific timeframe.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param from_date
     *         [Optional] Start date, format: yyyy-MM-dd HH:mm:ss
     * @param to_date
     *         [Optional] End date, format: yyyy-MM-dd HH:mm:ss
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The user
     *         count
     * @return a Future object to optionally wait for the {@code Count} result or to cancel the query
     */
    public static Future<Count> getUserCount(final PLYAndroid client, final Date from_date, final Date
            to_date, PLYCompletion<Count> completion) {
        return client.submit(new PLYAndroid.Query<Count>() {
            @Override
            public Count execute() {
                return com.productlayer.rest.client.services.UserService.getUserCount(client.getRestClient
                        (), from_date, to_date);
            }
        }, completion);
    }

    /**
     * Gets the points of a user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userId
     *         The identifier of the user
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The user's
     *         points
     * @return a Future object to optionally wait for the {@code Long} result or to cancel the query
     */
    public static Future<Long> getUserPoints(final PLYAndroid client, final String userId,
            PLYCompletion<Long> completion) {
        return client.submit(new PLYAndroid.Query<Long>() {
            @Override
            public Long execute() {
                return com.productlayer.rest.client.services.UserService.getUserPoints(client.getRestClient
                        (), userId);
            }
        }, completion);
    }

    /**
     * Get a user's scoring history within a time range.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userId
     *         The identifier of the user
     * @param from_date
     *         [Optional] Start date, format: yyyy-MM-dd
     * @param to_date
     *         [Optional] End date, format: yyyy-MM-dd
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The user's
     *         scoring history
     * @return a Future object to optionally wait for the {@code Long} result or to cancel the query
     */
    public static Future<Long> getUserPointsHistory(final PLYAndroid client, final String userId, final
    Date from_date, final Date to_date, PLYCompletion<Long> completion) {
        return client.submit(new PLYAndroid.Query<Long>() {
            @Override
            public Long execute() {
                return com.productlayer.rest.client.services.UserService.getUserPointsHistory(client
                        .getRestClient(), userId, from_date, to_date);
            }
        }, completion);
    }

    /**
     * Checks if the active session is valid and if the user is signed in.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: True if
     *         signed in, false else
     * @return a Future object to optionally wait for the {@code Boolean} result or to cancel the query
     */
    public static Future<Boolean> isSignedIn(final PLYAndroid client, PLYCompletion<Boolean> completion) {
        return client.submit(new PLYAndroid.Query<Boolean>() {
            @Override
            public Boolean execute() {
                return com.productlayer.rest.client.services.UserService.isSignedIn(client.getRestClient());
            }
        }, completion);
    }

    /**
     * Logs in the user using basic authentication.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param username
     *         The username (will be set in {@code client} on success)
     * @param password
     *         The password (will be set in {@code client} on success)
     * @param rememberMe
     *         Whether to remember the login (a token will be received and set in {@code client})
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The logged
     *         in user or null if the login failed
     * @return a Future object to optionally wait for the {@code User} result or to cancel the query
     */
    public static Future<User> login(final PLYAndroid client, final String username, final String password,
            final Boolean rememberMe, PLYCompletion<User> completion) {
        return client.submit(new PLYAndroid.Query<User>() {
            @Override
            public User execute() {
                return com.productlayer.rest.client.services.UserService.login(client.getRestClient(),
                        username, password, rememberMe);
            }
        }, completion);
    }

    /**
     * Logs in the user using an authorization token.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param token
     *         The authorization token (will be set in {@code client} as session or token on success)
     * @param rememberMe
     *         Whether to remember the login
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The logged
     *         in user or null if the login failed
     * @return a Future object to optionally wait for the {@code User} result or to cancel the query
     */
    public static Future<User> login(final PLYAndroid client, final String token, final Boolean rememberMe,
            PLYCompletion<User> completion) {
        return client.submit(new PLYAndroid.Query<User>() {
            @Override
            public User execute() {
                return com.productlayer.rest.client.services.UserService.login(client.getRestClient(),
                        token, rememberMe);
            }
        }, completion);
    }

    /**
     * Logs out the current user.
     *
     * Clears username/password/session/token in {@code client}.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     */
    public static Future<Void> logout(final PLYAndroid client, PLYCompletion<Void> completion) {
        return client.submit(new PLYAndroid.Query<Void>() {
            @Override
            public Void execute() {
                com.productlayer.rest.client.services.UserService.logout(client.getRestClient());
                return null;
            }
        }, completion);
    }

    /**
     * Sends a report about a user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userId
     *         The identifier of the user
     * @param report
     *         The report
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         problem report object
     * @return a Future object to optionally wait for the {@code ProblemReport} result or to cancel the query
     */
    public static Future<ProblemReport> reportUser(final PLYAndroid client, final String userId, final
    ProblemReport report, PLYCompletion<ProblemReport> completion) {
        return client.submit(new PLYAndroid.Query<ProblemReport>() {
            @Override
            public ProblemReport execute() {
                return com.productlayer.rest.client.services.UserService.reportUser(client.getRestClient(),
                        userId, report);
            }
        }, completion);
    }

    /**
     * Generates a new password for the user. The password will be sent to the user's email address.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param user
     *         The user
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The user
     *         if found
     * @return a Future object to optionally wait for the {@code User} result or to cancel the query
     */
    public static Future<User> resetPasswordViaEmail(final PLYAndroid client, final UserEmail user,
            PLYCompletion<User> completion) {
        return client.submit(new PLYAndroid.Query<User>() {
            @Override
            public User execute() {
                return com.productlayer.rest.client.services.UserService.resetPasswordViaEmail(client
                        .getRestClient(), user);
            }
        }, completion);
    }

    /**
     * Searches for a user with a simple text search.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param query
     *         The query may contain the email, nickname, first name and last name of the user.
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any users
     *         matching the text search
     * @return a Future object to optionally wait for the {@code User[]} result or to cancel the query
     */
    public static Future<User[]> searchUsers(final PLYAndroid client, final String query,
            PLYCompletion<User[]> completion) {
        return client.submit(new PLYAndroid.Query<User[]>() {
            @Override
            public User[] execute() {
                return com.productlayer.rest.client.services.UserService.searchUsers(client.getRestClient()
                        , query);
            }
        }, completion);
    }

    /**
     * Unfollows a specific user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param followUser
     *         The nickname of the user
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated user
     * @return a Future object to optionally wait for the {@code User} result or to cancel the query
     */
    public static Future<User> unfollowUser(final PLYAndroid client, final String followUser,
            PLYCompletion<User> completion) {
        return client.submit(new PLYAndroid.Query<User>() {
            @Override
            public User execute() {
                return com.productlayer.rest.client.services.UserService.unfollowUser(client.getRestClient
                        (), followUser);
            }
        }, completion);
    }

    /**
     * Updates a specific user. Users can only update their own account.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param user
     *         The user
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         updated user
     * @return a Future object to optionally wait for the {@code User} result or to cancel the query
     */
    public static Future<User> updateUser(final PLYAndroid client, final User user, PLYCompletion<User>
            completion) {
        return client.submit(new PLYAndroid.Query<User>() {
            @Override
            public User execute() {
                return com.productlayer.rest.client.services.UserService.updateUser(client.getRestClient(),
                        user);
            }
        }, completion);
    }
}
