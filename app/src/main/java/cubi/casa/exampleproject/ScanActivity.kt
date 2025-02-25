package cubi.casa.exampleproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cubi.casa.cubicapture.CubiCapture
import cubi.casa.cubicapture.utils.CubiEventListener
import cubi.casa.cubicapture.utils.TrueNorth
import java.io.File
import java.util.*

/**
 * Example Activity demonstrating the implementation and usage of
 * the CubiCapture 3.2.2 library module.
 */
class ScanActivity : AppCompatActivity(), CubiEventListener {

    // Declare a lateinit variable for CubiCapture, initialized in onCreate().
    private lateinit var cubiCapture: CubiCapture

    // Used to disable navigation bar back press during saving.
    private var saving = false

    // Indicates if the scan has been successfully saved to start ViewScanActivity.
    private var saved = false

    // Directory for the current scan, set after a successful scan.
    private var scanDirectory: File? = null

    // SharedPreferences instance for user settings.
    private val settings by lazy { getSharedPreferences("settings", 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        // Initialize CubiCapture fragment.
        cubiCapture = supportFragmentManager.findFragmentById(R.id.cubiFragment) as CubiCapture

        /** -------------------------- REQUIRED SETTINGS -------------------------- */

        // Set the directory where all the scan directories will be stored.
        cubiCapture.allScansDirectory = getExternalFilesDir(null) ?: filesDir

        // Set the scan output directory name.
        cubiCapture.scanDirectoryName = UUID.randomUUID().toString()

        // Set the property type.
        cubiCapture.propertyType = intent.getSerializable("propertyType")!!

        /** -------------------------- OPTIONAL SETTINGS -------------------------- */

        // Enable or disable photo capturing. Disabled by default.
        cubiCapture.photoCapturingEnabled = true

        /* Configure True North detection to 'TrueNorth.ENABLED' or 'TrueNorth.DISABLED'.
         * - Default: TrueNorth.ENABLED
         * - Note: 'TrueNorth.ENABLED' requires the 'ACCESS_FINE_LOCATION' permission to be declared
         *   in the manifest file, and 'com.google.android.gms:play-services-location' added to the
         *   app level 'build.gradle' dependencies.
         *   For True North data to be collected:
         *   1. Location Services must be turned on.
         *   2. The location permission must be granted by the user.
         * - No position data is collected by CubiCapture. */
        cubiCapture.trueNorth = TrueNorth.ENABLED

        /* Configure Safe mode based on the user's selection.
         * - Default: Disabled (false).
         * - Safe mode disables ARCore's Depth API to prevent crashes caused by potential
         *   bugs in the API.
         * - This setting should only be displayed to users with devices that support the Depth API.
         * - Users who experience stability issues should enable Safe mode for a more reliable
         *   experience. */
        cubiCapture.safeMode = intent.getBooleanExtra("safeMode", false)

        // Add app version and build information to the scan data.
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        cubiCapture.appVersion = packageInfo.versionName
        cubiCapture.appBuild = packageInfo.longVersionCode.toInt()

        // Handle back press: Move to background if saving; otherwise, finish activity.
        onBackPressedDispatcher.addOnClickListener(this) {
            if (saving) {
                moveTaskToBack(true)
            } else {
                finish()
            }
        }

        /** -------------------------- UI SETTINGS -------------------------- */

        // Position of the capture photo button. By default, it’s on the right.
        // cubiCapture.isCapturePhotoButtonOnRight = false

        // Whether to initialize and display the scan timer. Initialized and visible by default.
        // cubiCapture.timerEnabled = false

        // Whether to display the CubiCapture's close button. Visible by default.
        // cubiCapture.closeButtonEnabled = false

        // Enable or disable the record button. Enabled by default.
        // cubiCapture.recordButtonEnabled = false

        // Enable or disable low storage warnings. Enabled by default.
        // cubiCapture.storageWarningsEnabled = false

        // Change the tracking warning sound.
        // cubiCapture.trackingWarningSoundResId = R.raw.new_warning_sound

        /* Modify UI elements like colors, sizes, margins, and graphics in:
         * - colors.xml
         * - dimens.xml
         * - drawables.xml
         * Refer to comments in those files or ExampleProject's README.md for details. */

        /* Update CubiCapture default texts in strings.xml.
         * Refer to comments in strings.xml or ExampleProject's README.md for details. */

        /** ----------------- AUTOMATIC AND MANUAL ZIPPING ----------------- */

        // Enable or disable automatic zipping after a scan. Enabled by default.
        // cubiCapture.autoZippingEnabled = false

        /* Example usage of the ScanZipper.zip method to manually compress scan files.
         * Use this method if automatic zipping is disabled and ensure scan files are saved
         * successfully.
         *
         * The zipping process runs in a background coroutine to avoid blocking the main thread.
         * On success, the created zip file is passed to the success callback.
         * On failure, the error callback receives an exception.
         * Both callbacks are always executed on the main thread.
         *
         * If any required files are missing, the error callback is invoked with a
         * MissingRequiredFilesException. */
        /*
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
         */

        /** ----------------- STORAGE SPACE ESTIMATION ----------------- */

        /* Example usage of the `estimatePreScanStorageMinutes` method to estimate the scan duration
         * that can be stored on the device before starting a scan. */
        // val allScansDirectory = getExternalFilesDir(null) ?: filesDir
        // val estimatedMinutes: Int = CubiCapture.estimatePreScanStorageMinutes(allScansDirectory)

        /* Example usage of the `estimateRemainingStorageMinutes` method to estimate the remaining
         * scan duration during an ongoing scan.
         *
         * Throws `IllegalStateException` if the `allScansDirectory` is not initialized. */
        // val remainingMinutes: Int = cubiCapture.estimateRemainingStorageMinutes()
    }

    /**
     * Receives the scan directory and the scan zip file.
     */
    override fun onFile(code: Int, file: File) {
        when (code) {
            1 -> { // Scan directory as File
                scanDirectory = file
            }
            2 -> { } // Zip File
        }
    }

    /**
     * Receives status updates related to the scanning process.
     */
    override fun onStatus(code: Int, description: String) {
        Log.d("DEBUGTAG", "code: $code, description: $description") // Logging status updates

        when (code) {
            1 -> { // Started recording
                // Setting the 'showSafeModeSwitch' Boolean to indicate whether the device supports
                // the Depth API.
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
                /**
                 * This code is always received after a scan. To determine whether the scan was
                 * successful, you need to handle the preceding codes (e.g., codes 4 and 7).
                 *
                 * For example:
                 * - On a successful scan, you'll first receive code 4 (indicating successful
                 *   saving of scan data), followed by code 7 (indicating successful zipping of scan
                 *   files, if 'autoZipping' is enabled).
                 * - On an unsuccessful scan, code 4 will be skipped and an error code (e.g.,
                 *   code 3: "Finished recording - Not enough data") will be received instead.
                 */
                if (saved && scanDirectory != null) {
                    // Scan was successful. Starting ViewScanActivity to view its video
                    val viewScanIntent = Intent(baseContext, ViewScanActivity::class.java)
                    viewScanIntent.putExtra("scanFolder", scanDirectory)
                    startActivity(viewScanIntent)
                }
                finish()
            }
            19 -> { // Close button press confirmed
                finish()
            }
            3, 13, 14, 28, 54, 56, 66, 79, 105, 106, 107, 141, 142 -> {
                /* 3, Finished recording - Not enough data.
                 * 13, Device position is sliding in an unnatural manner.
                 * 14, The device was in the wrong orientation for too long.
                 * 28, ARCore was unable to start tracking during the first five seconds.
                 * 54, Failed to write AR data file: $exception
                 * 56, Failed to write config file: $exception
                 * 66, Unable to get correct values for the device's position.
                 * 79, Device ran out of storage space.
                 * 105, Unable to create ARCore session: $exception
                 * 106, Device is not compatible with ARCore.
                 * 107, Device is too hot to start scanning.
                 * 141, Failed to initialize the encoder: $exception
                 * 142, Encoding timed out due to frame mismatch: $exception */

                // Scan was not successful. Error message will be displayed in ScanInfoActivity
                val displayErrorIntent = Intent()
                displayErrorIntent.putExtra("errorMessage", description)
                setResult(RESULT_OK, displayErrorIntent)
                /** You will receive code 5 after this code */
            }
        }
    }

    /**
     * Override the Activity#onWindowFocusChanged(hasFocus: Boolean) method to call
     * CubiCapture#onWindowFocusChanged(hasFocus: Boolean, activity: Activity) to allow CubiCapture
     * to handle its internal processes accordingly based on the window focus change.
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        cubiCapture.onWindowFocusChanged(hasFocus, this)
    }
}
