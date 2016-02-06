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
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.common.util.ThemeUtil;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.core.beans.Opine;
import com.productlayer.core.beans.Product;
import com.productlayer.core.beans.SimpleUserInfo;
import com.productlayer.core.beans.User;

/**
 * A compound view displaying an opinion.
 */
public class OpinionView extends CardView {

    private final int cardBackgroundColor;
    private final int friendBackgroundColor;

    private AuthorView authorView;
    private View speechBubble;
    private TextView opinionView;
    private LikeView likeView;

    private int widthPx;

    private Opine opinion;

    /**
     * Creates the cardview setting the dimensions of the author image placeholder.
     *
     * @param context
     *         the application context
     * @param navigationHandler
     *         the activity's navigation handler to open new screens
     * @param widthPx
     *         the width of the cardview in px
     * @param avatarSizePx
     *         the width/height of the author's avatar
     */
    public OpinionView(Context context, final NavigationHandler navigationHandler, int widthPx, int
            avatarSizePx) {
        this(context, null);
        this.widthPx = widthPx;
        authorView.setAvatarSize(avatarSizePx);
        authorView.showBubbleTriangle();
        authorView.setOnClickListeners(navigationHandler);
        OnClickListener writeOpinionClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (opinion != null) {
                    Product product = opinion.getProduct();
                    if (product != null) {
                        navigationHandler.openOpinionPage(product, opinion);
                    }
                }
            }
        };
        speechBubble.setOnClickListener(writeOpinionClickListener);
    }

    /**
     * Creates the cardview. Is called by {@link #OpinionView(Context, NavigationHandler, int, int)} and if
     * inflated from XML.
     *
     * @param context
     *         the application context
     */
    public OpinionView(Context context) {
        this(context, null);
    }

    /**
     * Creates the cardview. Is called by {@link #OpinionView(Context, NavigationHandler, int, int)} and if
     * inflated from XML.
     *
     * @param context
     *         the application context
     * @param attrs
     *         any attributes set via XML
     */
    public OpinionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates the cardview. Is called by {@link #OpinionView(Context, NavigationHandler, int, int)} and if
     * inflated from XML.
     *
     * @param context
     *         the application context
     * @param attrs
     *         any attributes set via XML
     * @param defStyleAttr
     *         any style set via XML
     */
    public OpinionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.compound_opinion, this, true);
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
        authorView = (AuthorView) findViewById(R.id.author);
        speechBubble = findViewById(R.id.speech_bubble);
        opinionView = (TextView) findViewById(R.id.opinion);
        likeView = (LikeView) findViewById(R.id.like_view);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setPreventCornerOverlap(false);
        }
    }

    /**
     * Sets a new opinion to be displayed in the cardview.
     *
     * @param opinion
     *         the opine to show
     * @param author
     *         the author to show
     * @param authorImageUrl
     *         the URL of the author's avatar image to load
     * @param userHandler
     *         the handler to retrieve user info from for friend and voting info
     * @param client
     *         the PLYAndroid client to use for voting
     */
    public void setOpinion(Opine opinion, SimpleUserInfo author, String authorImageUrl, UserHandler
            userHandler, PLYAndroid client) {
        if (this.opinion != null && opinion.equals(this.opinion)) {
            return;
        }
        // display new opinion
        this.opinion = opinion;
        opinionView.setText(opinion.getText());
        boolean isFriend = userHandler.isFriend(author);
        // colorize cardview if this entry was created by oneself or a friend
        setCardBackgroundColor(isFriend ? friendBackgroundColor : cardBackgroundColor);
        // display new author
        authorView.setAuthor(author, authorImageUrl, isFriend, opinion.getProduct(), opinion.getCreated());
        // update like view
        likeView.setObjectId(opinion.getId());
        likeView.setClient(client);
        User currentUser = userHandler.getUser();
        String userId = currentUser == null ? null : currentUser.getId();
        likeView.setFromVoters(opinion.getUpVoters(), opinion.getDownVoters(), userId, userHandler
                .getFriends(), widthPx);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        OpinionView that = (OpinionView) o;

        return !(opinion != null ? !opinion.equals(that.opinion) : that.opinion != null);

    }

    @Override
    public int hashCode() {
        return opinion != null ? opinion.hashCode() : 0;
    }
}
