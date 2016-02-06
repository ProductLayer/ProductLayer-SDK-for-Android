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

package com.productlayer.android.common.adapter;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.common.view.UserPreview;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.services.ImageService;
import com.productlayer.core.beans.User;

import java.util.List;

/**
 * Provides the list of user views to look at and (un)follow.
 */
public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.UserPreviewHolder> {

    private Context context;
    private NavigationHandler navigationHandler;
    private UserHandler userHandler;
    private PLYAndroid client;

    private int avatarSizePx;
    private DialogFragment dismissableDialog;
    private boolean showFollowButton;

    private User[] users;

    private User currentUser;
    private List<User> currentUserFriends;

    /**
     * Creates the adapter listing the supplied users.
     *
     * @param context
     *         the application context
     * @param navigationHandler
     *         the activity's navigation handler to open new screens
     * @param userHandler
     *         the activity's user handler to get current user information
     * @param client
     *         the PLYAndroid REST client to use for querying for data
     * @param avatarSizePx
     *         the size of the user images in px
     * @param dismissableDialog
     *         if this adapter is listing users in a dialog it will be dismissed on any user click
     * @param showFollowButton
     *         whether to show an (un)follow button
     * @param users
     *         the users that are listed
     */
    public FollowAdapter(Context context, NavigationHandler navigationHandler, UserHandler userHandler,
            PLYAndroid client, int avatarSizePx, DialogFragment dismissableDialog, boolean
            showFollowButton, User[] users) {
        this.context = context;
        this.navigationHandler = navigationHandler;
        this.userHandler = userHandler;
        this.client = client;
        this.avatarSizePx = avatarSizePx;
        this.dismissableDialog = dismissableDialog;
        this.showFollowButton = showFollowButton;
        this.users = users == null ? new User[0] : users;
        setHasStableIds(true);
    }

    @Override
    public UserPreviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UserPreview userPreview = new UserPreview(context, navigationHandler, avatarSizePx,
                dismissableDialog, showFollowButton);
        return new UserPreviewHolder(userPreview);
    }

    @Override
    public void onBindViewHolder(UserPreviewHolder holder, int position) {
        User user = users[position];
        String userImageUrl = ImageService.getUserAvatarURL(client, user.getId(), avatarSizePx);
        holder.userPreview.setUser(user, userImageUrl, userHandler, client);
    }

    @Override
    public int getItemCount() {
        return users.length;
    }

    @Override
    public long getItemId(int position) {
        return users[position].getId().hashCode();
    }

    /**
     * ViewHolder cached by the RecyclerView. Holds a UserPreview.
     */
    public static class UserPreviewHolder extends RecyclerView.ViewHolder {

        public UserPreview userPreview;

        /**
         * Creates the ViewHolder caching a UserPreview.
         *
         * @param userPreview
         *         the UserPreview to cache
         */
        public UserPreviewHolder(UserPreview userPreview) {
            super(userPreview);
            this.userPreview = userPreview;
        }
    }
}
