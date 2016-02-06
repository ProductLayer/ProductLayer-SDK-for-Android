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
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.handler.DataChangeListener;
import com.productlayer.android.common.util.ColorUtil;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.ImageService;
import com.productlayer.android.sdk.services.OpineService;
import com.productlayer.android.sdk.services.ProductService;
import com.productlayer.core.beans.Opine;
import com.productlayer.core.beans.Product;
import com.productlayer.core.beans.ProductImage;
import com.productlayer.core.beans.SimpleUserInfo;
import com.productlayer.core.beans.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Contains smiley and frowny icons and the amount of likes and dislikes respectively. Includes click
 * listeners to like or dislike a product/opinion/image. Shows any friends that have liked or disliked the
 * object.
 *
 * Set {@link #setClient} for the click listeners to work.
 */
public class LikeView extends RelativeLayout {

    private static final int DEFAULT_COLOR_ID = R.color.white;
    private static final int DEFAULT_COLOR_ACTIVE_ID = R.color.ply_accent;

    private View likeContainer;
    private View dislikeContainer;

    private ImageView likeImage;
    private ImageView dislikeImage;

    private TextView likeAmountText;
    private TextView dislikeAmountText;

    private TextView likeFriendsText;
    private TextView dislikeFriendsText;

    private Type type;
    private String objectId;

    private Status status = Status.UNDECIDED;
    private int likes;
    private int dislikes;

    private int color;
    private int colorActive;

    private PLYAndroid client;

    public LikeView(Context context, Type type, String objectId) {
        super(context);
        this.type = type;
        this.objectId = objectId;
        //noinspection deprecation
        color = context.getResources().getColor(DEFAULT_COLOR_ID);
        //noinspection deprecation
        colorActive = context.getResources().getColor(DEFAULT_COLOR_ACTIVE_ID);
        setup(context);
    }

    public LikeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // use custom attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LikeView);
        int typeInt = a.getInteger(R.styleable.LikeView_likeType, -1);
        objectId = a.getString(R.styleable.LikeView_objectId);
        int iconSize = a.getInteger(R.styleable.LikeView_iconSize, 0);
        //noinspection deprecation
        color = a.getColor(R.styleable.LikeView_iconColor, context.getResources().getColor(DEFAULT_COLOR_ID));
        //noinspection deprecation
        colorActive = a.getColor(R.styleable.LikeView_iconColorActive, context.getResources().getColor
                (DEFAULT_COLOR_ACTIVE_ID));
        a.recycle();
        type = Type.fromValue(typeInt);
        if (type == null) {
            throw new RuntimeException("Missing or invalid likeType attribute in LikeView");
        }
        setup(context);
        if (iconSize == 1) {
            setSizeMini();
        }
    }

    /**
     * Builds a string of user nicknames connected by {@code glue}.
     *
     * @param users
     *         the users to connect in a string
     * @param glue
     *         the glue to connect the users with
     * @return a new string containing all users connected by {@code glue}
     */
    private static String implodeUsers(List<SimpleUserInfo> users, String glue) {
        if (users == null || users.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(users.get(0).getNickname());
        int cntUsers = users.size();
        if (cntUsers > 1) {
            for (int i = 1; i < cntUsers; i++) {
                sb.append(glue).append(users.get(i).getNickname());
            }
        }
        return sb.toString();
    }

    /**
     * Sets up the layout and click listeners.
     *
     * @param context
     *         the application context
     */
    private void setup(Context context) {
        inflateLayout(context);
        setupListeners();
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
        inflater.inflate(R.layout.compound_like, this, true);
        // get fields
        likeContainer = findViewById(R.id.like_container);
        dislikeContainer = findViewById(R.id.dislike_container);
        likeImage = (ImageView) findViewById(R.id.like_image);
        likeAmountText = (TextView) findViewById(R.id.like_amount_text);
        dislikeImage = (ImageView) findViewById(R.id.dislike_image);
        dislikeAmountText = (TextView) findViewById(R.id.dislike_amount_text);
        likeFriendsText = (TextView) findViewById(R.id.like_friends_text);
        dislikeFriendsText = (TextView) findViewById(R.id.dislike_friends_text);
    }

    /**
     * Sets up the click listeners on the images to enable user voting.
     */
    private void setupListeners() {
        // like button
        likeContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == Status.LIKED) {
                    return;
                }
                if (client == null) {
                    return;
                }
                final int prevLikes = likes;
                final int prevDislikes = dislikes;
                final Status prevStatus = status;
                if (status == Status.DISLIKED) {
                    setLikes(likes + 1, dislikes - 1);
                } else {
                    setLikes(likes + 1, dislikes);
                }
                setStatus(Status.LIKED);
                switch (type) {
                    case PRODUCT:
                        ProductService.upVoteProduct(client, objectId, new PLYCompletion<Product>() {
                            @Override
                            public void onSuccess(Product result) {
                                DataChangeListener.productUpdate(result);
                            }

                            @Override
                            public void onError(PLYAndroid.QueryError error) {
                            }

                            @Override
                            public void onPostError(PLYAndroid.QueryError error) {
                                setLikes(prevLikes, prevDislikes);
                                setStatus(prevStatus);
                            }
                        });
                        break;
                    case OPINE:
                        OpineService.upVoteOpine(client, objectId, new PLYCompletion<Opine>() {
                            @Override
                            public void onSuccess(Opine result) {
                                DataChangeListener.opinionUpdate(result);
                            }

                            @Override
                            public void onError(PLYAndroid.QueryError error) {
                            }

                            @Override
                            public void onPostError(PLYAndroid.QueryError error) {
                                setLikes(prevLikes, prevDislikes);
                                setStatus(prevStatus);
                            }
                        });
                        break;
                    case IMAGE:
                        ImageService.upVoteProductImage(client, objectId, new PLYCompletion<ProductImage>() {
                            @Override
                            public void onSuccess(ProductImage result) {
                                DataChangeListener.imageUpdate(result);
                            }

                            @Override
                            public void onError(PLYAndroid.QueryError error) {
                            }

                            @Override
                            public void onPostError(PLYAndroid.QueryError error) {
                                setLikes(prevLikes, prevDislikes);
                                setStatus(prevStatus);
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });
        // dislike button
        dislikeContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == Status.DISLIKED) {
                    return;
                }
                if (client == null) {
                    return;
                }
                final int prevLikes = likes;
                final int prevDislikes = dislikes;
                final Status prevStatus = status;
                if (status == Status.LIKED) {
                    setLikes(likes - 1, dislikes + 1);
                } else {
                    setLikes(likes, dislikes + 1);
                }
                setStatus(Status.DISLIKED);
                switch (type) {
                    case PRODUCT:
                        ProductService.downVoteProduct(client, objectId, new PLYCompletion<Product>() {
                            @Override
                            public void onSuccess(Product result) {
                                DataChangeListener.productUpdate(result);
                            }

                            @Override
                            public void onError(PLYAndroid.QueryError error) {
                            }

                            @Override
                            public void onPostError(PLYAndroid.QueryError error) {
                                setLikes(prevLikes, prevDislikes);
                                setStatus(prevStatus);
                            }
                        });
                        break;
                    case OPINE:
                        OpineService.downVoteOpine(client, objectId, new PLYCompletion<Opine>() {
                            @Override
                            public void onSuccess(Opine result) {
                                DataChangeListener.opinionUpdate(result);
                            }

                            @Override
                            public void onError(PLYAndroid.QueryError error) {
                            }

                            @Override
                            public void onPostError(PLYAndroid.QueryError error) {
                                setLikes(prevLikes, prevDislikes);
                                setStatus(prevStatus);
                            }
                        });
                        break;
                    case IMAGE:
                        ImageService.downVoteProductImage(client, objectId, new PLYCompletion<ProductImage>
                                () {
                            @Override
                            public void onSuccess(ProductImage result) {
                                DataChangeListener.imageUpdate(result);
                            }

                            @Override
                            public void onError(PLYAndroid.QueryError error) {
                            }

                            @Override
                            public void onPostError(PLYAndroid.QueryError error) {
                                setLikes(prevLikes, prevDislikes);
                                setStatus(prevStatus);
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * Sets the ID of the type to vote on.
     *
     * @param objectId
     *         the ID
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * Sets the amount of likes and dislikes present for the object.
     *
     * @param likes
     *         the amount of likes
     * @param dislikes
     *         the amount of dislikes
     */
    public void setLikes(int likes, int dislikes) {
        this.likes = likes;
        this.dislikes = dislikes;
        likeAmountText.setText(String.valueOf(likes));
        dislikeAmountText.setText(String.valueOf(dislikes));
    }

    /**
     * Sets the current vote status and updated the visuals.
     *
     * @param status
     *         whether the user has liked or disliked or is still undecided
     */
    public void setStatus(Status status) {
        this.status = status;
        int likeColor = status == Status.LIKED ? colorActive : color;
        int dislikeColor = status == Status.DISLIKED ? colorActive : color;
        Drawable likeDrawable = likeImage.getDrawable();
        // invalidate is required for Android 4
        likeImage.invalidateDrawable(likeDrawable);
        ColorUtil.mutateAndTintDrawable(likeDrawable, likeColor);
        likeAmountText.setTextColor(likeColor);
        Drawable dislikeDrawable = dislikeImage.getDrawable();
        dislikeImage.invalidateDrawable(dislikeDrawable);
        ColorUtil.mutateAndTintDrawable(dislikeDrawable, dislikeColor);
        dislikeAmountText.setTextColor(dislikeColor);
    }

    /**
     * Sets the amount of likes and dislikes as well as the voting status of the specified user.
     *
     * @param upVoters
     *         the users liking the object
     * @param downVoters
     *         the users disliking the object
     * @param userId
     *         the ID of the user to check for in {@code upVoters} and {@code downVoters} to set the voting
     *         status
     * @param friends
     *         the friends to check for in {@code upVoters} and {@code downVoters} to display as having voted
     *         that way
     * @param widthPx
     *         the total available width in px to determine if and where to break up text about friends liking
     *         or disliking the object
     */
    public void setFromVoters(Collection<SimpleUserInfo> upVoters, Collection<SimpleUserInfo> downVoters,
            String userId, Iterable<User> friends, int widthPx) {
        // set amount of likes/dislikes
        int likes = upVoters == null ? 0 : upVoters.size();
        int dislikes = downVoters == null ? 0 : downVoters.size();
        setLikes(likes, dislikes);
        // set voting status
        if (userId == null) {
            setStatus(Status.UNDECIDED);
            likeFriendsText.setVisibility(GONE);
            dislikeFriendsText.setVisibility(GONE);
            return;
        }
        Status status = Status.UNDECIDED;
        if (upVoters != null) {
            for (SimpleUserInfo u : upVoters) {
                if (userId.equals(u.getId())) {
                    status = Status.LIKED;
                    break;
                }
            }
        }
        if (status == Status.UNDECIDED && downVoters != null) {
            for (SimpleUserInfo u : downVoters) {
                if (userId.equals(u.getId())) {
                    status = Status.DISLIKED;
                    break;
                }
            }
        }
        setStatus(status);
        // collect friends that have voted
        List<SimpleUserInfo> upVotingFriends = new ArrayList<SimpleUserInfo>();
        List<SimpleUserInfo> downVotingFriends = new ArrayList<SimpleUserInfo>();
        if (friends != null) {
            for (User friend : friends) {
                if (upVoters != null) {
                    for (SimpleUserInfo u : upVoters) {
                        if (friend.getId().equals(u.getId())) {
                            upVotingFriends.add(u);
                        }
                    }
                }
                if (downVoters != null) {
                    for (SimpleUserInfo u : downVoters) {
                        if (friend.getId().equals(u.getId())) {
                            downVotingFriends.add(u);
                        }
                    }
                }
            }
        }
        Resources resources = getResources();
        int availWidthPx;
        int dislikeMarginLeft;
        if (!upVotingFriends.isEmpty() && !downVotingFriends.isEmpty()) {
            availWidthPx = widthPx / 2 - likeFriendsText.getPaddingLeft() - likeFriendsText.getPaddingRight();
            dislikeMarginLeft = widthPx / 2;
        } else {
            availWidthPx = widthPx - likeFriendsText.getPaddingLeft() - dislikeFriendsText.getPaddingRight();
            dislikeMarginLeft = 0;
        }
        // display friends that have upvoted the object
        if (upVotingFriends.isEmpty()) {
            likeFriendsText.setVisibility(GONE);
        } else {
            String verbText = " " + resources.getQuantityString(R.plurals.likesThis, upVotingFriends.size()
            ) + ".";
            TextPaint likeFriendsTextPaint = likeFriendsText.getPaint();
            Rect verbBounds = new Rect();
            likeFriendsTextPaint.getTextBounds(verbText, 0, verbText.length(), verbBounds);
            int peopleWidthPx = availWidthPx - verbBounds.width();
            String peopleCommaSep = implodeUsers(upVotingFriends, ", ");
            CharSequence people = TextUtils.commaEllipsize(peopleCommaSep, likeFriendsTextPaint,
                    peopleWidthPx, resources.getQuantityString(R.plurals.morePeople, 1), resources
                            .getQuantityString(R.plurals.morePeople, 2));
            if (people.length() == 0) {
                // for some reason TextUtils.commaEllipsize returns an empty string at times
                people = TextUtils.ellipsize(peopleCommaSep, likeFriendsTextPaint, peopleWidthPx, TextUtils
                        .TruncateAt.END);
            }
            String fullText = people + verbText;
            likeFriendsText.setText(fullText);
            likeFriendsText.setVisibility(VISIBLE);
        }
        // display friends that have downvoted the object
        if (downVotingFriends.isEmpty()) {
            dislikeFriendsText.setVisibility(GONE);
        } else {
            String verbText = " " + resources.getQuantityString(R.plurals.likesThisNot, downVotingFriends
                    .size()) + ".";
            TextPaint dislikeFriendsTextPaint = dislikeFriendsText.getPaint();
            Rect verbBounds = new Rect();
            dislikeFriendsTextPaint.getTextBounds(verbText, 0, verbText.length(), verbBounds);
            int peopleWidthPx = availWidthPx - verbBounds.width();
            String peopleCommaSep = implodeUsers(downVotingFriends, ", ");
            CharSequence people = TextUtils.commaEllipsize(peopleCommaSep, dislikeFriendsTextPaint,
                    peopleWidthPx, resources.getQuantityString(R.plurals.morePeople, 1), resources
                            .getQuantityString(R.plurals.morePeople, 2));
            if (people.length() == 0) {
                // for some reason TextUtils.commaEllipsize returns an empty string at times
                people = TextUtils.ellipsize(peopleCommaSep, dislikeFriendsTextPaint, peopleWidthPx,
                        TextUtils.TruncateAt.END);
            }
            String fullText = people + verbText;
            dislikeFriendsText.setText(fullText);
            RelativeLayout.LayoutParams lpForDislikeFriendsText = (LayoutParams) dislikeFriendsText
                    .getLayoutParams();
            lpForDislikeFriendsText.leftMargin = dislikeMarginLeft;
            dislikeFriendsText.setVisibility(VISIBLE);
        }
    }

    /**
     * Sets the PLYAndroid client to send like or dislike requests.
     *
     * @param client
     *         the PLYAndroid client
     */
    public void setClient(PLYAndroid client) {
        this.client = client;
    }

    /**
     * Reduces the height of the view to mini sized icons for the timeline.
     */
    public void setSizeMini() {
        int sizePx = getResources().getDimensionPixelSize(R.dimen.feed_interaction_icon_mini);
        LayoutParams lpForLikeImage = (LayoutParams) likeImage.getLayoutParams();
        lpForLikeImage.width = sizePx;
        lpForLikeImage.height = sizePx;
        LayoutParams lpForDislikeImage = (LayoutParams) dislikeImage.getLayoutParams();
        lpForDislikeImage.width = sizePx;
        lpForDislikeImage.height = sizePx;
    }

    /**
     * Enum for the type of view the like is attached to.
     */
    public enum Type {
        PRODUCT(0), OPINE(1), IMAGE(2);

        public int value;

        Type(int value) {
            this.value = value;
        }

        static Type fromValue(int value) {
            for (Type t : Type.values()) {
                if (t.value == value) {
                    return t;
                }
            }
            return null;
        }
    }

    /**
     * Enum for the voting status of the current user.
     */
    public enum Status {
        UNDECIDED, LIKED, DISLIKED
    }
}
