package cubi.casa.exampleproject

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import cubi.casa.cubicapture.CubiCapture
import cubi.casa.cubicapture.CubiEventListener
import cubi.casa.cubicapture.PropertyType
import cubi.casa.cubicapture.TrueNorth
import java.io.File

/** Example Activity which provides an example implementation
 * and use of the CubiCapture 2.10.1 library module */

class ScanActivity : AppCompatActivity(), CubiEventListener {

    // Create lateinit variable for CubiCapture
    private lateinit var cubiCapture: CubiCapture

    // Boolean 'saving' is used to disable navigation bar back press during saving
    private var saving = false

    // Boolean 'saved' is used to start ViewScanActivity after successful scan
    private var saved = false

    // File 'scanFolder' is set after successful scan
    private var scanFolder: File? = null

    // SharedPreferences for Settings
    private val settings by lazy { getSharedPreferences("settings", 0) }

    /** CubiCapture requires CAMERA permission to be granted.
     * Remember to request the permission before you start your scanning Activity.
     * To avoid crashes you should also check that the device's ARCore version is up-to-date. */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        // To keep the screen turned on and in landscape orientation
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Getting order info from extras
        val orderInfo = getOrderInfoFromExtras()

        // Initialize CubiCapture lateinit variable
        cubiCapture = supportFragmentManager.findFragmentById(R.id.cubiFragment) as CubiCapture

        // Register CubiEventListener's interface callback
        cubiCapture.registerCallback(this)

        /* 'trueNorth' set to 'ENABLED' to enable True North detection.
        'ACCESS_FINE_LOCATION' permission is requested on the app side (in ScanInfoActivity.kt) */
        cubiCapture.trueNorth = TrueNorth.ENABLED // 'ENABLED_AND_REQUEST' by default

        /* Show Location Services reminder -view if Location Services are turned off
        and Location permission is granted for the app */
        cubiCapture.requestLocationServices =
            permissionIsGranted(Manifest.permission.ACCESS_FINE_LOCATION)

        // Add information about your app's version to be written to the scan data (Optional)
        cubiCapture.appVersion = BuildConfig.VERSION_NAME
        cubiCapture.appBuild = BuildConfig.VERSION_CODE

        // Before scanning with CubiCapture you first have to set scan output folder name
        cubiCapture.scanFolderName = orderInfo[0]

        /* You also need to set the File (directory) which contains all the scan folders.
        We suggest the directory returned by 'getExternalFilesDir(null)' for storing scan data.
        Just make sure that the storage is available and that the returned File is not null. */
        cubiCapture.allScansFolder = getExternalFilesDir(null)

        // Before starting the scan you can add order information (not required)
        cubiCapture.setOrderInfo(
            orderInfo[0], // street
            orderInfo[1], // number
            orderInfo[2], // suite
            orderInfo[3], // city
            orderInfo[4], // state
            orderInfo[5], // country
            orderInfo[6] // postalCode
        )

        /* Enabling or disabling the safe mode based on the user's selection.
         * Safe mode disables the ARCore's Depth API to prevent any native ARCore crashes caused by
         * any possible bugs in ARCore's Depth API.
         * Safe mode setting should only be shown for users with Depth API supported devices.
         * User with a Depth API supported device should turn on Safe mode if they are experiencing
         * stability issues while scanning. */
        cubiCapture.safeMode = intent.getBooleanExtra("safeMode", false)

        // Overriding the back press to move the app to background when saving
        onBackPressedDispatcher.addOnClickListener(this) {
            if (saving) {
                moveTaskToBack(true)
            } else {
                finish()
            }
        }

        /** -------------------------- SPEECH RECOGNITION BELOW -------------------------- */

        /** To disable the speech recognition and hide its `View`s call: */
        // cubiCapture.speechRecognitionEnabled = false

        /** Speech recognition is enabled ('speechRecognitionEnabled' is set to 'true') by default.
         * If you are going to use speech recognition you need to declare the
         * 'RECORD_AUDIO' and 'INTERNET' permissions in your app's manifest file. */

        /** -------------------------- UI SETTINGS BELOW -------------------------- */

        /** To show/hide scan timer. Visible (true) by default */
        // cubiCapture.setTimerEnabled(false) // Visible (true) by default

        /** To show/hide the CubiCapture's back button. Visible (true) by default */
        // cubiCapture.setBackButtonEnabled(false)

        /** To enable/disable the record button. Enabled (true) by default */
        // cubiCapture.recordButtonEnabled(false)

        /** To enable/disable low storage warnings. Enabled (true) by default */
        // cubiCapture.storageWarningsEnabled = false

        /** To change the warning sound call: */
        // cubiCapture.setWarningSound(R.raw.new_warning_sound)

        /** To replace the CubiCapture's 'statusText: TextView' with your own 'TextView':*/
        // val newView: TextView = findViewById(R.id.newStatusText)
        // cubiCapture.setNewView(cubiCapture.statusText, newView)

        /** Set hint label widths to match the wider one ('true') or to wrap label text ('false').
         * This is 'true' by default. This is ignored if the speech recognition is disabled.
         * In this case the label width is set to wrap the label text if the 'hint_label_width' is
         * not changed from the default '0dp' in 'dimens.xml'. */
        // cubiCapture.matchHintLabelWidth = false

        /** CubiCapture's default colors, layout sizes, margins, text sizes and graphics can be
         * modified by using the 'colors.xml', 'dimens.xml' and 'drawables.xml' files.
         * To see how to do that, see the comments in those files or the documentation
         * (ExampleProject's README.md file) of this library module version. */

        /** CubiCapture's default texts can be changed by redefining the strings in the
         * 'strings.xml' file.
         * To see how to do that, see the comments in the 'strings.xml' file or the documentation
         * (ExampleProject's README.md file) of this library module version. */

        /** ----------------- ABOUT AUTOMATIC AND MANUAL ZIPPING BELOW ----------------- */

        /** To disable automatic zipping after scan call: */
        // cubiCapture.setAutoZippingEnabled(false) // true (auto zips) by default

        /** Zipping scan folder if automatic zipping is disabled.
         * This can be called after scan files are saved successfully.
         * This returns the Zip file if it's is successful or null if zipping failed. */
        // val zipFile = cubiCapture.zipScan(scanFolderPath) // Pass scan folder path as String

        /** Manual zipping 'zipScan()' method expects the scan folder to contain the following files;
         * arkitData.json, config.json and video.mp4.
         * If any of the files doesn't exist, 'zipScan()' returns null. */

        /** ----------------- ABOUT AVAILABLE STORAGE SPACE BELOW ----------------- */

        /** To get an estimation in minutes of the maximum scan length the device can store.
         * Pass 'getAvailableStorageMinutes()' the File (directory) which contains all the scan
         * folders */
        // val minutes: Int = cubiCapture.getAvailableStorageMinutes(getExternalFilesDir(null)!!)
    }

    /** Gets the property type to be written to the scan data */
    override fun getPropertyType(): PropertyType {
        return intent.getSerializable("propertyType")!!
    }

    /** Receives the scan folder and the scan zip file */
    override fun onFile(code: Int, file: File) {
        when (code) {
            1 -> { // Scan folder as File
                scanFolder = file
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
                if (saved && scanFolder != null) {
                    // Scan was successful. Starting ViewScanActivity to view its video
                    val viewScanIntent = Intent(baseContext, ViewScanActivity::class.java)
                    viewScanIntent.putExtra("scanFolder", scanFolder)
                    startActivity(viewScanIntent)
                }
                finish()
            }
            19 -> { // Back button pressed twice
                finish()
            }
            3, 12, 13, 28, 54, 64, 66, 79, 105, 106, 107 -> {
                /* 3, Finished recording - Not enough data.
                 * 12, MediaFormat and MediaCodec failed to be configured and started.
                 * 13, Scan drifted! Position changed by over 10 meters during 2 second interval.
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

    // Returns order info as Array<String>
    private fun getOrderInfoFromExtras(): Array<String> {
        val extras = intent.extras
        if (extras != null && intent.hasExtra("orderInfo")) {
            val orderInfo = extras.getStringArray("orderInfo")

            if (orderInfo != null && orderInfo.size == 7)
                return orderInfo
        }
        return arrayOf("ExampleStreet", "", "", "", "", "", "")
    }
}
