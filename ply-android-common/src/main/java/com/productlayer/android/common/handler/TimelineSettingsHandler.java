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

import com.productlayer.android.common.fragment.TimelineFragment;

/**
 * Implementing classes configure/load/save/apply timeline settings.
 */
public interface TimelineSettingsHandler {

    /**
     * Configures the timeline settings view and displays a button to pop them up.
     *
     * @param tag
     *         the tag of the timeline to persistently save settings for
     * @param availableSettings
     *         the settings that are available to select for this timeline
     * @param defaultSettings
     *         the settings that are enabled by default for this timeline if they haven't been adapted yet
     * @param timeline
     *         the timeline to apply the settings on
     */
    void configureTimelineSettings(String tag, TimelineSetting[] availableSettings, int defaultSettings,
            TimelineFragment timeline);

    /**
     * Shows the timeline settings button.
     */
    void showTimelineSettings();

    /**
     * Hides the timeline settings button.
     */
    void hideTimelineSettings();

    /**
     * Loads the currently enabled settings for a timeline from persistent memory.
     *
     * @param tag
     *         the tag of the timeline to load settings for
     * @param defaultSettings
     *         the default settings to load if none have been stored yet
     * @return the currently enabled timeline settings
     */
    int getTimelineSettings(String tag, int defaultSettings);

    /**
     * Settings to include/exclude certain types of timeline entries.
     */
    enum TimelineSetting {
        FRIENDS_ONLY(1), INCLUDE_IMAGES(2);

        public final int value;

        TimelineSetting(int value) {
            this.value = value;
        }
    }

}
