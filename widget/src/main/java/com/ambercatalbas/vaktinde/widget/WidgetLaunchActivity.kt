package com.ambercatalbas.vaktinde.widget

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class WidgetLaunchActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(launchIntent)
        }
        finish()
    }
}
