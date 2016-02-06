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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;

/**
 * A dialog asking a user to provide a user name and password to log in.
 *
 * Requires the activity to implement {@link SignInExistingUserDialogListener}.
 */
public class SignInExistingUserDialogFragment extends VerboseDialogFragment {

    private static final String KEY_USERNAME = "userName";

    private SignInExistingUserDialogListener listener;
    private PLYAndroid.Query queryOnSuccess;
    private PLYCompletion queryOnSuccessCompletion;
    private PLYAndroid.QueryError queryError;

    private EditText userNameField;
    private EditText passwordField;

    /**
     * Constructs a new instance with the specified parameters. The parameters passed this way survive
     * recreation of the fragment due to orientation changes etc.
     *
     * @param userName
     *         the user name to preset (null for none)
     * @param queryOnSuccess
     *         a query to run on successful login (this is used for example if a query fails due to
     *         insufficient credentials and needs to be repeated once logged in) or null
     * @param queryOnSuccessCompletion
     *         a completion callback to go hand in hand with {@code queryOnSuccess} or null
     * @param queryError
     *         the error resulting from {@code queryOnSuccess} if it was run before
     * @return the fragment with the given parameters (if any)
     */
    public static SignInExistingUserDialogFragment newInstance(CharSequence userName, PLYAndroid.Query
            queryOnSuccess, PLYCompletion queryOnSuccessCompletion, PLYAndroid.QueryError queryError) {
        SignInExistingUserDialogFragment signInExistingUserDialogFragment = new
                SignInExistingUserDialogFragment();
        if (userName != null) {
            Bundle args = new Bundle();
            args.putCharSequence(KEY_USERNAME, userName);
            signInExistingUserDialogFragment.setArguments(args);
        }
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
            listener = (SignInExistingUserDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement SignInExistingUserDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View signInLayout = inflater.inflate(R.layout.dialog_sign_in_existing_user, null);
        userNameField = (EditText) signInLayout.findViewById(R.id.user_name);
        passwordField = (EditText) signInLayout.findViewById(R.id.password);
        // set up the dialog supplying the layout and button actions
        builder.setView(signInLayout).setPositiveButton(R.string.sign_in_button, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                listener.onDialogSignInExistingUserClick(userNameField.getText().toString(), passwordField
                        .getText().toString(), queryOnSuccess, queryOnSuccessCompletion, queryError);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getDialog().getWindow().getDecorView().getWindowToken(), 0);
            }
        }).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getDialog().getWindow().getDecorView().getWindowToken(), 0);
            }
        });
        final AlertDialog dialog = builder.create();
        // set up done action of password field
        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return actionId == EditorInfo.IME_ACTION_DONE && dialog.getButton(DialogInterface
                        .BUTTON_POSITIVE).performClick();
            }
        });
        if (savedInstanceState == null) {
            // get any supplied parameters
            Bundle args = getArguments();
            if (args != null) {
                CharSequence userName = getArguments().getCharSequence(KEY_USERNAME);
                if (userName != null) {
                    userNameField.setText(userName);
                    passwordField.requestFocus();
                }
            }
        }
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // keyboard remains hidden for now to not overlap any toasts popping up
        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        listener.onDialogCancelSignInExistingUserClick(queryOnSuccessCompletion, queryError);
    }

    /**
     * The interface activities using this dialog need to implement. Handles the sign in action and cancelling
     * the dialog.
     */
    public interface SignInExistingUserDialogListener {

        /**
         * Initiates actions after the sign in button was clicked.
         *
         * @param userName
         *         the user name the user requests to sign in with
         * @param password
         *         the password the user requests to sign in with
         * @param queryOnSuccess
         *         a query to run on successful login (this is used for example if a query fails due to
         *         insufficient credentials and needs to be repeated once logged in) or null
         * @param queryOnSuccessCompletion
         *         a completion callback to go hand in hand with {@code queryOnSuccess} or null
         * @param queryError
         *         the error resulting from {@code queryOnSuccess} if it was run before
         */
        void onDialogSignInExistingUserClick(String userName, String password, PLYAndroid.Query
                queryOnSuccess, PLYCompletion queryOnSuccessCompletion, PLYAndroid.QueryError queryError);

        /**
         * Called if the sign in dialog is dismissed using the Cancel button.
         *
         * @param queryOnSuccessCompletion
         *         a completion callback that was going to be called if the user would have signed in
         * @param queryError
         *         the error resulting from a failed query that was going to be called if the user would have
         *         signed in
         */
        void onDialogCancelSignInExistingUserClick(PLYCompletion queryOnSuccessCompletion, PLYAndroid
                .QueryError queryError);

    }

}
