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
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.core.beans.BrandOwner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A filterable adapter of likely brand owners (returned first) as well as of all available brand owners
 * (returned last). The latter are shown only at a minimum input of one character.
 */
public class BrandOwnerAdapter extends BaseAdapter implements Filterable {

    private final LayoutInflater layoutInflater;

    private final Set<String> likelyBrandOwners;
    private final TreeSet<String> allBrandOwners;

    private int resource;

    private List<String> filteredBrandOwners;

    /**
     * Creates a new adapter using the brand owners contained in the specified {@code likelyBrandOwners} array
     * as well as all brands in {@code allBrandOwners}.
     *
     * @param context
     *         the application context
     * @param resource
     *         the text view resource to fill per brand owner
     * @param likelyBrandOwners
     *         likely brand owners
     * @param allBrandOwners
     *         all available brand owners
     */
    public BrandOwnerAdapter(Context context, int resource, BrandOwner[] likelyBrandOwners, String[]
            allBrandOwners) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.allBrandOwners = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        if (allBrandOwners != null) {
            this.allBrandOwners.addAll(Arrays.asList(allBrandOwners));
        }
        this.likelyBrandOwners = new HashSet<String>();
        if (likelyBrandOwners != null) {
            for (BrandOwner brandOwner : likelyBrandOwners) {
                String brandOwnerName = brandOwner.getName();
                if (brandOwnerName != null && !brandOwnerName.isEmpty() && !"unknown".equals
                        (brandOwnerName)) {
                    this.likelyBrandOwners.add(brandOwnerName);
                    this.allBrandOwners.remove(brandOwnerName);
                }
            }
        }
        filteredBrandOwners = new ArrayList<String>();
        filteredBrandOwners.addAll(this.likelyBrandOwners);
    }

    /**
     * @param position
     *         an item's position
     * @return whether the specified position is valid
     */
    private boolean isInBounds(int position) {
        return position < getCount();
    }

    @Override
    public int getCount() {
        return filteredBrandOwners.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredBrandOwners.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            // create new view
            view = layoutInflater.inflate(resource, parent, false);
        }
        if (!isInBounds(position)) {
            return view;
        }
        // fill in values
        CharSequence brandOwnerName = (CharSequence) getItem(position);
        TextView brandOwner = (TextView) view.findViewById(R.id.brand_owner);
        brandOwner.setText(brandOwnerName);
        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                Collection<String> filteredBrandOwners = new ArrayList<String>();
                if (constraint == null || constraint.length() == 0) {
                    // no constraining input
                    if (!likelyBrandOwners.isEmpty()) {
                        // only show likely brand owners
                        filteredBrandOwners.addAll(likelyBrandOwners);
                    }
                    filterResults.values = filteredBrandOwners;
                    filterResults.count = filteredBrandOwners.size();
                    return filterResults;
                }
                // filter by constraint
                String constraintStr = constraint.toString();
                String constraintStrLower = constraintStr.toLowerCase();
                // likely brands
                for (String brandOwnerName : likelyBrandOwners) {
                    if (brandOwnerName.toLowerCase().contains(constraintStrLower)) {
                        filteredBrandOwners.add(brandOwnerName);
                    }
                }
                // all brands
                Set<String> tailSet = allBrandOwners.tailSet(constraintStr);
                for (String brandOwnerName : tailSet) {
                    if (brandOwnerName.toLowerCase().startsWith(constraintStrLower)) {
                        filteredBrandOwners.add(brandOwnerName);
                    } else {
                        break;
                    }
                }
                filterResults.values = filteredBrandOwners;
                filterResults.count = filteredBrandOwners.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                filteredBrandOwners = (List<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
