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

import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Convenience utils to tint drawables.
 */
public class ColorUtil {

    /**
     * Tints a drawable without modifying the source resource.
     *
     * @param drawable
     *         the drawable to tint
     * @param color
     *         the color to use
     */
    public static void mutateAndTintDrawable(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        drawable.mutate();
        tintDrawable(drawable, color);
    }

    /**
     * Tints a drawable. Also tints the original resource, use {@link #mutateAndTintDrawable} to not touch the
     * source resource.
     *
     * @param drawable
     *         the drawable to tint
     * @param color
     *         the color to use
     */
    public static void tintDrawable(Drawable drawable, int color) {
        DrawableCompat.setTint(drawable, color);
    }

    /**
     * Tints all items in a menu.
     *
     * @param menu
     *         the menu items to tint
     * @param color
     *         the color to use
     * @param excludeGroup
     *         any group ID to exclude from tinting (0 for items without group)
     */
    public static void tintMenuItems(Menu menu, int color, int excludeGroup) {
        int cntMenuItems = menu.size();
        for (int i = 0; i < cntMenuItems; i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.getGroupId() == excludeGroup) {
                continue;
            }
            Drawable icon = menuItem.getIcon();
            if (icon == null) {
                continue;
            }
            mutateAndTintDrawable(icon, color);
            menuItem.setIcon(icon);
        }
    }

}
