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

package com.productlayer.android.common.handler;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * OnScrollListener suitable for RecyclerViews with a StaggeredGridLayoutManager. Informs about hitting the
 * top or bottom edge as well as scrolling. Integrates support for rate-limiting the execution of code on
 * hitting an edge to once per second.
 */
public abstract class EdgeScrollListener extends RecyclerView.OnScrollListener {

    private static final long NANOS_BETWEEN_RETRIEVALS = 1000000000;

    private StaggeredGridLayoutManager gridLayoutManager;
    private int gridColumns;

    private int scrollX = 0;
    private int scrollY = 0;
    private int scrollYLastFling = 0;
    private long lastRetrievalNanoTime = 0;

    /**
     * Creates a new EdgeScrollListener.
     *
     * @param gridLayoutManager
     *         the layout manager to question for item visibility when determining whether top/bottom is hit
     * @param gridColumns
     *         the amount of columns in the grid to use for determining whether top/bottom is closed in on
     */
    public EdgeScrollListener(StaggeredGridLayoutManager gridLayoutManager, int gridColumns) {
        this.gridLayoutManager = gridLayoutManager;
        this.gridColumns = gridColumns;
    }

    /**
     * Runs when closing in on the top edge.
     */
    public abstract void onHitTop();

    /**
     * Runs when closing in on the bottom edge.
     */
    public abstract void onHitBottom();

    /**
     * Runs when scrolling up.
     */
    public abstract void onScrollUp();

    /**
     * Runs when scrolling down.
     */
    public abstract void onScrollDown();

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        //Log.v("RView.OnScrollStateCh", "Scroll state changed to " + newState + " at coordinate " +
        //        scrollY);
        if (newState == RecyclerView.SCROLL_STATE_IDLE && scrollY <= 0) {
            // scrollY will be too low and may become negative after navigating away and pressing
            // back due to vars being reset and scrollToPosition not triggering the OnScrollListener
            if (scrollYLastFling == scrollY) {
                // no change in scrollY since last fling -> at top
                if (checkRetrievalTime()) {
                    onHitTop();
                }
            }
            scrollYLastFling = scrollY;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        scrollX += dx;
        scrollY += dy;
        //Log.v("RecyclerView.OnScrolled", "Current vertical scroll position: " + scrollY);
        if (dy < 0) {
            // retrieve more recent items on scrolling up if close to the top
            int[] firstVisibleItemPositions = gridLayoutManager.findFirstVisibleItemPositions(null);
            if (firstVisibleItemPositions == null || firstVisibleItemPositions.length == 0 ||
                    firstVisibleItemPositions[0] == RecyclerView.NO_POSITION ||
                    firstVisibleItemPositions[0] < gridColumns) {
                //Log.v("RecyclerView.OnScrolled", "Nearing the top, scrolling up by " + -dy + "px");
                if (checkRetrievalTime()) {
                    onHitTop();
                }
            }
            onScrollUp();
        } else if (dy > 0) {
            // retrieve less recent items on scrolling down if close to the bottom
            int itemCount = gridLayoutManager.getItemCount();
            int[] lastVisibleItemPositions = gridLayoutManager.findLastVisibleItemPositions(null);
            if (lastVisibleItemPositions == null || lastVisibleItemPositions.length == 0 ||
                    lastVisibleItemPositions[0] == RecyclerView.NO_POSITION ||
                    lastVisibleItemPositions[0] > itemCount - gridColumns * 4) {
                //Log.v("RecyclerView.OnScrolled", "Nearing the bottom, scrolling down by " + dy +
                // "px");
                if (checkRetrievalTime()) {
                    onHitBottom();
                }
            }
            onScrollDown();
        }
    }

    /**
     * @return true if the last timeline retrieval was more than {@code NANOS_BETWEEN_RETRIEVALS} nanoseconds
     * ago, false else
     */
    private boolean checkRetrievalTime() {
        long currentNanoTime = System.nanoTime();
        if (currentNanoTime > lastRetrievalNanoTime + NANOS_BETWEEN_RETRIEVALS) {
            lastRetrievalNanoTime = currentNanoTime;
            return true;
        }
        return false;
    }

}
