package cubi.casa.exampleproject

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cubi.casa.cubicapture.CubiCapture
import cubi.casa.cubicapture.CubiEventListener
import cubi.casa.cubicapture.TrueNorth
import java.io.File
import java.util.*

/** Example Activity which provides an example implementation
 * and use of the CubiCapture 3.1.1 library module */

class ScanActivity : AppCompatActivity(), CubiEventListener {

    // Declare a lateinit variable for CubiCapture.
    private lateinit var cubiCapture: CubiCapture

    // Boolean 'saving' is used to disable navigation bar back press during saving.
    private var saving = false

    // Boolean 'saved' is used to start ViewScanActivity after a successful scan.
    private var saved = false

    // File 'scanDirectory' is set after a successful scan.
    private var scanDirectory: File? = null

    // SharedPreferences for Settings.
    private val settings by lazy { getSharedPreferences("settings", 0) }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the desired orientation to portrait.
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Set the content view.
        setContentView(R.layout.activity_scan)

        // Initialize the CubiCapture lateinit variable.
        cubiCapture = supportFragmentManager.findFragmentById(R.id.cubiFragment) as CubiCapture

        /* Set 'trueNorth' to 'ENABLED' or 'DISABLED' to enable/disable True North detection.
         * The 'ACCESS_FINE_LOCATION' permission is requested in ScanInfoActivity.kt.
         * Note: 'ENABLED' is the default value. */
        cubiCapture.trueNorth = TrueNorth.ENABLED

        // Add your app's version information to the scan data (optional).
        cubiCapture.appVersion = BuildConfig.VERSION_NAME
        cubiCapture.appBuild = BuildConfig.VERSION_CODE

        // Set the scan output directory name.
        cubiCapture.scanDirectoryName = UUID.randomUUID().toString()

        // Set the directory where all scan directories will be stored.
        cubiCapture.allScansDirectory = getExternalFilesDir(null) ?: filesDir

        // Set the property type.
        cubiCapture.propertyType = intent.getSerializable("propertyType")!!

        // Enable or disable photo capturing (disabled by default).
        cubiCapture.photoCapturingEnabled = true

        /* Enable or disable safe mode based on the user's selection.
         * Safe mode disables ARCore's Depth API to prevent crashes caused by possible bugs in the
         * API. This setting should be shown only for users with Depth API supported devices.
         * Users experiencing stability issues should enable Safe mode. */
        cubiCapture.safeMode = intent.getBooleanExtra("safeMode", false)

        // Override the back press to move the app to the background when saving.
        onBackPressedDispatcher.addOnClickListener(this) {
            if (saving) {
                moveTaskToBack(true)
            } else {
                finish()
            }
        }

        /** -------------------------- UI SETTINGS BELOW -------------------------- */

        /** Show or hide the scan timer (visible by default). */
        // cubiCapture.timerEnabled = false

        /** Show or hide CubiCapture's back button (visible by default). */
        // cubiCapture.backButtonEnabled = false

        /** Enable or disable the record button (enabled by default). */
        // cubiCapture.recordButtonEnabled = false

        /** Enable or disable low storage warnings (enabled by default). */
        // cubiCapture.storageWarningsEnabled = false

        /** Change the warning sound: */
        // cubiCapture.trackingWarningSoundResId = R.raw.new_warning_sound

        /** Modify CubiCapture's default colors, layout sizes, margins, text sizes, and graphics
         * using 'colors.xml', 'dimens.xml', and 'drawables.xml'.
         * For details, see the comments in those files or the documentation in the ExampleProject's
         * README.md file. */

        /** Change CubiCapture's default texts by redefining the strings in 'strings.xml'.
         * For details, see the comments in 'strings.xml' or the documentation in the
         * ExampleProject's README.md file. */

        /** ----------------- ABOUT AUTOMATIC AND MANUAL ZIPPING BELOW ----------------- */

        /** Disable automatic zipping after scan by setting: */
        // cubiCapture.autoZippingEnabled = false // Default is true (auto zips).

        /** Manually zip the scan folder if automatic zipping is disabled.
         * Call this after scan files are saved successfully.
         * Returns the Zip file if successful, or null if zipping failed. */
        // val zipFile = cubiCapture.zipScan(scanFolderPath) // Pass scan folder path as a String.

        /** The manual 'zipScan()' method expects the scan folder to contain these files:
         * arkitData.json, config.json, and video.mp4.
         * If any of these files are missing, 'zipScan()' returns null. */

        /** ----------------- ABOUT AVAILABLE STORAGE SPACE BELOW ----------------- */

        /** Get an estimation in minutes of the maximum scan length the device can store.
         * Pass 'getAvailableStorageMinutes()' the File (directory) containing all the scan folders. */
        // val allScansDirectory = getExternalFilesDir(null) ?: filesDir
        // val minutes: Int = cubiCapture.getAvailableStorageMinutes(allScansDirectory)
    }

    /** Receives the scan folder and the scan zip file */
    override fun onFile(code: Int, file: File) {
        when (code) {
            1 -> { // Scan folder as File
                scanDirectory = file
            }
            2 -> { } // Zip File
        }
    }

    /** Receives status updates of the scan */
    override fun onStatus(code: Int, description: String) {
        Log.d("DEBUGTAG", "code: $code, description: $description") // Logging status updates

        when (code) {
            1 -> { // Started recording
                /* Setting the 'showSafeModeSwitch' Boolean based on if device is Depth API
                * supported or not. */
                val editor = settings.edit()
                editor.putBoolean("showSafeModeSwitch", cubiCapture.depthApiSupported)
                editor.apply()
            }
            2 -> { // Finished recording
                saving = true
            }
            4 -> { // Saving finished
                saved = true
            }
            5 -> { // CubiCapture is finished
                /** You will always receive this code after a scan. To determine if the scan was
                 * successful or not you have to handle the codes received before this code,
                 * e.g. codes 4 and 7.
                 *
                 * For example; When a scan is successful, before code 5 you will receive a code 4
                 * for successful saving of scan data, and then a code 7 for successful zipping of
                 * the scan files (if you have 'autoZipping' enabled).
                 * When a scan is not successful you will not receive code 4, but instead you will
                 * receive an error code (e.g. code 3, "Finished recording - Not enough data."). */
                if (saved && scanDirectory != null) {
                    // Scan was successful. Starting ViewScanActivity to view its video
                    val viewScanIntent = Intent(baseContext, ViewScanActivity::class.java)
                    viewScanIntent.putExtra("scanFolder", scanDirectory)
                    startActivity(viewScanIntent)
                }
                finish()
            }
            19 -> { // Back button press confirmed
                finish()
            }
            3, 12, 13, 28, 54, 64, 66, 79, 105, 106, 107 -> {
                /* 3, Finished recording - Not enough data.
                 * 12, MediaFormat and MediaCodec failed to be configured and started.
                 * 13, Device position is sliding in an unnatural manner.
                 * 14, The device was in the wrong orientation for too long.
                 * 28, ARCore was unable to start tracking during the first five seconds.
                 * 54, Writing of scan data failed: $exception
                 * 64, Unable to start saving. Error: $error
                 * 66, Unable to get correct values for the device's position.
                 * 79, Device ran out of storage space
                 * 105, Unable to create ARCore session. Error: $error
                 * 106, Device is not compatible with ARCore.
                 * 107, Device is too hot to start scanning. */

                // Scan was not successful. Error message will be displayed in ScanInfoActivity
                val displayErrorIntent = Intent()
                displayErrorIntent.putExtra("errorMessage", description)
                setResult(RESULT_OK, displayErrorIntent)
                /** You will receive code 5 after this code */
            }
        }
    }

    /** Override the 'onWindowFocusChanged()' function to call CubiCapture's
     * 'onWindowFocusChanged()' to allow CubiCapture to handle its processes correctly when
     * the current 'Window' of the activity gains or loses focus */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        cubiCapture.onWindowFocusChanged(hasFocus, this)
    }
}
