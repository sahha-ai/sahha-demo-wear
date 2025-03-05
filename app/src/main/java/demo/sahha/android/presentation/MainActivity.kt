/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package demo.sahha.android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import demo.sahha.android.framework.manager.PermissionManager
import demo.sahha.android.presentation.navigation.NavGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        val permissionManager = PermissionManager(this)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            NavGraph(permissionManager)
        }

        permissionManager.requestPermissions()
    }
}