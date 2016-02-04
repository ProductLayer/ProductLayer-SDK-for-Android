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
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.global.LoadingIndicator;
import com.productlayer.android.common.handler.AppBarHandler;
import com.productlayer.android.common.handler.DataChangeListener;
import com.productlayer.android.common.handler.FacebookHandler;
import com.productlayer.android.common.handler.HasAppBarHandler;
import com.productlayer.android.common.handler.HasFacebookHandler;
import com.productlayer.android.common.handler.HasLoginHandler;
import com.productlayer.android.common.handler.HasPLYAndroidHolder;
import com.productlayer.android.common.handler.HasTwitterHandler;
import com.productlayer.android.common.handler.HasUserHandler;
import com.productlayer.android.common.handler.LoginHandler;
import com.productlayer.android.common.handler.PLYAndroidHolder;
import com.productlayer.android.common.handler.TwitterHandler;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.common.util.LocaleUtil;
import com.productlayer.android.common.util.SnackbarUtil;
import com.productlayer.android.common.util.SystemBarsUtil;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.OpineService;
import com.productlayer.core.beans.ObjectReference;
import com.productlayer.core.beans.Opine;
import com.productlayer.core.beans.Product;
import com.productlayer.core.utils.StringUtils;

/**
 * The screen to post an opinion about a product.
 *
 * Requires the activity to implement {@link HasAppBarHandler}, {@link HasUserHandler}, {@link
 * HasLoginHandler}, {@link HasFacebookHandler}, {@link HasTwitterHandler}, {@link HasPLYAndroidHolder}.
 */
public class OpinionFragment extends NamedFragment {

    public static final String NAME = "Opinion";

    private static final String KEY_PRODUCT = "product";
    private static final String KEY_PARENT_OPINION = "parentOpinion";

    private AppBarHandler appBarHandler;
    private UserHandler userHandler;
    private LoginHandler loginHandler;
    private FacebookHandler facebookHandler;
    private TwitterHandler twitterHandler;

    private PLYAndroid client;

    private Product product;
    private Opine parentOpinion;

    private int opinionCharMax;

    private EditText opinionText;
    private TextView charsText;
    private ImageView facebookButton;
    private ImageView twitterButton;

    /**
     * Constructs a new instance with the specified parameters. The parameters passed this way survive
     * recreation of the fragment due to orientation changes etc.
     *
     * @param product
     *         the product to post an opinion to
     * @param parentOpinion
     *         the opinion to reply to (if any)
     * @return the fragment with the given parameters
     */
    @SuppressWarnings("TypeMayBeWeakened")
    public static OpinionFragment newInstance(Product product, Opine parentOpinion) {
        OpinionFragment opinionFragment = new OpinionFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_PRODUCT, product);
        args.putSerializable(KEY_PARENT_OPINION, parentOpinion);
        opinionFragment.setArguments(args);
        return opinionFragment;
    }

    /**
     * @param product
     *         the product to post an opinion to
     * @param parentOpinion
     *         the opinion to reply to (if any)
     * @return this fragment's name and initialization parameters
     */
    public static String makeInstanceName(Product product, Opine parentOpinion) {
        String productParam = product == null ? "" : product.getId();
        String parentOpinionParam = parentOpinion == null ? "" : parentOpinion.getId();
        return NAME + "(" + productParam + "," + parentOpinionParam + ")";
    }

    @Override
    public String getInstanceName() {
        return makeInstanceName(product, parentOpinion);
    }

    @Override
    public FragmentGrouping getGrouping() {
        return FragmentGrouping.NONE;
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
            userHandler = ((HasUserHandler) activity).getUserHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasUserHandler");
        }
        try {
            loginHandler = ((HasLoginHandler) activity).getLoginHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasLoginHandler");
        }
        try {
            facebookHandler = ((HasFacebookHandler) activity).getFacebookHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasFacebookHandler");
        }
        try {
            twitterHandler = ((HasTwitterHandler) activity).getTwitterHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasTwitterHandler");
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
        opinionCharMax = context.getResources().getInteger(R.integer.opinion_char_max);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get product to write opinion for
        Bundle args = getArguments();
        product = (Product) args.getSerializable(KEY_PRODUCT);
        parentOpinion = (Opine) args.getSerializable(KEY_PARENT_OPINION);
        // this fragment adds actions to the app bar
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // inflate the layout
        View layout = inflater.inflate(R.layout.fragment_opinion, container, false);
        opinionText = (EditText) layout.findViewById(R.id.opinion_text);
        charsText = (TextView) layout.findViewById(R.id.chars_text);
        facebookButton = (ImageView) layout.findViewById(R.id.facebook_image);
        twitterButton = (ImageView) layout.findViewById(R.id.twitter_image);
        // set the behavior of the facebook button
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                if (facebookHandler.isShareOnFacebook(activity)) {
                    // deactivate sharing on facebook
                    facebookHandler.setShareOnFacebook(activity, false);
                    SnackbarUtil.make(activity, getView(), R.string.not_sharing_post_on_facebook, Snackbar
                            .LENGTH_LONG).show();
                    updateFacebookButton();
                } else {
                    // activate sharing on facebook
                    Runnable success = new Runnable() {
                        @Override
                        public void run() {
                            Activity activity = getActivity();
                            if (activity == null) {
                                return;
                            }
                            facebookHandler.setShareOnFacebook(activity, true);
                            SnackbarUtil.make(activity, getView(), R.string.sharing_post_on_facebook,
                                    Snackbar.LENGTH_LONG).show();
                            updateFacebookButton();
                        }
                    };
                    Runnable failure = new Runnable() {
                        @Override
                        public void run() {
                            Activity activity = getActivity();
                            if (activity == null) {
                                return;
                            }
                            facebookHandler.setShareOnFacebook(activity, false);
                            SnackbarUtil.make(activity, getView(), R.string.facebook_connect_failed,
                                    Snackbar.LENGTH_LONG).show();
                            updateFacebookButton();
                        }
                    };
                    if (userHandler.isSocialNetworkConnected(getString(R.string.provider_key_facebook))) {
                        // make sure facebook publish permission is granted
                        // facebookHandler.requestFacebookPublishPermission(success, failure);
                        // the fb token appears to get lost at times, connect and request pub. perm. again
                        facebookHandler.connectFacebook(activity, loginHandler, success, failure);
                    } else {
                        // attempt to connect to facebook
                        facebookHandler.connectFacebook(activity, loginHandler, success, failure);
                    }
                }
            }
        });
        // set the behavior of the twitter button
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                if (twitterHandler.isShareOnTwitter(activity)) {
                    // deactivate sharing on twitter
                    twitterHandler.setShareOnTwitter(activity, false);
                    SnackbarUtil.make(activity, getView(), R.string.not_sharing_post_on_twitter, Snackbar
                            .LENGTH_LONG).show();
                    updateTwitterButton();
                } else {
                    // activate sharing on twitter
                    Runnable success = new Runnable() {
                        @Override
                        public void run() {
                            Activity activity = getActivity();
                            if (activity == null) {
                                return;
                            }
                            twitterHandler.setShareOnTwitter(activity, true);
                            SnackbarUtil.make(activity, getView(), R.string.sharing_post_on_twitter,
                                    Snackbar.LENGTH_LONG).show();
                            updateTwitterButton();
                        }
                    };
                    Runnable failure = new Runnable() {
                        @Override
                        public void run() {
                            Activity activity = getActivity();
                            if (activity == null) {
                                return;
                            }
                            twitterHandler.setShareOnTwitter(activity, false);
                            SnackbarUtil.make(activity, getView(), R.string.twitter_connect_failed,
                                    Snackbar.LENGTH_LONG).show();
                            updateTwitterButton();
                        }
                    };
                    if (userHandler.isSocialNetworkConnected(getString(R.string.provider_key_twitter))) {
                        success.run();
                    } else {
                        // attempt to connect to twitter
                        twitterHandler.connectTwitter(activity, loginHandler, success, failure);
                    }
                }
            }
        });
        // update the characters used textview
        opinionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                charsText.setText(String.format("%d", opinionCharMax - s.toString().length()));
            }
        });
        // prevent input elements being covered by the navigation bar
        Context context = getContext();
        if (SystemBarsUtil.hasTranslucentNavigationBar(context)) {
            layout.setPadding(layout.getPaddingLeft(), layout.getPaddingTop(), layout.getPaddingRight(),
                    layout.getPaddingBottom() + SystemBarsUtil.getNavigationBarHeight(context));
            ((ViewGroup) layout).setClipToPadding(false);
            // TODO move up if safe for all screens
        }
        appBarHandler.setOpinionAppBar(layout, product.getName());
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        // if connected check for facebook token expiry to reconnect if necessary
        if (userHandler.isSocialNetworkConnected(getString(R.string.provider_key_facebook))) {
            Activity activity = getActivity();
            if (activity != null) {
                facebookHandler.renewFacebookToken(activity, loginHandler);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // set appearance of social buttons (done here to react to new social connections)
        // TODO may require a user update callback depending on timing
        updateFacebookButton();
        updateTwitterButton();
        // focus on opinion edittext and show keyboard
        opinionText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.showSoftInput(opinionText, 0);
    }

    // FRAGMENT LIFECYCLE - END //

    // MENU - START //

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actions_post, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_post) {
            // post opinion
            String opinionStr = opinionText.getText().toString();
            if (StringUtils.hasText(opinionStr)) {
                Opine opine = new Opine();
                opine.setGtin(product.getGtin());
                opine.setText(opinionStr);
                opine.setLanguage(LocaleUtil.getKeyboardLanguage(getActivity()));
                opine.setShareViaFacebook(facebookHandler.isShareOnFacebook(getContext()));
                opine.setShareViaTwitter(twitterHandler.isShareOnTwitter(getContext()));
                if (parentOpinion != null) {
                    ObjectReference parentReference = new ObjectReference();
                    parentReference.set_class(parentOpinion.getBeautifiedClass());
                    parentReference.setId(parentOpinion.getId());
                    opine.setParent(parentReference);
                }
                LoadingIndicator.show();
                // TODO enable/disable post button
                OpineService.createOpine(client, opine, new PLYCompletion<Opine>() {
                    @Override
                    public void onSuccess(Opine result) {
                        LoadingIndicator.hide();
                        SnackbarUtil.make(getActivity(), getView(), R.string.opinion_posted, Snackbar
                                .LENGTH_LONG).show();
                        DataChangeListener.opinionCreate(result);
                    }

                    @Override
                    public void onPostSuccess(Opine result) {
                        FragmentActivity activity = getActivity();
                        if (activity != null) {
                            activity.onBackPressed();
                        }
                    }

                    @Override
                    public void onError(PLYAndroid.QueryError error) {
                        Log.d("CreateOpineCallback", error.getMessage());
                        LoadingIndicator.hide();
                        SnackbarUtil.make(getActivity(), getView(), error.getMessage(), Snackbar
                                .LENGTH_LONG).show();
                    }
                });
            } else {
                SnackbarUtil.make(getActivity(), getView(), R.string.error_post_empty, Snackbar
                        .LENGTH_LONG).show();
            }
            return true;
        }
        return false;
    }

    // MENU - END //

    /**
     * Updates the visuals of the Facebook button depending on the social connection and default sharing.
     */
    private void updateFacebookButton() {
        Context context = getContext();
        boolean buttonActive = userHandler.isSocialNetworkConnected(getString(R.string
                .provider_key_facebook)) && facebookHandler.isShareOnFacebook(context) &&
                facebookHandler.hasFacebookPublishPermission();
        facebookButton.setImageResource(buttonActive ? R.drawable.facebook_blue_24dp : R.drawable
                .facebook_gray_24dp);
        facebookHandler.setShareOnFacebook(context, buttonActive);
    }

    /**
     * Updates the visuals of the Twitter button depending on the social connection and default sharing.
     */
    private void updateTwitterButton() {
        Context context = getContext();
        boolean buttonActive = userHandler.isSocialNetworkConnected(getString(R.string
                .provider_key_twitter)) && twitterHandler.isShareOnTwitter(context);
        twitterButton.setImageResource(buttonActive ? R.drawable.twitter_blue_24dp : R.drawable
                .twitter_gray_24dp);
        twitterHandler.setShareOnTwitter(context, buttonActive);
    }

}
