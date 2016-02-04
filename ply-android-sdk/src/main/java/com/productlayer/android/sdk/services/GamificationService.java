package com.productlayer.android.sdk.services;

import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.core.beans.gamification.Achievement;

import java.util.concurrent.Future;

public class GamificationService {

    /**
     * Gets localized achievement for key.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param key
     *         The key of the achievement. e.g.: pl-achv-first_photo
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: The
     *         localized achievement
     * @return a Future object to optionally wait for the {@code Achievement} result or to cancel the query
     */
    public static Future<Achievement> getAchievementForKey(final PLYAndroid client, final String key, final
    String language, PLYCompletion<Achievement> completion) {
        return client.submit(new PLYAndroid.Query<Achievement>() {
            @Override
            public Achievement execute() {
                return com.productlayer.rest.client.services.GamificationService.getAchievementForKey
                        (client.getRestClient(), key, language);
            }
        }, completion);
    }

    /**
     * Gets all achievements unlocked by the given user.
     *
     * @param client
     *         the PLYAndroid SDK client configured to handle communications with the ProductLayer API server
     * @param userId
     *         The identifier of the user
     * @param language
     *         [Optional] The preferred language (e.g.: 'en' or 'de')
     * @param completion
     *         dealing with any errors or successful completion of the query, handling the results: Any
     *         achievements unlocked by the user
     * @return a Future object to optionally wait for the {@code Achievement[]} result or to cancel the query
     */
    public static Future<Achievement[]> getAchievementForUser(final PLYAndroid client, final String userId,
            final String language, PLYCompletion<Achievement[]> completion) {
        return client.submit(new PLYAndroid.Query<Achievement[]>() {
            @Override
            public Achievement[] execute() {
                return com.productlayer.rest.client.services.GamificationService.getAchievementForUser
                        (client.getRestClient(), userId, language);
            }
        }, completion);
    }
}
