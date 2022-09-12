Example project using the CubiCapture 2.7.0 library module for Android
======================
This project provides an example implementation and use of the CubiCapture 2.7.0 library module.
From this project you can get the basic idea of how to implement the scanning and scan playback with
CubiCapture to your app.

For your app the next step would be to upload the scan to your server and
use [CubiCasa Conversion API](https://cubicasaconversionapi.docs.apiary.io/#).

# CubiCapture 2.7.0 library module

CubiCapture library module provides a scanning `Fragment` which can be used to scan a floor plan
with an Android device. The scanning `Fragment` saves scan files into a zip file, which your app can
upload to the CubiCasa back-end for processing. CubiCapture also provides a scan playback `Fragment`
which can be used to review the scan and to see any warnings that were shown during the scan, as well
as any room labels that were added.

## Updating to CubiCapture 2.7.0

- Update your app to use the CubiCapture 2.7.0 library module
- Update the new app level `build.gradle` dependencies (see [Implementation](#headimplementation) below)

**Note! If you've previously implemented version 2.6.3 you've probably done the next steps already:**
- If you want property type written to the scan data
(see variable `propertyType` from [Implementation](#headimplementation) below)

**Note! If you've previously implemented version 2.6.0 you've probably done the next steps already:**
- Update your `targetSdkVersion` to API level 31
- If you want to implement the new scan playback `Fragment` to your application,
see [Scan playback](#headscanplayback) and new resources for customization
- If you want to show a reminder view for microphone or location permissions when the permission
can't be requested via Android OS, see [Reminder view](#headreminderview) and new resources for
customization
- If you have customized the texts, text size or background of the reminder view for Location
Services, see [Release Notes](#headreleasenotes) for resource name changes
- Check if you want to handle the new status codes: 95-98. If you handle codes 37 or 38, see the
changes to those status codes

**Note! If you've previously implemented version 2.5.2 you've probably done the next steps already:**
- Change your `CubiCapture.CubiEventListener` interface implementation to `CubiEventListener`
- Check if you want to handle the new status codes: 70-76, 90 and 94
- In previous library module documentation there was a typo where status code `88` was written as `86`.
If you handle code `86`, change it to `88`. This code is received when the user is not scanning too
close to objects anymore and the too close warning is hidden.

**Note! If you've previously implemented version 2.5.1 you've probably done the next steps already:**
- See [Release Notes](#headreleasenotes) if you want to customize CubiCapture's text sizes,
hint label widths or the size of the processing progress bar

**Note! If you've previously implemented version 2.5.0 you've probably done the next steps already:**
- Customization of the CubiCapture has changed. See [Release Notes](#headreleasenotes) for
information about deprecations and how the CubiCapture is customized now
- Check if you want to handle the new status codes: 37, 38, 85, 88 and 93
- If you want to show Location Services reminder -view when the Location Services are turned off
see [Release Notes](#headreleasenotes) for more information
- If your app is using speech recognition and targets Android 11 (API level 30) or above,
you need to query `android.speech.RecognitionService` in your app's `AndroidManifest.xml` file

**Note! If you've previously implemented version 2.4.0 you've probably done the next steps already:**
- Remove deprecated `feedbackGatheringEnabled: Boolean` if you used it
- Check if you want to handle the new status codes (codes 39, 87, 89, 92, 105 and 106)

**Note! If you've previously implemented version 2.3.1 you've probably done the next steps already:**
- Set `allScansFolder: File?` which replaced `allScansFolderName: String`
(see [Implementation](#headimplementation) below)
- If you want to get an estimation in minutes of the maximum scan length the device can store,
see [Release Notes](#headreleasenotes) for more information
- Check if you want to handle the new status codes (codes 78, 91 and 100-102)

**Note! If you've previously implemented version 2.3.0 you've probably done the next steps already:**
- See how to enable/disable the true north detection
(see the end of [Implementation](#headimplementation) below)
- Check if you want to handle the new status codes (codes 31-36 and 67-69)

**Note! If you've previously implemented version 2.2.2 you've probably done the next steps already:**
- If you want to set the enabled status of the record button `View` see
[Release Notes](#headreleasenotes) for more information

**Note! If you've previously implemented version 2.2.1 you've probably done the next steps already:**
- Check if you want to handle the new status codes (codes 65 and 66)
- See if you want to add information about your app's version to be written to the scan data
(see variables `appVersion` and `appBuild` from [Implementation](#headimplementation) below)

**Note! If you've previously implemented version 2.2.0 you've probably done the next steps already:**
- See how to enable/disable speech recognition (see the end of [Implementation](#headimplementation) below)
- Remove calls to CubiCapture lifecycle functions `resume()`, `pause()`, `stop()` and `destroy()`
- Check if you want to handle the new status codes (codes 40-49 and 59-64)


## <a name="headreleasenotes"></a>Release Notes

**2.7.0:**
- Ceiling warning adjustments
- Updated dependencies (see [Implementation](#headimplementation) below)

**2.6.3:**
- Property type can now be added to the scan data with the new variable `propertyType: PropertyType`
(see [Implementation](#headimplementation) below)
- Patch to prevent majority of the native ARCore crashes. Too close -warning is disabled for the
time being and will be enabled again once Google has fixed the issue
- Updated dependencies (see [Implementation](#headimplementation) below)
- Various bug fixes

**2.6.1:**
- Bug fix: Fixed record button not being vertically centered when speech recognition was disabled

**2.6.0:**
- New scan playback `Fragment` available, which can be used to review the scan and to see any warnings
that were shown during the scan, as well as any room labels that were added. See
[Scan playback](#headscanplayback) for more information
- Reminder view for microphone and location permission. See [Reminder view](#headreminderview)
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
(see [Implementation](#headimplementation) below)
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
wrap label text. This is `true` by default. For more information, see [UI Settings](#headuisettings) below
- New customization resources in `dimens.xml` for CubiCapture text sizes, hint label width and
processing progress bar size. See [Dimensions](#headdimensions) below
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
This is `false` by default (see the end of [Implementation](#headimplementation) below).
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
    - For more information about the customization changes, see [UI Settings](#headuisettings) below
- New status codes: 37, 38, 85, 88 and 93. Changes to the `description: String` of the code 24
- CubiCapture's `targetSdkVersion` has been updated to API level 30. Specifying a `targetSdkVersion`
in your project's `build.gradle` or `AndroidManifest.xml` will override the CubiCapture's value
- CubiCapture no longer use the `WRITE_EXTERNAL_STORAGE` permission or request the
`requestLegacyExternalStorage` attribute
- Default scan warning background is now more translucent
- Various bug fixes
- Updated dependencies and new dependency `com.facebook.shimmer:shimmer:0.5.0` for the end scan
confirmation slider (see [Implementation](#headimplementation) below)

**2.4.0:**
- Fast rotation warning. A warning which will trigger if the user turns around too fast while
scanning. This is a customizable `View` `fastMovementWarning: ImageView`
- Deprecated `feedbackGatheringEnabled: Boolean` option
- New status codes (codes 39, 87, 89, 92, 105 and 106)
- UI improvements
- Various bug fixes
- Updated ARCore version to 1.25.0

**2.3.1:**
- Replaced `allScansFolderName: String` with `allScansFolder: File?` (see [Implementation](#headimplementation) below)
- AR Session initializing indication text. This text can be customized by changing the value of
`initializingErrorText: CharSequence`
- Low storage warning. Low storage warning text can be changed by redefining the string in your applications
`strings.xml` file (see [UI Settings](#headuisettings) below)
- New function `getAvailableStorageMinutes(File)` which returns an estimation in minutes of the maximum scan
length the device can store (see the end of [Implementation](#headimplementation) below)
- New variable `feedbackGatheringEnabled: Boolean` to toggle the feedback data gathering, this is `true` by default
- Various bug fixes and optimizations
- New status codes (codes 78, 91 and 100-102)
- Changes to the description message of the error code 45. It now contains the error code of the SpeechRecognizer
- Updated ARCore version to 1.24.0

**2.3.0:**
- Horizontal scanning warning. This is a customizable `View` `horizontalWarning: ImageView`
- True north detection (see the end of [Implementation](#headimplementation) below)
- New status codes (codes 31-36 and 67-69)
- Dropped values from the `arkitData.json` (scan data file). Saving of the scan files is now faster
- Changes to the device orientation detection (landscape, portrait etc)
- Memory optimization
- Fixes to scaling of depth camera intrinsics

**2.2.2:**
- Hint label texts can be changed by redefining the strings in your applications `strings.xml` file
(see the end of [UI Settings](#headuisettings) below)
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
See the end of [UI Settings](#headuisettings) below to see how to customize the graphics,
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


## Glossary

Term | Description
-----|------------
Scan | The process of capturing the surroundings in indoor space using the phone's camera.
ARCore | Google's Augmented Reality library that is used in CubiCapture library for scanning.
Tracking | The process of aligning the device to it's surroundings properly. We use ARCore to track where the phone is relative to the world around it.
Sideways walk | An error which occurs during a scan when the user walks sideways. Walking sideways makes it hard to track the position of the device and can affect the quality of the scan.


## <a name="headimplementation"></a>Implementation
This implementation was made with Android Studio Chipmunk | 2021.2.1 Patch 2
using Gradle plugin version 7.2.2.

Start by [downloading the Android library module](https://sdk-files.s3.us-east-2.amazonaws.com/android/cubicapture-release-2.7.0.aar).

Add the CubiCapture library module to your project:
1. Place the `cubicapture-release-2.7.0.aar` file to your project's `app/libs/` folder.
2. In Android Studio navigate to: `File` -> `Project Structure` -> `Dependencies` -> `app` ->
In the `Declared Dependencies` tab, click `+` and select `JAR/AAR Dependency`.
3. In the `JAR/AAR Dependency` dialog, enter the path as `libs/cubicapture-release-2.7.0.aar` and
select `implementation` as configuration. -> Press `OK`.
4. Check your app's `build.gradle` file to confirm a that in contains the following declaration:
`implementation files('libs/cubicapture-release-2.7.0.aar')`.

Set the `targetSdkVersion` to API level `31` in app level `build.gradle`.

Add the following lines to the app level `build.gradle` inside the `dependencies` branch:
```Groovy
implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
implementation files('libs/cubicapture-release-2.7.0.aar')
implementation 'com.google.ar:core:1.32.0'
implementation 'com.google.code.gson:gson:2.8.6'
implementation 'com.jaredrummler:android-device-names:2.0.0'
implementation 'com.facebook.shimmer:shimmer:0.5.0'

// Implement the following if 'CubiCapture.trueNorth' is set to 'ENABLED' or 'ENABLED_AND_REQUEST':
implementation 'com.google.android.gms:play-services-location:20.0.0'

// Implement the following if using 'ScanPlayback':
implementation 'com.google.android.exoplayer:exoplayer:2.18.1'
implementation 'androidx.recyclerview:recyclerview:1.2.1'
```

If your Gradle plugin version is 4.1.0 or earlier, add the following lines to the app level
`build.gradle` inside the `android` branch:
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

To allow your scanning `Activity` to consume more RAM, to lock screen orientation to landscape
and to prevent multiple `onCreate()` calls add the following code to your `AndroidManifest` file
inside your scanning `Activity`'s `activity` tag - Example:
```xml
<activity
    android:name=".ExampleActivity"
    android:largeHeap="true"
    android:screenOrientation="landscape"
    android:configChanges="orientation|screenSize">
```

Create lateinit variable for CubiCapture to your scanning `Activity`:
```Kotlin
private lateinit var cubiCapture: CubiCapture
```

Initialize your CubiCapture lateinit variable in `onCreate()`:
```Kotlin
cubiCapture = supportFragmentManager.findFragmentById(R.id.cubiFragment) as CubiCapture
```

Implement `CubiEventListener` interface to your scanning `Activity` - Example:
```Kotlin
class ExampleActivity : AppCompatActivity(), CubiEventListener
```

Register `CubiEventListener`'s interface callback in `onCreate()`:
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

Before scanning with CubiCapture you first have to set folder name `scanFolderName: String`
where we store the scan files.
**Note!** You should always check that a scan folder with that name does not already exist,
and that the `String` value of the scan folder name is a valid `File` name!
Example:
```Kotlin
val scanName: String = getScanFolderName()
cubiCapture.scanFolderName = scanName
```

Set the `allScansFolder: File?` to set the directory where CubiCapture will save all the
scan folders. Android 11 Storage updates will restrict where your app can store data.
We suggest the directory returned by `getExternalFilesDir()` for storing scan data.
Just make sure that the storage is available and that the returned `File` is not `null`.
Read more about Android 11 storage updates from
[here](https://developer.android.com/about/versions/11/privacy/storage).
Example:
```Kotlin
val mainStorage: File = getExternalFilesDir(null) ?: getBackupStorage()
cubiCapture.allScansFolder = mainStorage
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

Add property type to be written to the scan data (Optional). Example:
```Kotlin
cubiCapture.propertyType = PropertyType.APARTMENT
```
Possible values for the `propertyType: PropertyType` are
`SINGLE_UNIT_RESIDENTIAL`, `TOWNHOUSE`,`APARTMENT`, `OTHER` and `null` (not written).

Add information about your app's version to be written to the scan data (Optional). Example:
```Kotlin
cubiCapture.appVersion = BuildConfig.VERSION_NAME // e.g. String "1.2.3"
cubiCapture.appBuild = BuildConfig.VERSION_CODE // e.g. Int 25
```

Override the `onWindowFocusChanged()` function to call CubiCapture's `onWindowFocusChanged()`
to allow CubiCapture to handle its processes correctly when the current `Window` of the
activity gains or loses focus:
```Kotlin
override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    cubiCapture.onWindowFocusChanged(hasFocus, this)
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

To keep the screen turned on and in landscape orientation add the following lines below `setContentView()` in `onCreate()`:
```Kotlin
window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
```

CubiCapture requires `CAMERA` permission to be granted. The permission is already declared in
the library's manifest. To avoid crashes, remember to require the permission before starting your
scanning Activity. You should also check that the device has ARCore installed and that it's up-to-date.

To declare your app to be AR Required, add the following entry to your `AndroidManifest.xml` file
inside the `application` tag:
```xml
<!-- "AR Required" app, requires "Google Play Services for AR" (ARCore)
to be installed, as the app does not include any non-AR features. -->
<meta-data android:name="com.google.ar.core" android:value="required" />
```

Remember to always call `finish()` when your activity is done and should be closed to avoid memory leaks.
You should call `finish()` when you receive status code 5 or 19.

To disable the speech recognition and hide its `View`s call:
```Kotlin
cubiCapture.speechRecognitionEnabled = false
```
Speech recognition is enabled (`speechRecognitionEnabled` is set to `true`) by default.
If you are going to use speech recognition you need to declare the
`RECORD_AUDIO` and `INTERNET` permissions in your app's manifest file.

For apps targeting Android 11 (API level 30) or above, interaction with a speech recognition service
requires element to be added to the `AndroidManifest.xml` file:
```xml
<queries>
    <intent>
        <action android:name="android.speech.RecognitionService" />
    </intent>
</queries>
```

#### True north detection

True north detection can be used to enable the CubiCapture to capture the heading relative to the
"true north" to the scan. This information can then be used to add the information the floor plan
(e.g. compass). In order to the true north data to be collected, the Location Services must be
turned on and the location permission must be granted. No position data is collected by the CubiCapture.

True north detection `trueNorth` has the following three settings:

Name | Description
-----|------------
`TrueNorth.DISABLED`| True north detection disabled
`TrueNorth.ENABLED` | True north detection enabled
`TrueNorth.ENABLED_AND_REQUEST` | True north detection enabled and location permission requested by CubiCapture

Example, where we enable the true north detection:
```Kotlin
cubiCapture.trueNorth = TrueNorth.ENABLED
```

`trueNorth` is set to `TrueNorth.ENABLED_AND_REQUEST` by default. If using this setting and location
permission is not granted, CubiCapture will try to request the permission via Android OS.
If the permission can't be requested, CubiCapture will inform the user about the not granted
permission with a reminder view. Pressing the reminder view opens application settings, where user
can grant the permission.

If you are going to use true north detection you need to declare the `ACCESS_COARSE_LOCATION` and
`ACCESS_FINE_LOCATION` permissions in your app's manifest file. You also need to implement the
Google Play services' location library by adding it to the app level `build.gradle` dependencies
(see the `build.gradle` implementation in the start of [Implementation](#headimplementation) above).

If you're going to use the `TrueNorth.ENABLED` setting, you should request the `ACCESS_FINE_LOCATION`
permission on application side. This gives you the possibility to explain the user why your
applications needs the permission before requesting it.

#### Get available storage space estimation

`getAvailableStorageMinutes(File)` is a public function which returns an estimation in minutes
of the maximum scan length the device can store.
This will be useful in a case where user's available storage space is running low and you want to inform the user
about it before starting your scanning activity.

Example:
```Kotlin
val availableMinutes = CubiCapture().getAvailableStorageMinutes(mainStorage)
if (availableMinutes <= 60) {
    showLowStorageWarning(availableMinutes)
}
```

#### <a name="headscanplayback"></a>Scan playback

Scan playback is a `Fragment` which can be used to review the scan and to see any warnings that were
shown during the scan, as well as any room labels that were added.
The `Fragment` has a media control panel which can be used to play/pause, jump between scan events
and video frames one by one. All the scan events can be found in the Events list, which can be used
to easily jump between the events. The active events are displayed in the area above the Events
list. The video playback speed can be changed from the top bar which appears when the video is in
paused state.

To implement the ScanPlayback, first add the required dependencies
(see [Implementation](#headimplementation)).

Add `ScanPlayback` `Fragment` to your projects scan playback's XML layout file:
```xml
<fragment
	android:id="@+id/scanPlayback"
	android:name="cubi.casa.cubicapture.ScanPlayback"
	android:layout_width="match_parent"
	android:layout_height="match_parent" />
```

Create lateinit variable for `ScanPlayback` to your scan playback `Activity`:
```Kotlin
private lateinit var scanPlayback: ScanPlayback
```

Initialize your `ScanPlayback` lateinit variable in `onCreate()`:
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

#### <a name="headreminderview"></a>Reminder view

Reminder view is a view shown by CubiCapture which can be used to remind the user to turn on
Location Services or to grant location or microphone (record audio) permissions.
Note that multiple reminders can be shown at the same time. Reminder view for permission(s) is
shown when the permission can't be requested via Android OS. On Android 11 and later the
permission can't be requested again with the Android's own dialog if the permission has been
denied on the first request.

Reminder view is shown when the device is turned to landscape orientation or
when the speech recognition button is pressed while not recording, and when the conditions for
showing the reminder view apply (e.g. permission is not granted, can't be requested and reminder
is enabled). Pressing the reminder view opens the device's Location settings if the reminder view
is for Location Services or Application settings if the reminder view is for location or microphone
permission. Reminder view disappears by pressing it, when its permission is granted or when the
recording it started.

To enable the reminder view for Location Services, set the `requestLocationServices: Boolean` to
`true` (this is `false` by default), and enable true north detection.
To enable the reminder view for location permission, set the `trueNorth` to `TrueNorth.ENABLED_AND_REQUEST`.
To enable the reminder view for microphone permission, set the `speechRecognitionEnabled` to `true`.

Location Services and location permission are required to enable a compass in the floor plan, and
Microphone permission is required for the speech recognition.

## <a name="headuisettings"></a>UI settings

To set the visibility of scan timer call:
```Kotlin
cubiCapture.setTimerEnabled(false) // Visible (true) by default
```

To set the visibility of CubiCapture's back button call:
```Kotlin
cubiCapture.setBackButtonEnabled(false) // Visible (true) by default
```

To set the enabled status of the record button `View`:
```Kotlin
cubiCapture.recordButtonEnabled(false)
```

To change the warning sound call:
```Kotlin
cubiCapture.setWarningSound(R.raw.new_warning_sound)
```

To replace the CubiCapture's 'statusText: TextView' with your own 'TextView' (example):
```Kotlin
val newView: TextView = findViewById(R.id.newStatusText)
cubiCapture.setNewView(cubiCapture.statusText, newView)
```

Use `matchHintLabelWidth: Boolean` to set hint label widths to match the wider one (`true`),
or to wrap label text (`false`). This is `true` by default.
This is ignored if the speech recognition is disabled. In this case the label width is set to wrap
the label text if the `hint_label_width` is not changed from the default `0dp` in `dimens.xml`.
```Kotlin
cubiCapture.matchHintLabelWidth = false
```

#### Colors and alphas

To change the colors or alphas of the default CubiCapture graphics you have to redefine the
default `color`s in your applications `colors.xml` file.

Here's all the default colors and alphas defined in CubiCapture library:
```xml
<!-- Speech recognition and end recording slider text color -->
<color name="ccTextColor">#FFFFFF</color>

<!-- Hint label's background and stroke color -->
<color name="ccHintColor">#000000</color>
<color name="ccHintStrokeColor">#FFFFFF</color>

<!-- Speech recognition pop-up text's background color-->
<color name="ccPopupColor">#000000</color>

<!-- Speech recognition button colors -->
<color name="ccMicColor">#FFFFFF</color>
<color name="ccMicDisabledColor">#B4808080</color>
<color name="ccCircleColor">#569789</color>
<color name="ccCircleListeningColor">#F90067</color>
<color name="ccCircleDisabledColor">#50808080</color>

<!-- Volume/dB circle's color -->
<color name="ccVolumeCircleColor">#7FF90067</color>

<!-- Recognition results view's colors -->
<color name="ccResultColor">#CC569789</color>
<color name="ccCancelColor">#808080</color>

<!-- Recording button colors -->
<color name="ccRecordingColor">#D83B31</color>
<color name="ccNotRecordingColor">#565656</color>
<color name="ccRecButtonBorderColor">#AAAAAA</color>

<!-- End recording slider background color -->
<color name="ccSliderColor">#C8D83B31</color>

<!-- Status border colors -->
<color name="ccStatusTrackingColor">#569789</color>
<color name="ccStatusFailureColor">#FAB40E</color>

<!-- Scan playback text color for events and playback speed spinner -->
<color name="ccPlaybackMainTextColor">#FFFFFF</color>

<!-- Scan playback text color for "Events", "Frame" and "Time" texts -->
<color name="ccPlaybackControllerTextColor">#569789</color>

<!-- Scan playback main background color -->
<color name="ccPlaybackBackgroundColor">#071d29</color>

<!-- Scan playback top bar background color -->
<color name="ccPlaybackTopBarBackgroundColor">#80071d29</color>

<!-- Scan playback controller background color -->
<color name="ccPlaybackControllerBackgroundColor">#163241</color>

<!-- Scan playback controller button tint -->
<color name="ccPlaybackControllerButtonTint">#569789</color>

<!-- Scan playback event colors -->
<color name="ccPlaybackWarningColor">#EC1667</color>
<color name="ccPlaybackSpaceLabelColor">#569789</color>

<!-- Scan playback timeline colors -->
<color name="ccTimelineBackgroundColor">#3A3A3A</color>
<color name="ccTimelineWarningColor">#80EC1667</color>
<color name="ccTimelineSpaceLabelColor">#CC569789</color>
<color name="ccTimelineThumbColor">#FFFFFF</color>

<!-- Scan playback scrollbar color -->
<color name="ccPlaybackScrollbarColor">#FFFFFF</color>
```

For example, if you want to change the color and alpha of the volume/dB circle
you can define the new color in your applications `colors.xml` file like so:
```xml
<!-- Volume/dB circle's color -->
<color name="ccVolumeCircleColor">#50FF0000</color>
```

The colors can be defined with color notation `#RRGGBB`s or with a color notation
including a hexadecimal alpha value `#AARRGGBB`s.

#### <a name="headdimensions"></a>Dimensions

To change the CubiCapture's default layout sizes, margins and text sizes
you have to override the library's default dimensions by defining the dimensions in your applications
`dimens.xml` file. To override the library's default dimensions, you need to have the `dimens.xml` file created
to the `values` directory where the `colors.xml` file is as well.

Here's all the default dimensions defined in CubiCapture library:
```xml
<!-- Size of the record and speech recognition buttons -->
<dimen name="button_record_size">72dp</dimen>

<!-- Margin end of the record and speech recognition buttons -->
<dimen name="button_record_margin_end">52dp</dimen>

<!-- Hint label width(s). Using '0dp' for the 'hint_label_width' wraps the label text -->
<dimen name="hint_label_width">0dp</dimen>

<!-- Hint label height(s) -->
<dimen name="hint_label_height">52dp</dimen>

<!-- Margin end of the hint label(s) -->
<dimen name="hint_label_margin_end">12dp</dimen>

<!-- Processing progress bar size scale -->
<dimen name="cc_progress_bar_size_scale">1.6</dimen>

<!-- Recording slider text size -->
<dimen name="cc_slider_text_size">20sp</dimen>

<!-- Hint label text size -->
<dimen name="cc_hint_label_text_size">16sp</dimen>

<!-- Speech recognition results text size -->
<dimen name="cc_recognition_results_text_size">20sp</dimen>

<!-- Speech recognition pop-up text size-->
<dimen name="cc_recognition_popup_text_size">16sp</dimen>

<!-- Status text size (Used for ARCore tracking errors for example) -->
<dimen name="cc_status_text_size">16sp</dimen>

<!-- Back button text size -->
<dimen name="cc_back_button_text_size">14sp</dimen>

<!-- Scan timer text size -->
<dimen name="cc_scan_timer_text_size">14sp</dimen>

<!-- Processing text size -->
<dimen name="cc_processing_text_size">16sp</dimen>

<!-- Processing percentage text size -->
<dimen name="cc_percentage_text_size">16sp</dimen>

<!-- Warning title text size -->
<dimen name="cc_warning_title_text_size">44sp</dimen>

<!-- Warning message text size -->
<dimen name="cc_warning_message_text_size">28sp</dimen>

<!-- Text size reminder view's texts -->
<dimen name="cc_reminder_text_size">16sp</dimen>

<!-- Scan playback controller button width percent -->
<dimen name="cc_controller_button_width_percent">0.10</dimen>

<!-- Scan playback small text size (Used for all texts except active events) -->
<dimen name="cc_playback_text_size_small">14sp</dimen>

<!-- Scan playback big text size (Used for active events) -->
<dimen name="cc_playback_text_size_big">20sp</dimen>
```

**Note!** Dimensions `button_record_size` and `button_record_margin_end` define the size or layout
margins of the the record button, end recording slider and speech recognition button, since end
recording slider and speech recognition button are set relative to the record button.

For example, if you want to change the size of the record and speech recognition buttons
you can define the new size in your applications `dimens.xml` file like so:
```xml
<dimen name="button_record_size">80dp</dimen>
```

#### Texts

To change CubiCapture's default texts you have to override the library's default strings
by redefining the strings in your applications `strings.xml` file.

Here's all the default texts defined in CubiCapture library:
```xml
<!-- Hint label texts -->
<string name="cc_record_hint_text"><i>1. Start scanning</i></string>
<string name="cc_speech_hint_text"><i>2. Say the room name</i></string>

<!-- Low storage warning text. Shown in the `statusText: TextView` for 5 seconds in certain intervals
during a scan if the estimation of the maximum scan length the device can store is less than 10 minutes.
Always include '%1$d' in the string. It's a placeholder for the value of the available minutes -->
<string name="cc_low_storage_text">Low storage: %1$d minutes left!</string>

<!-- Slider text -->
<string name="cc_slider_text"><i>Slide to end</i></string>

<!-- Processing text -->
<string name="cc_processing_text">Processing scan.\nPlease do not exit the application.</string>

<!-- Back button texts -->
<string name="cc_back_button_text">Back</string>
<string name="cc_back_button_confirm_text">Are you sure?</string>

<!-- Text of the cancel button in speech recognition results view -->
<string name="cc_cancel_button_text">Cancel</string>

<!-- Speech recognition pop-up texts -->
<string name="cc_ready_for_speech_text">Say the room name</string>
<string name="cc_speech_no_results_text">No results</string>

<!-- ARCore tracking status texts -->
<string name="cc_excessive_motion_text">You are moving too fast</string>
<string name="cc_insufficient_features_text">We have trouble tracking your position
	\nbecause there are not enough visual features here
</string>
<string name="cc_insufficient_light_text">We have trouble tracking your position
	\nbecause it is too dark here
</string>
<string name="cc_initializing_text">Move your device to start tracking</string>

<!-- Recognition results over the max length text.
Always include '%1$d' in the string. It's a placeholder for the value of the maximum characters -->
<string name="cc_results_over_max_length_text">Space name exceeded %1$d characters</string>

<!-- Displayed title for the sideways warning -->
<string name="cc_warning_sideways_title">Do not walk sideways</string>
<!-- Displayed info text for sideways left warning -->
<string name="cc_warning_sideways_left_info">Turn left and point the camera in your walking direction.\nWalk along the wall.</string>
<!-- Displayed info text for sideways right warning -->
<string name="cc_warning_sideways_right_info">Turn right and point the camera in your walking direction.\nWalk along the wall.</string>

<!-- Displayed info text for the rotate warning -->
<string name="cc_warning_rotate_info">Rotate your device 180°.\nMake sure the record button is on the right.</string>

<!-- Displayed title for ceiling scanning warning -->
<string name="cc_warning_ceiling_title">Do not scan ceilings</string>
<!-- Displayed info text for ceiling scanning warning -->
<string name="cc_warning_ceiling_info">We can lose track of your position if ceilings are scanned.</string>

<!-- Displayed title for floor scanning warning -->
<string name="cc_warning_floor_title">Tilt the device back a bit</string>
<!-- Displayed info text for floor scanning warning -->
<string name="cc_warning_floor_info">Point the camera approximately 8ft / 2.5m in front of you.\nKeep at least 3ft / 1m distance from all objects.</string>

<!-- Displayed on the guide screen before the device is turned to landscape -->
<string name="cc_guide_turn_device_info">Turn your device left to landscape orientation</string>

<!-- Displayed on title in the warning view on too much horizontal scanning -->
<string name="cc_warning_tilt_down_title">Tilt the device forward a bit</string>
<!-- Displayed info text for too much horizontal scanning -->
<string name="cc_warning_tilt_down_info">Focus on the floor about 8ft / 2.5m in front of you.</string>

<!-- Displayed in the warning view title in fast rotation warning -->
<string name="cc_warning_fast_rotation_title">You are turning too fast</string>
<!-- Displayed in the warning view info text in fast rotation warning -->
<string name="cc_warning_fast_rotation_info">Avoid fast turns for best scanning results.</string>

<!-- Displayed on the warning view title when user is scanning too close to objects -->
<string name="cc_warning_too_close_title">You are too close</string>
<!-- Displayed on the warning view info text when user is scanning too close to objects -->
<string name="cc_warning_too_close_info">Keep more distance from the objects you are scanning.</string>

<!-- Displayed on the Location Services reminder view's info text when Location Services are
turned off -->
<string name="cc_location_services_reminder_text">Turn on <b>Location Services</b> from
	<b>Quick Settings</b> or <b>Settings</b> to enable a compass in the floor plan</string>
<!-- Displayed on the location permission reminder view's info text when location permission
is not granted -->
<string name="cc_location_permission_reminder_text">Allow <b>Location</b> permission from
	<b>App permission settings</b> to enable a compass in the floor plan</string>
<!-- Displayed on the reminder view's settings button text when Location Services are turned off
or location permission is not granted -->
<string name="cc_open_location_settings_text"><u>Go to <b>Settings</b></u></string>

<!-- Scan playback speed spinner text. Always include '%1$s' in the string. -->
<string name="cc_playback_speed_text">Playback speed: %1$s</string>
<!-- Scan playback speed spinner text for normal speed -->
<string name="cc_playback_normal_speed_text">Normal</string>

<!-- Scan playback events header text -->
<string name="cc_playback_events_header_text">Events</string>

<!-- Scan playback frame text. Always include the '%1$s' in the string!-->
<string name="cc_playback_frame_text">Frame: %1$s</string>
<!-- Scan playback time text. Always include the '%1$s' in the string!-->
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

<!-- Displayed on the speech recognition pop-up view if microphone button is
pressed while recording and microphone permission is not granted and can't be requested -->
<string name="cc_microphone_permission_not_granted">Microphone permission not granted</string>
<!-- Displayed on the microphone permission reminder view's info text when the microphone
button is pressed while not recording and microphone permission is not granted and can't be
requested -->
<string name="cc_microphone_permission_settings_text">Allow <b>Microphone</b> permission from
	<b>App permission settings</b></string>
<!-- Displayed on the microphone permission reminder view's settings button text -->
<string name="cc_open_microphone_settings_text"><u>Go to <b>App settings</b></u></string>
```

#### Drawable graphics

To change CubiCapture's default drawables you have to override them by redefining the drawables in
your applications `drawables.xml` file. You need to have the `drawables.xml` file created to the
`values` directory where the `colors.xml` file is as well.

Here's all the default drawables defined in CubiCapture library:
```xml
<!-- Record button, hint label and end recording slider background -->
<drawable name="cc_not_recording">@drawable/cc_not_recording_original</drawable>
<drawable name="cc_recording">@drawable/cc_recording_original</drawable>
<drawable name="cc_hint_label_background">@drawable/cc_hint_label_background_original</drawable>
<drawable name="cc_slider_background">@drawable/cc_slider_background_original</drawable>

<!-- Status borders -->
<drawable name="cc_status_border_tracking">@drawable/cc_status_border_tracking_original</drawable>
<drawable name="cc_status_border_failure">@drawable/cc_status_border_failure_original</drawable>

<!-- Warning and guide icons -->
<drawable name="cc_turn_device_icon">@drawable/cc_turn_device_original</drawable>
<drawable name="cc_rotate_device_icon">@drawable/cc_rotate_device_original</drawable>
<drawable name="cc_turn_left_icon">@drawable/cc_warning_arrow_left_original</drawable>
<drawable name="cc_turn_right_icon">@drawable/cc_warning_arrow_right_original</drawable>
<drawable name="cc_ceiling_icon">@drawable/cc_warning_arrow_down_original</drawable>
<drawable name="cc_horizontal_icon">@drawable/cc_horizontal_original</drawable>
<drawable name="cc_floor_icon">@drawable/cc_warning_arrow_original</drawable>
<drawable name="cc_fast_movement_icon">@drawable/cc_fast_movement_original</drawable>
<drawable name="cc_too_close_icon">@drawable/cc_too_close_original</drawable>

<!-- Warning and guide backgrounds, which are just colors by default -->
<drawable name="cc_warning_background">#66F90066</drawable>
<drawable name="cc_guide_background">#D9163241</drawable>

<!-- Processing screen background, which is just a color by default -->
<drawable name="cc_processing_background">#071d29</drawable>

<!-- Icon for Location Services and location permission reminders -->
<drawable name="cc_location_icon">@drawable/cc_location_icon_original</drawable>
<!-- Icon for microphone permission reminder -->
<drawable name="cc_microphone_icon">@drawable/cc_microphone_icon_original</drawable>
<!-- Background for reminder view -->
<drawable name="cc_reminder_background">@drawable/cc_location_reminder_background_original</drawable>

<!-- Scan playback timeline thumb -->
<drawable name="cc_timeline_thumb">@drawable/cc_thumb_original</drawable>

<!-- Scan playback back arrow -->
<drawable name="cc_playback_back_arrow">@drawable/cc_back_arrow_original</drawable>
```

For example, if you want to change the default record button drawable graphics to your own drawables,
you need place your new record button drawable files to your applications `drawable` directory and
then define the drawables in your applications `drawables.xml` file like so:
```xml
<drawable name="cc_not_recording">@drawable/not_recording_new</drawable>
<drawable name="cc_recording">@drawable/recording_new</drawable>
```

## About warning priorities

During a scan, CubiCapture shows a warning to the user if it detects
bad scanning styles or any issues with the tracking.
All the warnings are divided into priority level groups.
Higher priority warnings will override and hide any lower priority warning in order to
only show the higher priority warning. As an exception, ceiling warning will override horizontal
warning, although they have the same priority level.
Priority level `1` is the highest priority, `2` is the second highest priority and so on.

Priority level | Warnings
---------------|---------
1 | Not tracking*
2 | Rotate device
3 | Sideways walking, Fast movement, Too close
4 | Ceiling scanning, Floor scanning, Horizontal scanning

*Not tracking: ARCore's `TrackingState` is **not** `TRACKING`,
and the position of the device can not be determined.
In this case, CubiCapture shows a tracking status error message (e.g. `cc_insufficient_features_text`)
in `statusText: TextView` so that we can get the device tracking again as quickly as possible.

## Automatic and manual zipping

To disable the automatic zipping after a scan call:
```Kotlin
cubiCapture.setAutoZippingEnabled(false) // true (auto zips) by default
```

Zipping scan folder if automatic zipping is disabled. This can be called after scan files are
saved successfully. This returns the Zip file if it's is successful or null if zipping failed.
```Kotlin
val zipFile = cubiCapture.zipScan(scanFolderPath) // Pass scan folder path as String
```

Manual zipping `zipScan()` method expects the scan folder to contain the following files;
`arkitData.json`, `config.json` and `video.mp4`.
If any of the files above doesn't exist, `zipScan()` returns `null`.

Here's an example directory structure where `zipScan()` method would successfully zip the scan
folders `ExampleStreet 123` and `AnotherStreet 10`:
```.
.
└── AllScansFolder
    ├── ExampleStreet 123
    │   ├── arkitData.json
    │   ├── config.json
    │   ├── video.mp4
    │   ├── feedback.json
    │   ├── scanLog.json
    │   ├── intrinsics.json
    │   └── allDepthFrames.bin
    └── AnotherStreet 10
        ├── arkitData.json
        ├── config.json
        ├── video.mp4
        ├── feedback.json
        ├── scanLog.json
        ├── intrinsics.json
        └── allDepthFrames.bin
```

Please note that the `allDepthFrames.bin` will be only present for devices which support depth
capturing.

## Status codes

### 0, "Device turned to landscape orientation."
Received when the device is in landscape orientation and either the turn device guidance or
the rotate device warning is hidden.

### 1, "Started recording."
Received when the scan is started by pressing the record button.

### 2, "Finished recording."
Received when user has ended the scan with the slider and scan has enough data.
The saving of the scan files begins after this.

### 3, "Finished recording - Not enough data."
Received when user has ended the scan with the slider and scan does not have enough data.
The scan files will be deleted (code 15) and CubiCapture will be finished after this (code 5).

### 4, "Saving of scan files finished. (Beginning to zip files.)"
Received when the saving of the scan files is finished. Receiving this code means that
the scan has data and scan files are successfully saved without errors.
If auto zipping is enabled, zipping will start after this.

### 5, "CubiCapture is finished. You can now finish your scanning Activity."
You will always receive this code after a scan. To determine if the scan was
successful or not you have to handle the codes received before this code,
e.g. codes 4 and 7.

For example; When a scan is successful, before code 5 you will receive a code 4
for successful saving of scan data, and then a code 7 for successful zipping of
the scan files (if you have auto zipping enabled).
When a scan is not successful you will not receive code 4, but instead you will
receive an error code (e.g. code 3, "Finished recording - Not enough data.").

### 6, "Scan folder: $folderPath"
Received when the saving of the scan files is finished.
The description will contain a path to the scan folder.
To receive the scan folder as a `File` use the `CubiEventListener`'s
`getFile(code: Int, file: File)` method where code 1 receives the scan folder.

### 7, "Zipping is done. Zip file: $zipFilePath"
Received when the zipping of the scan files is finished.
The description will contain a path to the zip file.
To receive the zip file as a `File` use the `CubiEventListener`'s
`getFile(code: Int, file: File)` method where code 2 receives the zip file.

### 8, "ARCore TrackingFailureReason: INSUFFICIENT_LIGHT"
Received when ARCore motion tracking is lost due to poor lighting conditions.
`statusText: TextView`'s `text` is set to string resource `cc_insufficient_light_text`.

### 9, "ARCore TrackingFailureReason: EXCESSIVE_MOTION"
Received when ARCore motion tracking is lost due to excessive motion.
`statusText: TextView`'s `text` is set to string resource `cc_excessive_motion_text`.

### 10, "ARCore TrackingFailureReason: INSUFFICIENT_FEATURES"
Received when ARCore motion tracking is lost due to insufficient visual features.
`statusText: TextView`'s `text` is set to string resource `cc_insufficient_features_text`.

### 11, "ARCore TrackingState is TRACKING."
Received when ARCore is tracking again. Any error text from `statusText: TextView` is removed.

### 12, "MediaFormat and MediaCodec failed to be configured and started."
Received if MediaFormat and MediaCodec fails to be configured and started
when record button is pressed for the first time. You will receive code 5 after this.

### 13, "Scan drifted! Position changed by over 10 meters during 2 second interval."
Received if the position of the device has changed by over 10 meters during a 2 second interval.
The scan files will be deleted (code 15) and CubiCapture will be finished after this (code 5).

### 15, "Error shutdown. Deleting scan folder: $folder-path
Received when the scan is not successful.
The scan files are deleted and CubiCapture will be finished after this (code 5).

### 16, "Portrait to landscape guidance has to be dismissed first in order to start recording."
Received when the record button is pressed and recording cannot be started because turn device
guidance is still visible (device is not in landscape orientation).

### 17, "Device is in reverse-landscape orientation."
Received when the device is in reverse-landscape orientation and if the device has been in
landscape orientation at least once. Rotate device warning is shown.

### 18, "Playing error sound."
Received when an error sound is played and ARCore motion tracking is lost (codes 8, 9 and 10).
Only received if the ARCore was tracking before the tracking is lost to avoid playing the error sound
multiple times in a row.
(The ARCore `TrackingFailureReason` might change (between codes 8, 9 and 10) during a short period of time).

### 19, "Back button pressed twice. You can now finish your scanning Activity."
Received when the CubiCapture's back button is pressed twice. You should call `finish()`.

### 20, "Name of the scan output folder has to be set first. Set .scanFolderName"
Received when the record button is pressed and recording cannot be started because `scanFolderName`
has not been set.

### 21, "Scanning floor."
Received when the pitch of the camera has been too low for a certain amount of time
and the floor warning is shown. Only received if there's no higher priority warnings visible.

### 22, "Scanning ceiling."
Received when the pitch of the camera has been too high for a certain amount of time
and the ceiling warning is shown. Only received if there's no higher priority warnings visible.

### 23, "Not scanning ceiling or floor anymore."
Received when the pitch of the camera is valid again and the ceiling or the floor warning is hidden.

### 24, "ARCore TrackingState is PAUSED."
Received when ARCore's `TrackingState` is `PAUSED` and pitch of the camera cannot be calculated.
The ceiling or the floor warning to hidden.

### 25, "Walking sideways. Displaying turn left warning."
Received when the user is walking sideways to the left while the camera is pointing forward.
Sideways left warning is shown.

### 26, "Walking sideways. Displaying turn right warning."
Received when the user is walking sideways to the right while the camera is pointing forward.
Sideways right warning is shown.

### 27, "Not walking sideways anymore."
Received when the user is not walking sideways anymore and the sideways warning is hidden.
Sideways warning is always displayed for at least a certain amount of time to avoid quick flashes
of guidance images.

### 28, "ARCore was unable to start tracking during the first five seconds."
Received if the ARCore is unable to start tracking during the first five seconds.
The scan files will be deleted (code 15) and CubiCapture will be finished after this (code 5).

### 31, "Horizontal scanning."
Received when the pitch of the camera has been too horizontal for a certain amount of time
and horizontal warning is shown.

### 32, "Not scanning horizontally anymore."
Received when the pitch of the camera is valid again (not scanning horizontally) for a certain
amount of time, and horizontal warning is hidden.

### 33, "Requesting LOCATION permissions."
Received when `trueNorth` is set to `TrueNorth.ENABLED_AND_REQUEST` and the `ACCESS_FINE_LOCATION`
permission is not granted. Requests `ACCESS_FINE_LOCATION` permission to be granted to this application.

### 34, "User granted LOCATION permissions."
Received when user has granted the `ACCESS_FINE_LOCATION` permission and `trueNorth` is set to
`TrueNorth.ENABLED_AND_REQUEST`.

### 35, "User denied LOCATION permissions."
Received when user has denied the `ACCESS_FINE_LOCATION` permission and `trueNorth` is set to
`TrueNorth.ENABLED_AND_REQUEST`.

### 36, "LOCATION permission is not granted. Not saving true north."
Received when the `ACCESS_FINE_LOCATION` permission is not granted and `trueNorth` is set to
`TrueNorth.ENABLED`. Not saving true north.

### 37, "Showing Location reminder view."
Received when the Location reminder view is shown. Location reminder view is used for both Location
Services and location permission. The reminder view disappears by pressing it, when the recording is
started or when the location permission is granted if the reminder view was for location permission.

### 38, "Location reminder view pressed. Opening settings."
Received when the Location reminder view is pressed. Opening Location Settings if reminder view is
for Location Services or application settings if the reminder view is for location permission.

### 39, "Unable to start listening for speech. Error: $error"
Received if the speech recognition service fails to allow access to start listening for speech.

### 40, "Started listening for speech."
Received when the speech recognition button is pressed and speech recognition starts
listening for speech. This requires that the scan is started (recording), and that the
`RECORD_AUDIO` permission is granted.

### 41, "Listening finished, displaying recognition results."
Received when the speech recognition stops listening for speech and the
recognition results are ready and displayed.

### 42, "Recognition result '$result' was chosen."
Received when one of the recognition results is pressed.

### 43, "Recognition results canceled."
Received when the recognition results are canceled by pressing the Cancel -button.

### 44, "Speech recognition aborted."
Received when the speech recognition is aborted by pressing the speech recognition button
while speech recognition was listening for speech.

### 45, "No speech recognition results. Error: $error"
Received when the speech recognition doesn't return any results. `error: Int` is defined in
[SpeechRecognizer](https://developer.android.com/reference/android/speech/SpeechRecognizer)

### 46, "All recognition results over the max length of 40 characters."
Received when all the recognition results are over the max length of 40 characters.
Results will not be displayed, instead, the string resource `cc_results_over_max_length_text`
will be displayed in the speech recognition pop-up view next to speech recognition button.

### 47, "Requesting RECORD_AUDIO permission."
Received when the speech recognition button is pressed and the `RECORD_AUDIO` permission
is not granted. Requests `RECORD_AUDIO` permission to be granted to this application.

### 48, "User granted RECORD_AUDIO permission."
Received when user has granted the `RECORD_AUDIO` permission.

### 49, "User denied RECORD_AUDIO permission."
Received when user has denied the `RECORD_AUDIO` permission.

### 50, "MediaMuxer.writeSampleData Exception: $exception"
Received if the video encoder fails to write an encoded sample into the muxer.

### 51, "Zipping failed! You can try zipping again with .zipScan($scanFolderPath)"
Received if the automatic zipping fails.

### 52, "Exception on the OpenGL thread: $throwable"
Received if there's an exception on the OpenGL thread.

### 53, "Failed to read an asset file: $exception"
Received if camera video preview surface fails to be initialized.

### 54, "Writing of scan data failed: $exception"
Received if the writing of the scan data fails.
The scan files will be deleted (code 15) and CubiCapture will be finished after this (code 5).

### 55, "InputBuffer IllegalStateException: $exception"
Received if the input buffer is not in Executing state.

### 56, "MediaCodec.stop() exception: $exception"
Received if the finishing of the encode session fails.

### 57, "MediaMuxer.stop() exception: $exception"
Received if the stopping of the muxer fails.

### 58, "MediaCodec.releaseOutputBuffer() exception: $exception"
Received if the releasing of the output buffer fails.

### 59, "Failed to print camera intrinsics!"
Received if printing `intrinsics.json` file to scan folder fails.

### 60, "dequeueOutputBuffer IllegalStateException: $exception"
Received if dequeuing an output buffer fails.

### 61, "getOutputBuffer IllegalStateException: $exception"
Received if the getting of the output buffer fails.

### 62, "queueInputBuffer IllegalStateException: $exception"
Received if queueing an input buffer to the codec fails.

### 63, "Depth sensor is no longer producing depth data!"
Received if device's depth (time-of-flight) sensor is no longer producing depth data.
The scan will be saved as a regular (non-depth) scan.

### 64, "Unable to start saving. Error: $error"
Received if the conditions for saving are not met.
The scan files will be deleted (code 15) and CubiCapture will be finished after this (code 5).

### 65, "Frame processing exception: $exception"
Received if the frame processing fails.

### 66, "Unable to get correct values for the device's position."
Received if ARCore has been unable to return correct values for the device's position for a certain
amount of time.
The scan files will be deleted (code 15) and CubiCapture will be finished after this (code 5).

### 67, "Location Services are off."
Received if the device has Location Services turned off on start. Only received if true north
detection is enabled and `ACCESS_FINE_LOCATION` permission is granted.

### 68, "Not able to get true north because Location Services were still off once recording was started."
Received if the device had Location Services turned off once recording was started.
Only received if true north detection is enabled and `ACCESS_FINE_LOCATION` permission is granted.

### 69, "Sensor is reporting true north data with low or unreliable accuracy. Not saving these true north values."
Received if the sensor is reporting true north data with low or unreliable accuracy. These values
cannot be trusted so true north detection is not saving these values. Only received if true north
detection is enabled and running.

### 70, "Thermal state nominal"
Received if thermal state changes to nominal.

### 71,	"Thermal state light"
Received if thermal state changes to light.

### 72,	"Thermal state moderate"
Received if thermal state changes to moderate.

### 73,	"Thermal state severe"
Received if thermal state changes to severe.

### 74,	"Thermal state critical"
Received if thermal state changes to critical.

### 75,	"Thermal state emergency"
Received if thermal state changes to emergency.

### 76,	"Thermal state shutdown"
Received if thermal state changes to shutdown.

### 78, "Storage: $minutes minutes left"
Received once on start when the fragment's activity has been created and in certain intervals during a scan
while recording. `minutes: Int` is an estimation in minutes of the maximum scan length the device can store.

### 85, "Scanning too close. Showing proximity warning."
Received if the user is scanning too close to objects and too close warning is shown.

### 87, "Too fast rotations. Showing fast movement warning."
Received when the user turns around too fast while scanning and fast movement warning is shown.

### 88, "Scan range normal. Hiding proximity warning."
Received when the user is not scanning too close to objects anymore and the too close warning is hidden.
Too close warning is always displayed for at least a certain amount of time to avoid quick flashes
of warnings.

### 89, "Not moving too fast anymore."
Received when the user is not turning around too fast anymore and the fast movement warning is hidden.
Fast movement warning is always displayed for at least a certain amount of time to avoid quick
flashes of warnings.

### 90, "Failed to write scan log data. $exception"
Received if writing of the `scanLog.json` file to scan folder fails.

### 91, "Failed to write feedback data. $exception"
Received if writing of the `feedback.json` file to scan folder fails.

### 92, "Gyroscope sensor not available. Not able to detect fast movements."
Received if there's no gyroscope sensor available.
CubiCapture will not be able to detect fast movements.

### 93, "Proximity detection exception: $exception"
Received if the proximity detection fails.

### 94, "Rotation vector sensor is not supported. Using accelerometer and magnetometer instead."
Received if the rotation vector sensor is not supported on device. True north data will be acquired
by using accelerometer and magnetometer instead.

### 95, "Speech recognition service is not available on the system."
Received when the speech recognition service is not available on the system. Speech recognition will
be disabled and the speech recognition user interface will not be displayed.

### 96, "LOCATION permission can't be requested. Location reminder view will be displayed later."
Received when the location permission can't be requested via Android OS and `trueNorth` is set to
`TrueNorth.ENABLED_AND_REQUEST`. Reminder view for location permission will be displayed when the
device is turned to landscape orientation.

### 97, "RECORD_AUDIO permission can't be requested. Informing user."
Received when the speech recognition button is pressed but the record audio permission can't be
requested via Android OS. If not recording, a reminder view for record audio permission will be
displayed. If recording, a speech recognition pop-up view will be displayed with the text of the
string resource `cc_microphone_permission_not_granted`.

### 98, "Microphone open settings view pressed. Opening settings."
Received when the reminder view for record audio permission is pressed. Opening application settings
where user can grant the record audio permission.

### 100, "ARCore session is initializing"
Received if the ARCore session is initializing normally.
`statusText: TextView`'s `text` is set to string resource `cc_initializing_text` if the turn device
guidance is not shown.

### 101, "ARCore session is initialized"
Received if the ARCore session is initialized and ARCore's `TrackingState` is `TRACKING`.
Only received when the recording has not been started. While recording, status code 11 is received instead.
`statusText: TextView`'s `text` is reset.

### 102, "ARCore session has to be initialized first in order to start recording."
Received when the record button is pressed and recording cannot be started because ARCore session is still initializing.

### 105, "Unable to create ARCore session. Error: $error"
Received if an internal error occurred while creating the ARCore session.
You will receive code 5 after this.

### 106, "Device is not compatible with ARCore."
Received if the device is not compatible with ARCore. If encountered after completing the
installation check, this usually indicates that ARCore has been side-loaded onto an incompatible
device. You will receive code 5 after this.