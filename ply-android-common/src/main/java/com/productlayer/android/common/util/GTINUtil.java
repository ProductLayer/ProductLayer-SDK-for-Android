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

import com.productlayer.core.utils.GTINValidator;

/**
 * Utility class extracting Global Trade Item Numbers (GTINs) from barcode structures.
 */
public class GTINUtil {

    /**
     * Extracts a GTIN from a GS1-128 (Code 128) formatted bar code.
     *
     * @param code
     *         the raw bar code value
     * @return the extracted GTIN or null if none found
     */
    public static String extractFromCode128(String code) {
        if (code.length() < 16) {
            return null;
        }
        if (code.startsWith("01") || code.startsWith("02")) {
            return code.substring(2, 16);
        }
        return null;
    }

    /**
     * Extracts a GTIN from a GS1 DataMatrix formatted bar code.
     *
     * @param code
     *         the raw bar code value
     * @return the extracted GTIN or null if none found
     */
    public static String extractFromDataMatrix(String code) {
        if (code.length() < 16) {
            return null;
        }
        if (code.startsWith("01")) {
            return code.substring(2, 16);
        }
        return null;
    }

    /**
     * Converts a UPC-E code to UPC-A.
     *
     * @param code
     *         the raw bar code value
     * @return the extracted UPC-A code or null on any error
     */
    public static String extractFromUPCE(String code) {
        int len = code.length();
        String trimmed;
        if (len == 6) {
            trimmed = code;
        } else if (len == 7) {
            trimmed = code.substring(0, len - 1);
        } else if (len == 8) {
            trimmed = code.substring(1, len - 1);
        } else {
            return null;
        }
        char c1 = trimmed.charAt(0);
        char c2 = trimmed.charAt(1);
        char c3 = trimmed.charAt(2);
        char c4 = trimmed.charAt(3);
        char c5 = trimmed.charAt(4);
        char c6 = trimmed.charAt(5);
        String manufacturer;
        String item;
        switch (c6) {
            case '0':
            case '1':
            case '2':
                manufacturer = c1 + c2 + c6 + "00";
                item = "00" + c3 + c4 + c5;
                break;
            case '3':
                manufacturer = c1 + c2 + c3 + "00";
                item = "000" + c4 + c5;
                break;
            case '4':
                manufacturer = c1 + c2 + c3 + c4 + "0";
                item = "0000" + c5;
                break;
            default:
                manufacturer = c1 + c2 + c3 + c4 + c5 + "";
                item = "0000" + c6;
        }
        String newCode = "0" + manufacturer + item;
        return newCode + GTINValidator.calcChecksum(newCode.toCharArray(), newCode.length());
    }

}
