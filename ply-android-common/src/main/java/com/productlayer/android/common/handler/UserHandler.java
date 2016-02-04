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

package com.productlayer.android.common.handler;

import com.productlayer.core.beans.SimpleUserInfo;
import com.productlayer.core.beans.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides, updates and restores information about the current user and notifies listeners of events.
 */
public interface UserHandler {

    /**
     * @return the current user
     */
    User getUser();

    /**
     * Updates the currently stored user object if its retrieval time is newer than the current one.
     *
     * @param user
     *         the new user object
     * @param userRetrievalTime
     *         the retrieval time of the user object or null for now
     */
    void setUser(User user, Long userRetrievalTime);

    /**
     * Checks whether the specified user is the currently logged in one.
     *
     * @param user
     *         the user to compare with the current one
     * @return true if this user is currently logged in, false else
     */
    boolean isCurrentUser(User user);

    /**
     * @param key
     *         the social provider key
     * @return true if the current user is connected with the specified social network, false else
     */
    boolean isSocialNetworkConnected(String key);

    /**
     * @return the nanotime of the last remote user retrieval
     */
    long getUserRetrievalTime();

    /**
     * @return the current user's friends (followed users)
     */
    ArrayList<User> getFriends();

    /**
     * Sets the currently stored user friends.
     *
     * @param friends
     *         the current user's followed users
     */
    void setFriends(ArrayList<User> friends);

    /**
     * Tests whether the specified user is a friend of the currently logged in user.
     *
     * @param user
     *         the possible friend
     * @return true if the specified user is in the stored friends list (or if the specified user is the
     * current user), false else
     */
    boolean isFriend(User user);

    /**
     * Tests whether the specified user is a friend of the currently logged in user.
     *
     * @param user
     *         the possible friend
     * @return true if the specified user is in the stored friends list (or if the specified user is the
     * current user), false else
     */
    boolean isFriend(SimpleUserInfo user);

    /**
     * Adds a user to the current user's friend list.
     *
     * @param user
     *         the user to add
     */
    void addFriend(User user);

    /**
     * Removes a user from the current user's friend list.
     *
     * @param user
     *         the user to remove
     */
    void removeFriend(User user);

    /**
     * Sets a user's score.
     *
     * @param points
     *         the new score
     */
    void setPoints(long points);

    /**
     * Updates a user's list of unlocked achievements.
     *
     * @param keys
     *         the keys of the unlocked achievements
     */
    void newAchievements(List<String> keys);

    /**
     * @return whether the user handler is currently restoring a saved state (non-blocking)
     */
    boolean isLoadingState();

    /**
     * Adds a listener to be notified of friends list updates.
     *
     * Only a weak reference to the listener is kept - maintain a strong reference to avoid garbage collection
     * of the listener.
     *
     * @param onFriendsUpdateListener
     *         the listener to add
     */
    void addOnFriendsUpdateListener(OnFriendsUpdateListener onFriendsUpdateListener);

    /**
     * Removes the specified listener.
     *
     * @param onFriendsUpdateListener
     *         the listener to remove
     */
    void removeOnFriendsUpdateListener(OnFriendsUpdateListener onFriendsUpdateListener);

    /**
     * Adds a listener to be notified of points updates.
     *
     * Only a weak reference to the listener is kept - maintain a strong reference to avoid garbage collection
     * of the listener.
     *
     * @param onPointsUpdateListener
     *         the listener to add
     */
    void addOnPointsUpdateListener(OnPointsUpdateListener onPointsUpdateListener);

    /**
     * Removes the specified listener.
     *
     * @param onPointsUpdateListener
     *         the listener to remove
     */
    void removeOnPointsUpdateListener(OnPointsUpdateListener onPointsUpdateListener);

    /**
     * Adds a listener to be notified of new unlocked achievements.
     *
     * Only a weak reference to the listener is kept - maintain a strong reference to avoid garbage collection
     * of the listener.
     *
     * @param onAchievementsUnlockedListener
     *         the listener to add
     */
    void addOnAchievementsUnlockedListener(OnAchievementsUnlockedListener onAchievementsUnlockedListener);

    /**
     * Removes the specified listener.
     *
     * @param onAchievementsUnlockedListener
     *         the listener to remove
     */
    void removeOnAchievementsUnlockedListener(OnAchievementsUnlockedListener onAchievementsUnlockedListener);

    /**
     * Listens for updates to the friends list.
     */
    interface OnFriendsUpdateListener {

        /**
         * Called when the user's list of friends has been updated.
         */
        void onFriendsUpdate();

    }

    /**
     * Listens for updates to the user's score.
     */
    interface OnPointsUpdateListener {

        /**
         * Called when the user's score has been updated.
         *
         * @param points
         *         the new score
         */
        void onPointsUpdate(long points);

    }

    /**
     * Listens for new achievements unlocked by the user.
     */
    interface OnAchievementsUnlockedListener {

        /**
         * Called when one or more achievements have been unlocked by the user.
         *
         * @param keys
         *         the keys of the achievements that have been unlocked
         */
        void onAchievementsUnlocked(List<String> keys);

    }

}
