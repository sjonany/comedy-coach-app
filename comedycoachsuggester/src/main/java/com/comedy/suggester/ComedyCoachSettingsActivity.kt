package com.comedy.suggester

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.comedy.suggester.ui.MainScreen
import com.comedy.suggester.ui.theme.ComedyCoachControllerTheme

class ComedyCoachSettingsActivity : ComponentActivity() {
    companion object {
        private const val LOG_TAG = "ComedyCoachIMESettingsActivity"
        private const val REQUEST_CODE_OVERLAY_PERMISSION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(LOG_TAG, "onCreate")
        checkHasDrawOverlayPermission()

        // Draw the content
        enableEdgeToEdge()
        setContent {
            ComedyCoachControllerTheme {
                MainScreen()
            }
        }
    }

    private fun checkHasDrawOverlayPermission() {
        // Request for permission to draw overlays - needed for ChatWatcherAccessibilityService.
        // This permission has to be requested during runtime. Ugh.
        // https://stackoverflow.com/a/46390128
        if (!Settings.canDrawOverlays(this)) {
            Log.d(
                LOG_TAG,
                "can't draw overlays, requesting for permission"
            )
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
        }
    }
}