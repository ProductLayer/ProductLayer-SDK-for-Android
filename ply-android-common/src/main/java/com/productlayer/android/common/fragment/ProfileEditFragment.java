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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.dialog.ChangePasswordDialogFragment;
import com.productlayer.android.common.global.LoadingIndicator;
import com.productlayer.android.common.handler.AppBarHandler;
import com.productlayer.android.common.handler.FacebookHandler;
import com.productlayer.android.common.handler.HasAppBarHandler;
import com.productlayer.android.common.handler.HasFacebookHandler;
import com.productlayer.android.common.handler.HasLoginHandler;
import com.productlayer.android.common.handler.HasPLYAndroidHolder;
import com.productlayer.android.common.handler.HasPushHandler;
import com.productlayer.android.common.handler.HasTwitterHandler;
import com.productlayer.android.common.handler.HasUserHandler;
import com.productlayer.android.common.handler.LoginHandler;
import com.productlayer.android.common.handler.PLYAndroidHolder;
import com.productlayer.android.common.handler.PushHandler;
import com.productlayer.android.common.handler.TwitterHandler;
import com.productlayer.android.common.handler.UserHandler;
import com.productlayer.android.common.util.SnackbarUtil;
import com.productlayer.android.common.util.SystemBarsUtil;
import com.productlayer.android.common.util.ThemeUtil;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.android.sdk.PLYCompletion;
import com.productlayer.android.sdk.services.UserService;
import com.productlayer.core.beans.User;
import com.productlayer.core.error.PLYStatusCodes;

/**
 * Screen to update the currently logged in user's settings (profile, account, notifications, social
 * connections).
 *
 * Requires the activity to implement {@link HasAppBarHandler}, {@link HasUserHandler}, {@link
 * HasLoginHandler}, {@link HasFacebookHandler}, {@link HasTwitterHandler}, {@link HasPLYAndroidHolder}.
 */
public class ProfileEditFragment extends NamedFragment implements ChangePasswordDialogFragment
        .ChangePasswordDialogListener {

    public static final String NAME = "ProfileEdit";

    private static final String KEY_USER = "user";
    private static final String STATE_USER = "user";

    private AppBarHandler appBarHandler;
    private UserHandler userHandler;
    private LoginHandler loginHandler;
    private FacebookHandler facebookHandler;
    private TwitterHandler twitterHandler;
    private PushHandler pushHandler;

    private PLYAndroid client;

    private User user;

    private EditText firstNameEdit;
    private EditText lastNameEdit;
    private Spinner genderSpinner;
    private EditText nicknameEdit;
    private EditText emailEdit;

    /**
     * Constructs a new instance with the specified parameters. The parameters passed this way survive
     * recreation of the fragment due to orientation changes etc.
     *
     * @param user
     *         the user to edit
     * @return the fragment with the given parameters
     */
    public static ProfileEditFragment newInstance(@SuppressWarnings("TypeMayBeWeakened") User user) {
        ProfileEditFragment profileEditFragment = new ProfileEditFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_USER, user);
        profileEditFragment.setArguments(args);
        return profileEditFragment;
    }

    /**
     * @param user
     *         the user to edit
     * @return this fragment's name and initialization parameters
     */
    public static String makeInstanceName(User user) {
        String userParam = user == null ? "" : user.getId();
        return NAME + "(" + userParam + ")";
    }

    @Override
    public String getInstanceName() {
        return makeInstanceName(user);
    }

    @Override
    public FragmentGrouping getGrouping() {
        return FragmentGrouping.SETTINGS;
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
        try {
            pushHandler = ((HasPushHandler) activity).getPushHandler();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement HasPushHandler");
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            user = (User) savedInstanceState.getSerializable(STATE_USER);
        } else {
            Bundle args = getArguments();
            user = (User) args.getSerializable(KEY_USER);
        }
        // this fragment adds actions to the app bar
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final Context context = getContext();
        // inflate the layout
        View layout = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        firstNameEdit = (EditText) layout.findViewById(R.id.first_name_edit);
        lastNameEdit = (EditText) layout.findViewById(R.id.last_name_edit);
        genderSpinner = (Spinner) layout.findViewById(R.id.gender_spinner);
        nicknameEdit = (EditText) layout.findViewById(R.id.nickname_edit);
        emailEdit = (EditText) layout.findViewById(R.id.email_edit);
        // populate text fields with user values
        firstNameEdit.setText(user.getFirstName());
        lastNameEdit.setText(user.getLastName());
        nicknameEdit.setText(user.getNickname());
        emailEdit.setText(user.getEmail());
        // populate gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(context, R.array
                .gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        // set selected gender
        String gender = user.getGender();
        if (gender != null) {
            int genderResId = getResources().getIdentifier(gender, "string", context.getPackageName());
            if (genderResId != 0) {
                String genderLocalized = getString(genderResId);
                genderSpinner.setSelection(genderAdapter.getPosition(genderLocalized));
            }
        }
        // set password click listener
        Button passwordButton = (Button) layout.findViewById(R.id.password_button);
        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangePasswordDialogFragment().show(getChildFragmentManager(),
                        "ChangePasswordDialogFragment");
            }
        });
        // set push notification initial value and clicklistener
        Switch pushSwitch = (Switch) layout.findViewById(R.id.push_switch);
        pushSwitch.setChecked(pushHandler.isPushActive(context));
        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // (de)activate receipt of push notifications
                pushHandler.setPushActive(context, user.getId(), isChecked);
                SnackbarUtil.make(getActivity(), getView(), R.string.push_notification_updated, Snackbar
                        .LENGTH_SHORT).show();
            }
        });
        // set email notification initial value and clicklistener
        final Switch emailSwitch = (Switch) layout.findViewById(R.id.email_switch);
        emailSwitch.setChecked(user.getSettings().shouldSendEmailNotifications());
        emailSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                final CompoundButton.OnCheckedChangeListener thisListener = this;
                LoadingIndicator.show();
                UserService.enableOrDisableEmailNotification(client, isChecked, new PLYCompletion<User>() {
                    @Override
                    public void onSuccess(User result) {
                        LoadingIndicator.hide();
                        user.getSettings().sendEmailNotification(result.getSettings()
                                .shouldSendEmailNotifications());
                        userHandler.setUser(result, null);
                        SnackbarUtil.make(getActivity(), getView(), R.string.email_notification_updated,
                                Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(PLYAndroid.QueryError error) {
                        Log.d("EmailNotifCallback", error.getMessage());
                        LoadingIndicator.hide();
                        if (!error.isHttpStatusError()) {
                            SnackbarUtil.make(getActivity(), getView(), R.string
                                    .connection_failed_internet, Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPostError(PLYAndroid.QueryError error) {
                        emailSwitch.setOnCheckedChangeListener(null);
                        emailSwitch.setChecked(!isChecked);
                        emailSwitch.setOnCheckedChangeListener(thisListener);
                    }
                });
            }
        });
        // set email newsletter initial value and clicklistener
        final Switch newsletterSwitch = (Switch) layout.findViewById(R.id.newsletter_switch);
        newsletterSwitch.setChecked(user.getSettings().shouldSendEmailNewsletter());
        newsletterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                final CompoundButton.OnCheckedChangeListener thisListener = this;
                LoadingIndicator.show();
                UserService.enableOrDisableEmailNewsletter(client, isChecked, new PLYCompletion<User>() {
                    @Override
                    public void onSuccess(User result) {
                        LoadingIndicator.hide();
                        user.getSettings().sendEmailNewsletter(result.getSettings()
                                .shouldSendEmailNewsletter());
                        userHandler.setUser(result, null);
                        SnackbarUtil.make(getActivity(), getView(), R.string.newsletter_updated, Snackbar
                                .LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(PLYAndroid.QueryError error) {
                        Log.d("NewsletterCallback", error.getMessage());
                        LoadingIndicator.hide();
                        if (!error.isHttpStatusError()) {
                            SnackbarUtil.make(getActivity(), getView(), R.string
                                    .connection_failed_internet, Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPostError(PLYAndroid.QueryError error) {
                        newsletterSwitch.setOnCheckedChangeListener(null);
                        newsletterSwitch.setChecked(!isChecked);
                        newsletterSwitch.setOnCheckedChangeListener(thisListener);
                    }
                });
            }
        });
        // set facebook initial value and click listener
        final Switch facebookSwitch = (Switch) layout.findViewById(R.id.facebook_switch);
        facebookSwitch.setChecked(userHandler.isSocialNetworkConnected(getString(R.string
                .provider_key_facebook)));
        facebookSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                final CompoundButton.OnCheckedChangeListener thisListener = this;
                if (isChecked) {
                    // trigger facebook sdk login and provider connect
                    facebookHandler.connectFacebook(activity, loginHandler, new Runnable() {
                        @Override
                        public void run() {
                            SnackbarUtil.make(getActivity(), getView(), R.string
                                    .facebook_connect_succeeded, Snackbar.LENGTH_LONG).show();
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            SnackbarUtil.make(getActivity(), getView(), R.string.facebook_connect_failed,
                                    Snackbar.LENGTH_LONG).show();
                            facebookSwitch.setOnCheckedChangeListener(null);
                            facebookSwitch.setChecked(false);
                            facebookSwitch.setOnCheckedChangeListener(thisListener);
                        }
                    });
                    // an error can occur if the user cancels the facebook permission process
                    // or if a server is not reachable or if the email/fbid/fbtoken already exists on prod.ly
                } else {
                    // trigger facebook sdk logout and provider disconnect
                    facebookHandler.disconnectFacebook(activity, userHandler, client, new Runnable() {
                        @Override
                        public void run() {
                            SnackbarUtil.make(getActivity(), getView(), R.string
                                    .facebook_disconnect_succeeded, Snackbar.LENGTH_LONG).show();
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            SnackbarUtil.make(getActivity(), getView(), R.string
                                    .facebook_disconnect_failed, Snackbar.LENGTH_LONG).show();
                            facebookSwitch.setOnCheckedChangeListener(null);
                            facebookSwitch.setChecked(true);
                            facebookSwitch.setOnCheckedChangeListener(thisListener);
                        }
                    });
                }
            }
        });
        // set twitter initial value and click listener
        final Switch twitterSwitch = (Switch) layout.findViewById(R.id.twitter_switch);
        twitterSwitch.setChecked(userHandler.isSocialNetworkConnected(getString(R.string
                .provider_key_twitter)));
        twitterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                final CompoundButton.OnCheckedChangeListener thisListener = this;
                if (isChecked) {
                    // trigger twitter sdk login and provider connect
                    twitterHandler.connectTwitter(activity, loginHandler, new Runnable() {
                        @Override
                        public void run() {
                            SnackbarUtil.make(getActivity(), getView(), R.string.twitter_connect_succeeded,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            SnackbarUtil.make(getActivity(), getView(), R.string.twitter_connect_failed,
                                    Snackbar.LENGTH_LONG).show();
                            twitterSwitch.setOnCheckedChangeListener(null);
                            twitterSwitch.setChecked(false);
                            twitterSwitch.setOnCheckedChangeListener(thisListener);
                        }
                    });
                    // an error can occur if the user cancels the twitter permission process
                    // or if a server is not reachable or if the token already exists on prod.ly
                } else {
                    // trigger twitter sdk logout and provider disconnect
                    twitterHandler.disconnectTwitter(activity, userHandler, client, new Runnable() {
                        @Override
                        public void run() {
                            SnackbarUtil.make(getActivity(), getView(), R.string
                                    .twitter_disconnect_succeeded, Snackbar.LENGTH_LONG).show();
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            SnackbarUtil.make(getActivity(), getView(), R.string.twitter_disconnect_failed,
                                    Snackbar.LENGTH_LONG).show();
                            twitterSwitch.setOnCheckedChangeListener(null);
                            twitterSwitch.setChecked(true);
                            twitterSwitch.setOnCheckedChangeListener(thisListener);
                        }
                    });
                }
            }
        });
        // set expand/collapse click listeners on headers
        TextView personalHeaderText = (TextView) layout.findViewById(R.id.personal_header);
        View personalContentLayout = layout.findViewById(R.id.personal_content);
        personalHeaderText.setOnClickListener(new ExpandCollapseClickListener(context, personalHeaderText,
                personalContentLayout));
        TextView accountHeaderText = (TextView) layout.findViewById(R.id.account_header);
        View accountContentLayout = layout.findViewById(R.id.account_content);
        accountHeaderText.setOnClickListener(new ExpandCollapseClickListener(context, accountHeaderText,
                accountContentLayout));
        TextView notificationHeaderText = (TextView) layout.findViewById(R.id.notification_header);
        View notificationContentLayout = layout.findViewById(R.id.notification_content);
        notificationHeaderText.setOnClickListener(new ExpandCollapseClickListener(context,
                notificationHeaderText, notificationContentLayout));
        TextView socialHeaderText = (TextView) layout.findViewById(R.id.social_header);
        View socialContentLayout = layout.findViewById(R.id.social_content);
        socialHeaderText.setOnClickListener(new ExpandCollapseClickListener(context, socialHeaderText,
                socialContentLayout));
        // prevent input elements being covered by the navigation bar
        if (SystemBarsUtil.hasTranslucentNavigationBar(context)) {
            layout.setPadding(layout.getPaddingLeft(), layout.getPaddingTop(), layout.getPaddingRight(),
                    layout.getPaddingBottom() + SystemBarsUtil.getNavigationBarHeight(context));
            ((ViewGroup) layout).setClipToPadding(false);
            // TODO move up if safe for all screens
        }
        appBarHandler.setProfileEditAppBar(layout, user.getNickname());
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_USER, user);
        super.onSaveInstanceState(outState);
    }

    // FRAGMENT LIFECYCLE - END //

    // MENU - START //

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actions_save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {
            saveProfile();
            return true;
        }
        return false;
    }

    // MENU - END //

    /**
     * First retrieves a fresh remote User object to subsequently update it with data gathered from the
     * fragment's input fields.
     */
    private void saveProfile() {
        // retrieve uptodate User object
        LoadingIndicator.show();
        UserService.getUserByNicknameOrID(client, user.getId(), new PLYCompletion<User>() {
            @Override
            public void onSuccess(User result) {
                // save retrieved user with new values from input fields
                user = result;
                user.setNickname(nicknameEdit.getText().toString());
                user.setEmail(emailEdit.getText().toString());
                user.setFirstName(firstNameEdit.getText().toString());
                user.setLastName(lastNameEdit.getText().toString());
                String selectedGender = genderSpinner.getSelectedItem().toString();
                if (selectedGender.equals(getString(R.string.male))) {
                    user.setGender("male");
                } else if (selectedGender.equals(getString(R.string.female))) {
                    user.setGender("female");
                } else {
                    user.setGender(null);
                }
                UserService.updateUser(client, user, new PLYCompletion<User>() {
                    @Override
                    public void onSuccess(User result) {
                        LoadingIndicator.hide();
                        SnackbarUtil.make(getActivity(), getView(), R.string.profile_edited, Snackbar
                                .LENGTH_LONG).show();
                        if (userHandler.isCurrentUser(result)) {
                            // update global User object
                            userHandler.setUser(result, null);
                        }
                    }

                    @Override
                    public void onPostSuccess(User result) {
                        FragmentActivity activity = getActivity();
                        if (activity != null) {
                            activity.onBackPressed();
                        }
                    }

                    @Override
                    public void onError(PLYAndroid.QueryError error) {
                        Log.d("SaveProfileCallback", error.getMessage());
                        LoadingIndicator.hide();
                        if (!error.isHttpStatusError() || !error.hasInternalErrorCode(PLYStatusCodes
                                .OBJECT_NOT_UPDATED_NO_CHANGES_CODE)) {
                            SnackbarUtil.make(getActivity(), getView(), error.getMessage(), Snackbar
                                    .LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPostError(PLYAndroid.QueryError error) {
                        if (error.isHttpStatusError() && error.hasInternalErrorCode(PLYStatusCodes
                                .OBJECT_NOT_UPDATED_NO_CHANGES_CODE)) {
                            FragmentActivity activity = getActivity();
                            if (activity != null) {
                                activity.onBackPressed();
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(PLYAndroid.QueryError error) {
                Log.d("GetUserCallback", error.getMessage());
                LoadingIndicator.hide();
                SnackbarUtil.make(getActivity(), getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // CHANGE PASSWORD DIALOG - START //

    @Override
    public void onDialogChangePasswordClick(String currentPassword, String newPassword) {
        LoadingIndicator.show();
        UserService.changePassword(client, null, currentPassword, newPassword, new PLYCompletion<User>() {
            @Override
            public void onSuccess(User result) {
                LoadingIndicator.hide();
                userHandler.setUser(result, null);
                SnackbarUtil.make(getActivity(), getView(), R.string.password_change_succeeded, Snackbar
                        .LENGTH_LONG).show();
            }

            @Override
            public void onError(PLYAndroid.QueryError error) {
                Log.d("ChangePasswordCallback", error.getMessage());
                LoadingIndicator.hide();
                SnackbarUtil.make(getActivity(), getView(), R.string.password_change_failed, Snackbar
                        .LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDialogCancelChangePasswordClick() {
    }

    // CHANGE PASSWORD DIALOG - END //

    /**
     * OnClickListener that expands and collapses a content view by clicking on a header view.
     */
    private static class ExpandCollapseClickListener implements View.OnClickListener {

        private Context context;
        private TextView header;
        private View content;

        /**
         * Creates a new OnClickListener for expanding/collapsing a content section.
         *
         * @param context
         *         the app's context
         * @param header
         *         the header that is clicked on
         * @param content
         *         the content that is expanded and collapsed
         */
        private ExpandCollapseClickListener(Context context, TextView header, View content) {
            this.context = context;
            this.header = header;
            this.content = content;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onClick(View v) {
            if (content.getVisibility() == View.VISIBLE) {
                // hide content
                content.setVisibility(View.GONE);
                // make header more prominent
                header.setBackgroundColor(ThemeUtil.getIntegerValue(context, R.attr.colorAccent));
                header.setTextColor(ThemeUtil.getIntegerValue(context, R.attr.textColorOnAccentBg));
            } else {
                // show content
                content.setVisibility(View.VISIBLE);
                // make header less prominent
                header.setBackgroundColor(ThemeUtil.getIntegerValue(context, android.R.attr
                        .windowBackground));
                header.setTextColor(ThemeUtil.getIntegerValue(context, android.R.attr.textColorSecondary));
            }
        }
    }
}
