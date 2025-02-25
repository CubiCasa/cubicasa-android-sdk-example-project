# Table of Contents

* [Table of Contents](#table-of-contents)
* [Example Project](#example-project)
* [CubiCapture Library Module](#cubicapture-library-module)
  * [Updating to CubiCapture 3.2.2](#updating-to-cubicapture-322)
  * [Release Notes](#release-notes)
  * [Glossary](#glossary)
  * [Implementation](#implementation)
    * [Setting Up](#setting-up)
    * [True North Detection](#true-north-detection)
    * [Get Available Storage Space Estimation](#get-available-storage-space-estimation)
      * [Pre-Scan Storage Estimation](#pre-scan-storage-estimation)
      * [Remaining Storage Estimation During Scan](#remaining-storage-estimation-during-scan)
    * [Scan Playback](#scan-playback)
    * [Photo Capturing](#photo-capturing)
    * [Safe Mode](#safe-mode)
    * [Warning Sound](#warning-sound)
    * [Automatic and Manual Zipping](#automatic-and-manual-zipping)
      * [Enable or Disable Automatic Zipping](#enable-or-disable-automatic-zipping)
      * [Manual Zipping](#manual-zipping)
      * [Directory Structure for Successful Zipping](#directory-structure-for-successful-zipping)
  * [UI Settings](#ui-settings)
    * [General](#general)
    * [Colors and Alphas](#colors-and-alphas)
    * [Dimensions](#dimensions)
    * [Texts](#texts)
    * [Drawable Graphics](#drawable-graphics)
  * [Torch (Flashlight) Support](#torch-flashlight-support)
  * [About Warning Priorities](#about-warning-priorities)
  * [Status Codes](#status-codes)
  * [Archived Update Guides](#archived-update-guides)
  * [Archived Release Notes](#archived-release-notes)

# Example Project

This project provides an example implementation and use of the CubiCapture library module.
From this project you can get the basic idea of how to implement the scanning and scan playback with
CubiCapture to your app.

For your app the next step would be to upload the scan to your server and
use [CubiCasa Conversion API](https://cubicasaconversionapi.docs.apiary.io/#).

# CubiCapture Library Module

CubiCapture library module provides a scanning `Fragment` which can be used to scan a floor plan
with an Android device. The scanning `Fragment` saves scan files into a zip file, which your app can
upload to the CubiCasa back-end for processing. CubiCapture also provides a scan playback `Fragment`
which can be used to review the scan and to see any warnings that were shown during the scan, as well
as any room labels that were added.

## Updating to CubiCapture 3.2.2

- Update your app to use the CubiCapture 3.2.2 library module
- Update the app level `build.gradle` dependencies (see [Implementation](#implementation))
- Update the implementations of `CubiEventListener` and `TrueNorth` to use their new package
  location: `cubi.casa.cubicapture.utils.*`
- See the changes to the UI, including new scan guides/warnings, the new
  "Lower the device" warning, and the new setting to configure the capture photo button position
  (`isCapturePhotoButtonOnRight: Boolean`). Refer to [UI settings](#ui-settings) if you want to
  customize the colors, drawables, dimensions, or strings, and update your resource values accordingly
- See [Release Notes](#release-notes) for new, changed, or removed methods and status codes

**Note! If you've previously implemented version 3.1.1 you've probably done the next steps already:**
- If you used speech recognition, the `RECORD_AUDIO` and `INTERNET` permissions, and
  `android.speech.RecognitionService` can be removed from the `AndroidManifest.xml`
- Update your scanning `Activity` to use portrait orientation. Add `android:screenOrientation="portrait"`
  to the `activity` tag of the scanning `Activity` in the `AndroidManifest.xml`, and add
  `requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT` to the `onCreate()` method of
  the scanning `Activity`
- See the changes to the UI, including new scan guides/warnings, the new vertical tilt warning,
  and the end scan confirmation dialog. Refer to [UI settings](#ui-settings) if you want to customize
  the colors, drawables, dimensions, or strings, and update your resource values accordingly.
- Remove `window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)` and
  `CubiCapture.registerCallback(activity: Activity)` as these are now handled by the CubiCapture SDK
- Check if you want to enable the [photo capturing](#photo-capturing) option
- See [Release Notes](#release-notes) for CubiCapture methods that have been changed to properties,
  renamed variables, removed views, methods, and properties, and new, changed, and removed status codes.
  Update your code accordingly

For older update guides, see [Archived Update Guides](#archived-update-guides)

## Release Notes

**3.2.2:**
- Revised UI and warnings (see [UI settings](#ui-settings))
- Added [Torch (Flashlight) Support](#torch-flashlight-support)
- Added a "Lower the device" warning
- Added a new setting for configuring the capture photo button position
  (`isCapturePhotoButtonOnRight: Boolean`)
- The previously available `getAvailableStorageMinutes(File)` method has been removed and replaced with:
  - `estimatePreScanStorageMinutes(File)` for pre-scan storage estimation
  - `estimateRemainingStorageMinutes()` for estimating storage during a scan
- The previously available `zipScan(File)` method has been removed and replaced with the new
  `ScanZipper.zip(...)` method. See [Manual Zipping](#manual-zipping) for details
- New status codes:
  - [22, "Tilting too far up."](#22-tilting-too-far-up)
  - [113, "Failed to write photo log file"](#113-failed-to-write-photo-log-file-stack-trace)
  - [140, "Error in video encoding"](#140-error-in-video-encoding-stack-trace)
  - [141, "Failed to initialize the encoder"](#141-failed-to-initialize-the-encoder-stack-trace)
  - [142, "Encoding timed out due to frame mismatch"](#142-encoding-timed-out-due-to-frame-mismatch-stack-trace)
- Replaced status code 56 with a new status code:
  [56, "Failed to write config file"](#56-failed-to-write-config-file-stack-trace)
- Changes to the descriptions of status codes: 6, 7, 15, 19, 51, 54, 56, 90, 91, 93, 103, and 106
- Removed status codes: 12, 50, 55, 57, 58, 60, 61, 62, and 64
- Performance improvements
- Various fixes and improvements
- Updated CubiCapture's `targetSdkVersion` to API level `35`
- Updated dependencies (see [Implementation](#implementation))

**3.1.3:**
- Performance improvements
- Updated CubiCapture's `targetSdkVersion` to API level `34`
- Updated dependencies (see [Implementation](#implementation) below)

**3.1.1:**
- Scanning orientation is now portrait
- Added [photo capturing](#photo-capturing) option
- New UI, scan guides and end scan confirmation dialog
- Added vertical tilt warning
- Added camera permission handling
- Dropped speech recognition and depth capturing
- Aborting scan if the device is in the wrong orientation for too long
- Improved drift detection: Scan now aborts if device position shows unnatural "sliding" behavior,
  while "jumps" do not cause the scan to abort
- CubiCapture now adds flags to keep the screen on
- Removed `registerCallback(activity: Activity)`. Callback is now registered automatically
- `setTimerEnabled(enabled: Boolean)` is now `timerEnabled: Boolean`,
  `setBackButtonEnabled(enabled: Boolean)` is now `backButtonEnabled: Boolean`,
  `setRecordButtonEnabled(enabled: Boolean)` is now `recordButtonEnabled: Boolean`,
  `setWarningSound(resId: Int)` is now `trackingWarningSoundResId: Int`,
  `setAutoZippingEnabled(enabled: Boolean)` is now `autoZippingEnabled: Boolean`, and
  `CubiEventListener` method `getPropertyType(): PropertyType` is now `propertyType: PropertyType`
- Removed reminder views, hint labels, `matchHintLabelWidth: Boolean`,
  `requestLocationServices: Boolean`, `TrueNorth.ENABLED_AND_REQUEST`, `setNewView()`, and
  `setOrderInfo()`
- `scanFolderName` and `allScansFolder` renamed as `scanDirectoryName` and `allScansDirectory`,
  respectively
- Updated dependencies (see [Implementation](#implementation) below). Removed
  `com.facebook.shimmer:shimmer` dependency
- New status codes [14, "The device was in the wrong orientation for too long."](#14-the-device-was-in-the-wrong-orientation-for-too-long),
  [116, "Device position jumped in an unnatural manner."](#116-device-position-jumped-in-an-unnatural-manner),
  and CAMERA permission related codes [120-124](#120-camera-permission-is-not-granted)
- Changes to status codes 0, 13, 16, 17, 18, 21, and 23
- Removed status codes 20, 22, 31-49, 59, 95-98, and 103

For older release notes, see [Archived Release Notes](#archived-release-notes)

## Glossary

Term | Description
-----|------------
Scan | The process of capturing the surroundings in indoor space using the phone's camera.
ARCore | Google's Augmented Reality library that is used in CubiCapture library for scanning.
Tracking | The process of aligning the device to it's surroundings properly. We use ARCore to track where the phone is relative to the world around it.
Sideways walk | An error which occurs during a scan when the user walks sideways. Walking sideways makes it hard to track the position of the device and can affect the quality of the scan.

## Implementation

This implementation was made with Android Studio Ladybug Feature Drop | 2024.2.2
using Gradle plugin version 8.8.1.

#### Setting Up

Start by [downloading the Android library module](https://sdk-files.s3.us-east-2.amazonaws.com/android/cubicapture-release-3.2.2.aar).

Add the CubiCapture library module to your project:
1. Place the `cubicapture-release-3.2.2.aar` file to your project's `app/libs/` folder.
2. In Android Studio navigate to: `File` -> `Project Structure` -> `Dependencies` -> `app` ->
In the `Declared Dependencies` tab, click `+` and select `JAR/AAR Dependency`.
3. In the `JAR/AAR Dependency` dialog, enter the path as `libs/cubicapture-release-3.2.2.aar` and
select `implementation` as configuration. -> Press `OK`, `Apply` and `OK` .
4. Check your app's `build.gradle` file to confirm a that in contains the following declaration:
`implementation files('libs/cubicapture-release-3.2.2.aar')`.

Add the following lines to the app level `build.gradle` inside the `dependencies` branch:
```Groovy
implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
implementation files('libs/cubicapture-release-3.2.2.aar')
implementation 'com.google.ar:core:1.47.0'
implementation 'com.google.code.gson:gson:2.11.0'
implementation 'com.jaredrummler:android-device-names:2.1.1'

// Implement the following if 'CubiCapture.trueNorth' is set to 'ENABLED':
implementation 'com.google.android.gms:play-services-location:21.3.0'

// Implement the following if using 'ScanPlayback':
implementation 'androidx.recyclerview:recyclerview:1.4.0'
implementation 'androidx.media3:media3-exoplayer:1.5.1'
```

Add CubiCapture fragment to your projects scanning layout .xml file:
```xml
<androidx.fragment.app.FragmentContainerView
    android:id="@+id/cubiFragment"
    android:name="cubi.casa.cubicapture.CubiCapture"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

To allow your scanning `Activity` to consume more RAM, to lock screen orientation to portrait
and to prevent multiple `onCreate()` calls add the following code to your `AndroidManifest` file
inside your scanning `Activity`'s `activity` tag. Example:
```xml
<activity
    android:name=".ExampleActivity"
    android:largeHeap="true"
    android:screenOrientation="portrait"
    android:configChanges="orientation|screenSize">
```

Declare a `lateinit` variable for CubiCapture in your scanning `Activity`:
```Kotlin
private lateinit var cubiCapture: CubiCapture
```

Initialize the CubiCapture fragment in `onCreate()`:
```Kotlin
cubiCapture = supportFragmentManager.findFragmentById(R.id.cubiFragment) as CubiCapture
```

Implement `CubiEventListener` interface in your scanning `Activity`. Example:
```Kotlin
class ExampleActivity : AppCompatActivity(), CubiEventListener
```

Add the following method to receive status updates from `CubiEventListener`:
```Kotlin
override fun onStatus(code: Int, description: String) { }
```

Add the following method to receive the scan directory and the zip file as `File` from
`CubiEventListener`:
```Kotlin
override fun onFile(code: Int, file: File) { }
```
Where `code` `1` is the scan directory and `code` `2` is the scan zip file.

Set the directory where all scan directories will be stored. Example:
```Kotlin
cubiCapture.allScansDirectory = getExternalFilesDir(null) ?: filesDir
```

Set the scan output directory name. Example:
```Kotlin
cubiCapture.scanDirectoryName = UUID.randomUUID().toString()
```

Set the property type. Example:
```Kotlin
val selectedPropertyType: PropertyType = // ...
cubiCapture.propertyType = selectedPropertyType
```
Possible values for the `PropertyType` are `SINGLE_UNIT_RESIDENTIAL`, `TOWNHOUSE`, `APARTMENT`, and `OTHER`.

Add your app's version information to the scan data (optional). Example:
```Kotlin
cubiCapture.appVersion = BuildConfig.VERSION_NAME // e.g. String "1.2.3"
cubiCapture.appBuild = BuildConfig.VERSION_CODE // e.g. Int 25
```

Override the `Activity#onWindowFocusChanged(hasFocus: Boolean)` method to call
`CubiCapture#onWindowFocusChanged(hasFocus: Boolean, activity: Activity)` to allow CubiCapture
to handle its internal processes accordingly based on the window focus change:
```Kotlin
override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    cubiCapture.onWindowFocusChanged(hasFocus, this)
}
```

You should also prevent Navigation bar back presses during saving. Example:
```Kotlin
// Handle back press: Move to background if saving; otherwise, finish activity.
onBackPressedDispatcher.addOnClickListener(this) {
    // The saving variable is set to true when onStatus() receives code 2.
    if (saving) {
        moveTaskToBack(true)
    } else {
        finish()
    }
}
```

You should check that the device has ARCore installed and that it's up-to-date before starting the
scanning activity.

Scanning requires the `CAMERA` permission to be granted. The permission is already declared in the
library's manifest. Upon start, CubiCapture checks whether the permission is granted and shows a
missing permission view if it is not.

If you want to declare your app as "AR Required", add the following entry to your
`AndroidManifest.xml` file inside the `application` tag. This ensures that
the app is only visible to devices that support ARCore:
```xml
<!-- "AR Required" app, requires "Google Play Services for AR" (ARCore) to be installed -->
<meta-data android:name="com.google.ar.core" android:value="required" />
```

To avoid memory leaks, always remember to call `finish()` when your activity is done and should be closed.
You should call `finish()` when you receive status code 5 or 19.

#### True North Detection

True north detection can be used to enable the CubiCapture to capture the heading relative to the
"true north" to the scan. This information can then be used to add the information the floor plan
(e.g. compass). In order to the true north data to be collected, the Location Services must be
turned on and the location permission must be granted. No position data is collected by the CubiCapture.

True north detection `trueNorth` has the following two settings:

Name | Description
-----|------------
`TrueNorth.DISABLED`| True north detection disabled
`TrueNorth.ENABLED` | True north detection enabled

Example, where we enable the true north detection:
```Kotlin
cubiCapture.trueNorth = TrueNorth.ENABLED
```

`trueNorth` is set to `TrueNorth.ENABLED` by default.

If you are going to use true north detection you need to declare the `ACCESS_COARSE_LOCATION` and
`ACCESS_FINE_LOCATION` permissions in your app's manifest file, and request the `ACCESS_FINE_LOCATION`
permission before the scan. You also need to implement the Google Play services' location library
by adding it to the app level `build.gradle` dependencies (see the `build.gradle` implementation
in the start of [Implementation](#implementation) above).

#### Get Available Storage Space Estimation

`CubiCapture` provides two functions to estimate the scan duration that can be stored on the
device based on the available storage space. These functions help understand storage availability
either before starting a scan or during an ongoing scan.

##### Pre-Scan Storage Estimation

The `estimatePreScanStorageMinutes(File)` method estimates the maximum scan duration (in minutes)
that can be stored on the device before starting a scan.

This method is useful for warning the user if storage space is insufficient before
initiating the scanning process.

Example:
```Kotlin
val allScansDirectory = getExternalFilesDir(null) ?: filesDir
val estimatedMinutes: Int = CubiCapture.estimatePreScanStorageMinutes(allScansDirectory)

if (estimatedMinutes <= 60) {
    showLowStorageWarning(estimatedMinutes)
}
```

##### Remaining Storage Estimation During Scan

The `estimateRemainingStorageMinutes()` method estimates the remaining scan duration (in minutes)
during an ongoing scan. Ensure that `allScansDirectory` is initialized before using this method.

Example:
```Kotlin
val remainingMinutes: Int = cubiCapture.estimateRemainingStorageMinutes()
```
Note: Calling `estimateRemainingStorageMinutes()` without initializing the `allScansDirectory`
will throw an `IllegalStateException`.

#### Scan Playback

Scan playback is a `Fragment` which can be used to review the scan and to see any warnings that were
shown during the scan, as well as any room labels that were added.
The `Fragment` has a media control panel that can be used to play/pause, jump between scan events
and video frames one by one. All the scan events can be found in the Events list, which can be used
to easily jump between the events. The active events are displayed in the area above the Events
list. The video playback speed can be changed from the top bar which appears when the video is in
paused state.

To implement the ScanPlayback, first add the required dependencies
(see [Implementation](#implementation)).

Add `ScanPlayback` fragment to your projects scan playback's XML layout file:
```xml
<androidx.fragment.app.FragmentContainerView
	android:id="@+id/scanPlayback"
	android:name="cubi.casa.cubicapture.ScanPlayback"
	android:layout_width="match_parent"
	android:layout_height="match_parent" />
```

Declare a `lateinit` variable for `ScanPlayback` in your scan playback `Activity`:
```Kotlin
private lateinit var scanPlayback: ScanPlayback
```

Initialize the `ScanPlayback` `lateinit` variable in `onCreate()`:
```Kotlin
scanPlayback = supportFragmentManager.findFragmentById(R.id.scanPlayback) as ScanPlayback
```

Implement `ScanPlaybackListener` interface to your scan playback `Activity` - Example:
```Kotlin
class ViewScanActivity : AppCompatActivity(), ScanPlaybackListener
```

Add the following function to receive updates when the playing state changes (playing/paused):
```Kotlin
override fun onIsPlayingChanged(isPlaying: Boolean) { }
```

Set the scan folder of the scan you want to playback with `setScanFolder(scanFolder: File)` in `onCreate()`.
This function will return `true` if the scan folder has a video file, otherwise `false`. Example:
```Kotlin
scanPlayback.setScanFolder(scanFolder)
```

Set on `View.OnClickListener` for the top bar's back button. Example:
```Kotlin
scanPlayback.setOnBackButtonClickListener {
	finish()
}
```

#### Photo Capturing

Starting with release 3.1.1, the CubiCapture SDK now includes the option to enable users to take
photos during scanning. These photos are included in the scan directory and the scan zip file.

Enable or disable photo capturing (disabled by default):
```Kotlin
cubiCapture.photoCapturingEnabled = true
```

#### Safe Mode

Safe mode can be used to disable the ARCore's Depth API to prevent any native ARCore crashes caused
by any possible bugs in ARCore's Depth API.
We recommend implementing a Safe mode setting to your app. The setting should only be visible to
users with a Depth API supported device, and user should enable the Safe mode if they are
experiencing stability issues while scanning.
Enabling the Safe mode will disable the Depth API and Too close -warning.

To check if the device is Depth API supported, check the value of `depthApiSupported: Boolean` after
receiving code `1` from `onStatus(code: Int, description: String)`. Save this value to, for example,
SharedPreferences. And then use the saved value to determine if the Safe mode setting should be
visible to the user or not.

Then enable or disable the Safe mode based on the user's selection. Example:
```Kotlin
val settings = getSharedPreferences("settings", 0)
cubiCapture.safeMode = settings.getBoolean("safeMode", false)
```

`safeMode: Boolean` is set to `false` by default. Value is ignored on devices which do not support
Depth API, because the Depth API and Too close -warning is disabled on those devices by default.

#### Warning Sound

Warning sound is played when the ARCore's `TrackingState` is **not** `TRACKING`.

To change the tracking warning sound call:
```Kotlin
cubiCapture.trackingWarningSoundResId = R.raw.new_warning_sound
```

#### Automatic and Manual Zipping

CubiCapture provides flexibility for zipping scan files, allowing either automatic or manual
zipping based on your application's needs.

##### Enable or Disable Automatic Zipping

Automatic zipping is enabled by default, meaning scan files are automatically compressed after the
scan completes. You can disable this behavior if you prefer to handle zipping manually:
```Kotlin
cubiCapture.autoZippingEnabled = false
```

##### Manual Zipping

If automatic zipping is disabled, you can manually compress scan files using the `ScanZipper.zip`
method. This should be called only after all scan files are successfully saved.

The manual zip method expects the scan directory to contain the following files:
`arkitData.json`, `config.json` and `video.mp4`.
If any of these required files are missing, the zipping operation will fail, triggering the error
callback with a `MissingRequiredFilesException`.

Example Usage:
```Kotlin
val zipUUID = UUID.randomUUID().toString()
val zipFile = File(scanDirectory, "$zipUUID.zip")

ScanZipper.zip(
    scanDirectory = scanDirectory,
    zipFile = zipFile,
    onSuccess = { createdZipFile ->
        // Handle success: The zip file has been created successfully.
    },
    onError = { exception ->
        // Handle error: Missing required files or zipping failure.
    }
)
```
The `zip` method performs the operation in a background coroutine to avoid blocking the main thread.
Both the success and error callbacks are executed on the main thread, ensuring UI safety.

##### Directory Structure for Successful Zipping

For the `zip` method to succeed, the scan directory must contain all required files.
Optional files will be included in the zip if present.

Here’s an example directory structure where zipping will succeed:
```.
.
└── AllScansDirectory
    ├── ExampleScanDirectory
    │   ├── arkitData.json
    │   ├── config.json
    │   ├── video.mp4
    │   ├── feedback.json
    │   ├── scanLog.json
    │   ├── UUID*.jpg <- 0 or more snapshot files
    │   └── photo_capturer.json
```
Note: The `UUID*.jpg` and `photo_capturer.json` files will only be present if photos were taken
during the scan.

## UI Settings

#### General

Position of the capture photo button. By default, it’s on the right:
```Kotlin
cubiCapture.isCapturePhotoButtonOnRight = false
```

Whether to initialize and display the scan timer. Initialized and visible by default:
```Kotlin
cubiCapture.timerEnabled = false
```

Whether to display the CubiCapture's close button. Visible by default:
```Kotlin
cubiCapture.closeButtonEnabled = false
```

Enable or disable the record button. Enabled by default:
```Kotlin
cubiCapture.recordButtonEnabled = false
```

Enable or disable low storage warnings. Enabled by default:
```Kotlin
cubiCapture.storageWarningsEnabled = false
```

#### Colors and Alphas

To customize the colors or alphas of the default CubiCapture graphics, you need to redefine the
default `color`s in your application's `colors.xml` file.

Below is the list of all default colors and alphas defined in the CubiCapture library:
```xml
<!-- Scan related texts colors -->
<color name="cc_scan_text_color">#FDFDFD</color>
<color name="cc_guide_title_text_color">#E6F0FA</color>
<color name="cc_guide_info_text_color">#E6F0FA</color>
<color name="cc_processing_text_color">#FDFDFD</color>
<color name="cc_photo_count_non_zero_text_color">#0C2F46</color>

<!-- Scan close button background color. Only visible when fullscreen guide is shown -->
<color name="cc_scan_button_close_background_color">#131619</color>

<!-- Scan timer background colors -->
<color name="cc_scan_timer_background_color">#B62138</color>
<color name="cc_scan_timer_idle_background_color">#FDFDFD</color>

<!-- Record button colors -->
<color name="cc_record_button_primary_color">#FDFDFD</color>
<color name="cc_record_button_recording_fill_color">#B62138</color>

<!-- Photo capture button background color -->
<color name="cc_button_capture_photo_background_color">#FDFDFD</color>

<!-- Scan guide colors -->
<color name="cc_guide_warning_solid_color">#E0131619</color>
<color name="cc_guide_warning_stroke_color">#ECA80D</color>
<color name="cc_guide_info_solid_color">#E0131619</color>
<color name="cc_guide_info_stroke_color">#5DB2A7</color>

<!-- Colors for the scan guide warning corners -->
<color name="cc_guide_warning_corners_solid_color">#FE566F</color>
<color name="cc_guide_warning_corners_stroke_color">#FDFDFD</color>

<!-- Scan overlay colors -->
<!-- Note: Default gradient colors transition from lower opacity to higher opacity -->
<color name="cc_overlay_default_stroke_color">#5DB2A7</color>
<color name="cc_overlay_warning_stroke_color">#ECA80D</color>
<color name="cc_overlay_start_scanning_solid_color">#66131619</color>
<color name="cc_overlay_warning_solid_color">#665E1B2B</color>
<color name="cc_overlay_warning_gradient_start_color">#00E6F0FA</color>
<color name="cc_overlay_warning_gradient_center_color">#7AB62138</color>
<color name="cc_overlay_warning_gradient_end_color">#A3611225</color>

<!-- Request permission button background and text color -->
<color name="cc_permission_request_button_background_color">#FDFDFD</color>
<color name="cc_permission_request_button_text_color">#131619</color>

<!-- Scan dialog background and button colors -->
<color name="cc_dialog_background_color">#0C2F46</color>
<color name="cc_dialog_button_positive_text_color">#FDFDFD</color>
<color name="cc_dialog_button_negative_text_color">#FDFDFD</color>
<color name="cc_dialog_button_positive_gradient_start_color">#1D4357</color>
<color name="cc_dialog_button_positive_gradient_end_color">#008674</color>
<color name="cc_dialog_button_negative_stroke_color">#FDFDFD</color>

<!-- Processing view progress bar color -->
<color name="cc_processing_progress_bar_color">#FDFDFD</color>

<!-- Scan playback text color for events and playback speed spinner -->
<color name="ccPlaybackMainTextColor">#FDFDFD</color>

<!-- Scan playback text color for "Events", "Frame" and "Time" texts -->
<color name="ccPlaybackControllerTextColor">#569789</color>

<!-- Scan playback main background color -->
<color name="ccPlaybackBackgroundColor">#071d29</color>

<!-- Scan playback top bar background color -->
<color name="ccPlaybackTopBarBackgroundColor">#80071d29</color>

<!-- Scan playback events container background color -->
<color name="ccEventsContainerBackgroundColor">#80071d29</color>

<!-- Scan playback controller background color -->
<color name="ccPlaybackControllerBackgroundColor">#163241</color>

<!-- Scan playback controller button tint -->
<color name="ccPlaybackControllerButtonTint">#569789</color>

<!-- Scan playback event colors -->
<color name="ccPlaybackWarningColor">#B85E1B2B</color>
<color name="ccPlaybackSpaceLabelColor">#B8569789</color>

<!-- Scan playback timeline colors -->
<color name="ccTimelineBackgroundColor">#3A3A3A</color>
<color name="ccTimelineWarningColor">#B85E1B2B</color>
<color name="ccTimelineSpaceLabelColor">#B8569789</color>
<color name="ccTimelineThumbColor">#FDFDFD</color>

<!-- Scan playback scrollbar color -->
<color name="ccPlaybackScrollbarColor">#FDFDFD</color>
```

For example, if you want to change the color and alpha of the scan timer's background,
you can define the new colors in your application's `colors.xml` file like this:
```xml
<!-- Scan timer background colors -->
<color name="cc_scan_timer_background_color">#FFFF0000</color>
<color name="cc_scan_timer_idle_background_color">#FFFFFFFF</color>
```

The colors can be defined using either the `#RRGGBB` color notation or the `#AARRGGBB` notation,
which includes a hexadecimal alpha value.

#### Dimensions

To customize CubiCapture's default layout sizes, margins, and text sizes, you need to override the
library's default dimensions by defining them in your application's `dimens.xml` file.
This file should be located in the `values` directory.

For different screen sizes (e.g., tablets), you can define specific dimensions by creating a
`dimens.xml` file in the corresponding resource directory, such as `values-sw600dp` for tablets
with a screen width of at least 600dp.

Below is the list of all default dimensions defined in the CubiCapture library for phones and
tablets:

Phones (`values/dimens.xml`):
```xml
<!--
    Scan close button size, background radius and background margin

    Note:
    - Scan close button is constrained to vertically center the scan timer
    - Scan close button's background margin is applied to the original background drawable item
-->
<dimen name="cc_scan_button_close_size">24dp</dimen>
<dimen name="cc_scan_button_close_background_radius">10dp</dimen>
<dimen name="cc_scan_button_close_background_margin">12dp</dimen>

<!-- Scan timer margin top, width and background radius  -->
<dimen name="cc_scan_timer_width">96dp</dimen>
<dimen name="cc_scan_timer_height">24dp</dimen>
<dimen name="cc_scan_timer_background_radius">36dp</dimen>

<!-- Scan guide margins, paddings, and max width -->
<dimen name="cc_guide_margin">16dp</dimen>
<dimen name="cc_guide_text_contents_padding">16dp</dimen>
<dimen name="cc_guide_info_text_margin_top">4dp</dimen>
<dimen name="cc_guide_max_width">560dp</dimen>

<!-- Scan guide text sizes -->
<dimen name="cc_guide_title_text_size">20dp</dimen>
<dimen name="cc_guide_info_text_size">14dp</dimen>

<!-- Scan overlay stroke width -->
<dimen name="cc_overlay_stroke_width">2dp</dimen>

<!-- Scan guide icon height (using scale type center inside) -->
<dimen name="cc_guide_icon_height">160dp</dimen>

<!-- Scan guide background radius and stroke width -->
<dimen name="cc_guide_background_radius">8dp</dimen>
<dimen name="cc_guide_background_stroke_width">2dp</dimen>

<!-- Scan fullscreen guide padding -->
<dimen name="cc_guide_fullscreen_padding">32dp</dimen>

<!-- Scan fullscreen guide icon height -->
<dimen name="cc_guide_fullscreen_icon_height">72dp</dimen>

<!-- Scan fullscreen guide text margins -->
<dimen name="cc_guide_fullscreen_title_text_margin_top">32dp</dimen>
<dimen name="cc_guide_fullscreen_info_text_margin_top">16dp</dimen>

<!-- Scan controls container paddings and item margin (gap between items) -->
<dimen name="cc_scan_controls_container_padding_top">16dp</dimen>
<dimen name="cc_scan_controls_container_padding_bottom">24dp</dimen>
<dimen name="cc_scan_controls_item_margin">56dp</dimen>

<!-- Scan header container paddings -->
<dimen name="cc_scan_header_container_padding_horizontal">16dp</dimen>
<dimen name="cc_scan_header_container_padding_vertical">24dp</dimen>

<!-- Record button size -->
<dimen name="cc_button_record_size">64dp</dimen>

<!-- Photo capture button size, stroke width, and icon size-->
<dimen name="cc_button_capture_photo_button_size">56dp</dimen>
<dimen name="cc_button_capture_photo_button_stroke_width">2dp</dimen>
<dimen name="cc_button_capture_photo_icon_size">32dp</dimen>

<!-- Photo thumbnail size -->
<dimen name="cc_photo_thumbnail_size">56dp</dimen>

<!-- Photo count background (non-zero) and text size-->
<dimen name="cc_photo_count_non_zero_text_background_size">24dp</dimen>
<dimen name="cc_photo_count_text_size">12dp</dimen>

<!-- Request permission button radius, padding horizontal and margin top -->
<dimen name="cc_permission_request_button_radius">100dp</dimen>
<dimen name="cc_permission_request_button_padding_horizontal">32dp</dimen>
<dimen name="cc_permission_request_button_margin_top">32dp</dimen>

<!-- Scan dialog dimensions -->
<dimen name="cc_dialog_padding">32dp</dimen>
<dimen name="cc_dialog_margin">16dp</dimen>
<dimen name="cc_dialog_info_text_margin_top">16dp</dimen>
<dimen name="cc_dialog_button_positive_margin_top">24dp</dimen>
<dimen name="cc_dialog_button_negative_margin_top">16dp</dimen>
<dimen name="cc_dialog_background_radius">16dp</dimen>
<dimen name="cc_dialog_button_radius">100dp</dimen>
<dimen name="cc_dialog_button_negative_stroke_width">1dp</dimen>

<!-- Processing view progress bar size scale -->
<dimen name="cc_progress_bar_size_scale">1.6</dimen>

<!-- Processing view processing text margin -->
<dimen name="cc_processing_text_margin">16dp</dimen>

<!-- Scan timer text size -->
<dimen name="cc_text_size_scan_timer">14dp</dimen>

<!-- Heading 1 text size -->
<dimen name="cc_text_size_heading_1">20dp</dimen>

<!-- Heading 2 text size -->
<dimen name="cc_text_size_heading_2">16dp</dimen>

<!-- Body 1 text size -->
<dimen name="cc_text_size_body_1">16dp</dimen>

<!-- Body 2 text size -->
<dimen name="cc_text_size_body_2">14dp</dimen>

<!-- Scan playback controller button width percent -->
<dimen name="cc_controller_button_width_percent">0.10</dimen>

<!-- Scan playback small text size (Used for all texts except active events) -->
<dimen name="cc_playback_text_size_small">14dp</dimen>

<!-- Scan playback big text size (Used for active events) -->
<dimen name="cc_playback_text_size_big">20dp</dimen>
```

Tablets (`values-sw600dp/dimens.xml`):
```xml
<!-- Scan close button size-->
<dimen name="cc_scan_button_close_size">32dp</dimen>

<!-- Scan timer margin top, width and background radius  -->
<dimen name="cc_scan_timer_width">136dp</dimen>
<dimen name="cc_scan_timer_height">44dp</dimen>
<dimen name="cc_scan_timer_background_radius">36dp</dimen>

<!-- Scan guide text sizes -->
<dimen name="cc_guide_title_text_size">20dp</dimen>
<dimen name="cc_guide_info_text_size">20dp</dimen>

<!-- Scan guide icon height (using scale type center inside) -->
<dimen name="cc_guide_icon_height">210dp</dimen>

<!-- Scan controls container paddings -->
<dimen name="cc_scan_controls_container_padding_top">32dp</dimen>
<dimen name="cc_scan_controls_container_padding_bottom">32dp</dimen>

<!-- Scan header container paddings -->
<dimen name="cc_scan_header_container_padding_horizontal">16dp</dimen>
<dimen name="cc_scan_header_container_padding_vertical">24dp</dimen>

<!-- Record button size -->
<dimen name="cc_button_record_size">88dp</dimen>

<!-- Photo capture button and icon size-->
<dimen name="cc_button_capture_photo_button_size">72dp</dimen>
<dimen name="cc_button_capture_photo_icon_size">32dp</dimen>

<!-- Photo thumbnail size -->
<dimen name="cc_photo_thumbnail_size">72dp</dimen>

<!-- Photo count background (non-zero) and text size-->
<dimen name="cc_photo_count_non_zero_text_background_size">32dp</dimen>
<dimen name="cc_photo_count_text_size">20dp</dimen>

<!-- Scan timer text size -->
<dimen name="cc_text_size_scan_timer">20dp</dimen>
```

For example, if you want to change the size of the record button, you can define the new size in
your application's `dimens.xml` file like this:
```xml
<!-- Record button size -->
<dimen name="cc_button_record_size">72dp</dimen>
```

For tablets, you can define a different size in `values-sw600dp/dimens.xml`:
```xml
<!-- Record button size -->
<dimen name="cc_button_record_size">96dp</dimen>
```

#### Texts

To change CubiCapture's default texts, you need to override the library's default strings by
redefining them in your application's `strings.xml` file.

Below is the list of all default texts defined in the CubiCapture library:
```xml
<!-- Displayed when the ARCore is initializing -->
<string name="cc_tracking_initializing_title">Setting up…</string>
<string name="cc_tracking_initializing_info">Move your device so we can begin tracking your position</string>

<!-- Displayed when ready to start scanning -->
<string name="cc_start_scanning_title">Press Record</string>
<string name="cc_start_scanning_info">Start moving to scan</string>

<!-- Displayed if tracking is lost due to excessive motion -->
<string name="cc_tracking_excessive_motion_title">Slow down</string>
<string name="cc_tracking_excessive_motion_info">You are moving too fast, move slower</string>

<!-- Displayed if tracking is lost due to insufficient visual features -->
<string name="cc_tracking_insufficient_features_title">Lack of visual features</string>
<string name="cc_tracking_insufficient_features_info">Point the camera at distinctive features</string>

<!-- Displayed if tracking is lost due to poor lighting conditions -->
<string name="cc_tracking_insufficient_lighting_title">Turn the lights on</string>
<string name="cc_tracking_insufficient_lighting_info">It is too dark here</string>

<!-- Displayed if the user is walking sideways -->
<string name="cc_guide_sideways_left_title">Turn left</string>
<string name="cc_guide_sideways_right_title">Turn right</string>
<string name="cc_guide_sideways_left_info">Point the camera in your walking direction</string>
<string name="cc_guide_sideways_right_info">Point the camera in your walking direction</string>

<!-- Displayed title and info text for the rotate warning -->
<string name="cc_guide_rotate_title">Rotate your device</string>
<string name="cc_guide_rotate_info">Please scan in portrait orientation</string>

<!-- Displayed if the device is tilted forward too much -->
<string name="cc_guide_raise_device_title">Raise your device</string>
<string name="cc_guide_raise_device_info">You\'re pointing too much at the floor</string>

<!-- Displayed if the device is tilted up too much -->
<string name="cc_guide_lower_device_title">Lower your device</string>
<string name="cc_guide_lower_device_info">You\'re pointing too much at the ceiling</string>

<!-- Displayed if the user is rotating/turning too fast -->
<string name="cc_guide_fast_rotation_title">Turn slowly</string>
<string name="cc_guide_fast_rotation_info">Avoid fast turns for best scanning results</string>

<!-- Displayed if the user is scanning too close to objects -->
<string name="cc_guide_too_close_title">You\'re too close</string>
<string name="cc_guide_too_close_info">Keep more distance from the objects you\'re scanning</string>

<!-- Displayed if the device's storage space is running low -->
<string name="cc_guide_low_storage_title">Low storage</string>
<!-- Note! Always include the %1$d in the info string! -->
<string name="cc_guide_low_storage_info">Only %1$d minutes of scanning time left</string>

<!-- Displayed if the device experiences thermal throttling -->
<string name="cc_guide_throttling_title">Device getting hot</string>
<string name="cc_guide_throttling_info">You can continue scanning, but move slowly</string>

<!-- Displayed if camera permission is not granted -->
<string name="cc_permission_camera_title">No camera access</string>
<string name="cc_permission_camera_info">Cannot scan without camera permission. Please allow access to the camera</string>

<!-- Displayed if permission is not granted -->
<string name="cc_permission_request_button_text">Turn on</string>

<!-- Displayed if record button is pressed to finish scan -->
<string name="cc_dialog_finish_scan_title">Finish scan?</string>
<string name="cc_dialog_finish_scan_info">Are you sure you want to finish this scan?</string>

<!-- Displayed if close button is pressed to cancel scan -->
<string name="cc_dialog_cancel_scan_title">Cancel scan?</string>
<string name="cc_dialog_cancel_scan_info">Are you sure you want to cancel this scan?</string>

<!-- Displayed positive and negative button texts for scan dialogs -->
<string name="cc_dialog_button_positive_text">Yes</string>
<string name="cc_dialog_button_negative_text">No</string>

<!-- Displayed in the processing view after recording is finished -->
<string name="cc_processing_text">Processing scan.\nPlease do not exit the application.</string>

<!-- Scan playback speed spinner text. Always include the %1$s in the string!-->
<string name="cc_playback_speed_text">Playback speed: %1$s</string>
<!-- Scan playback speed spinner text for normal speed -->
<string name="cc_playback_normal_speed_text">Normal</string>

<!-- Scan playback events header text -->
<string name="cc_playback_events_header_text">Events</string>

<!-- Scan playback frame text. Always include the %1$s in the string!-->
<string name="cc_playback_frame_text">Frame: %1$s</string>
<!-- Scan playback time text. Always include the %1$s in the string!-->
<string name="cc_playback_time_text">Time: %1$s</string>

<!-- Scan playback active event texts -->
<string name="cc_playback_excessive_motion">Moving too fast</string>
<string name="cc_playback_insufficient">Insufficient lighting or features</string>
<string name="cc_playback_sideways">Walking sideways</string>
<string name="cc_playback_floor">Scanning floor</string>
<string name="cc_playback_ceiling">Scanning ceiling</string>
<string name="cc_playback_horizontal">Scanning horizontally</string>
<string name="cc_playback_wrong_orientation">Wrong device orientation</string>
<string name="cc_playback_fast_rotation">Turning too fast</string>
<string name="cc_playback_too_close">Too close</string>
```

#### Drawable Graphics

To change CubiCapture's default drawables, you need to override them by redefining the drawables
in your application's `drawables.xml` file. This file should be located in the `values` directory.

Below is the list of all default drawables defined in the CubiCapture library:
```xml
<!--
    Scan close button icon and background.
    Note: Background is only visible when fullscreen guide is shown
-->
<drawable name="cc_scan_button_close_icon">@drawable/cc_icon_close</drawable>
<drawable name="cc_scan_button_close_background">@drawable/cc_close_button_background</drawable>

<!-- Scan timer background -->
<drawable name="cc_scan_timer_background">@drawable/cc_timer_background</drawable>
<drawable name="cc_scan_timer_idle_background">@drawable/cc_timer_idle_background</drawable>

<!-- Photo count non-zero text background -->
<drawable name="cc_photo_count_non_zero_text_background">@drawable/cc_photo_count_text_background</drawable>

<!-- Record button -->
<drawable name="cc_not_recording">@drawable/cc_drawable_not_recording</drawable>
<drawable name="cc_recording">@drawable/cc_drawable_recording</drawable>

<!-- Photo capture button -->
<drawable name="cc_button_capture_photo_icon">@drawable/cc_icon_camera</drawable>
<drawable name="cc_button_capture_photo_background">@drawable/cc_button_capture_photo_background_circle</drawable>

<!-- Displayed when ready to start scanning -->
<drawable name="cc_guide_start_scanning_icon">@drawable/cc_icon_start_scanning</drawable>

<!-- Displayed if tracking is lost due to excessive motion -->
<drawable name="cc_tracking_excessive_motion_icon">@drawable/cc_icon_excessive_motion</drawable>

<!-- Displayed if tracking is lost due to insufficient visual features -->
<drawable name="cc_tracking_insufficient_features_icon">@drawable/cc_icon_insufficient_features</drawable>

<!-- Displayed if tracking is lost due to poor lighting conditions -->
<drawable name="cc_tracking_insufficient_lighting_icon">@drawable/cc_icon_insufficient_lighting</drawable>

<!-- Displayed if the user is walking sideways -->
<drawable name="cc_guide_sideways_left_icon">@drawable/cc_icon_turn_left</drawable>
<drawable name="cc_guide_sideways_right_icon">@drawable/cc_icon_turn_right</drawable>

<!-- Displayed icon for the rotate warning -->
<drawable name="cc_guide_rotate_device_icon">@drawable/cc_icon_rotate_device</drawable>

<!-- Displayed if the device is tilted forward too much -->
<drawable name="cc_guide_raise_device_icon">@drawable/cc_icon_raise_device</drawable>

<!-- Displayed if the device is tilted up too much -->
<drawable name="cc_guide_lower_device_icon">@drawable/cc_icon_lower_device</drawable>

<!-- Displayed if the user is rotating/turning too fast -->
<drawable name="cc_guide_fast_rotation_icon">@drawable/cc_icon_fast_rotation</drawable>

<!-- Displayed if the user is scanning too close to objects -->
<drawable name="cc_guide_too_close_icon">@drawable/cc_icon_too_close</drawable>

<!-- Displayed if camera permission is not granted -->
<drawable name="cc_permission_camera_icon">@drawable/cc_icon_camera</drawable>

<!-- Scan overlays -->
<drawable name="cc_overlay_default">@drawable/cc_overlay_default_drawable</drawable>
<drawable name="cc_overlay_start_scanning">@drawable/cc_overlay_start_scanning_drawable</drawable>
<drawable name="cc_overlay_warning_solid">@drawable/cc_overlay_warning_solid_drawable</drawable>
<drawable name="cc_overlay_warning_gradient">@drawable/cc_overlay_warning_gradient_drawable</drawable>

<!--
    Scan guide warning corners:
    - You can either change the entire main drawable (`cc_guide_warning_corners`),
      or modify the individual corner drawables, which are assembled to form
      the complete, default drawable.
-->
<drawable name="cc_guide_warning_corners">@drawable/cc_warning_corners</drawable>
<drawable name="cc_guide_warning_corner_top_left">@drawable/cc_warning_corner_top_left</drawable>
<drawable name="cc_guide_warning_corner_top_right">@drawable/cc_warning_corner_top_right</drawable>

<!-- Warning and info guide backgrounds -->
<drawable name="cc_guide_warning_background">@drawable/cc_warning_background</drawable>
<drawable name="cc_guide_info_background">@drawable/cc_info_background</drawable>
<!-- Fullscreen warning guide background, which is just a color by default -->
<drawable name="cc_guide_fullscreen_warning_background">#B85E1B2B</drawable>

<!-- Permission request button background -->
<drawable name="cc_permission_request_button_background">@drawable/cc_request_permission_button_background</drawable>

<!-- Scan dialog background and button backgrounds -->
<drawable name="cc_dialog_background">@drawable/cc_scan_dialog_background</drawable>
<drawable name="cc_dialog_button_positive_background">@drawable/cc_dialog_positive_button_background</drawable>
<drawable name="cc_dialog_button_negative_background">@drawable/cc_dialog_negative_button_background</drawable>

<!-- Scan view background color, which is just a color by default  -->
<drawable name="cc_scan_background">#0C2F46</drawable>

<!-- Processing screen background, which is just a color by default -->
<drawable name="cc_processing_background">#0C2F46</drawable>

<!-- Scan playback timeline thumb -->
<drawable name="cc_timeline_thumb">@drawable/cc_drawable_timeline_thumb</drawable>

<!-- Scan playback back arrow -->
<drawable name="cc_playback_back_arrow">@drawable/cc_icon_arrow_left</drawable>
```

For example, if you want to change the default record button drawable graphics to your own
drawables, you can redefine the drawables in your application's `drawables.xml` file like this:
```xml
<!-- Record button -->
<drawable name="cc_not_recording">@drawable/not_recording_new</drawable>
<drawable name="cc_recording">@drawable/recording_new</drawable>
```

## Torch (Flashlight) Support

CubiCapture supports automatic torch (flashlight) activation on devices that support the Depth API
and torch. If the surrounding lighting is too dark during a scan, CubiCapture will automatically
activate the torch to improve the scan's lighting conditions.

## About Warning Priorities

During a scan, CubiCapture displays warnings to the user if it detects any issues with the scanning
style or tracking. These warnings are categorized into different priority levels. Higher-priority
warnings will override and hide any lower-priority warnings, ensuring only the most critical
warning is shown at a given time.

Priority Level | Warnings
---------------|---------
1 | Not tracking*
2 | Rotate device
3 | Sideways walking, Fast rotation, Too close
4 | Device is tilted forward/up too much
5 | Low storage, Device is hot

*The `Not tracking` warning is displayed when ARCore's `TrackingState` is **not** `TRACKING`,
meaning the position of the device cannot be determined.
In this case, CubiCapture will display a tracking warning (e.g., insufficient visual features
warning) to help reestablish device tracking as quickly as possible.

## Status Codes

### 0, "Device turned to correct orientation."
Received when the device is turned to the correct (portrait) orientation.

### 1, "Started recording."
Received when the scan is started by pressing the record button.

### 2, "Finished recording."
Received when the user has ended the scan and the scan has enough data.
The saving of the scan files begins after this.

### 3, "Finished recording - Not enough data."
Received when the user has ended the scan and the scan does not have enough data.
The scan files will be deleted (code 15), and CubiCapture will be finished after this (code 5).

### 4, "Saving of scan files finished. (Beginning to zip files.)"
Received when the scan files are successfully saved without any errors.
If automatic zipping is enabled, zipping will start after this.

### 5, "CubiCapture is finished. You can now finish your scanning Activity."
Received when CubiCapture is finished, and the scanning `Activity` should be finished.
This code is always received after a scan. To determine whether the scan was successful,
you need to handle the preceding codes (e.g., codes 4 and 7).

For example:
- On a successful scan, you'll first receive code 4 (indicating successful
  saving of scan data), followed by code 7 (indicating successful zipping of scan
  files, if 'autoZipping' is enabled).
- On an unsuccessful scan, code 4 will be skipped and an error code (e.g.,
  code 3: "Finished recording - Not enough data") will be received instead.

### 6, "Scan directory location: <directory-path>"
Received when the scan files are successfully saved without any errors.
The description will contain a path to the scan directory.
To receive the scan directory as a `File` use the `CubiEventListener`'s
`onFile(code: Int, file: File)` method where the `code` `1` receives the scan directory.

### 7, "Zipping completed successfully. Zip file location: <zip-file-path>"
Received when the automatic zipping of the scan files is completed successfully.
The description will contain a path to the zip file.
To receive the zip file as a `File` use the `CubiEventListener`'s
`onFile(code: Int, file: File)` method where `code` `2` receives the zip file.

### 8, "ARCore TrackingFailureReason: INSUFFICIENT_LIGHT"
Received when ARCore motion tracking is lost due to poor lighting conditions.

### 9, "ARCore TrackingFailureReason: EXCESSIVE_MOTION"
Received when ARCore motion tracking is lost due to excessive motion.

### 10, "ARCore TrackingFailureReason: INSUFFICIENT_FEATURES"
Received when ARCore motion tracking is lost due to insufficient visual features.

### 11, "ARCore TrackingState is TRACKING."
Received when ARCore is tracking again.

### 13, "Device position is sliding in an unnatural manner."
Received if the position of the device is "sliding" in an unnatural manner.
The scan files will be deleted (code 15), and CubiCapture will be finished after this (code 5).

### 14, "The device was in the wrong orientation for too long."
Received if the device was in the wrong orientation for too long.
The scan files will be deleted (code 15), and CubiCapture will be finished after this (code 5).

### 15, "Error shutdown. (Deleted scan directory <directory-path>)
Received when the scan is not successful.
Any scan files are deleted (code 15), and CubiCapture will be finished after this (code 5).

### 16, "Device needs to be in correct orientation in order to start recording."
Received when the record button is pressed, but recording cannot be started because the device is
not in the correct orientation.

### 17, "Device is in the wrong orientation."
Received when the device is in the wrong orientation.

### 18, "Playing warning sound."
Received when a warning sound is played, and ARCore motion tracking is lost (codes 8, 9, and 10).
Only received if ARCore was tracking before the tracking was lost to avoid playing the error sound
multiple times in a row.
(The ARCore `TrackingFailureReason` might change (between codes 8, 9, and 10) during a short period of time).

### 19, "Close button press confirmed. You can now finish your scanning Activity."
Received when the CubiCapture's close button press is confirmed via dialog. You should call
`Activity.finish()`.

### 21, "Tilting too far down."
Received when the pitch of the camera has been too low for a certain amount of time.

### 22, "Tilting too far up."
Received when the pitch of the camera has been too high for a certain amount of time.

### 23, "Tilt angle optimal."
Received when the pitch of the camera is optimal again.

### 24, "ARCore TrackingState is PAUSED."
Received when ARCore's `TrackingState` is `PAUSED`, meaning the position of the device cannot be
determined.

### 25, "Walking sideways. Displaying turn left warning."
Received when the user is walking sideways to the left while the camera is pointing forward.

### 26, "Walking sideways. Displaying turn right warning."
Received when the user is walking sideways to the right while the camera is pointing forward.

### 27, "Not walking sideways anymore."
Received when the user is not walking sideways anymore.

### 28, "ARCore was unable to start tracking during the first five seconds."
Received if ARCore is unable to start tracking during the first five seconds.
The scan files will be deleted (code 15), and CubiCapture will be finished after this (code 5).

### 51, "Zipping failed due to an error: <stack-trace>"
Received if the automatic zipping fails.

### 52, "Exception on the OpenGL thread: <stack-trace>"
Received if there's an exception on the OpenGL thread.

### 53, "Failed to read an asset file: <stack-trace>"
Received if camera video preview surface fails to be initialized.

### 54, "Failed to write AR data file: <stack-trace>"
Received if the writing of the `arkitData.json` file fails.
The scan files will be deleted (code 15), and CubiCapture will be finished after this (code 5).

### 56, "Failed to write config file: <stack-trace>"
Received if the writing of the `config.json` file fails.
The scan files will be deleted (code 15), and CubiCapture will be finished after this (code 5).

### 65, "Frame processing exception: <stack-trace>"
Received if the frame processing fails.

### 66, "Unable to get correct values for the device's position."
Received if ARCore has been unable to return correct values for the device's position for a certain
amount of time.
The scan files will be deleted (code 15), and CubiCapture will be finished after this (code 5).

### 67, "Location Services are off."
Received if the device has Location Services turned off on start. Only received if true north
detection is enabled and `ACCESS_FINE_LOCATION` permission is granted.

### 68, "Not able to get true north because Location Services were still off once recording was started."
Received if the device had Location Services turned off once recording was started.
Only received if true north detection is enabled and `ACCESS_FINE_LOCATION` permission is granted.

### 69, "Sensor is reporting true north data with low or unreliable accuracy. Not saving these true north values."
Received if the sensor is reporting true north data with low or unreliable accuracy. These values
cannot be trusted, so true north detection is not saving these values. Only received if true north
detection is enabled and running.

### 70, "Thermal state nominal"
Received if the thermal state changes to nominal.

### 71,	"Thermal state light"
Received if the thermal state changes to light.

### 72,	"Thermal state moderate"
Received if the thermal state changes to moderate.

### 73,	"Thermal state severe"
Received if the thermal state changes to severe.

### 74,	"Thermal state critical"
Received if the thermal state changes to critical.

### 75,	"Thermal state emergency"
Received if the thermal state changes to emergency.

### 76,	"Thermal state shutdown"
Received if the thermal state changes to shutdown.

### 78, "Storage: <minutes> minutes left"
Received once on start when the fragment's activity has been created and in certain intervals during a scan
while recording. `minutes: Int` is an estimation in minutes of the maximum scan length the device can store.

### 79, "Device ran out of storage space"
Received if the device runs out of storage space while scanning.
The scan files will be deleted (code 15), and CubiCapture will be finished after this (code 5).

### 85, "Scanning too close. Showing proximity warning."
Received if the user is scanning too close to objects.

### 87, "Too fast rotations. Showing fast movement warning."
Received when the user turns around too fast while scanning.

### 88, "Scan range normal. Hiding proximity warning."
Received when the user is not scanning too close to objects anymore.

### 89, "Not moving too fast anymore."
Received when the user is not turning around too fast anymore.

### 90, "Failed to write scan log file: <stack-trace>"
Received if the writing of the `scanLog.json` file fails.

### 91, "Failed to write feedback file: <stack-trace>"
Received if the writing of the `feedback.json` file fails.

### 92, "Gyroscope sensor not available. Not able to detect fast movements."
Received if there's no gyroscope sensor available.
CubiCapture will not be able to detect fast movements.

### 93, "Error in depth data processing: <stack-trace>"
Received if the depth data processing fails.

### 94, "Rotation vector sensor is not supported. Using accelerometer and magnetometer instead."
Received if the rotation vector sensor is not supported on device. True North data will be acquired
using the accelerometer and magnetometer instead.

### 100, "ARCore session is initializing"
Received if the ARCore session is initializing normally.

### 101, "ARCore session is initialized"
Received if the ARCore session is initialized and ARCore's `TrackingState` is `TRACKING`.
This is only received when the recording has not been started.
While recording, status code 11 is received instead.

### 102, "ARCore session has to be initialized first in order to start recording."
Received when trying to start recording while the ARCore session is still in the initialization process.

### 103, Not initialized: <variable-names>
Received when any required variables have not been initialized at the start.
The list of uninitialized variables will be included in the description.
The required variables are: `scanDirectoryName`, `allScansDirectory`, and `propertyType`.

### 105, "Unable to create ARCore session: <stack-trace>"
Received if an internal error occurred while creating the ARCore session.
You will receive code 5 after this.

### 106, "Device is not compatible with ARCore: <stack-trace>"
Received if the device is not compatible with ARCore. If encountered after completing the
installation check, this usually indicates that ARCore has been side-loaded onto an incompatible
device. You will receive code 5 after this.

### 107, "Device is too hot to start scanning."
Received if the thermal state is critical or higher on startup. You will receive code 5 after this.

### 113, "Failed to write photo log file: <stack-trace>"
Received if the writing of the `photo_capturer.json` file fails.

### 116, "Device position jumped in an unnatural manner."
Received if the position of the device "jumps" in an unnatural manner. Note that this does NOT abort
the scan.

### 120, "CAMERA permission is not granted."
Received when the CAMERA permission is not granted.

### 121, "Launching application settings to grant CAMERA permission."
Received when the camera permission view's permission request button is pressed, launching the
application settings.

### 122, "Requesting CAMERA permission."
Received when the camera permission view's permission request button is pressed, prompting the
CAMERA permission through the Android OS permission dialog.

### 123, "User granted CAMERA permission."
Received when the CAMERA permission is granted through the Android OS permission dialog. Note that
this is not received if the permission is granted through the application settings.

### 124, "User denied CAMERA permission."
Received when the CAMERA permission is denied through the Android OS permission dialog. Note that
this is not received if the permission is denied through the application settings.

### 140, "Error in video encoding: <stack-trace>"
Received when an error occurs during the video encoding process.

### 141, "Failed to initialize the encoder: <stack-trace>"
Received when the video encoder fails to initialize properly.
The scan files will be deleted (code 15), and CubiCapture will be finished after this (code 5).

### 142, "Encoding timed out due to frame mismatch: <stack-trace>"
Received when encoding times out due to a mismatch between the number of frames queued and the
number of frames successfully encoded.
The scan files will be deleted (code 15), and CubiCapture will be finished after this (code 5).

## Archived Update Guides

**Note! If you've previously implemented version 2.10.1 you've probably done the next steps already:**
- Replace the `CubiCapture` and `ScanPlayback` `<fragment>` tags with the
  `<androidx.fragment.app.FragmentContainerView>` tags
- Handle the new status code [107, "Device is too hot to start scanning."](#107-device-is-too-hot-to-start-scanning)
  to show user an error message after the aborted scan

**Note! If you've previously implemented version 2.9.0 you've probably done the next steps already:**
- Update your `minSdkVersion` to API level `29` or higher
- Rename `getFile(code: Int, file: File)` and `getStatus(code: Int, description: String)` methods as
  as `onFile(code: Int, file: File)` and `onStatus(code: Int, description: String)`, respectively
- If you want to disable the low storage warnings, see the new `storageWarningsEnabled` below
  [General](#general) UI settings
- Check if you want to handle the new status code [103, "Not initialized"](#103-not-initialized-variable-names)

**Note! If you've previously implemented version 2.8.0 you've probably done the next steps already:**
- Check the new `safeMode` variable, and if you want to implement a [Safe mode](#safe-mode) setting
  to your app
- Handle the new status code [79, "Device ran out of storage space"](#79-device-ran-out-of-storage-space)
  to show user an error message after the scan

**Note! If you've previously implemented version 2.6.0 you've probably done the next steps already:**
- If you want to implement the new scan playback `Fragment` to your application,
  see [Scan playback](#scan-playback) and new resources for customization

**Note! If you've previously implemented version 2.5.2 you've probably done the next steps already:**
- Change your `CubiCapture.CubiEventListener` interface implementation to `CubiEventListener`
- Check if you want to handle the new status codes: 70-76, 90 and 94
- In previous library module documentation there was a typo where status code `88` was written as `86`.
  If you handle code `86`, change it to `88`. This code is received when the user is not scanning too
  close to objects anymore and the too close warning is hidden.

**Note! If you've previously implemented version 2.5.1 you've probably done the next steps already:**
- See [Archived Release Notes](#archived-release-notes) if you want to customize CubiCapture's text sizes,
  or the size of the processing progress bar

**Note! If you've previously implemented version 2.5.0 you've probably done the next steps already:**
- Customization of the CubiCapture has changed. See [Archived Release Notes](#archived-release-notes) for
  information about deprecations and how the CubiCapture is customized now
- Check if you want to handle the new status codes: 85, 88 and 93

**Note! If you've previously implemented version 2.4.0 you've probably done the next steps already:**
- Remove deprecated `feedbackGatheringEnabled: Boolean` if you used it
- Check if you want to handle the new status codes (codes 87, 89, 92, 105 and 106)

**Note! If you've previously implemented version 2.3.1 you've probably done the next steps already:**
- Set `allScansDirectory: File` which replaced `allScansFolderName: String`
  (see [Implementation](#implementation))
- If you want to get an estimation in minutes of the maximum scan length the device can store,
  see [Archived Release Notes](#archived-release-notes) for more information
- Check if you want to handle the new status codes (codes 78, 91 and 100-102)

**Note! If you've previously implemented version 2.3.0 you've probably done the next steps already:**
- See how to enable/disable the true north detection (see [Implementation](#implementation))
- Check if you want to handle the new status codes 67-69

**Note! If you've previously implemented version 2.2.2 you've probably done the next steps already:**
- If you want to set the enabled status of the record button `View` see
  [Archived Release Notes](#archived-release-notes) for more information

**Note! If you've previously implemented version 2.2.1 you've probably done the next steps already:**
- Check if you want to handle the new status codes 65 and 66
- See if you want to add information about your app's version to be written to the scan data
  (see variables `appVersion` and `appBuild` from [Implementation](#implementation))

**Note! If you've previously implemented version 2.2.0 you've probably done the next steps already:**
- Remove calls to CubiCapture lifecycle functions `resume()`, `pause()`, `stop()` and `destroy()`

## Archived Release Notes

**2.10.1:**
- `CubiCapture` and `ScanPlayback` `Fragments` now support the
  `<androidx.fragment.app.FragmentContainerView>` tag instead of the `<fragment>` tag
- Scan is now aborted if the thermal state is critical or higher on startup
- New status code [107, "Device is too hot to start scanning."](#107-device-is-too-hot-to-start-scanning)
- Scan data is no longer affected by enabled code shrinking

**2.9.0:**
- Setting the property type is now required. Added a new `CubiEventListener` method
  `getPropertyType(): PropertyType` which must be used to set the `PropertyType`
- `propertyType: PropertyType` variable is now deprecated
- `getFile(code: Int, file: File)` and `getStatus(code: Int, description: String)` methods renamed
  as `onFile(code: Int, file: File)` and `onStatus(code: Int, description: String)`, respectively
- Added new `storageWarningsEnabled` variable for enabling/disabling low storage warnings.
  See [General](#general) UI settings for more information
- Added new customization options for processing screen text color and progress bar color.
  See `ccProcessingTextColor` and `ccProgressBarColor` under [Colors and alphas](#colors-and-alphas)
- New status code [103, "Not initialized"](#103-not-initialized-variable-names)
- CubiCapture's `minSdkVersion` and `targetSdkVersion` have been updated to API levels `29` and `33`,
  respectively
- Updated dependencies (see [Implementation](#implementation))

**2.8.0:**
- Re-enabled Too close -warning for Depth API supported devices
- Added new `safeMode` variable for enabling/disabling Depth API. See [Safe mode](#safe-mode) for
  more information
- Aborting scan when device runs out of storage space
- New status code [79, "Device ran out of storage space"](#79-device-ran-out-of-storage-space)
- Margins of the scan back button now match the margins of the scan timer
- Updated dependencies (see [Implementation](#implementation) below)

**2.7.0:**
- Ceiling warning adjustments
- Updated dependencies (see [Implementation](#implementation))

**2.6.3:**
- Property type can now be added to the scan data with the new variable `propertyType: PropertyType`
- Patch to prevent majority of the native ARCore crashes. Too close -warning is disabled for the
  time being and will be enabled again once Google has fixed the issue
- Updated dependencies (see [Implementation](#implementation))
- Various bug fixes

**2.6.1:**
- Bug fix: Fixed record button not being vertically centered when speech recognition was disabled

**2.6.0:**
- New scan playback `Fragment` available, which can be used to review the scan and to see any warnings
  that were shown during the scan, as well as any room labels that were added. See
  [Scan playback](#scan-playback) for more information
- Reminder view for microphone and location permission. See [Reminder view](#reminder-view)
  for more information
- New customization resources for scan playback and reminder view
- Resource names `open_settings_text`, `cc_location_background`, `cc_location_reminder_text_size`
  and `location_services_reminder_text` renamed as `cc_open_location_settings_text`,
  `cc_reminder_background`, `cc_reminder_text_size` and `cc_location_services_reminder_text`
- Default processing background `cc_processing_background` is now a color (previously a PNG format
  image)
- CubiCapture's `targetSdkVersion` has been updated to API level 31
- New status codes: 95-98. Changes to the status codes 37 and 38
- Updated dependencies and added new dependencies for the scan playback
  (see [Implementation](#implementation))
- Various bug fixes and optimizations

**2.5.2:**
- Thermal state monitoring. Changes in thermal state can be listened by using status codes 70-76
- Scan log (extra information about the scanning session, included in the zip)
- Changes to the click area of the CubiCapture's back button to prevent accidental clicks
- `CubiCapture.CubiEventListener` interface implementation is now `CubiEventListener`
- New status codes: 70-76, 90 and 94
- Improvements to scan data

**2.5.1:**
- New variable `matchHintLabelWidth: Boolean` to set hint label widths to match the wider one or to
  wrap label text. This is `true` by default. For more information, see [UI Settings](#ui-settings)
- New customization resources in `dimens.xml` for CubiCapture text sizes, hint label width and
  processing progress bar size. See [Dimensions](#dimensions)
- Various bug fixes

**2.5.0:**
- Too close warning. A warning which will trigger if the user is scanning too close to objects.
  This warning is currently only available on
  [devices which support Depth API](https://developers.google.com/ar/devices#google_play_devices)
- End scan confirmation slider. In order to end the scan, user now has to slide the record button
  from right to left to the end of the slider. This feature was added to prevent the scan from being
  accidentally ended
- Location Services reminder and a new variable `requestLocationServices: Boolean`. Settings this to
  `true` will show the Location Services reminder -view if Location Services are turned off.
  This is `false` by default (see [Implementation](#implementation)).
- Changes to the customization of the CubiCapture
  - Most of the customization is now done in the following resource files:
    `colors.xml`, `dimens.xml`, `drawables.xml` and `strings.xml`
  - Deprecated public scan warning `ImageView`s, error and speech recognition text `CharSequence`s
    and image resource id `Int`s. All of these are now customizable via the resource files listed above
  - Scan warnings and guides are now combination of `drawable` icons, `string` resources and
    `drawable` backgrounds instead of being `ImageView`s. This allows better customization and the
    warnings can be localizable to any language
  - String resource names `recordHintString`, `speakHintString` and `lowStorageString` renamed as
    `cc_record_hint_text`, `cc_speech_hint_text` and `cc_low_storage_text` in the file `strings.xml`
  - Default string values reviewed
  - For more information about the customization changes, see [UI Settings](#ui-settings)
- New status codes: 37, 38, 85, 88 and 93. Changes to the `description: String` of the code 24
- CubiCapture's `targetSdkVersion` has been updated to API level 30. Specifying a `targetSdkVersion`
  in your project's `build.gradle` or `AndroidManifest.xml` will override the CubiCapture's value
- CubiCapture no longer use the `WRITE_EXTERNAL_STORAGE` permission or request the
  `requestLegacyExternalStorage` attribute
- Default scan warning background is now more translucent
- Various bug fixes
- Updated dependencies and new dependency `com.facebook.shimmer:shimmer:0.5.0` for the end scan
  confirmation slider (see [Implementation](#implementation))

**2.4.0:**
- Fast rotation warning. A warning which will trigger if the user turns around too fast while
  scanning. This is a customizable `View` `fastMovementWarning: ImageView`
- Deprecated `feedbackGatheringEnabled: Boolean` option
- New status codes (codes 39, 87, 89, 92, 105 and 106)
- UI improvements
- Various bug fixes
- Updated ARCore version to 1.25.0

**2.3.1:**
- Replaced `allScansFolderName: String` with `allScansFolder: File?` (see [Implementation](#implementation))
- AR Session initializing indication text. This text can be customized by changing the value of
  `initializingErrorText: CharSequence`
- Low storage warning. Low storage warning text can be changed by redefining the string in your applications
  `strings.xml` file (see [UI Settings](#ui-settings))
- New function `getAvailableStorageMinutes(File)` which returns an estimation in minutes of the maximum scan
  length the device can store (see [Implementation](#implementation))
- New variable `feedbackGatheringEnabled: Boolean` to toggle the feedback data gathering, this is `true` by default
- Various bug fixes and optimizations
- New status codes (codes 78, 91 and 100-102)
- Changes to the description message of the error code 45. It now contains the error code of the SpeechRecognizer
- Updated ARCore version to 1.24.0

**2.3.0:**
- Horizontal scanning warning. This is a customizable `View` `horizontalWarning: ImageView`
- True north detection (see [Implementation](#implementation))
- New status codes (codes 31-36 and 67-69)
- Dropped values from the `arkitData.json` (scan data file). Saving of the scan files is now faster
- Changes to the device orientation detection (landscape, portrait etc)
- Memory optimization
- Fixes to scaling of depth camera intrinsics

**2.2.2:**
- Hint label texts can be changed by redefining the strings in your applications `strings.xml` file
  (see [UI Settings](#ui-settings))
- New variables `speechNoResultsText` and `readyForSpeechText` to change speech recognition's pop-up texts
  (type: `CharSequence`, default values: `"No results"` and `"Say the room name"`)
- New function `recordButtonEnabled(Boolean)` to set the enabled status of the record button `View`
- Updated ARCore version to 1.23.0

**2.2.1:**
- Possibility to add information about your app's version with new variables
  `appVersion` and `appBuild`. These will be written to the scan data
- New status codes (codes 65 and 66)
- Various bug fixes

**2.2.0:**
- Depth capturing support for the following devices; `Galaxy S20 Ultra 5G`, `Galaxy S20+ 5G` and `Galaxy S10 5G`
- Speech recognition for room labels
- Optimized frame-rate handling (more stable capturing frame-rate)
- New status codes (codes 40-49 and 59-64)
- Updated dependencies
- CubiCapture lifecycle functions `resume()`, `pause()`, `stop()` and `destroy()` have been removed,
  those are now handled by CubiCapture. Note that `onWindowFocusChanged()` function is still required
- Views `buttonRecord` and `buttonRecordHint` are now private and part of the speech recognition UI.
  Customizing these Views now works differently, for example, the `setNewView()` function for those Views
  will no longer work
- Speech recognition UI can be modified by using the `colors.xml` and `dimens.xml` files.
  See [UI Settings](#ui-settings) to see how to customize the graphics,
  size of the views and layout margins
- New image resource variables `hintLabelBackground` and `notRecordingImage`
- Record button hint label `buttonRecordHint` is now TextView
- Default record button drawables have a new look and are now vector drawables for higher quality
  (previously a PNG format image)
- Default status border drawables `trackingStatusBorders` and `failureStatusBorders` now as vector drawables
  for higher quality (previously a PNG format image)
- Fixed `sidewaysWarning` status codes (codes 25 and 26) not being sent when image resource changes between
  `turnLeftImage` and `turnRightImage` when `sidewaysWarning` is already set to visible
- Fixed video encoder crashes