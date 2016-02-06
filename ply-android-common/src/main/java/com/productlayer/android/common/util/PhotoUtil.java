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

package com.productlayer.android.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.productlayer.android.common.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Methods to let the user select between picking an image from the gallery or taking a new photo, handling
 * the different results returned by the called intents to receive the selected file or the taken photo.
 */
public class PhotoUtil {

    private static final int REQUEST_CODE_TAKE_PHOTO = 13014;
    private static final int REQUEST_CODE_PICK_IMAGE = 13015;

    private static final String tempPhotoPath = getPhotoPathShared();

    /**
     * Adds an OnClickListener to the provided view to display a popup menu allowing the user to choose
     * between taking a photo and picking one from the gallery.
     *
     * @param targetActivity
     *         the target activity receiving the camera's/gallery's result (may be null if fragment is set)
     * @param targetFragment
     *         the target fragment receiving the camera's/gallery's result (may be null if activity is set)
     * @param anchor
     *         the view to anchor the popup menu to and to register the OnClickListener with
     */
    public static void registerSelectorPopup(final Activity targetActivity, final Fragment targetFragment,
            View anchor) {
        Context context = targetFragment != null ? targetFragment.getActivity() : targetActivity;
        if (context == null) {
            return;
        }
        final PopupMenu popup = new PopupMenu(context, anchor);
        popup.getMenuInflater().inflate(R.menu.popup_photo_source, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int selectedId = item.getItemId();
                if (selectedId == R.id.popup_take_photo) {
                    takePhoto(targetActivity, targetFragment);
                    return true;
                } else if (selectedId == R.id.popup_pick_from_gallery) {
                    pickGalleryImage(targetActivity, targetFragment);
                    return true;
                }
                return false;
            }
        });
        try {
            // try to display icons
            Field mPopupField = popup.getClass().getDeclaredField("mPopup");
            mPopupField.setAccessible(true);
            MenuPopupHelper mPopup = (MenuPopupHelper) mPopupField.get(popup);
            mPopup.setForceShowIcon(true);
            Integer tintColor = ThemeUtil.getIntegerValue(context, android.R.attr.textColorPrimary);
            if (tintColor != null) {
                ColorUtil.tintMenuItems(popup.getMenu(), tintColor, -1);
            }
        } catch (Exception ignored) {
        }
        anchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });
    }

    /**
     * Starts the camera app to take a photo.
     *
     * @param targetActivity
     *         the target activity receiving the camera's result (may be null if fragment is set)
     * @param targetFragment
     *         the target fragment receiving the camera's result (may be null if activity is set)
     */
    public static void takePhoto(Activity targetActivity, Fragment targetFragment) {
        Context context = targetFragment != null ? targetFragment.getActivity() : targetActivity;
        if (context == null) {
            return;
        }
        if (CameraUtil.hasCameraAny(context)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(tempPhotoPath)));
            if (targetFragment != null) {
                targetFragment.startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
            } else {
                targetActivity.startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
            }
        } else {
            if (targetFragment != null) {
                SnackbarUtil.make(targetFragment.getActivity(), targetFragment.getView(), R.string
                        .no_camera_found, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Opens the gallery for the user to pick an image.
     *
     * @param targetActivity
     *         the target activity receiving the gallery's result (may be null if fragment is set)
     * @param targetFragment
     *         the target fragment receiving the gallery's result (may be null if activity is set)
     */
    public static void pickGalleryImage(Activity targetActivity, Fragment targetFragment) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        if (targetFragment != null) {
            targetFragment.startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        } else if (targetActivity != null) {
            targetActivity.startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        }
    }

    /**
     * Convenience method to call from an activity's or a fragment's {@code onActivityResult} method to check
     * if a photo was taken by the camera app or if the user picked an image from the gallery. If so, moves
     * the file to the specified path.
     *
     * @param requestCode
     *         the request code passed to the method
     * @param resultCode
     *         the result code passed to the method
     * @param data
     *         any attached result data
     * @param context
     *         the application context
     * @param moveToPath
     *         the file path to move any taken/selected photo to
     * @return true if a photo was taken, false else
     */
    public static boolean onActivityResult(int requestCode, int resultCode, Intent data, Context context,
            String moveToPath) {
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode != AppCompatActivity.RESULT_CANCELED) {
            // photo taken
            Log.d(PhotoUtil.class.getSimpleName(), "New photo at " + tempPhotoPath);
            StorageUtil.copyFile(new File(tempPhotoPath), new File(moveToPath), true);
            // TODO bug: some devices save to gallery too -> find and delete
            return true;
        } else if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode != AppCompatActivity
                .RESULT_CANCELED) {
            // gallery image selected
            if (context == null) {
                return false;
            }
            Uri imageUri = data.getData();
            Log.d(PhotoUtil.class.getSimpleName(), "Selected gallery image at " + imageUri);
            InputStream is;
            try {
                is = context.getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                Log.w(PhotoUtil.class.getSimpleName(), e);
                return false;
            }
            StorageUtil.copyFile(is, new File(moveToPath));
            return true;
        }
        // TODO copying should be done in a background thread
        // TODO clear cache dir periodically
        return false;
    }

    /**
     * @param id
     *         the object ID
     * @param activity
     *         the current activity
     * @return the file path in internal cache any taken photos of products are moved to
     */
    public static String getPhotoPathCache(String id, Activity activity) {
        File photoFileCache = new File(activity.getCacheDir(), id + ".jpg");
        return photoFileCache.getAbsolutePath();
    }

    /**
     * @return the file path to have the external camera app save photos at
     */
    private static String getPhotoPathShared() {
        File photoFileShared = new File(StorageUtil.getExternalDir(), "prodly.jpg");
        return photoFileShared.getAbsolutePath();
    }

}
