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

package com.productlayer.android.demo.handler;

import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.core.beans.SimpleUserInfo;
import com.productlayer.core.beans.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Dummy user handler.
 */
public class DemoUserHandler implements UserHandler {
    @Override
    public User getUser() {
        return null;
    }

    @Override
    public void setUser(User user, Long userRetrievalTime) {
    }

    @Override
    public boolean isCurrentUser(User user) {
        return false;
    }

    @Override
    public boolean isSocialNetworkConnected(String key) {
        return false;
    }

    @Override
    public long getUserRetrievalTime() {
        return 0;
    }

    @Override
    public ArrayList<User> getFriends() {
        return null;
    }

    @Override
    public void setFriends(ArrayList<User> friends) {
    }

    @Override
    public boolean isFriend(User user) {
        return false;
    }

    @Override
    public boolean isFriend(SimpleUserInfo user) {
        return false;
    }

    @Override
    public void addFriend(User user) {
    }

    @Override
    public void removeFriend(User user) {
    }

    @Override
    public void setPoints(long points) {
    }

    @Override
    public void newAchievements(List<String> keys) {
    }

    @Override
    public boolean isLoadingState() {
        return false;
    }

    @Override
    public void addOnFriendsUpdateListener(OnFriendsUpdateListener onFriendsUpdateListener) {
    }

    @Override
    public void removeOnFriendsUpdateListener(OnFriendsUpdateListener onFriendsUpdateListener) {
    }

    @Override
    public void addOnPointsUpdateListener(OnPointsUpdateListener onPointsUpdateListener) {
    }

    @Override
    public void removeOnPointsUpdateListener(OnPointsUpdateListener onPointsUpdateListener) {
    }

    @Override
    public void addOnAchievementsUnlockedListener(OnAchievementsUnlockedListener
            onAchievementsUnlockedListener) {
    }

    @Override
    public void removeOnAchievementsUnlockedListener(OnAchievementsUnlockedListener
            onAchievementsUnlockedListener) {
    }
}
