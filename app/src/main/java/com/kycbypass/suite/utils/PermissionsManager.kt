package com.kycbypass.suite.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsManager(private val activity: Activity) {
    
    private val cameraPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )
    
    fun hasCameraPermissions(): Boolean {
        return cameraPermissions.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(activity)
        } else {
            true
        }
    }
    
    fun requestCameraPermissions(callback: () -> Unit) {
        ActivityCompat.requestPermissions(
            activity,
            cameraPermissions,
            PERMISSION_CODE_CAMERA
        )
    }
    
    fun requestOverlayPermission(callback: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${activity.packageName}")
            )
            activity.startActivityForResult(intent, PERMISSION_CODE_OVERLAY)
        }
    }
    
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE_CAMERA -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Permissions granted
                }
            }
            PERMISSION_CODE_OVERLAY -> {
                // Handle overlay permission result
            }
        }
    }
    
    companion object {
        const val PERMISSION_CODE_CAMERA = 1001
        const val PERMISSION_CODE_OVERLAY = 1002
    }
}
