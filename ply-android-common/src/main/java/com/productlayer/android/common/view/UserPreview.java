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
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.fragment.ProfileFragment;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.common.model.Level;
import com.productlayer.android.common.util.PicassoTarget;
import com.productlayer.android.common.util.ThemeUtil;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.core.beans.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * A compound view displaying a user including the option to (un)follow them.
 */
public class UserPreview extends CardView {

    private final int cardBackgroundColor;
    private final int friendBackgroundColor;

    private Context context;

    private User user;

    private ImageView userImage;
    private TextView userName;
    private TextView levelText;
    private FollowView followView;

    private Target userImageTarget;

    private boolean showFollowButton;

    /**
     * Creates the cardview setting the dimensions of the user image placeholder.
     *
     * @param context
     *         the application context
     * @param navigationHandler
     *         the activity's navigation handler to open new screens
     * @param avatarSizePx
     *         the width/height of the user's avatar
     * @param dismissableDialog
     *         any host dialog to be dismissed on click
     * @param showFollowButton
     *         whether to show an (un)follow button
     */
    public UserPreview(Context context, final NavigationHandler navigationHandler, int avatarSizePx, final
    DialogFragment dismissableDialog, boolean showFollowButton) {
        this(context, null);
        this.showFollowButton = showFollowButton;
        // set user image dimensions
        ViewGroup.LayoutParams userImageLayoutParams = userImage.getLayoutParams();
        userImageLayoutParams.width = avatarSizePx;
        userImageLayoutParams.height = avatarSizePx;
        // set profile click listener
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    navigationHandler.openProfilePage(user, ProfileFragment.TAB_INDEX_DEFAULT);
                    if (dismissableDialog != null) {
                        dismissableDialog.dismiss();
                    }
                }
            }
        });
    }

    /**
     * Creates the cardview. Is called by {@link #UserPreview(Context, NavigationHandler, int, DialogFragment,
     * boolean)} and if inflated from XML.
     *
     * @param context
     *         the application context
     */
    public UserPreview(Context context) {
        this(context, null);
    }

    /**
     * Creates the cardview. Is called by {@link #UserPreview(Context, NavigationHandler, int, DialogFragment,
     * boolean)} and if inflated from XML.
     *
     * @param context
     *         the application context
     * @param attrs
     *         any attributes set via XML
     */
    public UserPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates the cardview. Is called by {@link #UserPreview(Context, NavigationHandler, int, DialogFragment,
     * boolean)} and if inflated from XML.
     *
     * @param context
     *         the application context
     * @param attrs
     *         any attributes set via XML
     * @param defStyleAttr
     *         any style set via XML
     */
    public UserPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.compound_user_preview, this, true);
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int feedItemMarginPx = Math.round(getResources().getDimension(R.dimen.feed_item_margin));
        marginLayoutParams.setMargins(feedItemMarginPx, feedItemMarginPx, feedItemMarginPx, feedItemMarginPx);
        setLayoutParams(marginLayoutParams);
        setRadius(getResources().getDimension(R.dimen.feed_item_radius));
        Integer cardBackgroundColor = ThemeUtil.getIntegerValue(context, R.attr.cardBackground);
        this.cardBackgroundColor = cardBackgroundColor == null ? Color.WHITE : cardBackgroundColor;
        setCardBackgroundColor(this.cardBackgroundColor);
        Integer friendBackgroundColor = ThemeUtil.getIntegerValue(context, R.attr.friendBackground);
        this.friendBackgroundColor = friendBackgroundColor == null ? Color.WHITE : friendBackgroundColor;
        userImage = (ImageView) findViewById(R.id.user_image);
        userName = (TextView) findViewById(R.id.user_name);
        levelText = (TextView) findViewById(R.id.level_text);
        followView = (FollowView) findViewById(R.id.follow_view);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setPreventCornerOverlap(false);
        }
    }

    /**
     * Sets a new user to be displayed in the cardview.
     *
     * @param user
     *         the user to show
     * @param userImageUrl
     *         the URL of the user's avatar image to load
     * @param userHandler
     *         the handler to retrieve the relation to the currently logged in user
     * @param client
     *         the PLYAndroid SDK client to use for (un)following the user
     */
    public void setUser(User user, String userImageUrl, UserHandler userHandler, PLYAndroid client) {
        if (this.user != null && user.equals(this.user)) {
            return;
        }
        // display new user
        this.user = user;
        if (userImageTarget != null) {
            Picasso.with(context).cancelRequest(userImageTarget);
        }
        userImage.setImageBitmap(null);
        boolean isFriend = userHandler.isFriend(user);
        // highlight author image if a friend
        if (isFriend) {
            //noinspection deprecation
            userImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_rectangle_padded));
        } else {
            //noinspection deprecation
            userImage.setBackgroundDrawable(null);
            userImage.setPadding(0, 0, 0, 0);
        }
        // colorize cardview if this is a friend
        setCardBackgroundColor(isFriend ? friendBackgroundColor : cardBackgroundColor);
        if (userImageUrl != null) {
            userImage.setVisibility(VISIBLE);
            userImageTarget = PicassoTarget.roundedCornersImage(context, userImage, getResources()
                    .getDimension(R.dimen.feed_item_radius));
            Picasso.with(context).load(userImageUrl).into(userImageTarget);
        } else {
            userImage.setVisibility(GONE);
        }
        userName.setText(user.getNickname());
        long points = user.getPoints();
        Level level = new Level(points);
        levelText.setText(String.valueOf(level.getLevel()));
        // set and show (or hide) follow button
        if (showFollowButton) {
            followView.setUser(user, userHandler, client);
        }
        followView.setVisibility(showFollowButton ? View.VISIBLE : View.GONE);
    }

}
