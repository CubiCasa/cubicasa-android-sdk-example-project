package cubi.casa.exampleproject

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.permissionIsGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}