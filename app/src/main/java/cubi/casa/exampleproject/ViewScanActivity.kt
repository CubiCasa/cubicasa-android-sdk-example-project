package cubi.casa.exampleproject

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_view_scan.*
import java.io.File

/** Read the CubiCapture documentary at:
 * https://www.cubi.casa/developers/cubicasa-android-sdk */

/** Example Activity for watching the video produced by the CubiCapture and deleting the scan. */

class ViewScanActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_scan)

        val folderPath = intent.extras!!.getString("folderPath")

        videoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE)
        val uri = Uri.fromFile(File("$folderPath/video.mp4"))
        videoView.setVideoURI(uri)

        videoView.setOnPreparedListener { mp ->
            mediaPlayer = mp
            videoView.start()
        }

        val mediaController = MediaController(this)
        videoView.setMediaController(mediaController)
        mediaController.setAnchorView(frameLayout)

        // Deleting the scan folder and starting ScanInfoActivity
        deleteScanButton.setOnClickListener {
            if (folderPath != null)
                File(folderPath).deleteRecursively()

            startActivity(Intent(baseContext, ScanInfoActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.suspend()
    }

    override fun onResume() {
        super.onResume()
        if (!videoView.isPlaying)
            videoView.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(baseContext, ScanInfoActivity::class.java))
        finish()
    }
}
