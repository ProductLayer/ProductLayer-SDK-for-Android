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

package com.productlayer.android.common.adapter;

import android.content.Context;

import com.productlayer.android.common.R;
import com.productlayer.android.common.model.CategoryListItem;
import com.productlayer.core.beans.Category;

/**
 * An expandable list adapter for {@link Category} beans, displaying their names and sub-categories.
 */
public class CategoryListAdapter extends GenericExpandableListAdapter<CategoryListItem> {

    /**
     * Creates a new adapter from the specified expandable list items using specified layouts.
     *
     * Group layouts must provide a text view with ID label {@code group_text}. Child layouts must provide a
     * text view with ID label {@code child_text}.
     *
     * @param context
     *         the application context
     * @param items
     *         the items to fill the list with
     * @param expandedGroupLayout
     *         the group header layout when expanded
     * @param collapsedGroupLayout
     *         the group header layout when collapsed
     * @param childLayout
     *         the layout of child items
     * @param lastChildLayout
     *         the layout of the last child item in a group
     * @param indicatorId
     *         the ID of the image view indicating whether a group has children and whether it is collapsed or
     *         expanded
     */
    public CategoryListAdapter(Context context, CategoryListItem[] items, int expandedGroupLayout, int
            collapsedGroupLayout, int childLayout, int lastChildLayout, int indicatorId) {
        super(context, items, expandedGroupLayout, collapsedGroupLayout, new String[]{CategoryListItem
                .KEY_NAME}, new int[]{R.id.group_text}, childLayout, lastChildLayout, new
                String[]{CategoryListItem.KEY_NAME}, new int[]{R.id.child_text}, indicatorId);
    }

    /**
     * Creates a new adapter from the specified {@link Category} beans, displaying their names and
     * sub-categories, using specified layouts.
     *
     * Group layouts must provide a text view with ID label {@code group_text}. Child layouts must provide a
     * text view with ID label {@code child_text}.
     *
     * @param context
     *         the application context
     * @param categories
     *         the Category beans to use as source for names and sub-items
     * @param expandedGroupLayout
     *         the group header layout when expanded
     * @param collapsedGroupLayout
     *         the group header layout when collapsed
     * @param childLayout
     *         the layout of child items
     * @param lastChildLayout
     *         the layout of the last child item in a group
     * @param indicatorId
     *         the ID of the image view indicating whether a group has children and whether it is collapsed or
     *         expanded
     * @return an adapter containing names and sub-categories from {@code categories}
     */
    public static CategoryListAdapter newInstance(Context context, Category[] categories, int
            expandedGroupLayout, int collapsedGroupLayout, int childLayout, int lastChildLayout, int
            indicatorId) {
        CategoryListItem[] categoryListItems = CategoryListItem.fromCategories(categories);
        return new CategoryListAdapter(context, categoryListItems, expandedGroupLayout,
                collapsedGroupLayout, childLayout, lastChildLayout, indicatorId);
    }
}
