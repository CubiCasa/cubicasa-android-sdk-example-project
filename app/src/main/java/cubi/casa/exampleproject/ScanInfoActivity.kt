package cubi.casa.exampleproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.ar.core.ArCoreApk
import cubi.casa.cubicapture.PropertyType
import cubi.casa.exampleproject.databinding.ActivityScanInfoBinding

/** Example Activity for starting the scan. */

class ScanInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanInfoBinding

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        binding.root.post {
            result?.data?.getStringExtra("errorMessage")?.let { errorMessage ->
                displayErrorDialog(errorMessage)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!shouldRequestLocationPermission()) {
            /* Requesting 'ACCESS_FINE_LOCATION' permission on the app side because we've set
             * 'CubiCapture.trueNorth' to 'TrueNorth.ENABLED' in ScanActivity.kt */
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions, 123)
        }

        binding.startScanButton.setOnClickListener {
            if (!arCoreIsInstalledAndUpToDate()) {
                return@setOnClickListener
            }

            val propertyTypeIndex = binding.propertyTypeSpinner.selectedItemPosition - 1
            val propertyType = PropertyType.values().getOrNull(propertyTypeIndex)
            if (propertyType == null) {
                val spinnerTextView = binding.propertyTypeSpinner.selectedView as TextView
                spinnerTextView.error = "Required."
                return@setOnClickListener
            }

            val scanIntent = Intent(baseContext, ScanActivity::class.java)
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

    private fun shouldRequestLocationPermission(): Boolean {
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val permissionState = ContextCompat.checkSelfPermission(this, locationPermission)
        val isGranted = permissionState == PackageManager.PERMISSION_GRANTED
        if (isGranted) {
            return false
        }
        return !shouldShowRequestPermissionRationale(locationPermission)
    }

    /** Checks if the ARCore installed and is up-to-date. Returns true if it is, false otherwise.
     * Also requests install or update if needed */
    private fun arCoreIsInstalledAndUpToDate(): Boolean =
        ArCoreApk.getInstance().requestInstall(this, true) == ArCoreApk.InstallStatus.INSTALLED
}