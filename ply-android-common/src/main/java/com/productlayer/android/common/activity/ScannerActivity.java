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

package com.productlayer.android.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.productlayer.android.common.R;
import com.productlayer.android.common.util.GTINUtil;
import com.productlayer.core.error.PLYHttpException;
import com.productlayer.core.logic.ProductLogic;
import com.productlayer.core.utils.GTINValidator;

import java.util.Arrays;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * The barcode scanner component to search for products in the ProductLayer database.
 *
 * Features the zxing library, instructions, camera selection, flash light, auto focus. Supported bar code
 * formats: UPC-A, UPC-E, EAN-13, EAN-8, RSS 14 (GS1 DataBar), GS1-128 (Code 128), ITF-14, GS1 DataMatrix
 */
public class ScannerActivity extends VerboseActivity implements ZXingScannerView.ResultHandler {

    public static final String RESULT_GTIN = "gtin";

    private static final List<BarcodeFormat> supportedFormats = Arrays.asList(BarcodeFormat.UPC_A,
            BarcodeFormat.UPC_E, BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.RSS_14,
            BarcodeFormat.CODE_128, BarcodeFormat.ITF, BarcodeFormat.DATA_MATRIX);

    private ZXingScannerView scannerView;

    // ACTIVITY LIFECYCLE - START //

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        scannerView = new ZXingScannerView(this);
        scannerView.setFormats(supportedFormats);
        scannerView.setAutoFocus(true);
        setContentView(scannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
        //scannerView.setFlash(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    // ACTIVITY LIFECYCLE - END //

    @Override
    public void handleResult(Result result) {
        BarcodeFormat format = result.getBarcodeFormat();
        String barcode = result.getText();
        Log.i(getClass().getSimpleName(), "Scanned barcode from " + format + " with raw value " + barcode);
        String gtin;
        if (format == BarcodeFormat.CODE_128) {
            gtin = GTINUtil.extractFromCode128(barcode);
        } else if (format == BarcodeFormat.DATA_MATRIX) {
            gtin = GTINUtil.extractFromDataMatrix(barcode);
        } else if (format == BarcodeFormat.UPC_E) {
            gtin = GTINUtil.extractFromUPCE(barcode);
        } else {
            gtin = barcode;
        }
        try {
            gtin = ProductLogic.createFull14DigitsGTIN(gtin);
        } catch (PLYHttpException e) {
            gtin = null;
        }
        if (!GTINValidator.isValidGTIN(gtin)) {
            Log.i(getClass().getSimpleName(), "GTIN " + gtin + " is not a valid GTIN - resuming camera");
            Snackbar.make(scannerView, R.string.not_a_globally_valid_barcode, Snackbar.LENGTH_LONG).show();
            scannerView.startCamera();
            //scannerView.setFlash(true);
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(RESULT_GTIN, gtin);
        setResult(AppCompatActivity.RESULT_OK, intent);
        finish();
    }
}
