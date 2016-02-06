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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.productlayer.android.common.R;
import com.productlayer.core.utils.StringUtils;

/**
 * A dialog enabling users to change their password.
 *
 * Requires the parent (activity or fragment) to implement {@link ChangePasswordDialogListener}.
 */
public class ChangePasswordDialogFragment extends VerboseDialogFragment {

    private ChangePasswordDialogListener listener;

    private EditText currentPasswordField;
    private EditText newPasswordField;
    private EditText confirmNewPasswordField;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Fragment fragment = getParentFragment();
        if (fragment != null) {
            try {
                listener = (ChangePasswordDialogListener) fragment;
            } catch (ClassCastException e) {
                throw new ClassCastException(fragment + " must implement ChangePasswordDialogListener");
            }
        } else {
            try {
                listener = (ChangePasswordDialogListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity + " must implement ChangePasswordDialogListener");
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_change_password, null);
        currentPasswordField = (EditText) layout.findViewById(R.id.current_password);
        newPasswordField = (EditText) layout.findViewById(R.id.new_password);
        confirmNewPasswordField = (EditText) layout.findViewById(R.id.confirm_new_password);
        // set up the dialog supplying the layout and button actions
        builder.setView(layout).setPositiveButton(R.string.change_password_button, null).setNegativeButton
                (R.string.cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getDialog().getWindow().getDecorView().getWindowToken(), 0);
            }
        });

        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // overwrite the onclicklistener for the positive button here to be able to keep the dialog open
        AlertDialog alertDialog = (AlertDialog) getDialog();
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPassword = currentPasswordField.getText().toString();
                String newPassword = newPasswordField.getText().toString();
                String confirmNewPassword = confirmNewPasswordField.getText().toString();
                if (!StringUtils.isEmpty(currentPassword)) {
                    if (!StringUtils.isEmpty(newPassword)) {
                        if (newPassword.equals(confirmNewPassword)) {
                            listener.onDialogChangePasswordClick(currentPassword, newPassword);
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService
                                    (Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getDialog().getWindow().getDecorView()
                                    .getWindowToken(), 0);
                            dismiss();
                        } else {
                            newPasswordField.setError(getString(R.string.new_password_mismatch));
                            confirmNewPasswordField.setError(getString(R.string.new_password_mismatch));
                        }
                    } else {
                        newPasswordField.setError(getString(R.string.new_password_empty));
                    }
                } else {
                    currentPasswordField.setError(getString(R.string.current_password_empty));
                }
            }
        });
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        listener.onDialogCancelChangePasswordClick();
    }

    /**
     * The interface activities using this dialog need to implement. Handles the password change action and
     * cancelling the dialog.
     */
    public interface ChangePasswordDialogListener {

        /**
         * Initiates actions after the change password button was clicked.
         *
         * @param currentPassword
         *         the user's current password
         * @param newPassword
         *         the value the user wants to change the password to
         */
        void onDialogChangePasswordClick(String currentPassword, String newPassword);

        /**
         * Called if the password change dialog is dismissed using the Cancel button.
         */
        void onDialogCancelChangePasswordClick();

    }

}
