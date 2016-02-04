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
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.model.Level;
import com.productlayer.android.common.util.BitmapUtil;
import com.productlayer.android.common.util.MetricsUtil;
import com.productlayer.android.common.util.PicassoTarget;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.services.ImageService;
import com.productlayer.core.beans.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * A compound view displaying a short summary about the currently logged in user (avatar, points,
 * notifications).
 */
public class CurrentUser extends RelativeLayout {

    public static final int AVATAR_SIZE_PX_DEFAULT = MetricsUtil.inPx(36);
    private static final int USER_POINTS_UNSET = -9999;

    private Context context;

    private int avatarSizePx;

    private ImageView userImage;
    private TextView userFullName;
    private TextView userName;
    private TextView levelText;
    private TextView levelProgress;
    private Target userImageTarget;

    private User user;

    public CurrentUser(Context context) {
        this(context, null);
    }

    public CurrentUser(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CurrentUser(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        // use custom attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CurrentUser);
        avatarSizePx = a.getDimensionPixelSize(R.styleable.CurrentUser_avatarSize, AVATAR_SIZE_PX_DEFAULT);
        String userImageUrl = a.getString(R.styleable.CurrentUser_userImageUrl);
        String userFullNameValue = a.getString(R.styleable.CurrentUser_userFullName);
        boolean showFullName = a.getBoolean(R.styleable.CurrentUser_showFullName, userFullNameValue != null);
        String userNameValue = a.getString(R.styleable.CurrentUser_userName);
        long userPointsValue = a.getInteger(R.styleable.CurrentUser_userPoints, USER_POINTS_UNSET);
        a.recycle();
        // inflate layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.compound_current_user, this, true);
        // get fields
        userImage = (ImageView) findViewById(R.id.user_image);
        levelText = (TextView) findViewById(R.id.level_text);
        userFullName = (TextView) findViewById(R.id.user_full_name);
        userName = (TextView) findViewById(R.id.user_name);
        levelProgress = (TextView) findViewById(R.id.level_progress);
        userImage.setLayoutParams(new RelativeLayout.LayoutParams(avatarSizePx, avatarSizePx));
        userImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (showFullName) {
            userFullName.setVisibility(View.VISIBLE);
        }
        if (userImageUrl != null) {
            // load avatar from supplied image URL
            userImageTarget = PicassoTarget.roundedCornersImage(context, userImage);
            Picasso.with(context).load(userImageUrl).into(userImageTarget);
        } else {
            // use default user image
            clearUser();
        }
        if (userFullNameValue != null) {
            userFullName.setText(userFullNameValue);
        }
        if (userNameValue != null) {
            userName.setText(userNameValue);
        }
        if (userPointsValue != USER_POINTS_UNSET) {
            setPoints(userPointsValue);
        }
    }

    /**
     * Clears information about any user, displaying the default avatar and no text.
     */
    public void clearUser() {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar_36dp);
        setAvatarBitmap(bitmap);
        userFullName.setText("");
        userName.setText("");
        levelText.setText("");
        levelProgress.setText("");
        user = null;
    }

    /**
     * Updates the view to display the avatar and information about the specified user.
     *
     * @param user
     *         the user to display
     * @param loadImage
     *         whether to load the user avatar
     * @param client
     *         the PLYAndroid client (used to get the URL of the correctly sized avatar)
     */
    public void setUser(User user, boolean loadImage, PLYAndroid client) {
        if (loadImage) {
            String userImageUrl = ImageService.getUserAvatarURL(client, user.getId(), avatarSizePx);
            Log.d(getClass().getSimpleName(), "Loading avatar for current user " + user.getNickname() + " " +
                    "from " + userImageUrl);
            userImageTarget = PicassoTarget.roundedCornersImage(context, userImage);
            Picasso.with(context).load(userImageUrl).into(userImageTarget);
        }
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String fullName = (firstName != null ? firstName : "") + ((firstName != null && lastName != null) ?
                " " : "") + (lastName != null ? lastName : "");
        userFullName.setText(fullName);
        userName.setText(user.getNickname());
        this.user = user;
        setPoints(user.getPoints() == null ? 0 : user.getPoints());
    }

    /**
     * Updates the level and progress views if logged in.
     *
     * @param points
     *         the points value to calculate the level/progress from
     */
    public void setPoints(long points) {
        if (user == null) {
            return;
        }
        Level level = new Level(points);
        int levelInt = level.getLevel();
        levelText.setText(String.valueOf(levelInt));
        int progressInt = level.getProgressInt();
        if (progressInt == 0) {
            levelProgress.setText("");
        } else {
            levelProgress.setText(String.format("%d%% %s", progressInt, getResources().getString(R.string
                    .percentage_progress_to_next_level)));
        }
    }

    /**
     * @return the view containing the level progress text
     */
    public View getProgressView() {
        return levelProgress;
    }

    /**
     * @return the bitmap that is shown for the currently logged in user, or null if none logged in or not yet
     * loaded
     */
    public Bitmap getAvatarBitmap() {
        if (user == null) {
            return null;
        }
        Drawable drawable = userImage.getDrawable();
        if (drawable instanceof RoundedBitmapDrawable) {
            return ((RoundedBitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            return null;
        }
    }

    /**
     * Displays a rounded corner version of the specified bitmap as user avatar.
     *
     * @param bitmap
     *         the bitmap to show
     */
    public void setAvatarBitmap(Bitmap bitmap) {
        userImage.setImageDrawable(BitmapUtil.getRoundedBitmapDrawable(context, bitmap));
    }
}
