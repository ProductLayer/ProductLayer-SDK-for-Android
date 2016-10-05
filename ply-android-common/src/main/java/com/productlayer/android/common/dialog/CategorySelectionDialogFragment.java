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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.adapter.CategoryListAdapter;
import com.productlayer.android.common.model.CategoryListItem;

/**
 * A dialog presenting a selectable, expandable list of main categories and sub-categories.
 */
public class CategorySelectionDialogFragment extends VerboseDialogFragment {

    public static final int REQUEST_CODE_SELECT_CATEGORY = 200;
    public static final String KEY_SELECTION = "selection";
    private static final String KEY_CATEGORIES = "categories";

    public static CategorySelectionDialogFragment newInstance(CategoryListItem[] categories) {
        CategorySelectionDialogFragment categorySelectionDialogFragment = new
                CategorySelectionDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(KEY_CATEGORIES, categories);
        categorySelectionDialogFragment.setArguments(args);
        return categorySelectionDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // get categories from arguments
        Bundle args = getArguments();
        Parcelable[] parcelables = args.getParcelableArray(KEY_CATEGORIES);
        assert parcelables != null;
        CategoryListItem[] categories = new CategoryListItem[parcelables.length];
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(parcelables, 0, categories, 0, parcelables.length);
        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View categorySelectionLayout = inflater.inflate(R.layout.dialog_category_selection, null);
        ExpandableListView categoryList = (ExpandableListView) categorySelectionLayout.findViewById(R.id
                .category_list);
        final CategoryListAdapter categoryAdapter = new CategoryListAdapter(getActivity(), categories, R
                .layout.list_group_item_expanded, R.layout.list_group_item_collapsed, R.layout
                .list_child_item, R.layout.list_child_item, R.id.indicator);
        categoryList.setAdapter(categoryAdapter);
        categoryList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int
                    childPosition, long id) {
                return categorySelected((Parcelable) parent.getExpandableListAdapter().getChild
                        (groupPosition, childPosition));
            }
        });
        categoryList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                CategoryListItem category = (CategoryListItem) parent.getExpandableListAdapter().getGroup
                        (groupPosition);
                // only categories without sub-categories are selected directly
                return category.getSubItems().length == 0 && categorySelected(category);
            }
        });
        SearchView categorySearch = (SearchView) categorySelectionLayout.findViewById(R.id.category_search);
        categorySearch.setSubmitButtonEnabled(false);
        categorySearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                categoryAdapter.getFilter().filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
        });
        builder.setView(categorySelectionLayout);
        return builder.create();
    }

    /**
     * Sends the category back to the target fragment.
     *
     * @param category
     *         the selected category
     * @return false if no target fragment was set, true else
     */
    private boolean categorySelected(Parcelable category) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getDialog().getWindow().getDecorView().getWindowToken(), 0);
        // package the clicked category
        Intent data = new Intent();
        data.putExtra(KEY_SELECTION, category);
        // call back to the parent
        if (getParentFragment() != null) {
            getParentFragment().onActivityResult(REQUEST_CODE_SELECT_CATEGORY, 0, data);
            dismiss();
            return true;
        } else {
            Log.w(getClass().getSimpleName(), "getParentFragment() returned null");
            dismiss();
            return false;
        }
    }
}
