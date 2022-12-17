package com.example.inflation_irl.scanner

import android.graphics.Bitmap
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class BarCodeScanner {

    companion object {
        private const val BARCODE_NOT_FOUND = "Barcode not found."
        private const val BARCODE_SCAN_FAILED = "Barcode scan failed."
    }

    fun interface BarCodeHandler {
        fun onBarcodeRead(barCode: String, isBarCodeFound: Boolean)
    }


    suspend fun findBarcode(bitmap: Bitmap, barCodeHandler: BarCodeHandler) {
        return withContext(IO) {
            val scanner = BarcodeScanning.getClient()
            val image = InputImage.fromBitmap(bitmap, 0)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isEmpty()) {
                        barCodeHandler.onBarcodeRead(BARCODE_NOT_FOUND, false)
                    } else {
                        val barCode = barcodes[0].displayValue
                        barCode?.let { barCodeHandler.onBarcodeRead(barCode, true) }
                    }
                }
                .addOnFailureListener {
                    barCodeHandler.onBarcodeRead(BARCODE_SCAN_FAILED, false)
                }
                .addOnCompleteListener {
                    scanner.close()
                }
        }
    }
}