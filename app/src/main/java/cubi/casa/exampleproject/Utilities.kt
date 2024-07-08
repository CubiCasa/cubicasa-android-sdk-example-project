package cubi.casa.exampleproject

import android.content.Intent
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.lifecycle.LifecycleOwner
import java.io.Serializable

fun OnBackPressedDispatcher.addOnClickListener(owner: LifecycleOwner, onPressed: () -> Unit) {
    this.addCallback(
        owner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onPressed()
            }
        }
    )
}

inline fun <reified T : Serializable> Intent.getSerializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}
