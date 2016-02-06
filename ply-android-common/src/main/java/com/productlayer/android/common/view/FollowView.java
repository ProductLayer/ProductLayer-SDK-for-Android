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

package com.productlayer.android.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.productlayer.android.common.R;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.UserService;
import com.productlayer.core.beans.User;

/**
 * Contains follow and unfollow icons. Includes click listeners to follow or unfollow a user.
 */
public class FollowView extends RelativeLayout {

    private ImageView followButton;
    private ImageView unfollowButton;

    private User user;
    private UserHandler userHandler;

    public FollowView(Context context) {
        super(context);
        inflateLayout(context);
    }

    public FollowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateLayout(context);
    }

    public FollowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateLayout(context);
    }

    /**
     * Inflates the layout and sets the references to the UI elements.
     *
     * @param context
     *         the application context
     */
    private void inflateLayout(Context context) {
        // inflate layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.compound_follow, this, true);
        // get fields
        followButton = (ImageView) findViewById(R.id.follow_button);
        unfollowButton = (ImageView) findViewById(R.id.unfollow_button);
    }

    /**
     * Initializes the follow/unfollow button depending on the relation between the displayed and currently
     * logged in users.
     *
     * @param user
     *         the displayed user to follow or unfollow
     * @param userHandler
     *         the handler to retrieve information about the currently logged in user from
     * @param client
     *         the REST client to handle remote calls to follow or unfollow
     */
    public void setUser(final User user, final UserHandler userHandler, final PLYAndroid client) {
        this.user = user;
        this.userHandler = userHandler;
        followButton.setVisibility(GONE);
        unfollowButton.setVisibility(GONE);
        User currentUser = userHandler.getUser();
        // no (un)follow button if the currently logged in user is the one being displayed
        if (currentUser != null && user.getId().equals(currentUser.getId())) {
            return;
        }
        // set click listeners
        followButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService.followUser(client, user.getNickname(), new PLYCompletion<User>() {
                    @Override
                    public void onSuccess(User result) {
                    }

                    @Override
                    public void onPostSuccess(User result) {
                        friendAdded();
                        userHandler.setUser(result, null);
                    }

                    @Override
                    public void onError(PLYAndroid.QueryError error) {
                    }
                });
            }
        });
        unfollowButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService.unfollowUser(client, user.getNickname(), new PLYCompletion<User>() {
                    @Override
                    public void onSuccess(User result) {
                    }

                    @Override
                    public void onPostSuccess(User result) {
                        friendRemoved();
                        userHandler.setUser(result, null);
                    }

                    @Override
                    public void onError(PLYAndroid.QueryError error) {
                    }
                });
            }
        });
        if (userHandler.isFriend(user)) {
            // if the displayed user is a friend show the unfollow button
            unfollowButton.setVisibility(VISIBLE);
        } else {
            // if the displayed user is a friend show the follow button
            followButton.setVisibility(VISIBLE);
        }
    }

    /**
     * Updates the UI when a friend is added and notifies the user handler.
     *
     * Must be run on the UI thread.
     */
    private void friendAdded() {
        // update UI
        followButton.setVisibility(GONE);
        unfollowButton.setVisibility(VISIBLE);
        // update the friends list
        userHandler.addFriend(user);
    }

    /**
     * Updates the UI when a friend is removed and notifies the user handler.
     */
    private void friendRemoved() {
        // update UI
        unfollowButton.setVisibility(GONE);
        followButton.setVisibility(VISIBLE);
        // update the friends list
        userHandler.removeFriend(user);
    }
}
