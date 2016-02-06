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

package com.productlayer.android.common.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.productlayer.core.beans.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an item and any expandable sub-items in a list adapter.
 */
public class CategoryListItem implements ExpandableListItem<CategoryListItem>, Parcelable {

    public static final String KEY_NAME = "name";
    public static final String KEY_FULL_NAME = "fullName";
    public static final String KEY_KEY = "key";

    public static final Parcelable.Creator<CategoryListItem> CREATOR = new Parcelable
            .Creator<CategoryListItem>() {

        @Override
        public CategoryListItem createFromParcel(Parcel source) {
            return new CategoryListItem(source);
        }

        @Override
        public CategoryListItem[] newArray(int size) {
            return new CategoryListItem[size];
        }
    };

    private final String name;
    private final String fullName;
    private final String key;
    private final CategoryListItem[] subItems;

    /**
     * Converts a Category object into a CategoryListItem.
     *
     * @param category
     *         the category to extract the name and sub-categories from
     * @param parentItem
     *         the parent category or null if this is a root category
     */
    public CategoryListItem(Category category, CategoryListItem parentItem) {
        name = category.getName();
        fullName = parentItem == null ? name : parentItem.fullName + " / " + name;
        key = category.getKey();
        List<Category> subCategories = category.getSubCategories();
        int cntSubCategories = subCategories == null ? 0 : subCategories.size();
        if (cntSubCategories == 0) {
            subItems = new CategoryListItem[0];
        } else {
            subItems = new CategoryListItem[cntSubCategories];
            for (int i = 0; i < cntSubCategories; i++) {
                subItems[i] = new CategoryListItem(subCategories.get(i), this);
            }
            // add a clickable item for the expandable root category
            subItems[cntSubCategories - 1] = new CategoryListItem(name, fullName, key, null);
        }
    }

    /**
     * Creates a new instance initializing all of its fields.
     *
     * @param name
     *         the name of the category
     * @param fullName
     *         the path to this category if it is not at the root
     * @param key
     *         the unique key identifying the category
     * @param subItems
     *         any sub-categories
     */
    public CategoryListItem(String name, String fullName, String key, CategoryListItem[] subItems) {
        this.name = name;
        this.fullName = fullName == null ? name : fullName;
        this.key = key;
        this.subItems = subItems == null ? new CategoryListItem[0] : subItems;
    }

    /**
     * Creates a new instance with data read from a Parcel.
     *
     * @param source
     *         the data to read
     */
    private CategoryListItem(Parcel source) {
        name = source.readString();
        fullName = source.readString();
        key = source.readString();
        subItems = source.createTypedArray(CREATOR);
    }

    /**
     * Transforms {@link Category} beans into {@code CategoryListItem} objects flattened to a sub-category
     * hierarchy of at most two levels.
     *
     * @param categories
     *         the input objects
     * @return the transformed objects
     */
    public static CategoryListItem[] fromCategories(Category[] categories) {
        List<CategoryListItem> categoryListItems = new ArrayList<CategoryListItem>();
        for (Category category : categories) {
            List<CategoryListItem> flatCategories = flattenRootCategory(new CategoryListItem(category, null));
            categoryListItems.addAll(flatCategories);
        }
        return categoryListItems.toArray(new CategoryListItem[categoryListItems.size()]);
    }

    /**
     * Flattens a category so that it is at most two levels deep.
     *
     * @param category
     *         the category to flatten
     * @return the sub-categories that were moved up a level due to having sub-sub-categories and the original
     * category having those sub-categories removed, with all sub-(sub-...-)categories being treated the same
     * way
     */
    private static List<CategoryListItem> flattenRootCategory(CategoryListItem category) {
        CategoryListItem[] subCategories = category.subItems;
        List<CategoryListItem> siblingCategories = new ArrayList<CategoryListItem>();
        for (int i = 0; i < subCategories.length; i++) {
            CategoryListItem subCategory = subCategories[i];
            CategoryListItem[] subSubCategories = subCategory.subItems;
            if (subSubCategories.length != 0) {
                // remove sub-category from category
                CategoryListItem[] newSubCategories = new CategoryListItem[subCategories.length - 1];
                System.arraycopy(subCategories, 0, newSubCategories, 0, i);
                System.arraycopy(subCategories, i + 1, newSubCategories, i, subCategories.length -
                        i - 1);
                category = new CategoryListItem(category.name, category.fullName, category.key,
                        newSubCategories);
                // add new sibling category from removed sub-category and flatten it as well
                CategoryListItem siblingCategory = new CategoryListItem(category.name + " / " +
                        subCategory.name, null, subCategory.key, subSubCategories);
                List<CategoryListItem> flattenedSiblingCategory = flattenRootCategory(siblingCategory);
                siblingCategories.addAll(flattenedSiblingCategory);
            }
        }
        if (!siblingCategories.isEmpty()) {
            Log.v("FlattenCategories", category.fullName + " had " + siblingCategories.size() + " " +
                    "sub-categories moved up by one level");
        }
        siblingCategories.add(0, category);
        return siblingCategories;
    }

    /**
     * Retrieves a category name for a specified key from a list of categories.
     *
     * @param key
     *         the key to search for
     * @param categories
     *         the categories to search in
     * @return the name of the category matching the specified key or null if none found
     */
    public static String getName(String key, CategoryListItem[] categories) {
        if (key == null || categories == null) {
            return null;
        }
        for (CategoryListItem category : categories) {
            if (key.equals(category.key)) {
                return category.name;
            }
            String name = getName(key, category.getSubItems());
            if (name != null) {
                return name;
            }
        }
        return null;
    }

    @Override
    public Object get(String key) {
        if (KEY_FULL_NAME.equals(key)) {
            return fullName;
        } else if (KEY_KEY.equals(key)) {
            return this.key;
        } else {
            return name;
        }
    }

    @Override
    public CategoryListItem[] getSubItems() {
        return subItems;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(fullName);
        dest.writeString(key);
        dest.writeTypedArray(subItems, flags);
    }
}
