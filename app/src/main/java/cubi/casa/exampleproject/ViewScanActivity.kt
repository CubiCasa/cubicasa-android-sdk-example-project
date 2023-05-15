package cubi.casa.exampleproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cubi.casa.cubicapture.ScanPlayback
import cubi.casa.cubicapture.ScanPlaybackListener
import java.io.File

/** Example Activity for ScanPlayback */

class ViewScanActivity : AppCompatActivity(), ScanPlaybackListener {

    private lateinit var scanPlayback: ScanPlayback

    private lateinit var scanFolder: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_scan)

        scanPlayback = supportFragmentManager.findFragmentById(R.id.scanPlayback) as ScanPlayback
        scanFolder = intent.getSerializable("scanFolder")!!
        scanPlayback.setScanFolder(scanFolder)
        scanPlayback.setOnBackButtonClickListener {
            deleteScanAndFinish()
        }

        onBackPressedDispatcher.addOnClickListener(this) {
            deleteScanAndFinish()
        }
    }

    /** Called when the scan playback's playing state changes (playing/paused) */
    override fun onIsPlayingChanged(isPlaying: Boolean) { }

    private fun deleteScanAndFinish() {
        scanFolder.deleteRecursively()
        startActivity(Intent(baseContext, ScanInfoActivity::class.java))
        finish()
    }
}