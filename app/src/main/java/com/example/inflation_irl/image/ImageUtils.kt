package com.example.inflation_irl.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File

class ImageUtils {
    suspend fun createScaledImage(imagePath: String) : Bitmap {
        return withContext(IO) {
            val bmp = BitmapFactory.decodeFile(imagePath)
            val ratio = bmp.width.toDouble() / bmp.height
            createScaledBitmap(bmp, (800 * ratio).toInt(), 800, false)
        }
    }

    /**
     * Returns the File for a photo stored on disk given the fileName.
     * Creating the storage directory if it does not exist
     */
    fun getPhotoFile(context: Context, fileName: String): File {
        // Get safe storage directory for photos. Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("NewRecipeActivity", "failed to create directory")
        }
        return File(mediaStorageDir.path + File.separator + fileName)
    }
}