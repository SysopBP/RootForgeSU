package com.sysopbp.rootforgesu

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.io.File

class MainActivity : Activity() {
    private fun field(label: String, value: String): TextView = TextView(this).apply {
        text = "$label\n$value"
        textSize = 16f
        setTextColor(Color.WHITE)
        setPadding(24, 18, 24, 18)
        setBackgroundColor(Color.rgb(24, 24, 28))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 32, 24, 24)
            setBackgroundColor(Color.BLACK)
        }
        val title = TextView(this).apply {
            text = "RootForgeSU"
            textSize = 30f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(12, 12, 12, 32)
        }
        container.addView(title)
        val kernel = runCatching { File("/proc/sys/kernel/osrelease").readText().trim() }.getOrDefault("Unavailable")
        val values = listOf(
            "Device" to "${Build.MANUFACTURER} ${Build.MODEL}",
            "Android" to "API ${Build.VERSION.SDK_INT} • ${Build.VERSION.RELEASE}",
            "Kernel" to kernel,
            "Architecture" to Build.SUPPORTED_ABIS.joinToString(),
            "Root environment" to (if (listOf("/system/bin/su", "/system/xbin/su", "/debug_ramdisk/su").any { File(it).exists() }) "Possible su binary detected (not verified)" else "Not detected by basic path check"),
            "Status" to "Diagnostics only • No privileged actions"
        )
        values.forEach { (key, value) ->
            container.addView(field(key, value))
            container.addView(TextView(this).apply { height = 12 })
        }
        setContentView(ScrollView(this).apply { addView(container) })
    }
}
