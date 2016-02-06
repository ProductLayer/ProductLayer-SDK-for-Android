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

import com.productlayer.android.common.util.MathUtil;

/**
 * Encapsulates the level and advancement progress as parsed from the points of a user.
 */
public class Level {

    private long points;
    private double exactLevel;

    /**
     * Creates a new Level corresponding to the provided points value.
     *
     * @param points
     *         the overall points earned to calculate the level from
     */
    public Level(long points) {
        this.points = points;
        exactLevel = 1 + MathUtil.logOfBase(1.05, points + 6600) - MathUtil.logOfBase(1.05, 6600);
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return (int) exactLevel;
    }

    /**
     * @return the progress made to the next level
     */
    public double getProgress() {
        return exactLevel - getLevel();
    }

    /**
     * @return the progress made to the next level as an integer from 0 to 99
     */
    public int getProgressInt() {
        return (int) (getProgress() * 100);
    }

    /**
     * @return the raw points
     */
    public long getPoints() {
        return points;
    }

    /**
     * @return the raw level including the progress to the next level
     */
    public double getExactLevel() {
        return exactLevel;
    }
}
