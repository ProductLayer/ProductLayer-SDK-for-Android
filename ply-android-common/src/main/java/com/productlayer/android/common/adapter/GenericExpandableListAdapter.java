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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.android.common.model.ExpandableListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A searchable, expandable list adapter filling the view from an array of {@link ExpandableListItem}.
 */
public class GenericExpandableListAdapter<T extends ExpandableListItem> extends BaseExpandableListAdapter
        implements Filterable {

    private LayoutInflater inflater;

    private T[] allItems;
    private int expandedGroupLayout;
    private int collapsedGroupLayout;
    private String searchableProperty;
    private String[] groupFrom;
    private int[] groupTo;
    private int childLayout;
    private int lastChildLayout;
    private String[] childFrom;
    private int[] childTo;
    private int indicatorId;

    private List<T> filteredItems;

    /**
     * Creates a new adapter from the specified expandable list items, allowing configuration of
     * (expanded/collapsed) header and child layouts as well as the mapping between the properties of the
     * supplied items and the IDs of the text views within the layouts.
     *
     * @param context
     *         the application context
     * @param items
     *         the items to fill the list with
     * @param expandedGroupLayout
     *         the group header layout when expanded
     * @param collapsedGroupLayout
     *         the group header layout when collapsed
     * @param searchableProperty
     *         the property of items to use for filtering/searching
     * @param groupFrom
     *         the group items' properties to show
     * @param groupTo
     *         the IDs of the text views to map group properties with (matching the array indices of
     *         groupFrom)
     * @param childLayout
     *         the layout of child items
     * @param lastChildLayout
     *         the layout of the last child item in a group
     * @param childFrom
     *         the child items' properties to show
     * @param childTo
     *         the IDs of the text views to map child properties with (matching the array indices of
     *         childFrom)
     * @param indicatorId
     *         the ID of the image view indicating whether a group has children and whether it is collapsed or
     *         expanded
     */
    public GenericExpandableListAdapter(Context context, T[] items, int expandedGroupLayout, int
            collapsedGroupLayout, String searchableProperty, String[] groupFrom, int[] groupTo, int
            childLayout, int lastChildLayout, String[] childFrom, int[] childTo, int indicatorId) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.allItems = items;
        this.expandedGroupLayout = expandedGroupLayout;
        this.collapsedGroupLayout = collapsedGroupLayout;
        this.searchableProperty = searchableProperty;
        this.groupFrom = groupFrom;
        this.groupTo = groupTo;
        this.childLayout = childLayout;
        this.lastChildLayout = lastChildLayout;
        this.childFrom = childFrom;
        this.childTo = childTo;
        this.indicatorId = indicatorId;
        filteredItems = Arrays.asList(items);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(isExpanded ? expandedGroupLayout : collapsedGroupLayout, parent, false);
        }
        bindView(view, filteredItems.get(groupPosition), groupFrom, groupTo);
        ImageView indicator = (ImageView) view.findViewById(indicatorId);
        if (getChildrenCount(groupPosition) == 0) {
            // hide indicator if no children
            indicator.setVisibility(View.INVISIBLE);
        } else {
            // show either expand or collapse indicator
            indicator.setVisibility(View.VISIBLE);
            indicator.setImageResource(isExpanded ? R.drawable.ic_expand_less_black_18dp : R.drawable
                    .ic_expand_more_black_18dp);
        }
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
            ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(isLastChild ? lastChildLayout : childLayout, parent, false);
        }
        bindView(view, filteredItems.get(groupPosition).getSubItems()[childPosition], childFrom, childTo);
        return view;
    }

    /**
     * Binds an item's properties to a layout's text views.
     *
     * @param view
     *         the layout of the text views
     * @param item
     *         the item to display
     * @param from
     *         the properties of the item to parse
     * @param to
     *         the IDs of the text views to display the properties in (matching the array indices of from)
     */
    private void bindView(View view, ExpandableListItem item, String[] from, int[] to) {
        for (int i = 0; i < to.length; i++) {
            TextView textView = (TextView) view.findViewById(to[i]);
            if (textView != null) {
                textView.setText(item.get(from[i]).toString());
            }
        }
    }

    @Override
    public int getGroupCount() {
        return filteredItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return filteredItems.get(groupPosition).getSubItems().length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return filteredItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return filteredItems.get(groupPosition).getSubItems()[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0 || searchableProperty == null) {
                    filterResults.values = Arrays.asList(allItems);
                    filterResults.count = allItems.length;
                    return filterResults;
                }
                String constraintLower = constraint.toString().toLowerCase();
                List<ExpandableListItem> newItems = new ArrayList<ExpandableListItem>();
                for (ExpandableListItem item : allItems) {
                    if (item.get(searchableProperty).toString().toLowerCase().contains(constraintLower)) {
                        // if the category matches, display the category and any of its sub-categories
                        newItems.add(item);
                    } else {
                        // if it does not match, search further to show any matching sub-categories
                        for (ExpandableListItem subItem : item.getSubItems()) {
                            if (subItem.get(searchableProperty).toString().toLowerCase().contains
                                    (constraintLower) && subItem.getSubItems().length == 0) {
                                newItems.add(subItem);
                            }
                        }
                    }
                }
                filterResults.values = newItems;
                filterResults.count = newItems.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                @SuppressWarnings("unchecked") List<T> newItems = (List<T>) results.values;
                filteredItems = newItems;
                notifyDataSetChanged();
            }
        };
    }
}
