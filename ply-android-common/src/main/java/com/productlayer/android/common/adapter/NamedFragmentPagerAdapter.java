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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A Fragment Pager Adapter implementation supporting the adding of new fragments and custom titles.
 */
public class NamedFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<String> fragmentNames = new ArrayList<String>();
    private List<Fragment> fragments = new ArrayList<Fragment>();

    /**
     * Creates a new fragment pager adapter.
     *
     * @param fragmentManager
     *         the fragment manager to use for transactions
     */
    public NamedFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    /**
     * Looks up a fragment in the fragment manager's pool of managed fragments.
     *
     * Very dirty fix to get fragments back into the adapter after an orientation change. Relies on the
     * internal fragment naming scheme.
     *
     * @param fragmentPagerAdapter
     *         the fragment pager adapter
     * @param fragmentManager
     *         the fragment manager
     * @param viewPagerId
     *         the ID of the view pager containing the fragment
     * @param position
     *         the position of the tab
     * @return the fragment if found in the cache or null
     */
    public static Fragment findFragmentByPosition(FragmentPagerAdapter fragmentPagerAdapter,
            FragmentManager fragmentManager, int viewPagerId, int position) {
        return fragmentManager.findFragmentByTag(makeFragmentName(viewPagerId, fragmentPagerAdapter
                .getItemId(position)));
    }

    /**
     * Copied from {@link FragmentPagerAdapter}.
     *
     * @param viewId
     *         the ID of the view pager
     * @param id
     *         the ID of the tab
     * @return the fragment's tag name
     */
    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentNames.get(position);
    }

    /**
     * Adds a fragment to the pager adapter.
     *
     * @param name
     *         the name of the fragment to display in a tab
     * @param fragment
     *         the fragment to add
     */
    public void addFragment(String name, Fragment fragment) {
        fragmentNames.add(name);
        fragments.add(fragment);
    }

    /*@Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        while (position > fragments.size() - 1) {
            fragments.add(null);
            fragmentNames.add(null);
        }
        fragments.set(position, fragment);
        if (fragmentNames.get(position) == null) {
            fragmentNames.set(position, "");
        }
        return fragment;
    }*/
}
