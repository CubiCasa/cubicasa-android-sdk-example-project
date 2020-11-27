package cubi.casa.exampleproject

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import cubi.casa.cubicapture.CubiCapture
import java.io.File

class ExampleActivity : AppCompatActivity(), CubiCapture.CubiEventListener {

    // Create lateinit variable for CubiCapture
    private lateinit var cubiCapture: CubiCapture

    // Boolean 'saving' is used to disable navigation bar back press during saving
    private var saving = false

    /** Read the CubiCapture documentary at:
     * https://www.cubi.casa/developers/cubicasa-android-sdk */

    /** CubiCapture requires CAMERA and WRITE_EXTERNAL_STORAGE permissions to be granted.
     * Remember to request those permissions before you start your scanning Activity.
     * To avoid crashes you should also check that the device's ARCore version is up-to-date. */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_layout)

        // To keep the screen turned on and in landscape orientation
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Initialize CubiCapture lateinit variable
        cubiCapture = supportFragmentManager.findFragmentById(R.id.cubiFragment) as CubiCapture

        // Register CubiEventListener's interface callback
        cubiCapture.registerCallback(this)

        // Before scanning with CubiCapture you first have to set scan output folder name
        cubiCapture.scanFolderName = "exampleFolderName"

        // Name of the folder which contains all the scan folders. This is "CubiCapture" by default.
        cubiCapture.allScansFolderName = "MyScans"

        // Before starting the scan you can add order information (not required)
        cubiCapture.setOrderInfo(
            "ExampleStreet",
            "10",
            "ExampleSuite",
            "ExampleCity",
            "ExampleState",
            "ExampleCountry",
            "12345"
        )

        /** -------------------------- UI SETTINGS BELOW -------------------------- */

        /** If you want to hide scan timer call: */
        // cubiCapture.setTimerEnabled(false) // Visible (true) by default

        /** If you want to hide CubiCapture's back button call: */
        // cubiCapture.setBackButtonEnabled(false) // Visible (true) by default

        /** If you want to change CubiCapture's ARCore tracking error texts: */
        // cubiCapture.excessiveMotionErrorText = "Excessive motion!"
        // cubiCapture.insufficientErrorText = "Insufficient visual features or poor lighting!"

        /** If you want to change the warning sound call: */
        // cubiCapture.setWarningSound(R.raw.new_warning_sound)

        /**
         * The following CubiCapture Views are customizable:
         * buttonRecord: ImageView          // Changing image resource
         * buttonRecordHint: ImageView
         * statusBorder: ImageView          // Changing image resource
         * sidewaysWarning: ImageView       // Changing image resource
         * floorWarning: ImageView
         * ceilingWarning: ImageView
         * orientationWarning: ImageView    // Changing image resource
         * statusText: TextView
         *
         * 'Changing image resource' means that View's image resource might change during the
         * scan. For example the buttonRecord's image resource changes when user clicks it. */

        /** If you want to replace some CubiCapture View with your own View (example): */
        // val newView: ImageView = findViewById(R.id.newRecordingBtn)
        // cubiCapture.setNewView(cubiCapture.buttonRecord, newView)

        /** If you want to change image resource of some CubiCapture view call (example): */
        // cubiCapture.buttonRecord.setImageResource(R.drawable.new_not_recording)

        /**
         * The following image resources might be set to certain views during the scan:
         * recordingImage        // Set to buttonRecord: ImageView
         * rotate180Image        // Set to orientationWarning: ImageView
         * trackingStatusBorders // Set to statusBorder: ImageView
         * failureStatusBorders  // Set to statusBorder: ImageView
         * turnLeftImage         // Set to sidewaysWarning: ImageView
         * turnRightImage        // Set to sidewaysWarning: ImageView
         *
         * For example when the user clicks buttonRecord the image resource
         * of the view is set to recordingImage.
         * In this case you can change the image resource like this (example): */
        // cubiCapture.recordingImage = R.drawable.new_recording

        /** ---------------------------------------------------------------------------- */
        /** ----------------- ABOUT AUTOMATIC AND MANUAL ZIPPING BELOW ----------------- */

        /** If you want to disable automatic zipping after scan call: */
        // cubiCapture.setAutoZippingEnabled(false) // true (auto zips) by default

        /** Zipping scan folder if automatic zipping is disabled.
         * This can be called after scan files are saved successfully.
         * This returns the Zip file if it's is successful or null if zipping failed. */
        // val zipFile = cubiCapture.zipScan(scanFolderPath) // Pass scan folder path as String
    }

    // Receives Scan folder and Zip file from CubiEventListener
    override fun getFile(code: Int, file: File) {
        when (code)
        {
            1 -> { } // Scan folder as File
            2 -> { } // Zip File
        }
    }

    // Receives status updates from CubiEventListener
    override fun getStatus(code: Int, description: String) {
        Log.d("DEBUGTAG", "code: $code, description: $description")

        when (code)
        {
            2 -> { // Finished recording
                saving = true
            }
            5 -> { // CubiCapture is finished
                finish()
            }
            19 -> { // Back button pressed twice
                finish()
            }
        }
    }

    // The following (lifecycle) methods and calls allow CubiCapture to handle
    // its processes correctly when there's changes in the Activity.
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

    override fun onDestroy() {
        super.onDestroy()
        cubiCapture.destroy()
    }

    override fun onBackPressed() {
        // Navigation bar's back button press is disabled during saving.
        if (!saving) {
            super.onBackPressed()
        }
    }

}
