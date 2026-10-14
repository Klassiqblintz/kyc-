package com.kycbypass.suite

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kycbypass.suite.databinding.ActivitySettingsBinding
import com.kycbypass.suite.utils.PermissionsManager
import com.kycbypass.suite.utils.RootDetector
import com.kycbypass.suite.utils.StorageManager
import com.kycbypass.suite.utils.DeviceFingerprint

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var storageManager: StorageManager
    private lateinit var rootDetector: RootDetector
    private lateinit var deviceFingerprint: DeviceFingerprint
    
    private val requiredPermissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.SYSTEM_ALERT_WINDOW
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        permissionsManager = PermissionsManager(this)
        storageManager = StorageManager(this)
        rootDetector = RootDetector()
        deviceFingerprint = DeviceFingerprint(this)
        
        setupViews()
        checkPermissions()
        loadSettings()
        setupListeners()
    }
    
    private fun setupViews() {
        // Country spinner
        val countries = listOf(
            "United States", "United Kingdom", "Canada", "Australia",
            "Germany", "France", "Japan", "South Korea",
            "Brazil", "India", "China", "Singapore",
            "UAE", "Saudi Arabia", "Mexico", "Nigeria",
            "South Africa", "Netherlands", "Sweden", "Switzerland"
        )
        binding.spinnerCountry.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        binding.spinnerCountry.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                saveSetting("selected_country", countries[position])
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
        
        // Document type spinner
        val docTypes = listOf(
            "Driver License", "Passport", "National ID", "Residence Permit",
            "Voter ID", "Aadhaar Card", "PAN Card", "Social Security Card"
        )
        binding.spinnerDocType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, docTypes)
        binding.spinnerDocType.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                saveSetting("selected_doc_type", docTypes[position])
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
        
        // Target app spinner
        val targets = listOf(
            "Bank of America", "Chase", "Wells Fargo", "Citibank",
            "Cash App", "Venmo", "PayPal", "Zelle",
            "Binance", "Coinbase", "Kraken", "Robinhood",
            "Tinder", "Bumble", "Hinge", "Instagram"
        )
        binding.spinnerTargetApp.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, targets)
        binding.spinnerTargetApp.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                saveSetting("selected_target", targets[position])
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
        
        // Show root status
        binding.tvRootStatus.text = if (rootDetector.isDeviceRooted()) "Rooted: YES" else "Rooted: NO"
        binding.tvRootStatus.setTextColor(
            if (rootDetector.isDeviceRooted()) 
                ContextCompat.getColor(this, android.R.color.holo_red_dark) 
            else 
                ContextCompat.getColor(this, android.R.color.holo_green_dark)
        )
        
        // Show device fingerprint
        binding.tvDeviceFingerprint.text = deviceFingerprint.getDeviceFingerprint()
    }
    
    private fun checkPermissions() {
        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (missingPermissions.isNotEmpty()) {
            binding.tvPermissionsStatus.text = "Permissions: PENDING"
            binding.tvPermissionsStatus.setTextColor(
                ContextCompat.getColor(this, android.R.color.holo_orange_dark)
            )
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                PERMISSIONS_REQUEST_CODE
            )
        } else {
            binding.tvPermissionsStatus.text = "Permissions: GRANTED"
            binding.tvPermissionsStatus.setTextColor(
                ContextCompat.getColor(this, android.R.color.holo_green_dark)
            )
        }
    }
    
    private fun loadSettings() {
        binding.spinnerCountry.setSelection(countries.indexOf(storageManager.getSetting("selected_country", "United States")))
        binding.spinnerDocType.setSelection(docTypes.indexOf(storageManager.getSetting("selected_doc_type", "Driver License")))
        binding.spinnerTargetApp.setSelection(targets.indexOf(storageManager.getSetting("selected_target", "Bank of America")))
        
        binding.toggleAntiDetection.isChecked = storageManager.getSetting("anti_detection", "true") == "true"
        binding.toggleOverlay.isChecked = storageManager.getSetting("overlay_enabled", "true") == "true"
        binding.toggleVirtualCamera.isChecked = storageManager.getSetting("virtual_camera", "true") == "true"
    }
    
    private fun setupListeners() {
        binding.btnSaveSettings.setOnClickListener {
            saveAllSettings()
            Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnClearStorage.setOnClickListener {
            storageManager.clearAllData()
            Toast.makeText(this, "Storage cleared", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnExportData.setOnClickListener {
            val exported = storageManager.exportData()
            if (exported) {
                Toast.makeText(this, "Data exported to Downloads", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.toggleAntiDetection.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("anti_detection", isChecked.toString())
        }
        
        binding.toggleOverlay.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("overlay_enabled", isChecked.toString())
        }
        
        binding.toggleVirtualCamera.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("virtual_camera", isChecked.toString())
        }
    }
    
    private fun saveAllSettings() {
        val country = countries[binding.spinnerCountry.selectedItemPosition]
        val docType = docTypes[binding.spinnerDocType.selectedItemPosition]
        val target = targets[binding.spinnerTargetApp.selectedItemPosition]
        
        saveSetting("selected_country", country)
        saveSetting("selected_doc_type", docType)
        saveSetting("selected_target", target)
        saveSetting("anti_detection", binding.toggleAntiDetection.isChecked.toString())
        saveSetting("overlay_enabled", binding.toggleOverlay.isChecked.toString())
        saveSetting("virtual_camera", binding.toggleVirtualCamera.isChecked.toString())
    }
    
    private fun saveSetting(key: String, value: String) {
        storageManager.saveSetting(key, value)
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                binding.tvPermissionsStatus.text = "Permissions: GRANTED"
                binding.tvPermissionsStatus.setTextColor(
                    ContextCompat.getColor(this, android.R.color.holo_green_dark)
                )
            } else {
                binding.tvPermissionsStatus.text = "Permissions: DENIED"
                binding.tvPermissionsStatus.setTextColor(
                    ContextCompat.getColor(this, android.R.color.holo_red_dark)
                )
                Toast.makeText(this, "Some permissions are required", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 1001
        private val countries = listOf(
            "United States", "United Kingdom", "Canada", "Australia",
            "Germany", "France", "Japan", "South Korea",
            "Brazil", "India", "China", "Singapore",
            "UAE", "Saudi Arabia", "Mexico", "Nigeria",
            "South Africa", "Netherlands", "Sweden", "Switzerland"
        )
        private val docTypes = listOf(
            "Driver License", "Passport", "National ID", "Residence Permit",
            "Voter ID", "Aadhaar Card", "PAN Card", "Social Security Card"
        )
        private val targets = listOf(
            "Bank of America", "Chase", "Wells Fargo", "Citibank",
            "Cash App", "Venmo", "PayPal", "Zelle",
            "Binance", "Coinbase", "Kraken", "Robinhood",
            "Tinder", "Bumble", "Hinge", "Instagram"
        )
    }
}
