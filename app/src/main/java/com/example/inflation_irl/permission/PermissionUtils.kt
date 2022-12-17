package com.example.inflation_irl.permission

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast

class PermissionUtils {

    fun interface ActionHandler {
        fun onPermissionGranted()
    }

    fun handleLocationPermissionsResult(
        context: Context,
        grantResults: IntArray,
        actionHandler: ActionHandler
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            actionHandler.onPermissionGranted()
        } else {
            Toast.makeText(context, "Location Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleCameraPermissionsResult(
        context: Context,
        grantResults: IntArray,
        actionHandler: ActionHandler
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            actionHandler.onPermissionGranted()
        } else {
            Toast.makeText(
                context,
                "Camera permission is required to use the camera.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}