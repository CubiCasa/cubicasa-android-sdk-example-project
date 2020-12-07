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

Manual zipping `zipScan()` method expects the scan folder to contain the following files;
`arkitData.json`, `config.json` and `video.mp4`.
If any of the files doesn't exist, `zipScan()` returns `null`.

Here's an example directory structure where `zipScan()` method would successfully zip the scan folders
`ExampleStreet 123` and `AnotherStreet 10`:
```.
.
└── AllScansFolder
    ├── ExampleStreet 123
    │   ├── arkitData.json
    │   ├── config.json
    │   └── video.mp4
    └── AnotherStreet 10
        ├── arkitData.json
        ├── config.json
        └── video.mp4
```

## Status codes

### 0, "Device turned to landscape orientation."
Received when the device is in landscape orientation and `orientationWarning (ImageView)` 
is set to invisible. 
This is usually the first status code received when the device is turned to landscape orientation
in order to start recording. 
When the device is in landscape orientation for the first time `orientationWarning (ImageView)`'s 
image resource is set to `rotate180Image`.

### 1, "Started recording."
Received when the `buttonRecord` is pressed for the first time and scan is started. 

### 2, "Finished recording."
Received when the `buttonRecord` is pressed for the second time and scan has enough data. 
The saving of the scan files begins after this.

### 3, "Finished recording - Not enough data."
Received when the `buttonRecord` is pressed for the second time and scan does not have enough data. 
The scan files will be deleted (code 15) and CubiCapture will be finished after this (code 5).

### 4, "Saving of scan files finished. (Beginning to zip files.)"
Received when the saving of the scan files is finished. Receiving this code means that 
the scan has data and scan files are succesfully saved without errors. 
If `autoZipping` is enabled, zipping will start after this.

### 5, "CubiCapture is finished. You can now finish your scanning Activity."
You will always receive this code after a scan. To determine if the scan was 
successful or not you have to handle the codes received before this code, 
e.g. codes 4 and 7.

For example; When a scan is successful, before code 5 you will receive a code 4 
for successful saving of scan data, and then a code 7 for successful zipping of 
the scan files (if you have 'autoZipping' enabled). 
When a scan is not successful you will not receive code 4, but instead you will 
receive an error code (e.g. code 3, "Finished recording - Not enough data.").

### 6, "Scan folder: <folder-path>"
Received when the saving of the scan files is finished. 
The description will contain a path to the scan folder. 
To receive the scan folder as a `File` use the `CubiEventListener`'s 
`getFile(code: Int, file: File)` method where code 1 receives the scan folder.

### 7, "Zipping is done. Zip file: <zip-file-path>"
Received when the zipping of the scan files is finished. 
The description will contain a path to the zip file. 
To receive the zip file as a `File` use the `CubiEventListener`'s 
`getFile(code: Int, file: File)` method where code 2 receives the zip file.

### 8, "ARCore TrackingFailureReason: INSUFFICIENT_LIGHT"
Received when ARCore motion tracking is lost due to poor lighting conditions. 
`statusText (TextView)`'s `text` is set to `insufficientErrorText (CharSequence)`.

### 9, "ARCore TrackingFailureReason: EXCESSIVE_MOTION"
Received when ARCore motion tracking is lost due to excessive motion. 
`statusText (TextView)`'s `text` is set to `excessiveMotionErrorText (CharSequence)`.

### 10, "ARCore TrackingFailureReason: INSUFFICIENT_FEATURES"
Received when ARCore motion tracking is lost due to insufficient visual features. 
`statusText (TextView)`'s `text` is set to `insufficientErrorText (CharSequence)`.

### 11, "ARCore TrackingState is TRACKING."
Received when ARCore is tracking again. Any error text from `statusText (TextView)` is removed.

### 12, "MediaFormat and MediaCodec failed to be configured and started."
Received if MediaFormat and MediaCodec fails to be configured and started 
when `buttonRecord` is pressed for the first time. You will receive code 5 after this.

### 13, "Scan drifted! Position changed by over 10 meters during 2 second interval."
Received if the position of the device has changed by over 10 meters during a 2 second interval. 
The scan files will be deleted (code 15) and CubiCapture will be finished after this (code 5).

### 15, "Error shutdown. Deleting scan folder: <folder-path>
Received when the scan is not successful. 
The scan files are deleted and CubiCapture will be finished after this (code 5).

### 16, "Portrait to landscape guidance has to be dismissed first in order to start recording."
Received when `buttonRecord` is pressed for the first time and `orientationWarning (ImageView)` 
is still visible.

### 17, "Device is in reverse-landscape orientation."
Received when the device is in reverse-landscape orientation and if the device has been in 
landscape orientation at least once. `orientationWarning (ImageView)` is set to visible.

### 18, "Playing error sound."
Received when an error sound is played and ARCore motion tracking is lost (codes 8, 9 and 10). 
Only received if the ARCore was tracking before the tracking is lost to avoid playing the error sound 
multiple times in a row. 
(The ARCore `TrackingFailureReason` might change (between codes 8, 9 and 10) during a short period of time).

### 19, "Back button pressed twice. You can now finish your scanning Activity."
Received when the CubiCapture's back button is pressed twice. You should call `finish()`.

### 20, "Name of the scan output folder has to be set first. Set .scanFolderName"
Received when `buttonRecord` is pressed for the first time and `scanFolderName` has not been set.

### 21, "Scanning floor."
Received when the pitch of the camera has been too low for a certain amount of time 
and `floorWarning` is set to visible. 
Only received if there's no `orientationWarning` or `sidewaysWarning` visible.

### 22, "Scanning ceiling."
Received when the pitch of the camera has been too high for a certain amount of time 
and `ceilingWarning` is set to visible. 
Only received if there's no `orientationWarning` or `sidewaysWarning` visible.

### 23, "Not scanning ceiling or floor anymore."
Received when the pitch of the camera is valid again and `ceilingWarning` or `floorWarning` is set to invisible.

### 24, "Pitch of the device unknown because ARCore TrackingState is PAUSED."
Received when ARCore's `TrackingState` is `PAUSED` and pitch of the camera cannot be calculated. 
Sets `ceilingWarning` or `floorWarning` to invisible.

### 25, "Walking sideways. Displaying turn left warning."
Received when the user is walking sideways to the left while the camera is pointing forward. 
`sidewaysWarning` is set to visible and its image resource is set to `turnLeftImage`. 
Only received if the `orientationWarning` is set to invisible.

### 26, "Walking sideways. Displaying turn right warning."
Received when the user is walking sideways to the right while the camera is pointing forward. 
`sidewaysWarning` is set to visible and its image resource is set to `turnRightImage`. 
Only received if the `orientationWarning` is set to invisible.

### 27, "Not walking sideways anymore."
Received when the user is not walking sideways anymore and the `sidewaysWarning` is set to invisible. 
The `sidewaysWarning` is always displayed for at least a certain amount of time to avoid quick flashes 
of guidance images.

### 28, "ARCore was unable to start tracking during the first five seconds."
Received if the ARCore is unable to start tracking during the first five seconds. 
The scan files will be deleted (code 15) and CubiCapture will be finished after this (code 5).

### 50, "MediaMuxer.writeSampleData Exception: <exception>"
Received if the video encoder fails to write an encoded sample into the muxer.

### 51, "Zipping failed! You can try zipping again with .zipScan(<scanFolderPath>)"
Received if the automatic zipping fails.

### 52, "Exception on the OpenGL thread: <Throwable>"
Received if there's an exception on the OpenGL thread.

### 53, "Failed to read an asset file: <exception>"
Received if camera video preview surface fails to be initialized.

### 54, "Writing of scan data failed: <exception>"
Received if the writing of the scan data fails. 
The scan files will be deleted (code 15) and CubiCapture will be finished after this (code 5).

### 55, "InputBuffer IllegalStateException: <exception>"
Received if the input buffer is not in Executing state.

### 56, "MediaCodec.stop() exception: <exception>"
Received if the finishing of the encode session fails.

### 57, "MediaMuxer.stop() exception: <exception>"
Received if the stopping of the muxer fails.

### 58, "MediaCodec.releaseOutputBuffer() exception: <exception>"
Received if the releasing of the output buffer fails.
