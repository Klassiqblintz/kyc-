# KYC Bypass Suite - Setup Guide

## Prerequisites

1. **Android Studio** (Arctic Fox or later)
2. **JDK 17**
3. **Android SDK 34**
4. **NDK 25.1**

## Step 1: Import Project

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to `/workspace/New-Kyc`
4. Click "Open"
5. Wait for Gradle sync to complete

## Step 2: Download ML Models

### Face Swap Model
```bash
# Download from ONNX Model Zoo
wget -O app/src/main/assets/models/face_swap.onnx \
  "https://github.com/onnx/models/raw/main/validated/vision/body_analysis/face_evaluation/model/face-detection-esfd-10.onnx"
```

### Face Encoder Model
```bash
wget -O app/src/main/assets/models/face_encoder.onnx \
  "https://github.com/onnx/models/raw/main/validated/vision/body_analysis/face_evaluation/model/face-recognition-arcface-10.onnx"
```

### Tesseract Data
```bash
wget -O app/src/main/assets/tessdata/eng.traineddata \
  "https://github.com/tesseract-ocr/tessdata/raw/main/eng.traineddata"
```

## Step 3: Configure Build

Edit `app/build.gradle`:
```gradle
android {
    defaultConfig {
        applicationId "com.kycbypass.suite"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }
}
```

## Step 4: Build APK

```bash
cd /workspace/New-Kyc
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

## Step 5: Install on Device

### Via ADB
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Via USB
1. Enable USB Debugging on device
2. Connect device
3. Transfer APK
4. Install

## Step 6: Grant Permissions

On first launch, grant:
- Camera
- Storage
- Overlay (for injection)
- Phone (for IMEI spoofing)

## Step 7: Configure Virtual Camera

1. Launch app
2. Go to "Virtual Camera"
3. Select target app (e.g., "Cash App")
4. Upload face photo
5. Enable "Liveness Bypass"
6. Click "Start Virtual Camera"

## Testing Checklist

- [ ] Virtual camera works on unrooted device
- [ ] Virtual camera works on rooted device
- [ ] Document generation creates valid IDs
- [ ] MRZ parsing works correctly
- [ ] Overlay appears on target apps
- [ ] Anti-detection bypasses root detection
- [ ] Device fingerprint changes on each launch

## Troubleshooting

### ONNX Model Loading Fails
- Check model files exist in assets/models/
- Verify model format is ONNX
- Check ONNX Runtime version compatibility

### Camera Injection Fails
- Grant overlay permission
- Ensure foreground service permission
- Check virtual camera driver is installed

### Document Generation Fails
- Verify Tesseract data in assets/tessdata/
- Check photo format (PNG/JPEG)
- Ensure sufficient storage space

## Security Notes

1. **Root Detection**: App includes root bypass, but some apps may still detect
2. **Play Integrity**: May require additional bypass for Google-certified devices
3. **Network Monitoring**: Some apps use SSL pinning
4. **Legal**: Verify local regulations before use

## Support

For issues:
1. Check logs: `adb logcat | grep KYCBypass`
2. Verify all dependencies are installed
3. Test on clean Android installation

## License

Proprietary - Internal Use Only
