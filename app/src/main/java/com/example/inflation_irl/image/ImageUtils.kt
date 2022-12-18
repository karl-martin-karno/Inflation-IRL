package com.example.inflation_irl.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File

class ImageUtils {
    suspend fun createScaledImage(imagePath: String): Bitmap {
        return withContext(IO) {
            var bmp = BitmapFactory.decodeFile(imagePath)
            val ratio = bmp.width.toDouble() / bmp.height
            bmp = fixOrientation(bmp, imagePath)
            createScaledBitmap(bmp, (800 * ratio).toInt(), (800 * ratio).toInt(), false)
        }
    }

    private fun fixOrientation(bitmap: Bitmap, url: String): Bitmap {
        val exif = ExifInterface(url)

        return when (exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
    }
    /**
     * Returns the File for a photo stored on disk given the fileName.
     * Creating the storage directory if it does not exist
     */
    suspend fun getPhotoFile(context: Context, fileName: String): File {
        return withContext(IO) {
            // Get safe storage directory for photos. Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            val mediaStorageDir: File =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d("NewRecipeActivity", "failed to create directory")
            }
            File(mediaStorageDir.path + File.separator + fileName)
        }
    }
}