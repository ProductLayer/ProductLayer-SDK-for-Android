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
import com.productlayer.android.common.model.SimpleBrand;
import com.productlayer.core.beans.Brand;
import com.productlayer.core.beans.BrandOwner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A filterable adapter of likely brands and their owners (returned first) as well as of all available brands
 * (returned last). The latter are shown only at a minimum input of one character.
 */
public class BrandAdapter extends BaseAdapter implements Filterable {

    private final LayoutInflater layoutInflater;

    private final Set<SimpleBrand> likelyBrands;
    private final TreeSet<String> allBrands;

    private int resource;

    private List<Object> filteredBrands;
    private int positionStartAllBrands;

    /**
     * Creates a new adapter using the brand and brand owners contained in the specified {@code
     * likelyBrandOwners} array as well as all brands in {@code allBrands}.
     *
     * @param context
     *         the application context
     * @param resource
     *         the layout to use per brand information (must include brand and brand_owner fields)
     * @param likelyBrandOwners
     *         likely brand owners and brands
     * @param allBrands
     *         all available brands
     */
    public BrandAdapter(Context context, int resource, BrandOwner[] likelyBrandOwners, String[] allBrands) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.allBrands = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        if (allBrands != null) {
            this.allBrands.addAll(Arrays.asList(allBrands));
        }
        likelyBrands = new HashSet<SimpleBrand>();
        if (likelyBrandOwners != null) {
            for (BrandOwner brandOwner : likelyBrandOwners) {
                String brandOwnerName = brandOwner.getName();
                if ("unknown".equals(brandOwnerName)) {
                    brandOwnerName = "";
                }
                for (Brand brand : brandOwner.getBrands()) {
                    String brandName = brand.getName();
                    likelyBrands.add(new SimpleBrand(brandName, brandOwnerName));
                    this.allBrands.remove(brandName);
                }
            }
        }
        filteredBrands = new ArrayList<Object>();
        filteredBrands.addAll(likelyBrands);
        positionStartAllBrands = likelyBrands.size();
    }

    /**
     * @param position
     *         the item's position in the data set
     * @return whether the item at this position is a likely brand and of type {@code SimpleBrand}
     */
    private boolean isLikelyBrand(int position) {
        return position < positionStartAllBrands;
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
        return filteredBrands.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredBrands.get(position);
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
        // fill in values
        String brandName;
        String brandOwnerName;
        if (isLikelyBrand(position)) {
            SimpleBrand simpleBrand = (SimpleBrand) getItem(position);
            brandName = simpleBrand.brand;
            brandOwnerName = simpleBrand.brandOwner;
        } else if (isInBounds(position)) {
            brandName = (String) getItem(position);
            brandOwnerName = null;
        } else {
            return view;
        }
        TextView brand = (TextView) view.findViewById(R.id.brand);
        brand.setText(brandName);
        TextView brandOwner = (TextView) view.findViewById(R.id.brand_owner);
        if (brandOwnerName != null && !brandOwnerName.isEmpty()) {
            brandOwner.setText(brandOwnerName);
            brandOwner.setVisibility(View.VISIBLE);
        } else {
            brandOwner.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                Collection<Object> filteredBrands = new ArrayList<Object>();
                if (constraint == null || constraint.length() == 0) {
                    // no constraining input
                    if (!likelyBrands.isEmpty()) {
                        // only show likely brands
                        filteredBrands.addAll(likelyBrands);
                    }
                    filterResults.values = filteredBrands;
                    filterResults.count = filteredBrands.size();
                    return filterResults;
                }
                // filter by constraint
                String constraintStr = constraint.toString();
                String constraintStrLower = constraintStr.toLowerCase();
                // likely brands
                for (SimpleBrand simpleBrand : likelyBrands) {
                    if (simpleBrand.brandLower.contains(constraintStrLower) || simpleBrand
                            .brandAlphaNumeric.contains(constraintStrLower)) {
                        filteredBrands.add(simpleBrand);
                    }
                }
                // all brands
                Set<String> tailSet = allBrands.tailSet(constraintStr);
                for (String brandName : tailSet) {
                    if (brandName.toLowerCase().startsWith(constraintStrLower)) {
                        filteredBrands.add(brandName);
                    } else {
                        break;
                    }
                }
                filterResults.values = filteredBrands;
                filterResults.count = filteredBrands.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                @SuppressWarnings("unchecked") List<Object> values = (List<Object>) results.values;
                int positionFirstStringObject = results.count;
                for (int i = 0; i < results.count; i++) {
                    if (values.get(i) instanceof String) {
                        positionFirstStringObject = i;
                        break;
                    }
                }
                filteredBrands = values;
                positionStartAllBrands = positionFirstStringObject;
                notifyDataSetChanged();
            }
        };
    }
}
