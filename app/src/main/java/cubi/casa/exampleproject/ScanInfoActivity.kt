package cubi.casa.exampleproject

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.ar.core.ArCoreApk
import cubi.casa.cubicapture.PropertyType
import cubi.casa.exampleproject.databinding.ActivityScanInfoBinding

/** Example Activity for setting the scan folder name and filling the Order info.
 * String value of the streetInput (EditText) is used as the <scanFolderName> */

class ScanInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** CubiCapture requires CAMERA permission to be granted.
         * Remember to request the permission before you start your scanning Activity.
         * To avoid crashes you should also check that the device's ARCore version is up-to-date. */

        /* Requesting 'ACCESS_FINE_LOCATION' permission on the app side because we've set
         * 'CubiCapture.trueNorth' to 'TrueNorth.ENABLED' in ScanActivity.kt */
        if (!permissionIsGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, 321, openSettings = false)
        }

        val resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            binding.scanInfoConstraintLayout.post {
                result?.data?.getStringExtra("errorMessage")?.let { displayErrorDialog(it) }
            }
        }

        binding.startScanButton.setOnClickListener {
            /* Check if 'CAMERA' permission is granted, and ARCore is installed and up-to-date.
             * You should also explain user why you need these permission,
             * and handle a case where user denies a permission. */
            if (!permissionIsGranted(Manifest.permission.CAMERA)) {
                requestPermission(Manifest.permission.CAMERA, 123, openSettings = true)
                return@setOnClickListener
            }
            if (!arCoreIsInstalledAndUpToDate()) {
                return@setOnClickListener
            }

            val street = binding.streetInput.text.toString().trim()

            /** String value of the streetInput (EditText) is required
             * because we use it as the <scanFolderName> in this example project.
             * You should also check that scan folder with that name does not already exist,
             * and in this case that the String 'street' is a valid File name! */
            if (street.isBlank()) {
                binding.streetInput.error = "Required."
                binding.scrollView.smoothScrollTo(0, binding.streetInput.top)
                return@setOnClickListener
            }

            val propertyTypeIndex = binding.propertyTypeSpinner.selectedItemPosition - 1
            val propertyType = PropertyType.values().getOrNull(propertyTypeIndex)
            if (propertyType == null) {
                val spinnerTextView = binding.propertyTypeSpinner.selectedView as TextView
                spinnerTextView.error = "Required."
                binding.scrollView.smoothScrollTo(0, binding.propertyTypeSpinner.top)
                return@setOnClickListener
            }

            val number = binding.numberInput.text.toString().trim()
            val suite = binding.suiteInput.text.toString().trim()
            val city = binding.cityInput.text.toString().trim()
            val state = binding.stateInput.text.toString().trim()
            val country = binding.countryInput.text.toString().trim()
            val postalCode = binding.postalInput.text.toString().trim()

            val orderInfo = arrayOf(street, number, suite, city, state, country, postalCode)

            val scanIntent = Intent(baseContext, ScanActivity::class.java)
            scanIntent.putExtra("orderInfo", orderInfo)
            scanIntent.putExtra("propertyType", propertyType)
            scanIntent.putExtra("safeMode", binding.safeModeSwitch.isChecked)
            resultLauncher.launch(scanIntent)
        }

        // Showing Safe mode switch if device is Depth API supported
        val settings = getSharedPreferences("settings", 0)
        if (settings.getBoolean("showSafeModeSwitch", false)) {
            binding.safeModeLayout.visibility = View.VISIBLE
        }

        setupPropertyTypeSpinner()

        // Overriding the back press to move the app to background
        onBackPressedDispatcher.addOnClickListener(this) {
            moveTaskToBack(true)
        }
    }

    private fun setupPropertyTypeSpinner() {
        val adapter = object : ArrayAdapter<String>(
            this,
            R.layout.custom_spinner_item,
            resources.getStringArray(R.array.propertyTypes)
        ) {
            override fun isEnabled(position: Int) = position != 0

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTypeface(null, Typeface.ITALIC)
                    view.setTextColor(Color.GRAY)
                } else {
                    view.setTextColor(Color.BLACK)
                }
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.propertyTypeSpinner.adapter = adapter

        binding.propertyTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        (view as TextView).apply {
                            setTypeface(null, Typeface.ITALIC)
                            setTextColor(Color.GRAY)
                        }
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
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

    private fun requestPermission(permission: String, requestCode: Int, openSettings: Boolean) {
        if (!shouldShowRequestPermissionRationale(permission)) {
            // Permission can be requested, request it
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else if (openSettings) {
            // Permission can't be requested, open settings
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
            )
            Toast.makeText(baseContext, "Please grant permission", Toast.LENGTH_SHORT).show()
        }
    }

    /** Checks if the ARCore installed and is up-to-date. Returns true if it is, false otherwise.
     * Also requests install or update if needed */
    private fun arCoreIsInstalledAndUpToDate(): Boolean =
        ArCoreApk.getInstance().requestInstall(this, true) == ArCoreApk.InstallStatus.INSTALLED
}