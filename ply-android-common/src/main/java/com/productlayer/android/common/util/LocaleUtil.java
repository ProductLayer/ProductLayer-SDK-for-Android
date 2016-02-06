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

package com.productlayer.android.common.util;

import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import java.util.Arrays;
import java.util.Locale;

/**
 * Convenience functions to retrieve locales.
 */
public class LocaleUtil {

    /**
     * Gets the language of the keyboard, falling back to the default language on any error.
     *
     * @param context
     *         the application context
     * @return the language of the keyboard or the default language on any error
     */
    public static String getKeyboardLanguage(Context context) {
        if (context == null) {
            return getDefaultLanguage();
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();
        if (ims == null) {
            String defaultLanguage = getDefaultLanguage();
            Log.i(LocaleUtil.class.getSimpleName(), "Failed to get current input method subtype " +
                    "for determining the keyboard locale, falling back to default " + defaultLanguage);
            return defaultLanguage;
        }
        try {
            String localeStr = ims.getLocale().replace("_", "-");
            Locale locale = new Locale(localeStr);
            String validLocaleStr = makeTwoLetterCode(locale.getLanguage());
            if (validLocaleStr == null || validLocaleStr.isEmpty() || !validateLanguage(validLocaleStr)) {
                String defaultLanguage = getDefaultLanguage();
                Log.i(LocaleUtil.class.getSimpleName(), "Failed to determine keyboard locale, falling back " +
                        "to default " + defaultLanguage);
                return defaultLanguage;
            } else {
                return validLocaleStr;
            }
        } catch (Exception e) {
            String defaultLanguage = getDefaultLanguage();
            Log.i(LocaleUtil.class.getSimpleName(), "Failed to determine keyboard locale, falling back to " +
                    "default " + defaultLanguage, e);
            return defaultLanguage;
        }
    }

    /**
     * @return the language of the environment this app was started in
     */
    public static String getDefaultLanguage() {
        String defaultLanguage = makeTwoLetterCode(Locale.getDefault().getLanguage());
        if (!validateLanguage(defaultLanguage)) {
            Log.w(LocaleUtil.class.getSimpleName(), "Failed to get a valid default locale: " + Locale
                    .getDefault().getLanguage() + " -> " + defaultLanguage + ". Falling back to 'en'.");
            return "en";
        }
        return defaultLanguage;
    }

    /**
     * @param language
     *         the language string to validate
     * @return true if the specified language string is a valid ISO 639-1 code, false else
     */
    public static boolean validateLanguage(String language) {
        return Arrays.asList(Locale.getISOLanguages()).contains(language);
    }

    /**
     * @param language
     *         the language string to modify
     * @return a trimmed two-letter lower-case string or the original string on any error
     */
    private static String makeTwoLetterCode(String language) {
        try {
            return language.trim().substring(0, 2).toLowerCase();
        } catch (Exception e) {
            return language;
        }
    }

}
