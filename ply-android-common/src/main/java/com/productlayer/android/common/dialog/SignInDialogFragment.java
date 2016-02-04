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

package com.productlayer.android.common.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.productlayer.android.common.R;
import com.productlayer.android.common.handler.FacebookHandler;
import com.productlayer.android.common.handler.HasFacebookHandler;
import com.productlayer.android.common.handler.HasLoginHandler;
import com.productlayer.android.common.handler.HasNavigationHandler;
import com.productlayer.android.common.handler.HasPLYAndroidHolder;
import com.productlayer.android.common.handler.HasTwitterHandler;
import com.productlayer.android.common.handler.LoginHandler;
import com.productlayer.android.common.handler.NavigationHandler;
import com.productlayer.android.common.handler.PLYAndroidHolder;
import com.productlayer.android.common.handler.TwitterHandler;
import com.productlayer.android.common.util.MetricsUtil;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;

/**
 * A dialog presenting options to either log in through a social network or an exising user, or to sign up.
 *
 * Requires the activity to implement {@link NavigationHandler}, {@link LoginHandler}, {@link
 * FacebookHandler}, {@link TwitterHandler}, {@link PLYAndroidHolder}.
 */
public class SignInDialogFragment extends VerboseDialogFragment {

    private NavigationHandler navigationHandler;
    private LoginHandler loginHandler;
    private FacebookHandler facebookHandler;
    private TwitterHandler twitterHandler;
    private PLYAndroid client;

    private PLYAndroid.Query queryOnSuccess;
    private PLYCompletion queryOnSuccessCompletion;
    private PLYAndroid.QueryError queryError;

    /**
     * Constructs a new instance with the specified parameters. The parameters passed this way survive
     * recreation of the fragment due to orientation changes etc.
     *
     * @param queryOnSuccess
     *         a query to run on successful login (this is used for example if a query fails due to
     *         insufficient credentials and needs to be repeated once logged in) or null
     * @param queryOnSuccessCompletion
     *         a completion callback to go hand in hand with {@code queryOnSuccess} or null
     * @param queryError
     *         the error resulting from {@code queryOnSuccess} if it was run before
     * @return the fragment with the given parameters (if any)
     */
    public static SignInDialogFragment newInstance(PLYAndroid.Query queryOnSuccess, PLYCompletion
            queryOnSuccessCompletion, PLYAndroid.QueryError queryError) {
        SignInDialogFragment signInExistingUserDialogFragment = new SignInDialogFragment();
        // any query to automatically run on successful login will not survive app restarts
        signInExistingUserDialogFragment.queryOnSuccess = queryOnSuccess;
        signInExistingUserDialogFragment.queryOnSuccessCompletion = queryOnSuccessCompletion;
        signInExistingUserDialogFragment.queryError = queryError;
        return signInExistingUserDialogFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            navigationHandler = ((HasNavigationHandler) activity).getNavigationHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasNavigationHandler");
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
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View signInLayout = inflater.inflate(R.layout.dialog_sign_in, null);
        LinearLayout signInOptionsLayout = (LinearLayout) signInLayout.findViewById(R.id
                .signin_options_layout);
        // set up the dialog supplying the layout
        builder.setView(signInLayout);
        AlertDialog dialog = builder.create();
        // log in using existing user button
        Button loginExistingUserButton = (Button) signInLayout.findViewById(R.id.login_existing_user_button);
        loginExistingUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationHandler.showSignInExistingUserDialog(null, queryOnSuccess,
                        queryOnSuccessCompletion, queryError);
                dismiss();
            }
        });
        // sign up button
        Button signUpButton = (Button) signInLayout.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationHandler.showSignUpDialog(null, null, queryOnSuccess, queryOnSuccessCompletion,
                        queryError);
                dismiss();
            }
        });
        // facebook
        View fbButton = facebookHandler.getFacebookSignInView(getActivity(), loginHandler, client, this,
                queryOnSuccess, queryOnSuccessCompletion, queryError);
        LinearLayout.LayoutParams lpForFbButton = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpForFbButton.topMargin = MetricsUtil.inPx(8);
        signInOptionsLayout.addView(fbButton, 0, lpForFbButton);
        // twitter
        View twButton = twitterHandler.getTwitterSignInView(getActivity(), loginHandler, client, this,
                queryOnSuccess, queryOnSuccessCompletion, queryError);
        LinearLayout.LayoutParams lpForTwButton = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpForTwButton.topMargin = MetricsUtil.inPx(8);
        signInOptionsLayout.addView(twButton, 1, lpForTwButton);
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (queryError != null) {
            // error callback of the query that was going to be run after a successful login
            queryOnSuccessCompletion.onError(queryError);
            queryOnSuccessCompletion.onPostError(queryError);
        }
    }
}
