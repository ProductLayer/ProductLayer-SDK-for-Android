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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.productlayer.android.common.R;

/**
 * A simple alert dialog presenting a message with a title and one button.
 */
public class SimpleAlertDialogFragment extends VerboseDialogFragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_BUTTON = "button";

    /**
     * Constructs a new instance with the specified parameters. The parameters passed this way survive
     * recreation of the fragment due to orientation changes etc.
     *
     * @param title
     *         the title to display in the dialog's header
     * @param message
     *         the message to display in the dialog's body
     * @param buttonLabel
     *         the label of the confirmation button
     * @return the fragment with the given parameters
     */
    public static SimpleAlertDialogFragment newInstance(CharSequence title, CharSequence message,
            CharSequence buttonLabel) {
        SimpleAlertDialogFragment simpleAlertDialogFragment = new SimpleAlertDialogFragment();
        Bundle args = new Bundle();
        args.putCharSequence(KEY_TITLE, title);
        args.putCharSequence(KEY_MESSAGE, message);
        args.putCharSequence(KEY_BUTTON, buttonLabel);
        simpleAlertDialogFragment.setArguments(args);
        return simpleAlertDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // get supplied arguments
        Bundle args = getArguments();
        CharSequence title = args.getCharSequence(KEY_TITLE);
        CharSequence message = args.getCharSequence(KEY_MESSAGE);
        CharSequence buttonLabel = args.getCharSequence(KEY_BUTTON);
        // inflate layout and set text
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_simple_alert, null);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(title);
        TextView messageView = (TextView) view.findViewById(R.id.message);
        messageView.setText(message);
        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view).setPositiveButton(buttonLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}
