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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.adapter.NamedFragmentPagerAdapter;
import com.productlayer.android.common.global.LoadingIndicator;
import com.productlayer.android.common.handler.AppBarHandler;
import com.productlayer.android.common.handler.HasAppBarHandler;
import com.productlayer.android.common.handler.HasNavigationHandler;
import com.productlayer.android.common.handler.HasPLYAndroidHolder;
import com.productlayer.android.common.handler.HasTimelineSettingsHandler;
import com.productlayer.android.common.handler.HasUserHandler;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.handler.PLYAndroidHolder;
import com.productlayer.android.common.handler.TimelineSettingsHandler;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.common.model.Level;
import com.productlayer.android.common.util.CacheUtil;
import com.productlayer.android.common.util.MetricsUtil;
import com.productlayer.android.common.util.PhotoUtil;
import com.productlayer.android.common.util.PicassoBlur;
import com.productlayer.android.common.util.PicassoTarget;
import com.productlayer.android.common.util.SnackbarUtil;
import com.productlayer.android.common.util.StorageUtil;
import com.productlayer.android.common.util.SystemBarsUtil;
import com.productlayer.android.common.util.ThemeUtil;
import com.productlayer.android.common.view.FollowView;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.ImageService;
import com.productlayer.android.sdk.services.UserService;
import com.productlayer.core.beans.User;
import com.productlayer.core.beans.UserAvatarImage;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.File;

/**
 * Displays a user's profile, including the avatar photo and stats as part of the toolbar as well as the
 * user's timeline, friends, followers, achievements in the content section. If the logged in user's profile
 * is displayed, links to edit user settings and upload a new avatar are displayed.
 *
 * Requires the activity to have {@link AppBarHandler}, {@link TimelineSettingsHandler}, {@link UserHandler},
 * {@link NavigationHandler}, {@link PLYAndroidHolder} - {@link MetricsUtil} to be set up.
 */
public class ProfileFragment extends NamedFragment {

    public static final String NAME = "Profile";

    public static final int TAB_INDEX_DEFAULT = -1;
    public static final int TAB_INDEX_TIMELINE = 0;
    public static final int TAB_INDEX_FRIENDS = 1;
    public static final int TAB_INDEX_FOLLOWERS = 2;

    private static final String KEY_USER = "user";
    private static final String STATE_USER = "user";
    private static final String KEY_SELECTED_TAB = "selectedTab";

    private static int avatarSize;

    private AppBarHandler appBarHandler;
    private TimelineSettingsHandler timelineSettingsHandler;
    private NavigationHandler navigationHandler;
    private UserHandler userHandler;

    private PLYAndroid client;

    private User user;
    private int initialTabIndex = TAB_INDEX_DEFAULT;

    private ImageView avatar;
    private ImageView backdrop;
    private TextView userNameText;
    private TextView levelText;
    private View avatarScrim;
    private ImageView takePhotoImage;
    private FollowView followView;
    // target would be garbage-collected without reference
    @SuppressWarnings("FieldCanBeLocal")
    private Target avatarTarget;

    private NamedFragmentPagerAdapter pagerAdapter;

    /**
     * Constructs a new instance with the specified parameters. The parameters passed this way survive
     * recreation of the fragment due to orientation changes etc.
     *
     * @param user
     *         the user to present
     * @param selectedTab
     *         the index of the selected tab
     * @return the fragment with the given parameters
     */
    public static ProfileFragment newInstance(@SuppressWarnings("TypeMayBeWeakened") User user, int
            selectedTab) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_USER, user);
        args.putInt(KEY_SELECTED_TAB, selectedTab);
        profileFragment.setArguments(args);
        return profileFragment;
    }

    /**
     * @param user
     *         the user to present
     * @return this fragment's name and initialization parameters
     */
    public static String makeInstanceName(User user) {
        String userParam = user == null ? "" : user.getId();
        return NAME + "(" + userParam + ")";
    }

    @Override
    public String getInstanceName() {
        return makeInstanceName(user);
    }

    @Override
    public FragmentGrouping getGrouping() {
        if (user == null || userHandler == null || userHandler.getUser() == null || !userHandler.getUser()
                .getId().equals(user.getId())) {
            return FragmentGrouping.NONE;
        }
        return (initialTabIndex == TAB_INDEX_FRIENDS || initialTabIndex == TAB_INDEX_FOLLOWERS) ?
                FragmentGrouping.OWN_FRIENDS : FragmentGrouping.OWN_TIMELINE;
    }

    // FRAGMENT LIFECYCLE - START //

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        try {
            appBarHandler = ((HasAppBarHandler) activity).getAppBarHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasAppBarHandler");
        }
        try {
            timelineSettingsHandler = ((HasTimelineSettingsHandler) activity).getTimelineSettingsHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasTimelineSettingsHandler");
        }
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
        initialTabIndex = args.getInt(KEY_SELECTED_TAB, TAB_INDEX_DEFAULT);
        if (savedInstanceState != null) {
            // get user from stored fragment state
            user = (User) savedInstanceState.getSerializable(STATE_USER);
        }
        if (user == null) {
            // get user from initial arguments
            user = (User) args.getSerializable(KEY_USER);
        }
        assert user != null;
        // get a fresh version of the User object
        UserService.getUserByNicknameOrID(client, user.getId(), new PLYCompletion<User>() {
            @Override
            public void onSuccess(User result) {
            }

            @Override
            public void onPostSuccess(User result) {
                updateUser(result, false);
            }

            @Override
            public void onError(PLYAndroid.QueryError error) {
                Log.w(ProfileFragment.class.getSimpleName(), error.getMessage());
                if (!error.isHttpStatusError()) {
                    SnackbarUtil.make(getActivity(), getView(), R.string.connection_failed_internet,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
        // create tabs and subfragments
        pagerAdapter = new NamedFragmentPagerAdapter(getChildFragmentManager());
        UserTimelineFragment userTimeline = null;
        FollowFragment friends = null;
        FollowFragment followers = null;
        if (savedInstanceState != null) {
            // on orientation change fragments are managed by the fragment handler and getItem of the pager
            // adapter is not even called anymore (to get cached fragments see workarounds in adapter class)
            userTimeline = (UserTimelineFragment) NamedFragmentPagerAdapter.findFragmentByPosition
                    (pagerAdapter, getChildFragmentManager(), R.id.pager, TAB_INDEX_TIMELINE);
            friends = (FollowFragment) NamedFragmentPagerAdapter.findFragmentByPosition(pagerAdapter,
                    getChildFragmentManager(), R.id.pager, TAB_INDEX_FRIENDS);
            followers = (FollowFragment) NamedFragmentPagerAdapter.findFragmentByPosition(pagerAdapter,
                    getChildFragmentManager(), R.id.pager, TAB_INDEX_FOLLOWERS);
        }
        // only create fragments if they could not be recreated from a previous state
        if (userTimeline == null) {
            userTimeline = UserTimelineFragment.newInstance(user.getId());
        }
        if (friends == null) {
            friends = FollowFragment.newInstance(FollowFragment.Type.FRIENDS, user.getId());
        }
        if (followers == null) {
            followers = FollowFragment.newInstance(FollowFragment.Type.FOLLOWERS, user.getId());
        }
        pagerAdapter.addFragment(getString(R.string.profile_timeline), userTimeline);
        pagerAdapter.addFragment(getString(R.string.profile_following), friends);
        pagerAdapter.addFragment(getString(R.string.profile_followed_by), followers);
        // TODO custom tab views to display stats
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // get a fresh User object from the handler if this is one's own profile
        if (userHandler.isCurrentUser(user)) {
            user = userHandler.getUser();
        }
        Context context = getContext();
        // inflate the layout
        View layout = inflater.inflate(R.layout.fragment_profile, container, false);
        // set up the view pager
        ViewPager pager = (ViewPager) layout.findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        if (initialTabIndex != TAB_INDEX_DEFAULT && savedInstanceState == null) {
            // only switch to a page programmatically if the view is not restored
            pager.setCurrentItem(initialTabIndex);
        }
        // let the activity modify its app bar
        String title = user.getNickname();
        avatarSize = MetricsUtil.inPx(96);
        // height is the avatar size + top margin + bottom margin
        int height = avatarSize + MetricsUtil.inPx(48 + 32);
        if (appBarHandler.hasTranslucentStatusBar()) {
            height += SystemBarsUtil.getStatusBarHeight(context);
        }
        if (height > MetricsUtil.getHeightPx() / 2) {
            // decrease top margin to 12 if the toolbar takes too much of the screen
            height -= MetricsUtil.inPx(48 - 12);
        }
        // backdrop
        backdrop = new ImageView(context);
        Integer tintColor = ThemeUtil.getIntegerValue(context, R.attr.colorAccent);
        if (tintColor != null) {
            backdrop.setBackgroundColor(tintColor);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            backdrop.setImageAlpha(170);
        } else {
            //noinspection deprecation
            backdrop.setAlpha(170);
        }
        backdrop.setScaleType(ImageView.ScaleType.CENTER_CROP);
        // custom view with avatar and nick name
        View profileHeader = inflater.inflate(R.layout.vg_profile_header, null);
        avatar = (ImageView) profileHeader.findViewById(R.id.user_image);
        avatarScrim = profileHeader.findViewById(R.id.avatar_scrim);
        takePhotoImage = (ImageView) profileHeader.findViewById(R.id.take_photo_image);
        userNameText = (TextView) profileHeader.findViewById(R.id.user_name);
        levelText = (TextView) profileHeader.findViewById(R.id.level_text);
        followView = (FollowView) profileHeader.findViewById(R.id.follow_view);
        updateUser(user, false);
        appBarHandler.setProfileAppBar(layout, title, profileHeader, 0, height, backdrop);
        // show tabs
        final TabLayout tabLayout = (TabLayout) layout.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);
        // configure timeline settings per tab
        prepareTsb(pager.getCurrentItem());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab currentTab = tabLayout.getTabAt(position);
                assert currentTab != null;
                Log.v("ProfileFragment", "Page " + currentTab.getText() + " selected");
                prepareTsb(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save changes done to the user
        outState.putSerializable(STATE_USER, user);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getClass().getSimpleName(), "Received result code " + resultCode + " from request " +
                requestCode);
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        final String tempPath = PhotoUtil.getPhotoPathCache(user.getId(), activity);
        if (PhotoUtil.onActivityResult(requestCode, resultCode, data, getContext(), tempPath)) {
            // upload avatar
            LoadingIndicator.show();
            ImageService.updateUserAvatar(client, user.getId(), tempPath, new
                    PLYCompletion<UserAvatarImage>() {
                @Override
                public void onSuccess(UserAvatarImage result) {
                    Log.d("UploadAvatarCallback", "New avatar image uploaded");
                    LoadingIndicator.hide();
                    SnackbarUtil.make(getActivity(), getView(), R.string.avatar_updated, Snackbar
                            .LENGTH_LONG).show();
                    if (userHandler.isCurrentUser(user)) {
                        user = userHandler.getUser();
                    }
                    user.setAvatar(result);
                    StorageUtil.deleteFile(new File(tempPath));
                    // clear memory and disk cache
                    CacheUtil.clearPicassoMemoryCache();
                    CacheUtil.clearPicassoDiskCache();
                    // TODO reload toolbar and navigation drawer CurrentUser avatar
                }

                @Override
                public void onPostSuccess(UserAvatarImage result) {
                    // load new avatar
                    loadAvatar(false);
                }

                @Override
                public void onError(PLYAndroid.QueryError error) {
                    Log.d("UploadAvatarCallback", error.getMessage());
                    LoadingIndicator.hide();
                    SnackbarUtil.make(getActivity(), getView(), error.getMessage(), Snackbar.LENGTH_LONG)
                            .show();
                }
            });
        }
    }

    // FRAGMENT LIFECYCLE - END //

    /**
     * Updates the stored User object and the screen's visuals.
     *
     * Must be run on the UI thread.
     *
     * @param user
     *         the user to present
     * @param refreshAvatar
     *         true to reload the avatar even if one is already set, false to only load the avatar if none is
     *         displayed yet
     */
    private void updateUser(User user, boolean refreshAvatar) {
        this.user = user;
        if (avatar == null) {
            // not yet in #onCreateView
            return;
        }
        if (avatar.getDrawable() == null || refreshAvatar) {
            loadAvatar(!refreshAvatar);
        }
        // TODO update toolbar title
        if (user.getPoints() != null) {
            long points = user.getPoints();
            Level level = new Level(points);
            levelText.setText(String.valueOf(level.getLevel()));
        }
        followView.setUser(user, userHandler, client);
        decideEditLinks();
    }

    /**
     * Loads the avatar and backdrop.
     *
     * Must be run on the UI thread.
     *
     * @param useCache
     *         whether to look in the cache first
     */
    private void loadAvatar(boolean useCache) {
        if (user == null || avatar == null || avatarSize == 0) {
            return;
        }
        // set avatar image border dependent on friend status
        int borderDrawable = userHandler.isFriend(user) ? R.drawable.round_rectangle_padded_big : R
                .drawable.round_rectangle_padded_big_gray;
        //noinspection deprecation
        avatar.setBackgroundDrawable(getResources().getDrawable(borderDrawable));
        // load avatar image
        String imageUrl = ImageService.getUserAvatarURL(client, user.getId(), avatarSize);
        RequestCreator rcAvatar = Picasso.with(getContext()).load(imageUrl);
        if (!useCache) {
            rcAvatar.memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE);
        }
        avatarTarget = PicassoTarget.roundedCornersImage(getContext(), avatar);
        rcAvatar.into(avatarTarget);
        // load blurred avatar as backdrop image
        RequestCreator rcBackdrop = Picasso.with(getContext()).load(imageUrl).transform(new PicassoBlur
                (getContext()));
        if (!useCache) {
            rcBackdrop.memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE);
        }
        rcBackdrop.into(backdrop);
    }

    /**
     * Displays a camera icon and an edit pen to let the user update their avatar and profile settings if the
     * displayed user is the one currently logged in.
     *
     * Must be run on the UI thread.
     */
    @SuppressLint("SetTextI18n")
    private void decideEditLinks() {
        if (userHandler.isCurrentUser(user)) {
            if (takePhotoImage.getVisibility() == View.VISIBLE) {
                // already set
                return;
            }
            // show and set take photo link
            avatarScrim.setVisibility(View.VISIBLE);
            takePhotoImage.setVisibility(View.VISIBLE);
            PhotoUtil.registerSelectorPopup(getActivity(), this, avatar);
            // show and set edit link
            // TODO replace pencil with floating action button on backdrop
            userNameText.setText("✎ " + user.getNickname());
            userNameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigationHandler.openProfileEditPage(user);
                }
            });
        } else {
            userNameText.setText(user.getNickname());
        }
    }

    /**
     * Configures the timeline settings button with regards to the currently selected tab.
     *
     * @param position
     *         the current tab position
     */
    private void prepareTsb(int position) {
        if (position == TAB_INDEX_TIMELINE) {
            // configures and shows the timeline settings
            UserTimelineFragment userTimeline = (UserTimelineFragment) NamedFragmentPagerAdapter
                    .findFragmentByPosition(pagerAdapter, getChildFragmentManager(), R.id.pager,
                            TAB_INDEX_TIMELINE);
            if (userTimeline == null) {
                // if we can't find the fragment in the fragment manager, we'll try with the one in the pager
                userTimeline = (UserTimelineFragment) pagerAdapter.getItem(TAB_INDEX_TIMELINE);
            }
            userTimeline.prepareTimelineSettings(timelineSettingsHandler);
        } else {
            // hide the timeline settings button
            timelineSettingsHandler.hideTimelineSettings();
        }
    }
}
