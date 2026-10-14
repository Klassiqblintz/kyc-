package com.kycbypass.suite.services

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import com.kycbypass.suite.R
import com.kycbypass.suite.utils.AppMonitor

class OverlayService : Service() {
    
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private var isOverlayVisible = false
    
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createOverlay()
    }
    
    private fun createOverlay() {
        overlayView = LayoutInflater.from(this)
            .inflate(R.layout.overlay_control, null)
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 0
            y = 200
        }
        
        // Setup controls
        val btnToggle = overlayView.findViewById<ImageButton>(R.id.btn_overlay_toggle)
        val tvStatus = overlayView.findViewById<TextView>(R.id.tv_overlay_status)
        
        btnToggle.setOnClickListener {
            isOverlayVisible = !isOverlayVisible
            overlayView.visibility = if (isOverlayVisible) View.VISIBLE else View.GONE
            tvStatus.text = if (isOverlayVisible) "Active" else "Inactive"
        }
        
        // Drag to move
        var startX = 0
        var startY = 0
        
        overlayView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x.toInt()
                    startY = event.y.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x -= event.x.toInt() - startX
                    params.y -= event.y.toInt() - startY
                    windowManager.updateViewLayout(overlayView, params)
                    startX = event.x.toInt()
                    startY = event.y.toInt()
                }
            }
            true
        }
        
        windowManager.addView(overlayView, params)
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        overlayView.visibility = View.VISIBLE
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        if (::overlayView.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }
}
