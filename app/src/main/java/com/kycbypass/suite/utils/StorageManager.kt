package com.kycbypass.suite.utils

import android.content.Context
import android.net.Uri
import com.kycbypass.suite.ml.DocumentTemplate
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class StorageManager(private val context: Context) {
    
    private val facesDir = File(context.filesDir, "faces").apply { mkdirs() }
    private val videosDir = File(context.filesDir, "videos").apply { mkdirs() }
    private val documentsDir = File(context.filesDir, "documents").apply { mkdirs() }
    
    private val prefs = context.getSharedPreferences("kyc_prefs", Context.MODE_PRIVATE)
    
    fun saveFace(uri: Uri): String {
        val filename = "face_${UUID.randomUUID()}.png"
        val file = File(facesDir, filename)
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file.absolutePath
    }
    
    fun getSavedFaces(): List<String> {
        return facesDir.listFiles()?.map { it.absolutePath } ?: emptyList()
    }
    
    fun saveDocumentTemplate(template: DocumentTemplate) {
        val editor = prefs.edit()
        editor.putString("last_template_country", template.country)
        editor.putString("last_template_state", template.state)
        editor.putString("last_template_docType", template.docType)
        editor.putString("last_template_firstName", template.firstName)
        editor.putString("last_template_lastName", template.lastName)
        editor.putString("last_template_dob", template.dob)
        editor.putString("last_template_docNumber", template.docNumber)
        editor.putString("last_template_expiry", template.expiry)
        editor.putString("last_template_address", template.address)
        editor.apply()
    }
    
    fun getLastDocumentTemplate(): DocumentTemplate? {
        return DocumentTemplate(
            country = prefs.getString("last_template_country", "") ?: "",
            state = prefs.getString("last_template_state", "") ?: "",
            docType = prefs.getString("last_template_docType", "") ?: "",
            firstName = prefs.getString("last_template_firstName", "") ?: "",
            lastName = prefs.getString("last_template_lastName", "") ?: "",
            dob = prefs.getString("last_template_dob", "") ?: "",
            docNumber = prefs.getString("last_template_docNumber", "") ?: "",
            expiry = prefs.getString("last_template_expiry", "") ?: "",
            address = prefs.getString("last_template_address", "") ?: "",
            photoPath = prefs.getString("last_template_photo", null)
        ).takeIf { it.firstName.isNotEmpty() }
    }
    
    fun setAntiDetectionEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("anti_detection", enabled).apply()
    }
    
    fun isAntiDetectionEnabled(): Boolean {
        return prefs.getBoolean("anti_detection", false)
    }
    
    fun isCameraActive(): Boolean {
        return prefs.getBoolean("camera_active", false)
    }
    
    fun setCameraActive(active: Boolean) {
        prefs.edit().putBoolean("camera_active", active).apply()
    }
    
    fun isEngineReady(): Boolean {
        return prefs.getBoolean("engine_ready", false)
    }
    
    fun setEngineReady(ready: Boolean) {
        prefs.edit().putBoolean("engine_ready", ready).apply()
    }
    
    fun clearAllData() {
        facesDir.deleteRecursively()
        videosDir.deleteRecursively()
        documentsDir.deleteRecursively()
        facesDir.mkdirs()
        videosDir.mkdirs()
        documentsDir.mkdirs()
    }
}
