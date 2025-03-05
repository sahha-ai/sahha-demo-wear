package demo.sahha.android.framework.manager

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionManager(
    private val activity: ComponentActivity,
) {
    private val requestPermissionsLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                println("All permissions granted.")
            } else {
                println("One or more permissions were denied.")
            }
        }

    fun requestPermissions() {
        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.BODY_SENSORS,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        )
    }

    fun arePermissionsGranted(): Boolean {
        val bodySensorsGranted = ContextCompat.checkSelfPermission(
            activity, Manifest.permission.BODY_SENSORS
        ) == PackageManager.PERMISSION_GRANTED
        val activityRecognitionGranted = ContextCompat.checkSelfPermission(
            activity, Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
        return bodySensorsGranted && activityRecognitionGranted
    }
}