package cubi.casa.exampleproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.ar.core.ArCoreApk
import kotlinx.android.synthetic.main.activity_scan_info.*

/** Example Activity for setting the scan folder name and filling the Order info.
 * String value of the streetInput (EditText) is used as the <scanFolderName> */

class ScanInfoActivity : AppCompatActivity() {

    private var mUserRequestedInstall = true
    private val REQUEST_DISPLAY_ERROR = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_info)

        /** CubiCapture requires CAMERA permission to be granted.
         * Remember to request the permission before you start your scanning Activity.
         * To avoid crashes you should also check that the device's ARCore version is up-to-date. */

        /* Requesting 'ACCESS_FINE_LOCATION' permission on the app side because we've set
         * 'CubiCapture.trueNorth' to 'TrueNorth.ENABLED' in ScanActivity.kt */
        if (!locationPermissionGranted(this)) {
            requestLocationPermission()
        }

        startScanButton.setOnClickListener {
            /* Check if 'CAMERA' permission is granted, and ARCore is installed and up-to-date.
             * You should also explain user why you need these permission,
             * and handle a case where user denies a permission. */
            if (!cameraPermissionGranted()) {
                requestCameraPermission()
                return@setOnClickListener
            }
            if (!arCoreIsInstalledAndUpToDate()) {
                return@setOnClickListener
            }

            val street = streetInput.text.toString().trim()

            /** String value of the streetInput (EditText) is required
             * because we use as the <scanFolderName> in this example project.
             * You should also check that scan folder with that name does not already exist,
             * and in this case that the String 'street' is a valid File name! */
            if (street.isBlank()) {
                streetInput.error = "Required."
                return@setOnClickListener
            }

            val number = numberInput.text.toString().trim()
            val suite = suiteInput.text.toString().trim()
            val city = cityInput.text.toString().trim()
            val state = stateInput.text.toString().trim()
            val country = countryInput.text.toString().trim()
            val postalCode = postalInput.text.toString().trim()

            val orderInfo = arrayOf(street, number, suite, city, state, country, postalCode)

            val scanIntent = Intent(baseContext, ScanActivity::class.java)
            scanIntent.putExtra("orderInfo", orderInfo)
            startActivityForResult(scanIntent, REQUEST_DISPLAY_ERROR)
        }
    }

    // Receives an error message from the intent extras - e.g. when the scan was too short
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_DISPLAY_ERROR && resultCode == RESULT_OK) {
            if (data == null)
                return

            val errorMessage = data.getStringExtra("errorMessage") ?: return

            scanInfoConstraintLayout.post {
                displayErrorDialog(errorMessage)
            }
        }
    }

    // Displays an error dialog - e.g. when the scan was too short (not enough data)
    private fun displayErrorDialog(errorMessage: String) {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater
            .from(this)
            .inflate(R.layout.error_dialog, null)
        val okBtn = view.findViewById<Button>(R.id.okButton)
        val messageTextView = view.findViewById<TextView>(R.id.messageTextView)

        messageTextView.text = errorMessage
        builder.setView(view)

        val dialog = builder.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        okBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun cameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            123
        )
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            321
        )
    }

    /* Checks if the ARCore installed and is up-to-date. Returns true if it is, false otherwise.
     * Also requests install or update if needed */
    private fun arCoreIsInstalledAndUpToDate(): Boolean {
        return when (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
            ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                mUserRequestedInstall = false
                false
            }
            ArCoreApk.InstallStatus.INSTALLED -> { true }
            else -> { false }
        }
    }

    override fun onBackPressed() { } // Disabling Navigation bar back press
}