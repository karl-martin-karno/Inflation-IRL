package com.example.inflation_irl.scanner

import android.graphics.*
import android.widget.ImageView
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


    suspend fun findBarcode(bitmap: Bitmap, imageView: ImageView,  barCodeHandler: BarCodeHandler) {
        return withContext(IO) {
            val scanner = BarcodeScanning.getClient()
            val image = InputImage.fromBitmap(bitmap, 0)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isEmpty()) {
                        imageView.setImageBitmap(bitmap)
                        barCodeHandler.onBarcodeRead(BARCODE_NOT_FOUND, false)
                    } else {
                        val barCode = barcodes[0].displayValue
                        val rect: Rect? = barcodes[0].boundingBox

                        drawRectOnBitmap(imageView, bitmap, rect)
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

    private fun drawRectOnBitmap(imageView: ImageView, bitmap: Bitmap, rect: Rect?) {
        imageView.setImageBitmap(bitmap)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        rect?.let { canvas.drawRect(it, paint) }
    }
}