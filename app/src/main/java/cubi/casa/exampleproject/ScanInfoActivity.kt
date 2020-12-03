package cubi.casa.exampleproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.ar.core.ArCoreApk
import kotlinx.android.synthetic.main.activity_scan_info.*

/** Read the CubiCapture documentary at:
 * https://www.cubi.casa/developers/cubicasa-android-sdk */

/** Example Activity for setting the scan folder name and filling the Order info.
 * String value of the streetInput (EditText) is used as the <scanFolderName> */

class ScanInfoActivity : AppCompatActivity() {

    private var permissionsGranted = false
    private var mUserRequestedInstall = true
    private val requestPermissionCode = 0
    private val REQUEST_DISPLAY_ERROR = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_info)

        /** CubiCapture requires CAMERA and WRITE_EXTERNAL_STORAGE permissions to be granted.
         * Remember to request those permissions before you start your scanning Activity.
         * To avoid crashes you should also check that the device's ARCore version is up-to-date. */
        checkPermissionsAndArCore()

        startScanButton.setOnClickListener {
            if (!permissionsGranted) {
                requestPermissions()
                return@setOnClickListener
            }

            val street = streetInput.text.toString().trim()

            /** String value of the streetInput (EditText) is required
             * because it's used as the <scanFolderName>.
             * You should also check that scan folder with that name does not already exist! */
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

            val orderInfo: Array<String> =
                arrayOf(street, number, suite, city, state, country, postalCode)

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

    private fun checkPermissionsAndArCore() {
        // Checking Camera and Storage permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
        } else {
            permissionsGranted = true
        }

        // Request ARCore install if it's not installed
        when (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
            ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                mUserRequestedInstall = false
                return
            }
        }
    }

    // Requesting Camera and Storage permissions
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            requestPermissionCode
        )
    }

    // Called when permission is granted or not
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestPermissionCode &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
            grantResults[1] == PackageManager.PERMISSION_GRANTED
        ) {
            permissionsGranted = true
        }
    }

    override fun onBackPressed() { } // Disabling Navigation bar's back press
}
