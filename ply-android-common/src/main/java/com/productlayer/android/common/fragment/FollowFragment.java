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

package com.productlayer.android.common.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.productlayer.android.common.R;
import com.productlayer.android.common.adapter.FollowAdapter;
import com.productlayer.android.common.handler.HasNavigationHandler;
import com.productlayer.android.common.handler.HasPLYAndroidHolder;
import com.productlayer.android.common.handler.HasUserHandler;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.handler.PLYAndroidHolder;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.common.util.MetricsUtil;
import com.productlayer.android.common.util.SnackbarUtil;
import com.productlayer.android.common.util.SystemBarsUtil;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.UserService;
import com.productlayer.core.beans.User;

/**
 * Lists a static number of users (friends, followers) in a RecyclerView with a staggered grid layout.
 *
 * Requires the activity to implement {@link HasNavigationHandler}, {@link HasUserHandler}, {@link
 * HasPLYAndroidHolder}.
 */
public class FollowFragment extends NamedFragment {

    public static final String NAME = "Follow";

    private static final String ORDER_BY = "pl-usr-points_desc";

    private static final String KEY_TYPE = "type";
    private static final String KEY_USER_ID = "user_id";

    private static final int AVATAR_SIZE_DP = 64;

    private NavigationHandler navigationHandler;
    private UserHandler userHandler;
    private PLYAndroid client;

    private Type type;
    private String userId;

    private User[] users;

    private RecyclerView recyclerView;

    // listener would be garbage-collected without reference
    private UserHandler.OnFriendsUpdateListener onFriendsUpdateListener;

    /**
     * Constructs a new instance with the specified parameters. The parameters passed this way survive
     * recreation of the fragment due to orientation changes etc.
     *
     * @param type
     *         the type of users to list (friends, followers)
     * @param userId
     *         the user ID to show friends or followers of
     * @return the fragment with the given parameters
     */
    @SuppressWarnings("TypeMayBeWeakened")
    public static FollowFragment newInstance(Type type, String userId) {
        FollowFragment followFragment = new FollowFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_TYPE, type);
        args.putString(KEY_USER_ID, userId);
        followFragment.setArguments(args);
        return followFragment;
    }

    /**
     * @param type
     *         the type of users to list (friends, followers)
     * @param userId
     *         the user ID to show friends or followers of
     * @return this fragment's name and initialization parameters
     */
    public static String makeInstanceName(Type type, String userId) {
        String typeParam = type == null ? "" : type.name();
        String userIdParam = userId == null ? "" : userId;
        return NAME + "(" + typeParam + "," + userIdParam + ")";
    }

    @Override
    public String getInstanceName() {
        return makeInstanceName(type, userId);
    }

    @Override
    public FragmentGrouping getGrouping() {
        return userHandler != null && userHandler.getUser() != null && userHandler.getUser().getId().equals
                (userId) ? FragmentGrouping.OWN_FRIENDS : FragmentGrouping.NONE;
    }

    // FRAGMENT LIFECYCLE - START //

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        try {
            navigationHandler = ((HasNavigationHandler) activity).getNavigationHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasNavigationHandler");
        }
        try {
            userHandler = ((HasUserHandler) activity).getUserHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasUserHandler");
        }
        PLYAndroidHolder plyAndroidHolder;
        try {
            plyAndroidHolder = ((HasPLYAndroidHolder) activity).getPLYAndroidHolder();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasPLYAndroidHolder");
        }
        client = plyAndroidHolder.getPLYAndroid();
        if (client == null) {
            throw new RuntimeException("PLYAndroid must bet set before creating fragment " + this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        type = (Type) args.getSerializable(KEY_TYPE);
        userId = args.getString(KEY_USER_ID);
        loadUsers(false);
        setupListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Context context = getContext();
        // inflate the layout
        View layout = inflater.inflate(R.layout.fragment_follow, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.follow_recycler_view);
        int columns = (int) Math.floor(MetricsUtil.getWidthDp() / (AVATAR_SIZE_DP * 2 + 10));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, columns);
        recyclerView.setLayoutManager(gridLayoutManager);
        // prevent users from being covered by the navigation bar
        if (SystemBarsUtil.hasTranslucentNavigationBar(context)) {
            layout.setPadding(layout.getPaddingLeft(), layout.getPaddingTop(), layout.getPaddingRight(),
                    layout.getPaddingBottom() + SystemBarsUtil.getNavigationBarHeight(context));
            ((ViewGroup) layout).setClipToPadding(false);
            // TODO move up if safe for all screens
        }
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateRecyclerView(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // despite the recyclerview being destroyed it needs to be reset first to avoid memory leaks
        recyclerView.setAdapter(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyListener();
    }

    // FRAGMENT LIFECYCLE - END //

    /**
     * Loads the users to display.
     *
     * @param recreateAdapter
     *         whether to recreate the recyclerview adapter if it has already been set
     */
    private void loadUsers(final boolean recreateAdapter) {
        // TODO make loading indicator count calls of show and hide
        //LoadingIndicator.show();
        PLYCompletion<User[]> usersCompletion = new PLYCompletion<User[]>() {
            @Override
            public void onSuccess(User[] result) {
            }

            @Override
            public void onPostSuccess(User[] result) {
                users = result != null ? result : new User[0];
                populateRecyclerView(recreateAdapter);
                //LoadingIndicator.hide();
            }

            @Override
            public void onError(PLYAndroid.QueryError error) {
                Log.w(FollowFragment.class.getSimpleName(), error.getMessage());
                if (!error.isHttpStatusError()) {
                    SnackbarUtil.make(getActivity(), getView(), R.string.connection_failed_internet,
                            Snackbar.LENGTH_LONG).show();
                }
                //LoadingIndicator.hide();
            }
        };
        if (type == Type.FRIENDS) {
            // get friends (users that are being followed)
            UserService.getFollowedUsers(client, null, null, userId, ORDER_BY, usersCompletion);
        } else {
            // get followers (users that are following)
            UserService.getFollowingUsers(client, null, null, userId, ORDER_BY, usersCompletion);
        }
    }

    /**
     * Creates and attaches the adapter to populate the view.
     *
     * Must be run on the UI thread.
     *
     * @param recreateAdapter
     *         whether to recreate the recyclerview adapter if it has already been set
     */
    private void populateRecyclerView(boolean recreateAdapter) {
        if (users == null) {
            // friends/followers retrieval not finished yet
            return;
        }
        if (recyclerView == null) {
            // UI not inflated yet
            return;
        }
        RecyclerView.Adapter prevAdapter = recyclerView.getAdapter();
        if (!recreateAdapter && prevAdapter != null) {
            // don't run twice - adapter already attached
            return;
        }
        FollowAdapter followAdapter = new FollowAdapter(getContext(), navigationHandler, userHandler,
                client, MetricsUtil.inPx(AVATAR_SIZE_DP), null, false, users);
        if (prevAdapter == null) {
            recyclerView.setAdapter(followAdapter);
        } else {
            recyclerView.swapAdapter(followAdapter, false);
        }
    }

    /**
     * Sets up and registers a listener to be notified of updates to the current user's friends list.
     */
    private void setupListener() {
        if (onFriendsUpdateListener != null) {
            return;
        }
        onFriendsUpdateListener = new UserHandler.OnFriendsUpdateListener() {
            @Override
            public void onFriendsUpdate() {
                Log.v("FFragmentCallback", "Update to the friends list received");
                // reload the whole view
                loadUsers(true);
                // TODO implement fine-grained updates
            }
        };
        userHandler.addOnFriendsUpdateListener(onFriendsUpdateListener);
    }

    /**
     * Destroys listener to stop being notified of any updates.
     */
    private void destroyListener() {
        if (onFriendsUpdateListener == null) {
            return;
        }
        userHandler.removeOnFriendsUpdateListener(onFriendsUpdateListener);
        onFriendsUpdateListener = null;
    }

    /**
     * Describes the type of users to be listed.
     */
    public enum Type {
        FRIENDS, FOLLOWERS
    }
}
