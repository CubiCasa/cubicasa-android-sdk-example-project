Example project using the CubiCapture 2.0.0 library module for Android
======================
Copyright 2020 CubiCasa

This project provides an example implementation and use of the CubiCapture 2.0.0 library module.


## Documentation

To learn more about the functionalities, and for instructions on how to add the library module to your app 
see the [CubiCapture Android Library](//www.cubi.casa/developers/cubicasa-android-sdk) documentation.


## Release Notes

- Optimized image converting
- Fixed camera's video preview delay
- Changes to the status updates
- New status updates
- Added default warnings and graphics provided by CubiCasa
- Added an ability to change graphics and warning texts
- Added an ability to disable or enable scan timer and back button
- Added an ability to change all scans -folder's name
- Added a `setWarningSound()` method to change warning sound
- Added a `setNewView()` method to replace CubiCapture `View` with your own `View`
- Added a `setAutoZippingEnabled()` method to enable/disable auto zipping
- Added a `zipScan()` method to zip your scan data at any time
- Added a `onWindowFocusChanged()` method to set up the Android full screen mode
- Fixed a bug where devices with Android 11 stalled when aborting a started scan


## Implementation

Start by adding the CubiCapture library module to your project:

`File` -> `New` -> `New Module` -> `Import .JAR/.AAR Package` -> Locate to `"cubicapture-release-2.0.0.aar"` file and choose it -> `Finish`

Add the following lines to the app level `build.gradle` inside the `dependencies` branch:
```Groovy
implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
implementation project(":cubicapture-release-2.0.0")
implementation 'com.google.ar:core:1.19.0'
implementation 'com.google.code.gson:gson:2.8.6'
implementation 'com.jaredrummler:android-device-names:2.0.0'
```

Add the following lines to the app level `build.gradle` inside the `android` branch:
```Groovy
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```

Add CubiCapture fragment to your projects scanning layout .xml file
```xml
<fragment
        android:id="@+id/cubiFragment"
        android:name="cubi.casa.cubicapture.CubiCapture"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

To allow your scanning Activity to consume more RAM, to lock screen orientation to landscape and to prevent multiple `onCreate()` calls add the following code to your `AndroidManifest` file inside your scanning Activity's activity tag - Example:
```xml
<activity android:name=".ExampleActivity"
          android:largeHeap="true"
          android:screenOrientation="landscape"
          android:configChanges="orientation|screenSize">
```

Create lateinit variable for CubiCapture to your scanning Activity:
```Kotlin
private lateinit var cubiCapture: CubiCapture
```

Initialize your CubiCapture lateinit variable in `onCreate()`:
```Kotlin
cubiCapture = supportFragmentManager.findFragmentById(R.id.cubiFragment) as CubiCapture
```

Implement CubiEventListener interface to your scanning Activity - Example:
```Kotlin
class ExampleActivity : AppCompatActivity(), CubiCapture.CubiEventListener
```

Register CubiEventListener's interface callback in `onCreate()`:
```Kotlin
cubiCapture.registerCallback(this)
```

Add the following function to receive status updates from `CubiEventListener`:
```Kotlin
override fun getStatus(code: Int, description: String) { }
```

Add the following function to receive scan folder and zip file as `File` from `CubiEventListener`:
```Kotlin
override fun getFile(code: Int, file: File) { }
```

Before scanning with CubiCapture you first have to set folder name for the scan files:
```Kotlin
cubiCapture.scanFolderName = "exampleFolderName"
```

Before starting the scan you can add order information:
```Kotlin
cubiCapture.setOrderInfo(
    "ExampleStreet", //street
    "10", // number
    "ExampleSuite", // suite
    "ExampleCity", // city
    "ExampleState", // state
    "ExampleCountry", // country
    "12345" // postalCode
)
```

Add the following functions and calls to your scanning `Activity` to allow CubiCapture to handle its processes correctly when `Activity`'s lifecycle state changes:
```Kotlin
override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    cubiCapture.onWindowFocusChanged(hasFocus, this)
}

override fun onResume() {
    super.onResume()
    cubiCapture.resume()
}

override fun onPause() {
    super.onPause()
    cubiCapture.pause()
}

override fun onStop() {
    super.onStop()
    cubiCapture.stop()
}

// Make sure that onDestroy() is called after scanning is finished.
// For example call 'finish()' every time you're changing Activities.
override fun onDestroy() {
    super.onDestroy()
    cubiCapture.destroy()
}
```

You should also prevent Navigation bar back presses during saving:
```Kotlin
override fun onBackPressed() {
    // Navigation bar's back button press is disabled during saving.
    if (!saving) {
        super.onBackPressed()
    }
    // The saving variable is set to true when getStatus() receives code 2.
}
```

To keep the screen turned on and in landscape orientation add the following lines below `setContentView()`v in `onCreate()`:
```Kotlin
window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
```

## UI settings

To set the visibility of scan timer call:
```Kotlin
cubiCapture.setTimerEnabled(false) // Visible (true) by default
```

To set the visibility of CubiCapture's back button call:
```Kotlin
cubiCapture.setBackButtonEnabled(false) // Visible (true) by default
```

To change CubiCapture's ARCore tracking error texts:
```Kotlin
cubiCapture.excessiveMotionErrorText = "Excessive motion!"
cubiCapture.insufficientErrorText = "Insufficient visual features or poor lighting!"
```

To change the warning sound call:
```Kotlin
cubiCapture.setWarningSound(R.raw.new_warning_sound)
```

The following CubiCapture `View`s are customizable:
```Kotlin
buttonRecord: ImageView
buttonRecordHint: ImageView
statusBorder: ImageView
sidewaysWarning: ImageView
floorWarning: ImageView
ceilingWarning: ImageView
orientationWarning: ImageView
statusText: TextView
```

To replace a CubiCapture's `View` with your own `View` (example):
```Kotlin
val newView: ImageView = findViewById(R.id.newRecordingBtn)
cubiCapture.setNewView(cubiCapture.buttonRecord, newView)
```

To change an image resource of a CubiCapture `View` call (example):
```Kotlin
cubiCapture.buttonRecord.setImageResource(R.drawable.new_not_recording)
```

The following image resources might be set to certain views during the scan:
```Kotlin
recordingImage        // Set to buttonRecord: ImageView
rotate180Image        // Set to orientationWarning: ImageView
trackingStatusBorders // Set to statusBorder: ImageView
failureStatusBorders  // Set to statusBorder: ImageView
turnLeftImage         // Set to sidewaysWarning: ImageView
turnRightImage        // Set to sidewaysWarning: ImageView
```

For example when the user clicks `buttonRecord` the image resource of the view is set to `recordingImage`. In this case you can change the image resource like this (example):
```Kotlin
cubiCapture.recordingImage = R.drawable.new_recording
```

## Automatic and manual zipping

To disable the automatic zipping after a scan call:
```Kotlin
cubiCapture.setAutoZippingEnabled(false) // true (auto zips) by default
```

Zipping scan folder if automatic zipping is disabled. This can be called after scan files are saved successfully. This returns the Zip file if it's is successful or null if zipping failed.
```Kotlin
val zipFile = cubiCapture.zipScan(scanFolderPath) // Pass scan folder path as String
```