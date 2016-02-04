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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.productlayer.android.common.R;
import com.productlayer.core.beans.Category;
import com.productlayer.core.beans.Product;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Goes through the properties of a product and provides relevant ones to a RecyclerView.
 */
public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.DetailHolder> {

    private static final String[] relevantFields = {"category", "brand", "brandOwner", "shortDescription",
            "longDescription", "homepage"};

    private static final Map<String, Integer> fieldLabels;

    static {
        fieldLabels = new HashMap<String, Integer>();
        fieldLabels.put("category", R.string.category);
        fieldLabels.put("brand", R.string.brand);
        fieldLabels.put("brandOwner", R.string.brand_owner);
        fieldLabels.put("shortDescription", R.string.short_description);
        fieldLabels.put("longDescription", R.string.long_description);
        fieldLabels.put("homepage", R.string.homepage);
    }

    private List<AbstractMap.SimpleImmutableEntry<String, String>> fields;

    /**
     * Creates a new adapter to provide product details.
     *
     * @param context
     *         the application context
     * @param product
     *         the product to show properties of
     * @param categories
     *         categories to extract translated names of keys from
     */
    public ProductDetailsAdapter(Context context, Product product, Category[] categories) {
        setHasStableIds(true);
        fields = new ArrayList<AbstractMap.SimpleImmutableEntry<String, String>>();
        try {
            for (String fieldName : relevantFields) {
                Field f = Product.class.getDeclaredField(fieldName);
                f.setAccessible(true);
                String content = (String) f.get(product);
                if (content != null && !content.isEmpty()) {
                    String key = context.getResources().getString(fieldLabels.get(fieldName));
                    if (fieldName.equals("category") && categories != null) {
                        content = getCategoryName(categories, content);
                    }
                    fields.add(new AbstractMap.SimpleImmutableEntry<String, String>(key, content));
                }
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Unexpected error", e);
        }
    }

    /**
     * Looks for a name for the provided category key.
     *
     * @param categories
     *         the categories to search for a name in
     * @param key
     *         the key to look for
     * @return the name if the key was found, null else
     */
    public static String getCategoryName(Category[] categories, String key) {
        for (Category category : categories) {
            if (key.equals(category.getKey())) {
                return category.getName();
            }
            List<Category> subCategories = category.getSubCategories();
            if (subCategories != null) {
                String name = getCategoryName(subCategories.toArray(new Category[subCategories.size()]), key);
                if (name != null) {
                    return name;
                }
            }
        }
        return null;
    }

    @Override
    public DetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.key_value_pair, parent,
                false);
        return new DetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DetailHolder holder, int position) {
        AbstractMap.SimpleImmutableEntry<String, String> item = fields.get(position);
        String label = item.getKey();
        String content = item.getValue();
        holder.label.setText(label);
        holder.content.setText(content);
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    @Override
    public long getItemId(int position) {
        return fields.get(position).getKey().hashCode();
    }

    /**
     * ViewHolder cached by the RecyclerView. Holds a label and a content text view.
     */
    public static class DetailHolder extends RecyclerView.ViewHolder {

        public TextView label;
        public TextView content;

        /**
         * Creates the ViewHolder caching a view and its text views for a label and content.
         *
         * @param itemView
         *         the view to cache
         */
        public DetailHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.label);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
