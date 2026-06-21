package com.manoj.financetracker.presentation.payment

import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class ScanContractCustom : ScanContract()

fun createScanOptions(): ScanOptions {

    return ScanOptions().apply {

        setDesiredBarcodeFormats(
            ScanOptions.QR_CODE
        )

        setPrompt(
            "Scan UPI QR"
        )

        setBeepEnabled(true)

        setOrientationLocked(true)

        captureActivity =
            CaptureActivity::class.java
    }
}