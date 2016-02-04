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
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.fragment.ProfileFragment;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.util.PicassoTarget;
import com.productlayer.core.beans.Product;
import com.productlayer.core.beans.SimpleUserInfo;
import com.productlayer.core.beans.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * A relative layout containing the name of a product and its author.
 */
public class AuthorView extends RelativeLayout {

    private Context context;

    private View authorImageContainer;
    private ImageView authorImageView;
    private ImageView triangleView;
    private View authorInfoView;
    private TextView authorNameView;
    private TextView timePostedView;
    private Target authorImageTarget;

    private TextView productNameView;

    private SimpleUserInfo author;
    private long timeCreated;
    private Product product;

    /**
     * Creates the view. Is called if inflated from XML.
     *
     * @param context
     *         the application context
     */
    public AuthorView(Context context) {
        this(context, null);
    }

    /**
     * Creates the view. Is called if inflated from XML.
     *
     * @param context
     *         the application context
     * @param attrs
     *         any attributes set via XML
     */
    public AuthorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates the view. Is called if inflated from XML.
     *
     * @param context
     *         the application context
     * @param attrs
     *         any attributes set via XML
     * @param defStyleAttr
     *         any style set via XML
     */
    public AuthorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.compound_author, this, true);
        authorImageContainer = findViewById(R.id.author_image_container);
        authorImageView = (ImageView) findViewById(R.id.author_image);
        triangleView = (ImageView) findViewById(R.id.triangle);
        authorInfoView = findViewById(R.id.author_info_view);
        authorNameView = (TextView) findViewById(R.id.author_name);
        timePostedView = (TextView) findViewById(R.id.time_posted_view);
        productNameView = (TextView) findViewById(R.id.product_name);
    }

    /**
     * Sets OnClickListeners to provide navigation to user and product pages.
     *
     * @param navigationHandler
     *         the activity's navigation handler to open new screens
     */
    public void setOnClickListeners(final NavigationHandler navigationHandler) {
        productNameView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product != null) {
                    navigationHandler.openProductPage(product);
                }
            }
        });
        OnClickListener profileOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (author != null) {
                    User user = new User(author.getId());
                    user.setNickname(author.getNickname());
                    navigationHandler.openProfilePage(user, ProfileFragment.TAB_INDEX_DEFAULT);
                }
            }
        };
        authorImageContainer.setOnClickListener(profileOnClickListener);
        authorInfoView.setOnClickListener(profileOnClickListener);
    }

    /**
     * Sets the dimensions of the author image placeholder.
     *
     * @param avatarSizePx
     *         the width/height of the author's avatar
     */
    public void setAvatarSize(int avatarSizePx) {
        ViewGroup.LayoutParams authorImageLayoutParams = authorImageView.getLayoutParams();
        authorImageLayoutParams.width = avatarSizePx;
        authorImageLayoutParams.height = avatarSizePx;
    }

    /**
     * Sets an author and a product title to be displayed.
     *
     * @param author
     *         the author to show
     * @param authorImageUrl
     *         the URL of the author's avatar image to load
     * @param isFriend
     *         true if a user is logged in and the author of this entry is a friend
     * @param product
     *         the product to show the name of
     * @param timeCreated
     *         timestamp of when this entry was posted
     */
    public void setAuthor(SimpleUserInfo author, String authorImageUrl, boolean isFriend, Product product,
            long timeCreated) {
        if (this.product == null || !product.getId().equals(this.product.getId()) || !product.getName()
                .equals(this.product.getName())) {
            productNameView.setText(product.getName());
        }
        this.product = product;
        // highlight author image if a friend
        if (isFriend) {
            //noinspection deprecation
            authorImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable
                    .round_rectangle_padded));
        } else {
            //noinspection deprecation
            authorImageView.setBackgroundDrawable(null);
            authorImageView.setPadding(0, 0, 0, 0);
        }
        if (this.author == null || !author.getId().equals(this.author.getId()) || !author.getNickname()
                .equals(this.author.getNickname())) {
            // display new author image and name
            if (authorImageTarget != null) {
                Picasso.with(context).cancelRequest(authorImageTarget);
            }
            authorImageView.setImageBitmap(null);
            if (authorImageUrl != null) {
                authorImageView.setVisibility(VISIBLE);
                authorImageTarget = PicassoTarget.roundedCornersImage(context, authorImageView);
                Picasso.with(context).load(authorImageUrl).into(authorImageTarget);
            } else {
                authorImageView.setVisibility(GONE);
            }
            authorNameView.setText(author.getNickname());
        }
        this.author = author;
        // set time posted
        if (this.timeCreated == timeCreated) {
            return;
        }
        this.timeCreated = timeCreated;
        Resources res = getResources();
        String agoPrefix = res.getString(R.string.ago_prefix);
        if (!agoPrefix.isEmpty()) {
            agoPrefix += " ";
        }
        String agoSuffix = res.getString(R.string.ago_suffix);
        if (!agoSuffix.isEmpty()) {
            agoSuffix = " " + agoSuffix;
        }
        long now = System.currentTimeMillis();
        long diffMillis = Math.max(0, now - timeCreated);
        long diffSeconds = diffMillis / 1000;
        long diffMinutes = diffSeconds / 60;
        if (diffMinutes == 0) {
            timePostedView.setText(String.format("%s%d %s%s", agoPrefix, diffSeconds, diffSeconds == 1 ?
                    res.getString(R.string.sec_abbr) : res.getString(R.string.sec_pl_abbr), agoSuffix));
        } else {
            long diffHours = diffMinutes / 60;
            if (diffHours == 0) {
                timePostedView.setText(String.format("%s%d %s%s", agoPrefix, diffMinutes, diffMinutes == 1
                        ? res.getString(R.string.min_abbr) : res.getString(R.string.min_pl_abbr), agoSuffix));
            } else {
                long diffDays = diffHours / 24;
                if (diffDays == 0) {
                    timePostedView.setText(String.format("%s%d %s%s", agoPrefix, diffHours, diffHours == 1
                            ? res.getString(R.string.hour_abbr) : res.getString(R.string.hour_pl_abbr),
                            agoSuffix));
                } else {
                    long diffYears = diffDays / 365;
                    if (diffYears == 0) {
                        timePostedView.setText(String.format("%s%d %s%s", agoPrefix, diffDays, diffDays ==
                                1 ? res.getString(R.string.day_abbr) : res.getString(R.string.day_pl_abbr),
                                agoSuffix));
                    } else {
                        timePostedView.setText(String.format("%s%d %s%s", agoPrefix, diffYears, diffYears
                                == 1 ? res.getString(R.string.year_abbr) : res.getString(R.string
                                .year_pl_abbr), agoSuffix));
                    }
                }
            }
        }
    }

    /**
     * Shows the speech bubble triangle.
     */
    public void showBubbleTriangle() {
        triangleView.setVisibility(VISIBLE);
    }

    /**
     * Hides the speech bubble triangle (default).
     */
    public void hideBubbleTriangle() {
        triangleView.setVisibility(GONE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AuthorView that = (AuthorView) o;

        if (timeCreated != that.timeCreated)
            return false;
        if (author != null ? !author.equals(that.author) : that.author != null)
            return false;
        return !(product != null ? !product.equals(that.product) : that.product != null);

    }

    @Override
    public int hashCode() {
        int result = author != null ? author.hashCode() : 0;
        result = 31 * result + (int) (timeCreated ^ (timeCreated >>> 32));
        result = 31 * result + (product != null ? product.hashCode() : 0);
        return result;
    }
}
