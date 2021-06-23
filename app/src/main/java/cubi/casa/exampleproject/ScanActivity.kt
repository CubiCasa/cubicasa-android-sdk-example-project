package cubi.casa.exampleproject

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import cubi.casa.cubicapture.CubiCapture
import cubi.casa.cubicapture.TrueNorth
import java.io.File

/** Read the CubiCapture documentation at:
 * https://www.cubi.casa/developers/cubicasa-android-sdk */

/** Example Activity which provides an example implementation
 * and use of the CubiCapture 2.3.1 library module */

class ScanActivity : AppCompatActivity(), CubiCapture.CubiEventListener {

    // Create lateinit variable for CubiCapture
    private lateinit var cubiCapture: CubiCapture

    // Boolean 'saving' is used to disable navigation bar back press during saving
    private var saving = false

    // Boolean 'saved' is used to start ViewScanActivity after successful scan
    private var saved = false

    // File 'scanFolder' is set after successful scan
    private var scanFolder: File? = null

    private var errorMessage: String? = null

    /** CubiCapture requires CAMERA and WRITE_EXTERNAL_STORAGE permissions to be granted.
     * Remember to request those permissions before you start your scanning Activity.
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

        // Add information about your app's version to be written to the scan data (Optional)
        cubiCapture.appVersion = BuildConfig.VERSION_NAME
        cubiCapture.appBuild = BuildConfig.VERSION_CODE

        // Before scanning with CubiCapture you first have to set scan output folder name
        cubiCapture.scanFolderName = orderInfo[0]

        /* You also need to set the File (directory) which contains all the scan folders.
        Android 11 Storage updates will restrict where your app can store data.
        We suggest the directory returned by 'getExternalFilesDir(null)' for storing scan data.
        Just make sure that the storage is available and that the returned File is not null.
        Read more about Android 11 storage updates from here:
        https://developer.android.com/about/versions/11/privacy/storage */
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

        /** -------------------------- SPEECH RECOGNITION BELOW -------------------------- */

        /** To disable the speech recognition and hide its `View`s call: */
        // cubiCapture.speechRecognitionEnabled = false

        /** Speech recognition is enabled ('speechRecognitionEnabled' is set to 'true') by default.
         * If you are going to use speech recognition you need to declare the
         * 'RECORD_AUDIO' and 'INTERNET' permissions in your app's manifest file. */

        /** -------------------------- UI SETTINGS BELOW -------------------------- */

        /** To hide scan timer call: */
        // cubiCapture.setTimerEnabled(false) // Visible (true) by default

        /** To hide CubiCapture's back button call: */
        // cubiCapture.setBackButtonEnabled(false) // Visible (true) by default

        /** To set the enabled status of the record button 'View': */
        // cubiCapture.recordButtonEnabled(false)

        /** To change CubiCapture's ARCore tracking error texts: */
        // cubiCapture.excessiveMotionErrorText = "Excessive motion!"
        // cubiCapture.insufficientErrorText = "Insufficient visual features or poor lighting!"
        // cubiCapture.initializingErrorText = "AR Session is initializing normally."

        /** To change speech recognition's pop-up texts: */
        // cubiCapture.speechNoResultsText = "No results"
        // cubiCapture.readyForSpeechText = "Say the room name"

        /** To change the warning sound call: */
        // cubiCapture.setWarningSound(R.raw.new_warning_sound)

        /**
         * The following CubiCapture Views are customizable:
         * statusBorder: ImageView          // Changing image resource
         * sidewaysWarning: ImageView       // Changing image resource
         * floorWarning: ImageView
         * ceilingWarning: ImageView
         * horizontalWarning: ImageView
         * orientationWarning: ImageView    // Changing image resource
         * statusText: TextView
         *
         * 'Changing image resource' means that View's image resource might change during the
         * scan. For example the statusBorder's image resource changes when there's changes in
         * ARCore's TrackingState during a scan. */

        /** To replace some CubiCapture View with your own View (example): */
        // val newView: TextView = findViewById(R.id.newStatusText)
        // cubiCapture.setNewView(cubiCapture.statusText, newView)

        /** To change image resource of some CubiCapture view call (example): */
        // cubiCapture.ceilingWarning.setImageResource(R.drawable.new_ceiling_warning)

        /**
         * The following image resources might be set to a 'View' which is private and/or has
         * changing image resources:
         * recordingImage        // Set to private record button
         * notRecordingImage     // Set to private record button
         * hintLabelBackground   // Set to private hint labels
         * rotate180Image        // Set to orientationWarning: ImageView
         * trackingStatusBorders // Set to statusBorder: ImageView
         * failureStatusBorders  // Set to statusBorder: ImageView
         * turnLeftImage         // Set to sidewaysWarning: ImageView
         * turnRightImage        // Set to sidewaysWarning: ImageView
         *
         * For example, when the user clicks the record button the image resource of the view is set
         * to 'recordingImage'.
         * In this case you can change the image resource like this (example): */
        // cubiCapture.recordingImage = R.drawable.new_recording

        /** Speech recognition UI can be modified by using the 'colors.xml' and 'dimens.xml' files.
         * To see how to customize the graphics, size of the views and layout margins read the
         * comments in those files or the documentation of this library module version. */

        /** Hint label texts can be changed by redefining the strings in the 'strings.xml' file.
         * To see how to do that, see the comments in the 'strings.xml' file or the documentation
         * of this library module version. */

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

    /** Receives Scan folder and Zip file from CubiEventListener */
    override fun getFile(code: Int, file: File) {
        when (code) {
            1 -> { // Scan folder as File
                scanFolder = file
            }
            2 -> { } // Zip File
        }
    }

    /** Receives status updates from CubiEventListener */
    override fun getStatus(code: Int, description: String) {
        Log.d("DEBUGTAG", "code: $code, description: $description") // Logging status updates

        when (code) {
            2 -> { // Finished recording
                saving = true
            }
            3 -> { // Not enough data
                /** You will receive code 5 after this */
                errorMessage = description
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
                    // Scan successful. Starting ViewScanActivity to view its video
                    val viewScanIntent = Intent(baseContext, ViewScanActivity::class.java)
                    viewScanIntent.putExtra("folderPath", scanFolder?.path)
                    startActivity(viewScanIntent)
                } else if (errorMessage != null) {
                    // Not successful scan - 'errorMessage' will be displayed in ScanInfoActivity
                    val displayErrorIntent = Intent()
                    displayErrorIntent.putExtra("errorMessage", errorMessage)
                    setResult(RESULT_OK, displayErrorIntent)
                }
                finish()
            }
            12 -> { // MediaFormat and MediaCodec failed to be configured and started
                /** You will receive code 5 after this code */
                errorMessage = description
            }
            13 -> { // Scan drifted
                /** You will receive code 5 after this code */
                errorMessage = description
            }
            19 -> { // Back button pressed twice
                finish()
            }
            28 -> { // ARCore was unable to start tracking during the first five seconds
                /** You will receive code 5 after this code */
                errorMessage = description
            }
            54, 64, 66 -> {
                // 54, Writing of scan data failed: <e>,
                // 64, Unable to start saving. Error: <e>,
                // 66, Unable to get correct values for the device's position
                /** You will receive code 5 after this code */
                errorMessage = description
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

    override fun onBackPressed() {
        /** Navigation bar's back button press is disabled during saving. */
        if (!saving) {
            super.onBackPressed()
        }
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
